package com.bluersw.model


/**
 * 构建步骤类型
 */
enum StepType {
	//通过SH或BAT执行的判定执行状态，返回0代表成功
	COMMAND_STATUS,
	//通过SH或BAT执行的获得标准输出结果
	COMMAND_STDOUT,
	//绑定Jenkins构建参数控件的值
	BUILD_PARAMETER,
	//Jenkins junit 插件执行步骤
	JUNIT_PLUG_IN,
	//Jenkins jacoco 插件执行步骤
	JACOCO_PLUG_IN,
	//SonarQube处理步骤
	SONAR_QUBE
}