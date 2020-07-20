package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.PipelineUtility
import com.bluersw.model.StepResult
import com.bluersw.model.StepType

class GlobalVariable extends AbstractStep {

	private static final String STEP_NAME = "GlobalVariable"

	GlobalVariable(String name, String xpath, PipelineUtility pipelineUtility) {
		super(STEP_NAME, xpath, StepType.GLOBAL_VARIABLE, pipelineUtility)
	}

	@Override
	StepResult runStep() {
		this.stepProperty.each { key, value -> pipelineUtility.println(String.format("[%s:%s]", key, value))
		}
		return new StepResult(this.name, true, this.getInfo(), this.getWarning())
	}
}
