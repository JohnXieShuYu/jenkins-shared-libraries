package com.bluersw.Utils

import hudson.FilePath;
import hudson.remoting.VirtualChannel
import hudson.remoting.Channel
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer

/**
 * 处理Json文档，扩展了变量的概念
 */
class JSONExtend implements Serializable {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator")
	private FilePath filePath
	private String path
	private String text
	private VirtualChannel channel
	private JSONObject jsonObject


	/**
	 * 构造函数
	 * @param channel 运行构建脚本的服务器环境，如果为null就代表当前环境
	 * @param path 文件路径
	 */
	JSONExtend(VirtualChannel channel, String path) {
		this.channel = channel == null ? Channel.current() : channel
		this.path = path.replace("/",FILE_SEPARATOR).replace("\\",FILE_SEPARATOR)
		this.filePath = new FilePath(channel, path)
		this.text = filePath.readToString()
		this.jsonObject = JSONObject.fromObject(this.text)

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

}
