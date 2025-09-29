package com.zcxd.utils;

public class Message {
	
	private Integer code;
 
	private Object msg;
 
	public Integer getCode() {
		return code;
	}
 
	public void setCode(Integer code) {
		this.code = code;
	}
 
	public Object getMsg() {
		return msg;
	}
 
	public void setMsg(Object msg) {
		this.msg = msg;
	}
 
	
	public static Message getSuccessfulRes(Object obj) {
		return new Message(0, obj);
	}
 
	/**
	 * @param code
	 * @param obj
	 */
	public Message(Integer code, Object obj) {
		super();
		this.code = code;
		this.msg = obj;
	}
 
	
}