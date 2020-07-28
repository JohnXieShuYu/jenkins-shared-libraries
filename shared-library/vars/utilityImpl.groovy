import com.bluersw.model.Utility

def call() {
	return [
			println: { String info -> println(info) }
	] as Utility
}

