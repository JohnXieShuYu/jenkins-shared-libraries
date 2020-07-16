package com.bluersw.Utils

import hudson.FilePath;
import hudson.remoting.VirtualChannel
import hudson.remoting.Channel
import net.sf.json.JSONObject

/**
 * 处理Json文档，扩展了变量的概念
 */
class JSONExtend implements Serializable {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator")
	private static final String NODE_NAME_GLOBAL_VARIABLE = "GlobalVariable"
	private static final String NODE_NAME_LOCAL_VARIABLE = "Variable"
	private FilePath filePath
	private String path
	private String text
	private VirtualChannel channel
	private JSONObject jsonObject
	private LinkedHashMap<String, String> globalVariable = new LinkedHashMap<>()
	private LinkedHashMap<String, String> localVariableIndex = new LinkedHashMap<>()
	private StringBuffer info = new StringBuffer()


	/**
	 * 构造函数
	 * @param channel 运行构建脚本的服务器环境，如果为null就代表当前环境
	 * @param path 文件路径
	 */
	JSONExtend(VirtualChannel channel, String path) {
		this.channel = channel == null ? Channel.current() : channel
		this.path = path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR)
		this.filePath = new FilePath(channel, path)
		this.text = filePath.readToString()
		this.jsonObject = JSONObject.fromObject(this.text)
		analyzeJSONObject(this.jsonObject, '')
	}

	/**
	 * 构造函数
	 * @param text Json文档内容
	 */
	JSONExtend(String text) {
		this.text = text
		this.jsonObject = JSONObject.fromObject(this.text)
	}

	JSONObject getJsonObject() {
		return jsonObject
	}

	String getInfo() {
		return info.toString()
	}

	LinkedHashMap<String, String> getGlobalVariable() {
		return globalVariable
	}

	LinkedHashMap<String, String> getLocalVariable() {
		return localVariableIndex
	}

	private void analyzeJSONObject(Object o, String xpath) {
		Iterator<String> entrys = null;
		if (o instanceof JSONObject) {
			entrys = ((JSONObject) o).entrySet().iterator()
		}
		else if (o instanceof Map.Entry) {
			Map.Entry entry = (Map.Entry) o
			xpath = xpath + '/'  + entry.key.toString()
			if (entry.value instanceof String) {
				this.info.append(xpath + ':' + entry.value + '\r\n')
				if(xpath.indexOf(NODE_NAME_GLOBAL_VARIABLE) != -1){
					this.globalVariable.put(entry.key.toString(),entry.value.toString())
				}else if(xpath.indexOf(NODE_NAME_LOCAL_VARIABLE) != -1){
					this.localVariableIndex.put(xpath,entry.value.toString())
				}
			}
			else {
				entrys = entry.value.iterator()
			}
		}
		if (entrys != null) {
			while (entrys.hasNext()) {
				analyzeJSONObject(entrys.next(), xpath)
			}
		}
	}
}
