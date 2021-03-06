import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import com.bluersw.model.Step
import com.bluersw.model.Command
import com.bluersw.model.StepType
import com.bluersw.model.Steps
import groovy.transform.Field

@Field LinkedList<StepFactory> factories
@Field String[] jsonFilePaths
@Field Map<String,String> envVars

//加载JSON配置文件合集
void loadJSON(String projectPaths) {
	this.envVars = jenkinsVariable.getEnvironment()
	this.jsonFilePaths = getJSONFilePath(projectPaths)
	this.factories = createStepFactory(this.jsonFilePaths, this.envVars)
}

//运行指定构建步骤集合
void runJsonSteps(String stepsName) {
	//CPS脚本不能使用for( ..in ..) 和 each() 只能用for(;;)
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

//打印加载JSON配置文件时的日志
void printLoadFactoryLog() {
	for (StepFactory factory in this.factories) {
		//打印装载配置文件日志
		LogType logLevel = factory.getLogLevel()
		println(LogContainer.getLogByTag(factory.getInitStartTag(), factory.getInitEndTag(), logLevel))
	}
}

//执行构建步骤
private void runJsonStep(Step step) {
	switch (step.stepType) {
	default:
		break
	}
	if (step.containsCommands()) {
		runCommand(step)
	}
}

//执行构建步骤中的命令脚本
private void runCommand(Step step) {
	for (int i = 0; i < step.commandQueue.size(); i++) {
		Command cmd = step.commandQueue[i]
		def result = null
		println("开始执行[${cmd.name}]的${cmd.command}命令")
		if (step.stepType == StepType.COMMAND_STDOUT) {
			result = runStdoutScript(cmd.command)
			String success = step.getStepPropertyValue('Success-IndexOf')
			if (success != '' && result != null && result.toString().indexOf(success) == -1) {
				throw new Exception("[${cmd.command}]执行失败，返回[${result}],没有找到成功标准[${success}]")
			}
			else {
				String fail = step.getStepPropertyValue('Fail-IndexOf')
				if (fail != '' && result != null && result.toString().indexOf(fail) != -1) {
					throw new Exception("[${cmd.command}]执行失败，返回[${result}]，找到失败标准[${fail}]")
				}
			}
		}
		else if (step.stepType == StepType.SONAR_QUBE) {
			 runSqCheckScript(step)
		}
		else {
			result = runStatusScript(cmd.command)
			if (result != null && result != 0) {
				throw new Exception("[${cmd.command}]执行返回非0，返回[${result}]")
			}
		}
		println("执行完成[${result}]")
	}
}

//创建JSON文件对应的StepFactory对象
private static LinkedList<StepFactory> createStepFactory(String[] jsonFile, Map<String,String> envVars) {
	LinkedList<StepFactory> factoryList = new LinkedList<>()
	for (String json in jsonFile) {
		StepFactory factory = new StepFactory(json, envVars)
		factory.initialize()
		factoryList.add(factory)
	}
	return factoryList
}

//根据路径合集获得JSON配置文件的路径集合
private static String[] getJSONFilePath(String projectPaths) {
	String[] dirs = projectPaths.split(',')
	for (int i = 0; i < dirs.length; i++) {
		if (!dirs[i].endsWith('.json')) {
			//默认项目根目录或子项目目录下jenkins-project.json作为构建配置文件
			dirs[i] = dirs[i] + 'jenkins-project.json'
		}
	}
	return dirs
}


