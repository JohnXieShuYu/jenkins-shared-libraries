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

class StepFactory {

	private JSONExtend json
	private String configPath
	private LinkedHashMap<String, Steps> stepsMap = new LinkedHashMap<>()
	private JSONObject jsonObject
	private Utility utility
	private LinkedHashMap<String, String> globalVariable
	private final String STEP_TYPE_NODE_NAME = 'type'
	private final String GLOBAL_VARIABLE_NODE_NAME = 'GlobalVariable'

	StepFactory(String configPath, Utility utility) {
		this.configPath = configPath
		this.utility = utility
		this.json = new JSONExtend(Channel.current(), configPath)
		this.jsonObject = this.json.getJsonObject()
		this.globalVariable = this.json.getGlobalVariable()

		printGlobalVariable()
		initializeSteps()
	}

	@NonCPS
	void printGlobalVariable() {
		this.utility.println("成功加载${this.configPath}文件。")
		this.utility.println('全局变量集合：')
		this.globalVariable.each { key, value -> this.utility.println("[${key}:${value}]") }
	}

	CommandStep createCommandStep(JSONObject jo){
		return null
	}

	@NonCPS
	AbstractStep createAbstractStep(Object o){
		if(o instanceof JSONObject){
			JSONObject stepNode = (JSONObject) o
			if(stepNode.containsKey(STEP_TYPE_NODE_NAME)){
				StepType stepType = StepType.valueOf(stepNode.get(STEP_TYPE_NODE_NAME).toString())
				switch (stepType){
					case StepType.COMMAND_STATUS
				}
			}
		}
	}

	@NonCPS
	Steps createSteps(String stepsName, Object o){
		Steps steps = new Steps(stepsName,this.utility)
		if(o instanceof JSONObject){
			Iterator<String> iterator = ((JSONObject) o).entrySet().iterator()
			while (iterator.hasNext()){
				AbstractStep step = createAbstractStep(iterator.next())
				if(step != null){
					steps.setStep(step)
				}
			}
		}
		return steps
	}

	@NonCPS
	void initializeSteps() {
		Iterator<String> iterator = this.jsonObject.entrySet().iterator()
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			if(entry.key.toString() != GLOBAL_VARIABLE_NODE_NAME){
				Steps steps = createSteps(entry.key.toString(),entry.value)
				if(steps.size() > 0){
					this.stepsMap.put(entry.key.toString(),steps)
				}
			}
		}
	}
}
