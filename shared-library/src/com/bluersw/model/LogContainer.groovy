package com.bluersw.model

import java.util.Queue
import java.util.concurrent.atomic.AtomicInteger

import com.bluersw.model.LogType
import com.cloudbees.groovy.cps.NonCPS

class LogContainer {
	private static Queue<LogInfo> queue = new LinkedList<>()

	@NonCPS
	static void append(LogType logType, String message) {
		def date = new Date().format("HH:mm:ss:SSS")
		queue.offer(new LogInfo(logType, "${date}-${message}"))
	}

	static String getLogByTag(String startTag,String endTag, LogType logType){
		boolean findTag = false
		Object[] array = null
		StringBuilder builder = new StringBuilder()
		array = queue.toArray()
		for (Object o in array) {
			LogInfo logInfo = (LogInfo) o
			if(logInfo.message.endsWith(startTag)){
				findTag = true
			}
			if(findTag) {
				if (logInfo.type >= logType) {
					builder.append(logInfo)
				}
			}
			if(logInfo.message.endsWith(endTag)){
				findTag = false
			}
		}
		return builder.toString()
	}

	static String getLog(LogType logType) {
		Object[] array = null
		StringBuilder builder = new StringBuilder()
		array = queue.toArray()
		for (Object o in array) {
			LogInfo logInfo = (LogInfo) o
			if (logInfo.type >= logType) {
				builder.append(logInfo)
			}
		}
		return builder.toString()
	}

	static class LogInfo {
		LogType type
		String message

		LogInfo(LogType logType, String message) {
			this.type = logType
			this.message = message
		}

		@NonCPS
		@Override
		String toString() {
			return "${type}:${message}\n"
		}
	}
}
