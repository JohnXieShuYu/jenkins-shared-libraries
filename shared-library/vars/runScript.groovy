import groovy.transform.Field

@Field static final String UNIX_PREFIX = '#!/bin/bash -il\n '
@Field static final String UNIX_PREFIX_KEY_WORD = '#!/bin/bash'

def call(){
	return this
}

def returnStdout(String script){
	def result
	if(isUnix()) {
		if (script.indexOf(UNIX_PREFIX_KEY_WORD) == -1) {
			script = UNIX_PREFIX + script
		}
		result = sh(script: script, returnStdout: true)
	}
	else{
		result = bat(script: script, returnStdout: true)
	}
	return result
}

def returnStatus(String script){
	def result
	if(isUnix()) {
		if (script.indexOf(UNIX_PREFIX_KEY_WORD) == -1) {
			script = UNIX_PREFIX + script
		}
		result = sh(script: script, returnStatus: true)
	}
	else{
		result = bat(script: script, returnStatus: true)
	}
	return result
}

