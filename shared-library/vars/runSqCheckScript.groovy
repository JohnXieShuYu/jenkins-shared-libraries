import com.bluersw.model.Step
import com.bluersw.utils.HttpRequest

/**
 * 执行SonarQube代码扫描
 * @param step代码扫描阶段节点
 * @return 代码扫描结果
 */
def call(Step step){
	def sqScriptString = step.getStepPropertyValue('SonarScannerScript')
	if(sqScriptString instanceof String) {
		 String result =runStdoutScript(sqScriptString)
		if (result.indexOf('EXECUTION FAILURE') != -1) {
			throw new Exception("SonarQube扫描代码，执行${sqScriptString}失败。", result)
		}
	}

	return  getSonarQubeQualityGate(step)
}

/**
 * 获取SonarQube扫描代码后的结果
 * @param step代码扫描阶段节点
 * @return 扫描代码质量是否合格
 */
String getSonarQubeQualityGate(Step step){
	def reportTask = readProperties(file:step.getStepPropertyValue('SonarScannerReportTaskPath'))

	if(reportTask == null)
		throw new Exception('SonarQube扫描阶级发生异常','未读取到SonarScannerReportTaskPath配置的文件。')

	def sonarServerUrl = reportTask['serverUrl']
	def ceTaskUrl = reportTask['ceTaskUrl']
	def dashboardUrl = reportTask['dashboardUrl']
	def ceTask

	timeout(time:10,unit:'MINUTES'){
		waitUntil{
			def responseTask = HttpRequest.getResponse(ceTaskUrl)
			ceTask = readJSON(text: responseTask.content)
			println(ceTask)
			return "SUCCESS".equals(ceTask["task"]["status"])
		}
	}

	def qgResponseURL =  "${sonarServerUrl}/api/qualitygates/project_status?analysisId=${ceTask["task"]["analysisId"]}"
	def response = HttpRequest.getResponse(url : qgResponseURL)
	def qualitygate =  readJSON(text: response.content)

	println(qualitygate)

	if(contrastQualityGate(qualitygate["projectStatus"]["status"],step.getStepPropertyValue('QualityGate')))
		return  "SonarQube质量检查通过，${dashboardUrl}"
	else
		return "SonarQube质量检查未通过，浏览地址：${dashboardUrl}"
}

/**
 * 比较质量结果是否满足要求
 * @param qualityGate 质量结果
 * @param claim 要求的质量结果
 * @return 是否满足
 */
boolean contrastQualityGate(String qualityGate, String claim){

	String[] definition = ['OK','WARN','ERROR','NONE']

	int qualityGateIndex = -1
	int claimIndex = -1

	for (int i=0;i<definition.length;i++){
		if(definition[i].equals(qualityGate))
			qualityGateIndex = i
		if (definition[i].equals(claim))
			claimIndex = i
	}

	return qualityGateIndex <= claimIndex
}