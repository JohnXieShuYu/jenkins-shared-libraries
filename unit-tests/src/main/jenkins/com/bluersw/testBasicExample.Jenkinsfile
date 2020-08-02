@Library('shared-library')_
import com.bluersw.StepFactory
import com.bluersw.model.LogContainer

StepFactory sf = new StepFactory("./src/main/jenkins/com/bluersw/test-res/basic_example.json")
sf.initialize()
println(LogContainer.getLog(sf.getLogLevel()))
