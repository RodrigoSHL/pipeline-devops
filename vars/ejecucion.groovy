/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(){
  
  pipeline {

	agent any
	
	environment {
	    STAGE = ''
	}

	parameters {
		choice(name: 'buildTool', choices: ['gradle', 'maven'], description: 'Indicar herramienta de construcción')
	}

	stages{
		stage('Pipeline'){
			steps{
				script{
					try {
						println 'Pipeline'
						
	                    if (params.buildTool == "gradle") {
	                        def ejecucion = load 'gradle.groovy'
		                    gradle()
	                    } else {
	                        def ejecucion = load 'maven.groovy'
		                    maven()
	                    }

	                    slackSend color: 'good', message: "[Rodrigo Catalán][${env.JOB_NAME}][${params.buildTool}] Ejecución exitosa"

					} catch (Exception e){
					    slackSend color: 'danger', message: "[Rodrigo Catalán][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage ${STAGE}"
					    error "Ejecución fallida en stage ${STAGE}"
					}
				}
			}
		}
	}
}

}

return this;