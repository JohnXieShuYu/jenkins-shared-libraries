package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.StepResult
import com.bluersw.model.StepType
import com.bluersw.model.Utility

class CommandQueue extends AbstractStep {

	private LinkedHashMap<String, String> commandQueue = new LinkedHashMap<>()

	CommandQueue(String name, String xpath, Utility utility) {
		super(name, xpath, StepType.COMMAND,utility)
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
	StepResult runStep() {
		return null
	}
}
