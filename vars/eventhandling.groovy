def send(JSON_IN, EVENT_TYPE_IN) {

    sh(returnStdout: true, script: 'env')

    def response = sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -X POST --data-binary '${JSON_IN}' ${EVENT_PARSER_PUB_GEN_URI}${EVENT_TYPE_IN}").trim()

    sh "echo ${response}"
    response = readJSON text: "${response}"
    if(response.events[0].status_code != 200){throw new Exception()}
}