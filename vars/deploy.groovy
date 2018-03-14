def call(def server) {
    
	
	withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'RemoteCredentials',
                                              usernameVariable: 'USER',
                                              passwordVariable: 'PASSWORD']]) {

		def RESPONSE_scp = sh(returnStdout: true, script: "sshpass -p ${PASSWORD} scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no target/*.war ${USER}@${server}:/home/emichaf/test/").trim()

	return RESPONSE_scp

	}
			
	
}