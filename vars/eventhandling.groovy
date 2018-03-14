def send(Map pipelineParams) {

    echo sh(returnStdout: true, script: 'env')

    def response = sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -X POST --data-binary '${pipelineParams.JSON_IN}' ${EVENT_PARSER_PUB_GEN_URI}${pipelineParams.EVENT_TYPE_IN}").trim()

    sh "echo ${response}"
    response = readJSON text: "${response}"
    if(response.events[0].status_code != 200){throw new Exception()}
}