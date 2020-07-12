@Library('shared-library')
import com.bluersw.Utils

sh acme.name
acme.name = 'something'
sh acme.name

acme.caution('world')

sayHello 'World'
sayHello()

parallel(
        action1: {
            node() {
                def utils = new Utils()
                sh "${utils.gitTools()}"
                sh 'sleep 3'
                String json = libraryResource 'com/bluersw/request.json'
                println json
            }
        },
        action2: {
            node() {
                sh 'sleep 4'
                error 'message'
            }
        }
)