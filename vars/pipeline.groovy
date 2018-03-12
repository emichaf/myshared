import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException


def minfunc() {

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

