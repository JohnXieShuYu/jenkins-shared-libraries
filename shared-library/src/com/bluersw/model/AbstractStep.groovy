package com.bluersw.model

import com.bluersw.model.PipelineUtility
import com.bluersw.model.StepResult
import com.bluersw.model.StepType

abstract class AbstractStep {

	protected String name
	protected String xpath
	protected StepType stepType
	protected StringBuilder info = new StringBuilder()
	protected StringBuilder warning = new StringBuilder()
	protected LinkedHashMap<String, String> stepProperty = new LinkedHashMap<>()
	protected final String NEW_LINE = '\r\n'
	protected PipelineUtility pipelineUtility

	AbstractStep(String name, String xpath, StepType stepType, PipelineUtility pipelineUtility) {
		this.name = name
		this.xpath = xpath
		this.stepType = stepType
		this.pipelineUtility = pipelineUtility
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

	abstract StepResult runStep()
}
