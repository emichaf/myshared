import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException

def call(body) {

    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()


} // def call(body) {