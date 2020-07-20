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

	AbstractStep(String name, String xpath, StepType stepType, Utility utility) {
		this.name = name
		this.xpath = xpath
		this.stepType = stepType
		this.utility = utility
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

	void setStepProperty(String name, String value) {
		if (!stepProperty.containsKey(name)) {
			stepProperty.put(name, value)
		}
		else {
			throw new IllegalArgumentException("添加属性时${name}已经存在，不能添加同名属性。")
		}
	}

	abstract Result<String> run()
}
