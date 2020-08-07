def call(String script){

	String UNIX_PREFIX = '#!/bin/bash -il\n '
	String UNIX_PREFIX_KEY_WORD = '#!/bin/bash'

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

