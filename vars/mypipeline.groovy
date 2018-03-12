import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException


def minfunc(Map DOCKER_HOST) {

    unstash "eiffel-intelligence-artifact-wrapper"
    try {

        docker.withServer("$DOCKER_HOST.name", 'remote_docker_host') {
            docker.image('emtrout/nind23').inside("--privileged") {
                println "in stage"

                sh "echo ${DOCKER_HOST}"


            }
        }

    } catch (FlowInterruptedException interruptEx) {


    }
}

