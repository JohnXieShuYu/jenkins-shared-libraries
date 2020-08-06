package com.bluersw.model

import java.util.Queue

import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import com.bluersw.model.Step
import com.cloudbees.groovy.cps.NonCPS

class Steps {
	private String name
	HashMap<String, String> stepsProperty = new HashMap<>()
	Queue<Step> stepQueue = new LinkedList<>()
	static final String SHOW_LOG_KEY_NAME = 'ShowLog'
	static final String IS_RUN_KEY_NAME = 'Run'

	Steps(String name) {
		this.name = name
	}

	String getStepsName() {
		return this.name
	}

	boolean isValid() {
		return this.stepQueue.size() > 0 || this.stepsProperty.size() > 0
	}

	void setStepsProperty(String propertyName, String value) {
		if (this.stepsProperty.containsKey(propertyName)) {
			LogContainer.append(LogType.WARNING, "节点：${this.name}，名称为：${propertyName}的属性已经存在，该属性的内容被覆盖为：${value}")
		}
		this.stepsProperty.put(propertyName, value)
		LogContainer.append(LogType.DEBUG, "设置${this.name}节点${propertyName}属性，值：${value}")
	}

	String getStepsPropertyValue(String propertyName) {
		String value = this.stepsProperty.getOrDefault(propertyName, '')
		LogContainer.append(LogType.DEBUG, "读取${this.name}节点${propertyName}属性，返回值：${value}")
		return value
	}

	void append(Step step) {
		this.stepQueue.offer(step)
		LogContainer.append(LogType.DEBUG, "${this.name}节点：添加${step.name}步骤，类型为：${step.stepType}")
	}

	boolean isShowLog() {
		if (!this.stepsProperty.containsKey(SHOW_LOG_KEY_NAME)) {
			return false
		}
		else {
			return stepsProperty[SHOW_LOG_KEY_NAME] as boolean
		}
	}

	boolean isRun() {
		if (!this.stepsProperty.containsKey(IS_RUN_KEY_NAME)) {
			return true
		}
		else {
			return stepsProperty[IS_RUN_KEY_NAME] as boolean
		}
	}
}
