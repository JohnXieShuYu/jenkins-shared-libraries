import com.bluersw.StepFactory
import groovy.transform.Field

@Field List<StepFactory> factories = new LinkedList<>()
@Field String projectDirectory

def call(String projectDirectory){
	this.projectDirectory = projectDirectory
	return this
}

static String[] getDirectories(String projectDirectory){
	String[] dirs = projectDirectory.split(',')
	List<String> Directories = new LinkedList<>()

}

