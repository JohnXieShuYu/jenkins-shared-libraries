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
	private final String SHOW_LOG_KEY_NAME = 'ShowLog'
	private final String IS_RUN_KEY_NAME = 'Run'

	Steps(String name, Utility utility){
		this.name = name
		this.utility = utility
		this.displayInfo = createDisplayInfo()
	}

	Queue<AbstractStep> getStepQueue() {
		return stepQueue
	}

	String getStepsName() {
		return name
	}

	int size(){
		this.stepQueue.size()
	}

	void setStepsProperty(String propertyName, String value){
		this.stepsProperty.put(propertyName, value)
	}

	HashMap<String, String> getStepsProperty() {
		return stepsProperty
	}

	void append(AbstractStep step){
		step.setUtility(this.utility)
		step.setDisplayInfo(this.displayInfo)
		this.stepQueue.offer(step)
	}

	void run(){
		if(getIsRun()) {
			for (AbstractStep step in this.stepQueue) {
				this.utility.println("开始执行 [${step.getStepName()}]：")
				step.run()
				this.utility.println("[${step.getStepName()}] 执行结束。")
			}
		}else{
			this.utility.println("跳过${this.name}节点的执行。")
		}
	}

	private boolean getShowLog(){
		if(!this.stepsProperty.containsKey(SHOW_LOG_KEY_NAME)){
			return false
		}else{
			return stepsProperty[SHOW_LOG_KEY_NAME] as boolean
		}
	}

	private boolean getIsRun(){
		if(!this.stepsProperty.containsKey(IS_RUN_KEY_NAME)){
			return true
		}else{
			return stepsProperty[IS_RUN_KEY_NAME] as boolean
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
