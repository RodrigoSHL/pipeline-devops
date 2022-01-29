/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/


def call(String pipelineType){

    figlet pipelineType

    if (pipelineType == 'CI') {    
        stage('Build') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh 'chmod +x mvnw'
            sh './mvnw clean compile -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Test') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh './mvnw clean test -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Jar') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh './mvnw clean package -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Sonar') {
        figlet pipelineType env.STAGE_NAME
        STAGE = env.STAGE_NAME
        def scannerHome = tool 'sonar-scanner'; 
        withSonarQubeEnv('sonarqube-server') { // If you have configured more than one global server connection, you can specify its name
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-maven -Dsonar.sources=src -Dsonar.java.binaries=build"
        }           
        }
        stage('nexus') {
        figlet pipelineType env.STAGE_NAME
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
    } else {
        stage('Donwload-Nexus') {
            figlet pipelineType env.STAGE_NAME
            sh "curl -X GET -u admin:Rodrixxx69. http://192.168.1.86:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
        }
        stage('Run') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh 'chmod +x mvnw'
            sh 'nohup bash mvnw spring-boot:run &'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Test') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh './mvnw clean test -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Package') {
            figlet pipelineType env.STAGE_NAME
            STAGE = env.STAGE_NAME
            sh './mvnw clean package -e'
            println "Stage: ${env.STAGE_NAME}"
        }
        stage('Upload nexus') {
            figlet pipelineType env.STAGE_NAME
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
                        version: '1.0.0'
                    ]
                ]
            ] 
        }
    }
}
return this;