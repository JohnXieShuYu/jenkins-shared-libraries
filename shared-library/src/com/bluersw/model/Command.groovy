package com.bluersw.model

class Command {

	private String command
	private String name

	Command(String name, String command){
		this.command =command
		this.name = name
	}

	String getName() {
		return name
	}

	String getCommand() {
		return command
	}

	@Override
	String toString(){
		return "命令名称：${this.name}，命令内容：${this.command}"
	}
}
