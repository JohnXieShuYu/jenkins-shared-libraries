package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.DisplayInfo
import com.bluersw.model.LogType
import com.bluersw.model.Utility
import com.bluersw.model.StepType

class GlobalVariable extends AbstractStep {

	private static final String STEP_NAME = "GlobalVariable"

	GlobalVariable(Utility utility, DisplayInfo displayInfo) {
		super(STEP_NAME, StepType.GLOBAL_VARIABLE, utility, displayInfo)
	}

	@Override
	void run() {
		this.stepProperty.each { key, value -> this.println(LogType.MESSAGE, "[${key}:${value}]")
		}
	}
}
