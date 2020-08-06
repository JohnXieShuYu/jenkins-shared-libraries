package com.bluersw.model

class Command {
	String command
	String name

	Command(String name, String command) {
		this.command = command
		this.name = name
	}

	@Override
	String toString() {
		return "命令名称：${this.name}，命令内容：${this.command}"
	}
}
