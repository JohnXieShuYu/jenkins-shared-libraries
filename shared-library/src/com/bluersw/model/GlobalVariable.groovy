package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.StepResult
import com.bluersw.model.StepType

class GlobalVariable extends AbstractStep {

	GlobalVariable(String name, String xpath) {
		super(name, xpath, StepType.GLOBAL_VARIABLE)
	}

	@Override
	StepResult runStep(Closure display) {
		if (display != null) {
			this.stepProperty.each { key, value -> display(String.format("[%s:%s]", key, value))
			}
		}
		return new StepResult(this.name, true, this.getInfo(), this.getWarning())
	}
}
