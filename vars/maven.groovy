/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/


def call(String pipelineType){

    figle pipelineType
    stage('Build') {
        STAGE = env.STAGE_NAME
        sh 'chmod +x mvnw'
        sh './mvnw clean compile -e'
        println "Stage: ${env.STAGE_NAME}"
    }
    stage('Test') {
        STAGE = env.STAGE_NAME
        sh './mvnw clean test -e'
        println "Stage: ${env.STAGE_NAME}"
    }
    stage('Jar') {
        STAGE = env.STAGE_NAME
        sh './mvnw clean package -e'
        println "Stage: ${env.STAGE_NAME}"
    }
    stage('Sonar') {
        STAGE = env.STAGE_NAME
      def scannerHome = tool 'sonar-scanner'; 
      withSonarQubeEnv('sonarqube-server') { // If you have configured more than one global server connection, you can specify its name
      sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-maven -Dsonar.sources=src -Dsonar.java.binaries=build"
      }           
    }
    stage('nexus') {
        STAGE = env.STAGE_NAME
      nexusPublisher nexusInstanceId: 'Nexus-test-gradle',
      nexusRepositoryId: 'test-nexus',
      packages: [
          [
              $class: 'MavenPackage',
              mavenAssetList: [
                  [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/DevOpsUsach2020-0.0.1.jar"]
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