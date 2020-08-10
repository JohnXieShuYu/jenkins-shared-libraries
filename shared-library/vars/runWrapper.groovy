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

void loadJSON(String projectPaths){
	this.jsonFilePaths = getDirectories(projectPaths)
	this.factories = createStepFactory(this.jsonFilePaths)
}

void runJsonSteps(String stepsName) {
	for (int factoryIndex = 0; factoryIndex < this.factories.size(); factoryIndex++) {
		StepFactory factory = this.factories[factoryIndex]
		println("开始执行[${factory.configPath}]的[${stepsName}]")
		Steps steps = factory.getStepsByName(stepsName)
		if (steps != null && steps.isRun()) {
			for (int stepIndex = 0; stepIndex < steps.stepQueue.size(); stepIndex++) {
				Step step = steps.stepQueue[stepIndex]
				println("开始执行[${stepsName}]的[${step.name}]")
				runJsonStep(step)
				println("执行[${step.name}]完成")
			}
			println("执行[${stepsName}]完成")
		}
	}
}

void printLoadFactoryLog() {
	for (StepFactory factory in this.factories) {
		//打印装载配置文件日志
		LogType logLevel = factory.getLogLevel()
		println(LogContainer.getLogByTag(factory.getInitStartTag(), factory.getInitEndTag(), logLevel))
	}
}

private void runJsonStep(Step step) {
	switch (step.stepType) {
	default:
		break
	}
	if (step.containsCommands()) {
		runCommand(step)
	}
}

private void runCommand(Step step) {
	for (int i = 0; i < step.commandQueue.size(); i++) {
		Command cmd = step.commandQueue[i]
		def result = null
		println("开始执行[${cmd.name}]的${cmd.command}命令")
		if (step.stepType == StepType.COMMAND_STDOUT) {
			result = runStdoutScript(cmd.command)
		}
		else {
			result = runStatusScript(cmd.command)
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

