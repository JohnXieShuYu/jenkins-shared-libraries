package com.bluersw.model

import com.bluersw.model.StepResult
import com.bluersw.model.StepType

public abstract class AbstractStep {

	protected String name
	protected String xpath
	protected StepType stepType
	protected StringBuilder info = new StringBuilder()
	protected StringBuilder warning = new StringBuilder()
	protected LinkedHashMap<String, String> stepProperty = new LinkedHashMap<>()

	AbstractStep(String name, String xpath, StepType stepType) {
		this.name = name
		this.xpath = xpath
		this.stepType = stepType
	}

	protected void addInfo(String infoLog) {
		info.append(infoLog + '\r\n')
	}

	protected String getInfo() {
		return info.toString()
	}

	protected void addWarning(String warnLog) {
		info.append(warnLog + '\r\n')
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

	/**
	 * 执行步骤，并打印执行信息
	 * <pre>示例：StepResult result
	 * result = AbstractStep.runStep { displayInfo -> Println(displayInfo) } </pre>
	 * @param display 处理显示信息的闭包
	 * @return 执行结果
	 */
	abstract StepResult runStep(Closure display)
}
