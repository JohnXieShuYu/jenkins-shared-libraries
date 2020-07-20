package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.Command
import com.bluersw.model.Result
import com.bluersw.model.StepType
import com.bluersw.model.Utility

class CommandQueue extends AbstractStep {

	private final String UNIX_COMMAND_PREFIX = '#!/bin/bash -il\n '
	private final String COMMAND_PREFIX = '#!/bin/bash'
	private LinkedHashMap<String, Command> commandQueue = new LinkedHashMap<>()

	CommandQueue(String name, String xpath, Utility utility) {
		super(name, xpath, StepType.COMMAND, utility)
	}

	void append(String name, Command command) {
		if (!commandQueue.containsKey(name)) {
			commandQueue.put(name, command)
		}
		else {
			addWarning("名称为：${name}的命令已经存在，该命令被覆盖为：${command}")
		}
	}

	@Override
	Result<String> run() {
		return null
	}

	private String supplementPrefix(String script) {
		if (utility.isUnix()) {
			if (!script.startsWith(COMMAND_PREFIX)) {
				script = UNIX_COMMAND_PREFIX + script
			}
		}
		return script
	}

	private Result<String> runStatus(Command command) {
		Result<String> result = new Result<String>()
		String script = supplementPrefix(command.getCommand())
		def status = -1
		try {
			if (utility.isUnix()) {
				status = utility.sh(script, true, false)
			}
			else {
				status = utility.bat(script, true, false)
			}
			result.setIsSuccess(status == 0)
			result.setValue(status as String)
			result.setInfo("执行：${command}，返回状态码：${status}")
			return result
		}
		catch (Exception ex) {
			result.setIsSuccess(false)
			result.setValue(status as String)
			result.setError("执行脚本：${command}，发生异常：${ex.toString()} \n ${ex.getMessage()}")
			return result
		}
	}

	private Result<String> runStdout(Command command) {
		Result<String> result = new Result<String>()
		String script = supplementPrefix(command.getCommand())
		def stdout = ""
		try {
			if (utility.isUnix()) {
				stdout = utility.sh(script, false, true)
			}
			else {
				stdout = utility.bat(script, false, true)
			}

			result.setIsSuccess(true)
			result.setValue(stdout as String)
			result.setInfo("执行：${script}，输出内容：${stdout}")
			return result
		}
		catch (Exception ex) {
			result.setIsSuccess(false)
			result.setValue(stdout as String)
			result.setError("执行脚本：${command}，发生异常：${ex.toString()} \n ${ex.getMessage()}")
			return result
		}
	}
}
