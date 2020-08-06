import com.bluersw.StepFactory
import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import com.bluersw.model.Step
import com.bluersw.model.Command
import com.bluersw.model.StepType
import com.bluersw.model.Steps
import groovy.transform.Field

@Field LinkedList<StepFactory> factories
@Field String[] jsonFilePaths

def call(String projectPaths){
	this.jsonFilePaths = getDirectories(projectPaths)
	this.factories = createStepFactory(this.jsonFilePaths)
	return this
}

void runSteps(String stepsName){
	for(StepFactory factory in this.factories){
		println("开始执行[${factory.configPath}]的[${stepsName}]")
		Steps steps = factory.getStepsByName(stepsName)
		if(steps != null && steps.isRun()) {
			for (Step step in steps.stepQueue) {
				println("开始执行[${stepsName}]的[${step.name}]")
				runStep(step)
				println("执行[${step.name}]完成")
			}
		}
		println("执行[${stepsName}]完成")
	}
}

void printLoadFactoryLog() {
	for (StepFactory factory in this.factories) {
		//打印装载配置文件日志
		LogType logLevel = factory.getLogLevel()
		println(LogContainer.getLogByTag(factory.getInitStartTag(), factory.getInitEndTag(), logLevel))
	}
}

private runStep(Step step) {
	switch (step.stepType) {
	default:
		break
	}
	if (step.containsCommands()) {
		runCommand(step)
	}
}

private void runCommand(step) {
	for (Command cmd in step.commandQueue) {
		def result = null
		def runScript = runScript()
		println("开始执行[${cmd.name}]命令")
		if (step.stepType == StepType.COMMAND_STATUS) {
			result = runScript.returnStatus(cmd.command)
		}
		else {
			result = runScript.returnStdout(cmd.command)
		}
		println("执行完成[${result}]")
	}
}

private static LinkedList<StepFactory> createStepFactory(String[] jsonFile) {
	LinkedList<StepFactory> factoryList = new LinkedList<>()
	for (String json in jsonFile) {
		StepFactory factory = new StepFactory(json)
		factory.initialize()
		factoryList.add(factory)
	}
	return factoryList
}

private static String[] getDirectories(String projectPaths) {
	String[] paths = projectPaths.split(',')
	//设置构建配置文件
	configJSONFilePath(paths)
	return paths
}

private static void configJSONFilePath(String[] dirs) {
	for (int i = 0; i < dirs.length; i++) {
		if (!dirs[i].endsWith('.json')) {
			//默认项目根目录或子项目目录下jenkins-project.json作为构建配置文件
			dirs[i] = dirs[i] + 'jenkins-project.json'
		}
	}
}

