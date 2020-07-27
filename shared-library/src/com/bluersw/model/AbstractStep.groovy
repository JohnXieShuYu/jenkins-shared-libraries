package com.bluersw.model

import com.bluersw.model.Result
import com.bluersw.model.StepType
import com.bluersw.model.Utility

abstract class AbstractStep {

	protected String name
	protected String xpath
	protected StepType stepType
	protected StringBuilder info = new StringBuilder()
	protected StringBuilder warning = new StringBuilder()
	protected LinkedHashMap<String, String> stepProperty = new LinkedHashMap<>()
	protected final String NEW_LINE = '\r\n'
	protected Utility utility
	protected boolean showLog

	AbstractStep(String name, String xpath, StepType stepType, Utility utility, boolean showLog) {
		this.name = name
		this.xpath = xpath
		this.stepType = stepType
		this.utility = utility
		this.showLog = showLog

		this.info.append("${name}节点info："+NEW_LINE)
		this.warning.append("${name}节点warning："+NEW_LINE)
	}

	protected void addInfo(String infoLog) {
		info.append(infoLog + NEW_LINE)
	}

	protected String getInfo() {
		return info.toString()
	}

	protected void addWarning(String warnLog) {
		info.append(warnLog + NEW_LINE)
	}

	protected String getWarning() {
		return warning.toString()
	}

	String getStepProperty(String propertyName) {
		return stepProperty.getOrDefault(propertyName, '')
	}

	void setStepProperty(String propertyName, String value) {
		if (stepProperty.containsKey(propertyName)) {
			addWarning("名称为：${propertyName}的属性已经存在，该属性的内容被覆盖为：${value}")
		}
		stepProperty.put(propertyName, value)
	}

	abstract void run()
}
