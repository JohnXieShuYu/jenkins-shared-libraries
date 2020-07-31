package com.bluersw

import com.bluersw.model.AbstractStep
import com.bluersw.model.CommandStep
import com.bluersw.model.StepType
import com.bluersw.model.Steps
import com.bluersw.model.Utility
import com.cloudbees.groovy.cps.NonCPS
import hudson.remoting.Channel
import com.bluersw.utils.JSONExtend
import net.sf.json.JSONObject

/**
 * 构建过程配置工厂
 */
class StepFactory {

	private JSONExtend json
	private String configPath
	private LinkedHashMap<String, Steps> stepsMap = new LinkedHashMap<>()
	private JSONObject jsonObject
	private Utility utility
	private LinkedHashMap<String, String> globalVariable
	private final String STEP_TYPE_NODE_NAME = 'Type'
	private final String GLOBAL_VARIABLE_NODE_NAME = 'GlobalVariable'
	private final String COMMAND_SCRIPT_NODE_NAME = 'Script'

	/**
	 * 构造函数
	 * @param configPath 构建配置文件路径
	 * @param utility Jenkins环境函数或方法实现
	 */
	StepFactory(String configPath, Utility utility) {
		this.configPath = configPath
		this.utility = utility
		//加载配置文件
		this.json = new JSONExtend(Channel.current(), configPath)
		//获取经过变量赋值的配置文件JSON对象
		this.jsonObject = this.json.getJsonObject()
		//获得全局变量集合
		this.globalVariable = this.json.getGlobalVariable()
		//打印全局变量
		printGlobalVariable()
		//根据配置文件创建和初始化所有构建步骤
		initializeSteps()
	}

	/**
	 * 打印全局变量
	 */
	@NonCPS
	void printGlobalVariable() {
		this.utility.println("成功加载${this.configPath}文件。")
		this.utility.println('全局变量集合：')
		this.globalVariable.each { key, value -> this.utility.println("[${key}:${value}]") }
	}

	/**
	 * 按构建步骤集合（Steps）名称运行构建步骤
	 * @param stepsName 构建步骤集合（Steps）
	 */
	void run(String stepsName) {
		if (this.stepsMap.containsKey(stepsName)) {
			this.utility.println("开始执行 [${this.stepsMap[stepsName].getStepsName()}]:")
			this.stepsMap[stepsName].run()
			this.utility.println("[${this.stepsMap[stepsName].getStepsName()}] 执行结束。")
		}
		else {
			this.utility.println("没有找到${stepsName}节点")
		}
	}

	/**
	 * 打印构建步骤的JSON配置属性内容
	 * @param step 构建步骤对象
	 */
	private void printStepProperty(AbstractStep step) {
		this.utility.println("[${step.getStepName()}] 节点属性:")
		for (Map.Entry entry in step.getStepProperty()) {
			this.utility.println("key:${entry.key} value:${entry.value}")
		}
	}

	/**
	 * 打印构建步骤集合及其包含构建步骤的JSON配置属性内容
	 * @param stepsName 构建步骤集合名称
	 */
	void printStepsProperty(String stepsName) {
		if (this.stepsMap.containsKey(stepsName)) {
			this.utility.println("[${this.stepsMap[stepsName].getStepsName()}] 节点属性:")
			for (Map.Entry entry in this.stepsMap[stepsName].getStepsProperty()) {
				this.utility.println("key:${entry.key} value:${entry.value}")
			}
			for (AbstractStep step in this.stepsMap[stepsName].getStepQueue()) {
				printStepProperty(step)
			}
		}
		else {
			this.utility.println("没有找到${stepsName}节点")
		}
	}

	/**
	 * 创建命令行类型的构建步骤对象
	 * @param stepName 构建步骤对象
	 * @param stepType 构建步骤类型
	 * @param jo 命令行类型的构建步骤对应的JSON对象
	 * @return 命令行类型的构建步骤对象
	 */
	@NonCPS
	CommandStep createCommandStep(String stepName, StepType stepType, JSONObject jo) {
		CommandStep cmdStep = new CommandStep(stepName, stepType)
		Iterator<String> iterator = ((JSONObject) jo).entrySet().iterator()
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			if (entry.value instanceof String) {
				cmdStep.setStepProperty(entry.key.toString(), entry.value.toString())
			}
			else if (entry.value instanceof JSONObject && entry.key.toString() == COMMAND_SCRIPT_NODE_NAME) {
				Iterator<String> scriptIterator = ((JSONObject) entry.value).entrySet().iterator()
				while (scriptIterator.hasNext()) {
					Map.Entry scriptEntry = (Map.Entry) scriptIterator.next()
					cmdStep.append(scriptEntry.key.toString(), scriptEntry.value.toString())
				}
			}
		}
		return cmdStep
	}

	/**
	 * 创建构建步骤对象
	 * @param stepName 步骤名称
	 * @param o 构建步骤对应的JSON对象（也有可能不是）
	 * @return 构建步骤对象
	 */
	@NonCPS
	AbstractStep createAbstractStep(String stepName, Object o) {
		AbstractStep step = null
		if (o instanceof JSONObject) {
			JSONObject stepNode = (JSONObject) o
			if (stepNode.containsKey(STEP_TYPE_NODE_NAME)) {
				StepType stepType = StepType.valueOf(stepNode.get(STEP_TYPE_NODE_NAME).toString())
				switch (stepType) {
				case { it == StepType.COMMAND_STATUS || it == StepType.COMMAND_STDOUT }:
					step = createCommandStep(stepName, stepType, stepNode)
					break
				default:
					break
				}
			}
		}
		return step
	}

	/**
	 * 创建构建步骤集合（Steps）
	 * @param stepsName 集合名称
	 * @param o 步骤集合的JSON对象（也有可能不是）
	 * @return 构建步骤集合（Steps）
	 */
	@NonCPS
	Steps createSteps(String stepsName, Object o) {
		Steps steps = new Steps(stepsName, this.utility)
		if (o instanceof JSONObject) {
			Iterator<String> iterator = ((JSONObject) o).entrySet().iterator()
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next()
				if (entry.value instanceof String) {
					steps.setStepsProperty(entry.key.toString(), entry.value.toString())
				}
				else {
					AbstractStep step = createAbstractStep(entry.key.toString(), entry.value)
					if (step != null) {
						steps.append(step)
					}
				}
			}
		}
		return steps
	}

	/**
	 * 根据构建配置创建和初始化对应的构建对象
	 */
	@NonCPS
	void initializeSteps() {
		Iterator<String> iterator = this.jsonObject.entrySet().iterator()
		//循环配置文件JSON格式的第一层节点
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			//跳过全局配置节点
			if (entry.key.toString() != GLOBAL_VARIABLE_NODE_NAME) {
				//因为默认第一层节点为步骤集合Steps，所以尝试创建构建步骤集合对象
				Steps steps = createSteps(entry.key.toString(), entry.value)
				//抛弃构建步骤为0的对象
				if (steps.size() > 0) {
					this.stepsMap.put(entry.key.toString(), steps)
				}
			}
		}
	}
}
