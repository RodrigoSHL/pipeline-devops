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
 		string defaultValue: '', description: 'Agregar etapa de ejecución (separar con coma)', name: 'etapasPipeline'
	}

	stages{
		stage('Pipeline'){
			steps{
				script{
					try {
						println 'Pipeline'
						
						def ci_or_cd = verifyBranchName()
						
	                    if (params.buildTool == "gradle") {
							
							figlet 'Ejecución con gradle'
		                    gradle(verifyBranchName(), params.etapasPipeline)
	                    } else {
							figlet 'Ejecución con maven'

		                    maven(verifyBranchName(), params.etapasPipeline)
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

def verifyBranchName(){
	//def is_ci_or_cd = ( env.GIT_BRANCH.contains('feature-')) ? 'CI'  : 'CD' 
	if(env.GIT_BRANCH.contains('feature-') || env.GIT_BRANCH.contains('develop-')) {
		return 'CI'
	} else {
		return 'CD'
	}
	return 
}

return this;