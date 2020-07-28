package com.bluersw

import com.bluersw.Steps
import com.bluersw.model.AbstractStep
import com.bluersw.model.DisplayInfo
import com.bluersw.model.GlobalVariable
import com.bluersw.model.LogType
import com.bluersw.model.StepType
import com.bluersw.model.Utility
import com.cloudbees.groovy.cps.NonCPS
import hudson.remoting.Channel
import com.bluersw.utils.JSONExtend
import net.sf.json.JSONObject

class StepFactory {

	private JSONExtend json
	private String configPath
	private LinkedHashMap<String, Steps> steps = new LinkedList<>()
	private JSONObject jsonObject
	private Utility utility
	private LinkedHashMap<String, String> globalVariable

	StepFactory(String configPath, Utility utility) {
		this.configPath = configPath
		this.utility = utility
		this.json = new JSONExtend(Channel.current(), configPath)
		this.jsonObject = this.json.getJsonObject()
		this.globalVariable = this.json.getGlobalVariable()

		printGlobalVariable()
		initializeStep()
	}

	@NonCPS
	void printGlobalVariable() {
		this.utility.println("成功加载${this.configPath}文件，全局变量集合：")
		this.globalVariable.each { key, value -> this.utility.println("[${key}:${value}]") }
	}

	StepType findStepType(Object o){
		return StepType.COMMAND_STDOUT
	}

	@NonCPS
	void initializeStep() {
		Iterator<String> iterator = this.jsonObject.entrySet().iterator()
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next()
			if(entry.key.toString() != 'GlobalVariable'){
				findStepType(entry.value)
			}
		}
	}
}
