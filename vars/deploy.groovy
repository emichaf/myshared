def call(def server, def port) {
    
	
	withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'RemoteCredentials',
                                              usernameVariable: 'myuser_USER',
                                              passwordVariable: 'myuser_PASSWORD']]) {

			
    sh "sshpass -p ${myuser_PASSWORD} scp /target/*.jar ${myuser_USER}@${developmentServer}:/home/emichaf/myjarbuild.jar"
	}
			
	
}