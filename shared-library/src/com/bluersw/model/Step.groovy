package com.bluersw.model

import java.util.Queue

import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import com.bluersw.model.StepType
import com.bluersw.model.Command

class Step {
	String name
	StepType stepType
	HashMap<String, String> stepProperty = new HashMap<>()
	Queue<Command> commandQueue = new LinkedList<>()

	Step(String name, StepType stepType) {
		this.name = name
		this.stepType = stepType
	}

	String getStepPropertyValue(String propertyName) {
		String value = this.stepProperty.getOrDefault(propertyName, '')
		LogContainer.append(LogType.DEBUG, "读取${this.name}节点${propertyName}属性，返回值：${value}")
		return value
	}

	void setStepProperty(String propertyName, String value) {
		if (this.stepProperty.containsKey(propertyName)) {
			LogContainer.append(LogType.WARNING, "节点：${this.name}，名称为：${propertyName}的属性已经存在，该属性的内容被覆盖为：${value}")
		}
		this.stepProperty.put(propertyName, value)
		LogContainer.append(LogType.DEBUG, "设置${this.name}节点${propertyName}属性，值：${value}")
	}

	void appendCommand(String name, String command) {
		this.commandQueue.offer(new Command(name, command))
		LogContainer.append(LogType.DEBUG, "添加${this.name}节点${name}的命令，内容：${command}")
	}

	boolean containsCommands() {
		if (this.commandQueue.size() > 0) {
			return true
		}
		else {
			return false
		}
	}
}
