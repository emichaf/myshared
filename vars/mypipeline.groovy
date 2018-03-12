import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException


def minfunc(DOCKER_HOST) {

    unstash "eiffel-intelligence-artifact-wrapper"
    try {

        //docker.withServer("$DOCKER_HOST", 'remote_docker_host') {


        println "in stage"

        sh "ls"

       // }
    } catch (FlowInterruptedException interruptEx) {


    }
}

