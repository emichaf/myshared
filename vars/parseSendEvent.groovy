def parseSendEvent(json, EVENT_TYPE) {

    echo sh(returnStdout: true, script: 'env')

    return sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -X POST --data-binary '${json}' ${EVENT_PARSER_PUB_GEN_URI}${EVENT_TYPE}").trim()

}