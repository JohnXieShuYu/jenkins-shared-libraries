package com.bluersw.model

class Command {

	private String command

	Command(String command){
		this.command =command
	}

	String getCommand() {
		return command
	}

	@Override
	String toString(){
		return command
	}
}
