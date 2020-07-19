package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.StepResult
import com.bluersw.model.StepType

class CommandQueue extends AbstractStep {

	private LinkedHashMap<String, String> commandQueue = new LinkedHashMap<>()

	CommandQueue(String name, String xpath) {
		super(name, xpath, StepType.COMMAND)
	}

	void append(String name, String command) {
		if (!commandQueue.containsKey(name)) {
			commandQueue.put(name, command)
		}
		else {
			addWarning(String.format("名称为：${name}的命令已经存在，该命令被覆盖为：${command}"))
		}
	}

	@Override
	StepResult runStep(Closure display) {
		return null
	}
}
