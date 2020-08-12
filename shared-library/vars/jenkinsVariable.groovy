import com.cloudbees.groovy.cps.NonCPS
import hudson.EnvVars
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

//打印构建过程中的环境变量
@NonCPS
void printVars() {
	//env 是 org.jenkinsci.plugins.workflow.cps.EnvActionImpl类型
	if (env != null && env instanceof EnvActionImpl) {
		EnvVars env = env.getEnvironment()
		env.keySet().each { println("${it}:${env[it]}") }

		def build = $build()
		def userName = "Jenkins"
		if (build.getCause(Cause.UserIdCause) != null) {
			userName = build.getCause(Cause.UserIdCause).getUserName()
		}
		println(userName)
	}

	def SCM_CHANGE_TITLE = runStdoutScript("git --no-pager show -s --format=\"%s\" -n 1")
	println(SCM_CHANGE_TITLE)
}

//获得构建过程中的环境变量含构建参数变量
Map<String,String> getEnvironment() {
	Map<String, String> envMap = new LinkedHashMap<>()
	if (env != null && env instanceof EnvActionImpl) {
		EnvVars envVars = env.getEnvironment()
		Set<String> set = envVars.keySet()
		for (int i = 0; i < set.size(); i++) {
			envMap.put(set[i], envVars[set[i]])
		}

		def build = $build()
		String REAL_USER_NAME = "Jenkins"
		if (build.getCause(Cause.UserIdCause) != null) {
			REAL_USER_NAME = build.getCause(Cause.UserIdCause).getUserName()
		}
		envMap.put("REAL_USER_NAME",REAL_USER_NAME)
	}

	String CSM_CHANGE_TITLE = runStdoutScript("git --no-pager show -s --format=\"%s\" -n 1")
	if(CSM_CHANGE_TITLE != null){
		CSM_CHANGE_TITLE = CSM_CHANGE_TITLE.trim()
	}
	envMap.put("CSM_CHANGE_TITLE",CSM_CHANGE_TITLE)

	return envMap
}