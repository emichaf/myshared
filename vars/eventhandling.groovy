def parseandsend(JSON_IN, EVENT_TYPE_IN) {

    def EVENT_PARSER_PUB_GEN_URI = 'http://docker104-eiffel999.lmera.ericsson.se:9900/doit/?msgType='

    sh(returnStdout: true, script: 'env')

    def result =  sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -X POST --data-binary '${JSON_IN}' ${EVENT_PARSER_PUB_GEN_URI}${EVENT_TYPE_IN}").trim()

    return result
}