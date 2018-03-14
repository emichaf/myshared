def parseSendEvent(def json, def EVENT_TYPE) {

    def EVENT_PARSER_PUB_GEN_URI = 'http://docker104-eiffel999.lmera.ericsson.se:9900/doit/?msgType='

    //echo sh(returnStdout: true, script: 'env')

    def result =  sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -X POST --data-binary '${json}' ${EVENT_PARSER_PUB_GEN_URI}${EVENT_TYPE}").trim()

    return result
}