package com.bluersw

import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import com.bluersw.model.Step
import com.bluersw.model.StepType
import com.bluersw.model.Steps
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
	static final String STEP_TYPE_NODE_NAME = 'Type'
	static final String GLOBAL_VARIABLE_NODE_NAME = 'GlobalVariable'
	static final String COMMAND_SCRIPT_NODE_NAME = 'Script'
	static final String GLOBAL_LOG_LEVEL_NODE_NAME = 'LogLevel'
	static final String INIT_LOG_END_TAG = '初始化完成----------'

	/**
	 * 构造函数
	 * @param configPath 构建配置文件路径
	 * @param utility Jenkins环境函数或方法实现
	 */
	StepFactory(String configPath) {
		this.configPath = configPath
		//加载配置文件
		this.json = new JSONExtend(Channel.current(), configPath)
		//获取经过变量赋值的配置文件JSON对象
		this.jsonObject = this.json.getJsonObject()
	}

	/**
	 * 获取全局变量信息
	 * @return 全局变量信息
	 */
	String getGlobalVariableInfo() {
		StringBuilder builder = new StringBuilder()
		builder.append('全局变量集合：\n')
		if (this.stepsMap.containsKey(GLOBAL_VARIABLE_NODE_NAME)) {
			this.stepsMap[GLOBAL_VARIABLE_NODE_NAME].getStepsProperty().each { builder.append("[${it.key}:${it.value}]\n") }
		}
		builder.append('全局变量输出完成')
		LogContainer.append(LogType.DEBUG, builder.toString())
		return builder.toString()
	}

	/**
	 * 获取构建步骤集合及其包含构建步骤的JSON配置属性内容
	 * @param stepsName 构建步骤集合名称
	 * @return 构建步骤集合和该集合内的构建步骤属性内容
	 */
	String getStepsPropertyInfo(String stepsName) {
		StringBuilder builder = new StringBuilder()
		if (this.stepsMap.containsKey(stepsName)) {
			builder.append("[${this.stepsMap[stepsName].getStepsName()}] 节点属性:\n")
			for (Map.Entry entry in this.stepsMap[stepsName].getStepsProperty()) {
				builder.append("key:${entry.key} value:${entry.value}\n")
			}
			for (Step step in this.stepsMap[stepsName].getStepQueue()) {
				getStepPropertyInfo(step, builder)
			}
			builder.append("[${this.stepsMap[stepsName].getStepsName()}] 属性输出完成:")
		}
		else {
			builder.append("没有找到${stepsName}节点\n")
		}
		LogContainer.append(LogType.DEBUG, builder.toString())
		return builder.toString()
	}

	String getInitStartTag(){
		return this.configPath
	}

	static String getInitEndTag(){
		return INIT_LOG_END_TAG
	}

	/**
	 * 构建过程对象初始化
	 */
	void initialize() {
		LogContainer.append(LogType.INFO, this.getInitStartTag())
		LogContainer.append(LogType.INFO, '开始初始化..........')
		initializeSteps()
		LogContainer.append(LogType.INFO, '构建步骤初始化完成')
		perfectStepsProperty()
		LogContainer.append(LogType.INFO, '完善构建步骤属性')
		if (getLogLevel() <= LogType.DEBUG) {
			getGlobalVariableInfo()
			this.stepsMap.keySet().each { getStepsPropertyInfo(it) }
		}

		LogContainer.append(LogType.INFO, getInitEndTag())
	}

	/**
	 * 获得全局变量中的日志级别
	 * @return 全局变量中的日志级别
	 */
	LogType getLogLevel() {
		if (this.stepsMap.containsKey(GLOBAL_VARIABLE_NODE_NAME)) {
			return Enum.valueOf(LogType.class, this.stepsMap[GLOBAL_VARIABLE_NODE_NAME].getStepsPropertyValue(GLOBAL_LOG_LEVEL_NODE_NAME))
		}
		else {
			return LogType.INFO
		}
	}

	/**
	 * 完善循环命令构建步骤
	 * @param step 循环命令构建步骤对象
	 */
	private static void perfectCommandForStep(Step step) {
		String forValue = step.getStepPropertyValue('For')
		String scriptTemplate = step.getStepPropertyValue('ScriptTemplate')
		if (forValue != '' && scriptTemplate != '') {
			String[] forArray = forValue.split(',')
			for (String str in forArray) {
				step.appendCommand("For-${str}", scriptTemplate.replace('${loop-command-for}', str))
			}
		}
	}

	/**
	 * 使用默认方法创建构建步骤对象
	 * @param stepName 构建步骤对象
	 * @param stepType 构建步骤类型
	 * @param jo 构建步骤对应的JSON对象
	 * @return 构建步骤对象
	 */
	private static Step defaultCreateStep(String stepName, StepType stepType, JSONObject jo) {
		Step step = new Step(stepName, stepType)
		Iterator<String> iterator = ((JSONObject) jo).entrySet().iterator()
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			if (entry.value instanceof String) {
				//如果是字符串则添加属性
				step.setStepProperty(entry.key.toString(), entry.value.toString())
			}
			else if (entry.value instanceof JSONObject && entry.key.toString() == COMMAND_SCRIPT_NODE_NAME) {
				//如果有Script节点则循环创建命令对象
				Iterator<String> scriptIterator = ((JSONObject) entry.value).entrySet().iterator()
				while (scriptIterator.hasNext()) {
					Map.Entry scriptEntry = (Map.Entry) scriptIterator.next()
					step.appendCommand(scriptEntry.key.toString(), scriptEntry.value.toString())
				}
			}
		}
		return step
	}

	/**
	 * 创建构建步骤对象
	 * @param stepName 步骤名称
	 * @param o 构建步骤对应的JSON对象（也有可能不是）
	 * @return 构建步骤对象
	 */
	private static Step createStep(String stepName, Object o) {
		Step step = null
		if (o instanceof JSONObject) {
			JSONObject stepNode = (JSONObject) o
			//检查构建步骤类型
			if (stepNode.containsKey(STEP_TYPE_NODE_NAME)) {
				//获取构建步骤类型
				StepType stepType = StepType.valueOf(stepNode.get(STEP_TYPE_NODE_NAME).toString())
				//使用默认方法创建构建步骤对象
				step = defaultCreateStep(stepName, stepType, stepNode)
				//根据构建步骤类型完善构建对象
				switch (step.getStepType()) {
				case (StepType.COMMAND_STDOUT_FOR):
					perfectCommandForStep(step)
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
	private static Steps createSteps(String stepsName, Object o) {
		//创建构建步骤集合
		Steps steps = new Steps(stepsName)
		if (o instanceof JSONObject) {
			Iterator<String> iterator = ((JSONObject) o).entrySet().iterator()
			//循环构建步骤集合的子节点
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next()
				if (entry.value instanceof String) {
					//如果是字符串就当成属性添加
					steps.setStepsProperty(entry.key.toString(), entry.value.toString())
				}
				else {//如果不是字符串就尝试创建构建步骤对象
					Step step = createStep(entry.key.toString(), entry.value)
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
	private void initializeSteps() {
		Iterator<String> iterator = this.jsonObject.entrySet().iterator()
		//循环配置文件JSON格式的第一层节点
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			//因为默认第一层节点为步骤集合Steps，所以尝试创建构建步骤集合对象
			Steps steps = createSteps(entry.key.toString(), entry.value)
			//如果构建步骤集合中有内容（有效）或构建步骤是全局配置则加到Map中
			if (steps.isValid() || steps.getStepsName() == GLOBAL_VARIABLE_NODE_NAME) {
				this.stepsMap.put(entry.key.toString(), steps)
			}
		}
	}

	/**
	 * 完善构建集合的属性
	 */
	private void perfectStepsProperty() {
		Iterator<Map.Entry<String, Steps>> iterator = this.stepsMap.entrySet().iterator()
		while (iterator.hasNext()) {
			Map.Entry<String, Steps> entry = (Map.Entry<String, Steps>) iterator.next()
			Steps steps = entry.value
			if (steps.getStepsName() == GLOBAL_VARIABLE_NODE_NAME) {
				if (steps.getStepsPropertyValue('ProjectRoot') == '') {
					steps.setStepsProperty('ProjectRoot', '.')
				}
				if (steps.getStepsPropertyValue(GLOBAL_LOG_LEVEL_NODE_NAME) == '') {
					steps.setStepsProperty(GLOBAL_LOG_LEVEL_NODE_NAME, LogType.INFO.toString())
				}
			}
			else {
				if (steps.getStepsPropertyValue(Steps.IS_RUN_KEY_NAME) == '') {
					steps.setStepsProperty(Steps.IS_RUN_KEY_NAME, 'true')
				}
				if (steps.getStepsPropertyValue(Steps.SHOW_LOG_KEY_NAME) == '') {
					steps.setStepsProperty(Steps.SHOW_LOG_KEY_NAME, 'false')
				}
			}
		}
	}

	/**
	 * 获取构建步骤的JSON配置属性内容
	 * @param step 构建步骤
	 * @return 构建步骤属性内容
	 */
	private static String getStepPropertyInfo(Step step, StringBuilder builder) {
		builder.append("[${step.getStepName()}] 节点属性:\n")
		for (Map.Entry entry in step.getStepProperty()) {
			builder.append("key:${entry.key} value:${entry.value}\n")
		}
	}
}
