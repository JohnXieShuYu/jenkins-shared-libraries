def call(){
	pipeline {
		agent any
		stages {
			stage('Hello2') {
				steps {
					echo 'Hello World2'
				}
			}
		}
	}
}

