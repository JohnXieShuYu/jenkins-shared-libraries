import com.bluersw.StepFactory
import com.bluersw.model.LogContainer
import com.bluersw.model.LogType
import groovy.transform.Field

@Field LinkedList<StepFactory> factories
@Field String[] jsonFilePaths

def call(String projectPaths){
	this.jsonFilePaths = getDirectories(projectPaths)
	this.factories = createStepFactory(this.jsonFilePaths)
	return this
}

void printLoadFactoryLog(){
	for(StepFactory factory in this.factories){
		//打印装载配置文件日志
		LogType logLevel= factory.getLogLevel()
		println(LogContainer.getLogByTag(factory.getInitStartTag(),factory.getInitEndTag(),logLevel))
	}
}

private static LinkedList<StepFactory> createStepFactory(String[] jsonFile){
	LinkedList<StepFactory> factoryList = new LinkedList<>()
	for(String json in jsonFile){
		StepFactory factory = new StepFactory(json)
		factory.initialize()
		factoryList.add(factory)
	}
	return factoryList
}

private static String[] getDirectories(String projectPaths){
	String[] paths = projectPaths.split(',')
	//设置构建配置文件
	configJSONFilePath(paths)
	return paths
}

private static void configJSONFilePath(String[] dirs){
	for(int i = 0; i<dirs.length;i++){
		if(!dirs[i].endsWith('.json')){
			//默认项目根目录或子项目目录下jenkins-project.json作为构建配置文件
			dirs[i] = dirs[i] + 'jenkins-project.json'
		}
	}
}

