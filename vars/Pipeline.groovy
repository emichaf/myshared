import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException


def testar() {
    String DOCKER_HOST = "tcp://docker104-eiffel999.lmera.ericsson.se:4243"

    unstash "eiffel-intelligence-artifact-wrapper"
    try {

        docker.withServer("$DOCKER_HOST", 'remote_docker_host') {

            stage('Stage Groovy') {
                println "in stage"

                sh "ls"
            }
        }
    } catch (FlowInterruptedException interruptEx) {


    }
}


def SC_1(DOCKER_HOST, BUILD_COMMAND) {


    //String DOCKER_HOST = "tcp://docker104-eiffel999.lmera.ericsson.se:4243"
    //String BUILD_COMMAND = "mvn clean package -DskipTests"

    unstash "eiffel-intelligence"

    try {

        docker.withServer("$DOCKER_HOST", 'remote_docker_host') {
            docker.image('emtrout/nind23').inside("--privileged") {
                stage('XXStage GroovyX') {
                    println "in stageXXX"

                    // testar shared libs in local lib
                    def shellLib = new shell()
                    def commitId = shellLib.pipe("git rev-parse HEAD")
                    println commitId



                    sh "${BUILD_COMMAND}"
                    sh "ls"


                }
            }
        }
    } catch (FlowInterruptedException interruptEx) {


    }
}


def mybuildstep() {


    String DOCKER_HOST = "tcp://docker104-eiffel999.lmera.ericsson.se:4243"
    String BUILD_COMMAND = "mvn clean package -DskipTests"

    unstash "eiffel-intelligence-artifact-wrapper"

    println "mybuildstep was here!"


    try {

        docker.withServer("$DOCKER_HOST", 'remote_docker_host') {
            docker.image('emtrout/nind23').inside("--privileged") {
                stage('XXStage GroovyX') {
                    println "in stageXXX"

                    // testar shared libs in local lib
                    def shellLib = new shell()
                    def commitId = shellLib.pipe("git rev-parse HEAD")
                    println commitId



                    sh "${BUILD_COMMAND}"
                    sh "ls"


                }
            }
        }
    } catch (FlowInterruptedException interruptEx) {


    }
}


return this