package com.bluersw.model

import com.bluersw.model.AbstractStep
import com.bluersw.model.DisplayInfo
import com.bluersw.model.LogType
import com.bluersw.model.Utility
import com.cloudbees.groovy.cps.NonCPS

class Steps {
	private String name
	private HashMap<String, String> stepsProperty = new HashMap<>()
	private Queue<AbstractStep> stepQueue = new LinkedList<>()
	private Utility utility
	private DisplayInfo displayInfo

	Steps(String name, Utility utility){
		this.name = name
		this.utility = utility
		this.displayInfo = createDisplayInfo()
	}

	int size(){
		this.stepQueue.size()
	}

	void setStepsProperty(String propertyName, String value){
		this.stepsProperty.put(propertyName, value)
	}

	void setStep(AbstractStep step){
		this.stepQueue.offer(step)
	}

	private boolean getShowLog(){
		if(!this.stepsProperty.containsKey('ShowLog')){
			return false
		}else{
			return stepsProperty['ShowLog'] as boolean
		}
	}

	@NonCPS
	private DisplayInfo createDisplayInfo(){
		return [println:{ LogType logType, String content->
					if(logType == LogType.ERROR || logType == LogType.MESSAGE){
						this.utility.println("${logType}:${content}")
					}else{
						if (this.getShowLog()){
							this.utility.println("${logType}:${content}")
						}
					}}] as DisplayInfo
	}
}
