package com.bluersw.model


/**
 * 构建步骤类型
 */
enum StepType {
	/**
	 * 通过SH或BAT执行的步骤
	 */
	COMMAND,
	/**
	 * 通过执行Jenkins插件执行的步骤
	 */
	PLUG_IN,
	/**
	 * 绑定Jenkins构建参数控件的值
	 */
	PARAMETER_BINDING,
	/**
	 * 全局变量
	 */
	GLOBAL_VARIABLE

}