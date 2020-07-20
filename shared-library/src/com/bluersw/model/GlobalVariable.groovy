package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.StepResult
import com.bluersw.model.Utility

class GlobalVariable extends AbstractStep {

	private static final String STEP_NAME = "GlobalVariable"

	GlobalVariable(String name, String xpath, Utility utility) {
		super(STEP_NAME, xpath, StepType.GLOBAL_VARIABLE, utility)
	}

	@Override
	StepResult runStep() {
		this.stepProperty.each { key, value -> utility.println(String.format("[%s:%s]", key, value))
		}
		return new StepResult(this.name, true, this.getInfo(), this.getWarning())
	}
}
