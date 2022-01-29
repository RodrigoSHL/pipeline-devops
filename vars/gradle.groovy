/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(String pipelineType){
  
    figlet pipelineType

    if (pipelineType == 'CI') {
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

    } else {
      stage('Donwload-Nexus') {
            sh "curl -X GET -u admin:Rodrixxx69. http://192.168.1.107:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
        }
        stage('Run Jar') {
            STAGE = env.STAGE_NAME
            sh 'chmod +x mvnw'
            sh 'nohup bash mvnw spring-boot:run &'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Test') {
            STAGE = env.STAGE_NAME
            sh './mvnw clean test -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Package') {
            STAGE = env.STAGE_NAME
            sh './mvnw clean package -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('nexus') {
            STAGE = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'Nexus-test-gradle',
            nexusRepositoryId: 'test-nexus',
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
                        version: '1.0.0'
                    ]
                ]
            ] 
        }
    }
}

return this;