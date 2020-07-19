package com.bluersw.utils

import java.nio.charset.Charset

import com.cloudbees.groovy.cps.NonCPS

class HttpRequest {

	@NonCPS
	static String getResponse(String url){
		HttpURLConnection connection = null
		BufferedReader bufferedReader = null
		StringBuilder builder = new StringBuilder()
		try {
			URL httpURL = new URL(url)
			connection = (HttpURLConnection) httpURL.openConnection()
			connection.setRequestMethod('GET')
			connection.connect()

			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))

			String line = ""
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line)
			}
		}
		finally {
			if(bufferedReader != null){
				bufferedReader.close()
			}

			if(connection != null){
				connection.disconnect()
			}
		}

		return new String(builder.toString().getBytes(), Charset.defaultCharset())
	}
}
