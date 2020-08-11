package com.bluersw.utils

/**
 * 自定义返回值类
 */
class GResult implements Serializable{

    /**
     * 是否成果
     */
    boolean boolResult

    /**
     * 说明信息
     */
    String message

    /**
     * 构造函数
     * @param boolResult 是否成果
     * @param message 说明信息
     */
    GResult(boolean boolResult,String message){
        this.boolResult = boolResult
        this.message = message
    }

}
