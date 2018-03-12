@Grab('org.apache.commons:commons-math3:3.4.1')
import org.apache.commons.math3.primes.Primes
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException


def minfunc(DOCKER_HOST) {

    unstash "eiffel-intelligence-artifact-wrapper"
    try {

        docker.withServer("$DOCKER_HOST", 'remote_docker_host') {
            docker.image('emtrout/nind23').inside("--privileged") {
                println "in stage"

                sh "echo ${DOCKER_HOST}"

                // testar shared libs in local lib
                /*def shellLib = new shell()
                def commitId = shellLib.pipe("git rev-parse HEAD")
                println commitId*/
            }
        }

    } catch (FlowInterruptedException interruptEx) {


    }
}

