package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.Command
import com.bluersw.model.DisplayInfo
import com.bluersw.model.LogType
import com.bluersw.model.Result
import com.bluersw.model.StepType
import com.bluersw.model.Utility

class CommandQueue extends AbstractStep {

	private final String UNIX_COMMAND_PREFIX = '#!/bin/bash -il\n '
	private final String COMMAND_PREFIX = '#!/bin/bash'
	private LinkedHashMap<String, Command> commandQueue = new LinkedHashMap<>()

	CommandQueue(String name, String xpath, StepType stepType, Utility utility, DisplayInfo displayInfo) {
		super(name, xpath, stepType, utility, displayInfo)
	}

	void append(String name, Command command) {
		if (commandQueue.containsKey(name)) {
			this.println(LogType.WARNING,"名称为：${name}的命令已经存在，该命令被覆盖为：${command}")
		}
		commandQueue.put(name, command)
	}

	@Override
	void run() {
		for (Map.Entry<String, Command> entry in commandQueue.entrySet()) {
			if (stepType == StepType.COMMAND_STATUS) {
				runStatus(entry.value)

			}else if(stepType == StepType.COMMAND_STDOUT){
				runStdout(entry.value)
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
		if (utility.isUnix()) {
			status = utility.sh(supplementPrefix(script), true, false)
		}
		else {
			status = utility.bat(script, true, false)
		}
		if(status == 0){
			this.println(LogType.INFO,"执行：${command}成功！")
		}else{
			throw new Exception("执行：${command}失败！返回状态码：${status}。")
		}
	}

	private void runStdout(Command command) {
		String script = command.getCommand()
		def stdout
		if (utility.isUnix()) {
			stdout = utility.sh(supplementPrefix(script), false, true)
		}
		else {
			stdout = utility.bat(script, false, true)
		}
		this.println(LogType.INFO,"输出内容：${stdout}")
	}
}
