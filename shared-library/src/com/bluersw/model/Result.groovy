package com.bluersw.model

class Result<T> {
	private T value
	private Boolean isSuccess
	private String info
	private String warning
	private String error

	Result(){
		this.value = null
		this.isSuccess =false
		this.info = ""
		this.warning = ""
		this.error = ""
	}

	Result(T value, Boolean isSuccess,String info,String warning){
		this.value = value
		this.isSuccess = isSuccess
		this.info = info
		this.warning = warning
		this.error = ""
	}

	void setValue(T value) {
		this.value = value
	}

	void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess
	}

	void setInfo(String info) {
		this.info = info
	}

	void setWarning(String warning) {
		this.warning = warning
	}

	void setError(String error) {
		this.error = error
	}

	String getError() {
		return error
	}

	T getValue() {
		return value
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
