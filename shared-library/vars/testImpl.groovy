import com.bluersw.model.Utility

def call() {
	return [
			println: { String info -> println(info) },
			isUnix : { true },
			sh     : { String script, boolean returnStatus, boolean returnStdout -> println("${script} ${returnStatus} ${returnStdout}") },
			bat    : { String script, boolean returnStatus, boolean returnStdout -> bat(script, returnStatus, returnStdout) }
	] as Utility
}

