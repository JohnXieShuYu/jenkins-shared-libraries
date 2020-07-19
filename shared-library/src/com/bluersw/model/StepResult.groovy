package com.bluersw.model

class StepResult {
	private String stepName
	private Boolean isSuccess
	private String info
	private String warning

	StepResult(String stepName, Boolean isSuccess,String info,String warning){
		this.stepName = stepName
		this.isSuccess = isSuccess
		this.info = info
		this.warning = warning
	}

	String getStepName() {
		return stepName
	}

	Boolean getIsSuccess() {
		return isSuccess
	}

	String getInfo() {
		return String.format("%s info:%s",stepName, info)
	}

	String getWarning() {
		return String.format("%s warning:%s",stepName, warning)
	}
}
