def call(def server) {
    
	
	withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'RemoteCredentials',
                                              usernameVariable: 'myuser_USER',
                                              passwordVariable: 'myuser_PASSWORD']]) {

		def RESPONSE_scp = sh(returnStdout: true, script: "sshpass -p ${myuser_PASSWORD} scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no target/*.war ${myuser_USER}@${server}:/home/emichaf/test/").trim()

		//sh "sshpass -p ${myuser_PASSWORD} scp /target/*.jar ${myuser_USER}@${developmentServer}:/home/emichaf/myjarbuild.jar"

	return RESPONSE_scp

	}
			
	
}