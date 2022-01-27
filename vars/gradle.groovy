/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(){
    stage('Build y Unit Test') {
      STAGE = env.STAGE_NAME
      sh 'chmod +x gradlew'
      sh './gradlew clean build'
      println "Stage: ${env.STAGE_NAME}"
    }
    stage('Sonar') {
      STAGE = env.STAGE_NAME
      def scannerHome = tool 'sonar-scanner'; 
      withSonarQubeEnv('sonarqube-server') { // If you have configured more than one global server connection, you can specify its name
      sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.sources=src -Dsonar.java.binaries=build"
      }           
    }
    stage('Run') {
      STAGE = env.STAGE_NAME
      sh 'nohup bash gradlew bootRun &'
      println "Stage: ${env.STAGE_NAME}"
      sleep 20
    }
    stage('Test') {
      STAGE = env.STAGE_NAME
      println "Stage: ${env.STAGE_NAME}"
      sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    }
    stage('nexus') {
      STAGE = env.STAGE_NAME
      nexusPublisher nexusInstanceId: 'Nexus-test-gradle',
      nexusRepositoryId: 'test-nexus-gradle',
      packages: [
          [
              $class: 'MavenPackage',
              mavenAssetList: [
                  [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]
              ],
              mavenCoordinate: [
                  artifactId: 'DevOpsUsach2020',
                  groupId: 'com.devopsusach2020',
                  packaging: 'jar',
                  version: '0.0.1'
              ]
          ]
      ] 
    } 
}

return this;