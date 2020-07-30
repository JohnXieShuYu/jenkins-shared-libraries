package com.bluersw.model

import com.bluersw.model.DisplayInfo
import com.bluersw.model.LogType
import com.bluersw.model.StepType
import com.bluersw.model.Utility

abstract class AbstractStep {

	protected String name
	protected StepType stepType
	protected HashMap<String, String> stepProperty = new HashMap<>()
	protected Utility utility
	protected DisplayInfo displayInfo

	AbstractStep(String name, StepType stepType) {
		this.name = name
		this.stepType = stepType
	}

	protected void println(LogType logType, String content) {
		if (this.displayInfo) {
			this.displayInfo.println(logType, content)
		}
	}

	void setDisplayInfo(DisplayInfo displayInfo) {
		this.displayInfo = displayInfo
	}

	void setUtility(Utility utility) {
		this.utility = utility
	}

	String getStepProperty(String propertyName) {
		return this.stepProperty.getOrDefault(propertyName, '')
	}

	void setStepProperty(String propertyName, String value) {
		if (this.stepProperty.containsKey(propertyName)) {
			this.println(LogType.WARNING, "名称为：${propertyName}的属性已经存在，该属性的内容被覆盖为：${value}")
		}
		this.stepProperty.put(propertyName, value)
	}

	abstract void run()
}
