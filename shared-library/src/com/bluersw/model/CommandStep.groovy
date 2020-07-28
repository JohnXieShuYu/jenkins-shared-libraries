package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.Command
import com.bluersw.model.DisplayInfo
import com.bluersw.model.StepType
import com.bluersw.model.Utility
import com.bluersw.model.LogType

class CommandStep extends AbstractStep {

	private final String UNIX_COMMAND_PREFIX = '#!/bin/bash -il\n '
	private final String COMMAND_PREFIX = '#!/bin/bash'
	private Queue<Command> queue = new LinkedList<>()

	CommandStep(String name, StepType stepType, Utility utility, DisplayInfo displayInfo) {
		super(name, stepType, utility, displayInfo)
	}

	void append(Command command) {
		this.queue.offer(command)
	}

	@Override
	void run() {
		for (Command cmd in this.queue) {
			if (this.stepType == StepType.COMMAND_STATUS) {
				runStatus(cmd)
			}
			else if (this.stepType == StepType.COMMAND_STDOUT) {
				runStdout(cmd)
			}
		}
	}

	private String supplementPrefix(String script) {
		if (!script.startsWith(COMMAND_PREFIX)) {
			script = UNIX_COMMAND_PREFIX + script
		}
		return script
	}

	private void runStatus(Command command) {
		String script = command.getCommand()
		def status
		if (this.utility.isUnix()) {
			status = this.utility.sh(supplementPrefix(script), true, false)
		}
		else {
			status = this.utility.bat(script, true, false)
		}
		if (status == 0) {
			this.println(LogType.INFO, "执行：${command}成功！")
		}
		else {
			throw new Exception("执行：${command}失败！返回状态码：${status}。")
		}
	}

	private void runStdout(Command command) {
		String script = command.getCommand()
		def stdout
		if (this.utility.isUnix()) {
			stdout = this.utility.sh(supplementPrefix(script), false, true)
		}
		else {
			stdout = this.utility.bat(script, false, true)
		}
		this.println(LogType.INFO, "执行：${command},输出内容：${stdout}")
	}
}
