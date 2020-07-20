package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.Result
import com.bluersw.model.Utility
import com.bluersw.model.StepType

class GlobalVariable extends AbstractStep {

	private static final String STEP_NAME = "GlobalVariable"

	GlobalVariable(String xpath, Utility utility) {
		super(STEP_NAME, xpath, StepType.GLOBAL_VARIABLE, utility)
	}

	@Override
	Result<String> run() {
		this.stepProperty.each { key, value -> utility.println("[${key}:${value}]")
		}
		return new Result<String>(this.name, true, this.getInfo(), this.getWarning())
	}
}
