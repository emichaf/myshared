// ####################################################################################################################
// Java_CI_Pipeline_Travisfile_Test
//
// Build/test steps executed in docker containers
//
// Required InParams:
//     - ARM_URL
//     - DOCKER_HOST
//     - SOURCE_CODE_REPO
//     - WRAPPER_REPO
//     - BUILD_INFO_FILE
//     - BUILD_COMMAND
//     - SONARQUBE_LOGIN_TOKEN
//     - DOCKERIMAGE_BUILD_TEST
//     - DOCKERIMAGE_DOCKER_BUILD_PUSH
//
//  Maintainer: michael.frick@ericsson.com
//	 
// ####################################################################################################################
  
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException

def call(body) {

    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()


	node{

  // ######### NOTES & INFORMATION & WARNINGS ##############################################################################
  // OBS change dir in containers not working, so fetching scm in containers is required. Stash/unstash dir() not working..
  // https://issues.jenkins-ci.org/browse/JENKINS-46636
  // https://issues.jenkins-ci.org/browse/JENKINS-33510
  // ######### NOTES & INFORMATION & WARNINGS ##############################################################################

  // ## Global Vars
     String GIT_SHORT_COMMIT
     String GIT_LONG_COMMIT
     String GITHUB_HASH_TO_USE
     String ARM_ARTIFACT
     String ARM_ARTIFACT_PATH
     Object POM
     String OUTCOME_CONCLUSION

try {


 docker.withServer("$pipelineParams.DOCKER_HOST", 'remote_pipelineParams.DOCKER_HOST') {

     /*------------------------------------------------------------------------------------------
     For inside() to work, the Docker server and the Jenkins agent must use the same filesystem,
     so that the workspace can be mounted.

     When Jenkins detects that the agent is itself running inside a Docker container, it will automatically pass
     the --volumes-from argument to the inside container, ensuring that it can share a workspace with the agent.

     ------------------------------------------------------------------------------------------*/


       stage ('Wrapper Checkout') {

        // print existing env vars
        echo sh(returnStdout: true, script: 'env')



           dir ('wrapper') {

                            git branch: "master", url: "$pipelineParams.WRAPPER_REPO"

                            // Read build info file with github hash
                            sh "cat $pipelineParams.BUILD_INFO_FILE"
                            def props = readYaml file: "$pipelineParams.BUILD_INFO_FILE"
                            GITHUB_HASH_TO_USE = props.commit

                            sh "echo hash -> $GITHUB_HASH_TO_USE"

              }

        }



       stage ("GITHUB Checkout: $GITHUB_HASH_TO_USE") {

              dir ('sourcecode') {

                  checkout scm: [$class: 'GitSCM',
                          userRemoteConfigs: [[url: "$pipelineParams.SOURCE_CODE_REPO"]],
                          branches: [[name: "$GITHUB_HASH_TO_USE"]]]

                          GIT_SHORT_COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()

                          GIT_LONG_COMMIT =  sh(returnStdout: true, script: "git log --format='%H' -n 1").trim()

                          POM = readMavenPom file: 'pom.xml'

                          ARM_ARTIFACT = "${POM.artifactId}-${POM.version}.war"

                          ARM_ARTIFACT_PATH = "${pipelineParams.ARM_URL}/${POM.version}/${ARM_ARTIFACT}"
              }

        }








    dir ('sourcecode') {  // workaround to change dir outside container, not working inside container execution.. yet, see issues stated on top of file!


             docker.image("$pipelineParams.DOCKERIMAGE_BUILD_TEST").inside("--privileged"){

           /*
                       stage ('SonarQube Code Analysis') {

                                         //docker.image('sonarqube').withRun('-p 9000:9000 -p 9092:9092 -e "SONARQUBE_JDBC_USERNAME=sonar" -e "SONARQUBE_JDBC_PASSWORD=sonar" -e "SONARQUBE_JDBC_URL=jdbc:postgresql://localhost/sonar"') { c ->
                                         //docker.image('sonarqube').withRun('docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube') { c ->


                                                //dir ('wrapper') {
                                                        docker.image('emtrout/dind:latest').inside {

                                                              //sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000'

                                                              sh 'mvn sonar:sonar -Dsonar.host.url=https://sonarqube.lmera.ericsson.se'


                                                              //sh 'mvn sonar:sonar -Dsonar.host.url=http://docker104-eiffel999.lmera.ericsson.se:9000 -Dsonar.login=1c8363811fc123582a60ed4607782902e2f5ecc9'


                                                        }

                                                //}

                                         //}


                           }
           */


/*
             stage('SonarQube Code Analysis') {

                   //sh 'mvn sonar:sonar -Dsonar.host.url=https://sonarqube.lmera.ericsson.se'
                 //  sh "mvn sonar:sonar -Dsonar.host.url=http://docker104-eiffel999.lmera.ericsson.se:9000 -Dsonar.login=$pipelineParams.SONARQUBE_LOGIN_TOKEN"


             }

*/



             stage('Compile') {

                      sh "${pipelineParams.BUILD_COMMAND}"

                      sh 'ls target'
              }



              stage('UnitTests & FlowTests with TestDoubles)') {
                      // OBS privileged: true for image for embedded mongodb (flapdoodle) to work
                      // and glibc in image!

                        def travis_datas = readYaml file: ".travis.yml"

                        // Execute tests (steps) in travis file, ie same file which is used in travis build (open source)
                        travis_datas.script.each { item ->
                              // sh "$item"
                        };
              }




              stage('Publish Artifact ARM -> WAR/JAR)') {

                       withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'NEXUS_CREDENTIALS_EIFFEL_NEXUS_EXTENSION',
                                              usernameVariable: 'EIFFEL_NEXUS_USER',
                                              passwordVariable: 'EIFFEL_NEXUS_PASSWORD']]) {

                              // Upload to ARM (ex eiffel-intelligence-0.0.1-SNAPSHOT.war)
                              sh "curl -v -u ${EIFFEL_NEXUS_USER}:${EIFFEL_NEXUS_PASSWORD} --upload-file ./target/${ARM_ARTIFACT} ${ARM_ARTIFACT_PATH}"
                      }
                }


           } // docker.image(.....

    } // dir ('sourcecode') 




    dir ('wrapper') {  // workaround to change dir outside container, not working inside container execution.. yet, see issues stated on top of file!

       docker.image("$pipelineParams.DOCKERIMAGE_DOCKER_BUILD_PUSH").inside("--privileged"){

           stage('Build and Push Docker Image to Registry') {


                               def exists = fileExists '/src/main/docker/app.war'
                               if (exists) {
                                   sh "rm /src/main/docker/app.war"
                               }


                               withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'NEXUS_CREDENTIALS_EIFFEL_NEXUS_EXTENSION',
                                              usernameVariable: 'EIFFEL_NEXUS_USER',
                                              passwordVariable: 'EIFFEL_NEXUS_PASSWORD']]) {



                                   // Fetch Artifact (jar/war) from ARM
                                   sh "curl -X GET -u ${EIFFEL_NEXUS_USER}:${EIFFEL_NEXUS_PASSWORD} ${ARM_ARTIFACT_PATH} -o src/main/docker/app.war"

                                   sh "ls src/main/docker/"

                                }


                                withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                            credentialsId: 'DOCKERHUB_CREDENTIALS',
                                            usernameVariable: 'DOCKER_HUB_USER',
                                            passwordVariable: 'DOCKER_HUB_PASSWORD']]) {

                                   sh "ls src/main/docker/"

                                   sh "docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}"

                                   sh "docker build --no-cache=true -t ${DOCKER_HUB_USER}/${POM.artifactId}:latest -f src/main/docker/Dockerfile src/main/docker/"

                                   sh "docker push ${DOCKER_HUB_USER}/${POM.artifactId}:latest"

                                   sh "docker build --no-cache=true -t ${DOCKER_HUB_USER}/${POM.artifactId}:${GIT_SHORT_COMMIT} -f src/main/docker/Dockerfile src/main/docker/"

                                   sh "docker push ${DOCKER_HUB_USER}/${POM.artifactId}:${GIT_SHORT_COMMIT}"

                                   sh "docker logout"

                                   }


                       OUTCOME_CONCLUSION = "SUCCESSFUL"

           } // stage('..

       } // docker.image('....

    } // dir ('wrapper') {


         // Clean up workspace
         step([$class: 'WsCleanup'])

 } //  docker.withServer(...

currentBuild.result = 'SUCCESS'

} catch (FlowInterruptedException interruptEx) {



         OUTCOME_CONCLUSION = "ABORTED"
         currentBuild.result = 'ABORTED'

         // Throw
         throw interruptEx

} catch (err) {

        OUTCOME_CONCLUSION = "FAILED"
        currentBuild.result = 'FAILURE'

} finally {

} // finally


} // node
	
	
	
	
} // def call(body) 