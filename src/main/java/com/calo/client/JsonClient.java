package com.calo.client;

import java.util.ArrayList;

import net.sf.json.JSONObject;

public class JsonClient extends AbstractClient {

	private Object object;
	private JSONObject jsonObject;
	private ArrayList<Object> jsonArray;
	private int id = -1;
	
	public JsonClient() {
		this.clientTag = "json";
	}
	
	@Override
	public JsonClient setRequestURL(String url) {
		this.url = url;
		return this;
	}

	@Override
	public JsonClient setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	/**
	 * parameters array</BR>
	 * example   : [1,true,"hello word"]</BR>
	 * explain   : call the method in server which has the parameter list (int,boolean,String)</BR>
	 * Attention : Not Necessary,because some method may have no parameters</BR>
	 */
	public JsonClient setJsonClientParamsArray(ArrayList<Object> parametersArray) {
		this.jsonArray = parametersArray;
		return this;
	}

	/**
	 * parameters json object</BR>
	 * example   : {"id":100,"name":"Ban"}</BR>
	 * explain   : call the method in server which has the parameter list (int,String)</BR>
	 * Attention : Not Necessary,because some method may have no parameters</BR>
	 */
	public JsonClient setJsonClientParamsJsonObject(JSONObject parametersObject) {
		this.jsonObject = parametersObject;
		return this;
	}

	/**
	 * @param object should be the java basic type or String Type,but not byte([])</BR>
	 */
	public JsonClient setJsonClientParamObject(Object object){
		this.object = object;
		return this;
	}
	/**
	 * request id</BR>
	 */
	public JsonClient setRequestId(int id) {
		this.id = id;
		return this;
	}

	@Override
	public String setRequetContent() throws Exception {
		if (url == null) {
			throw new Exception("Request URL is NULL.");
		}
		
		if (requestMethod == null) {
			throw new Exception("Request Method is NULL.");
		}
		
		JSONObject requestContent = new JSONObject();
		requestContent.put("jsonrpc", "2.0");
		requestContent.put("method", requestMethod);
		
		if (this.object != null) {
			requestContent.put("params", object);
		}
		if (this.jsonArray != null) {
			requestContent.put("params", jsonArray);
		}
		
		if (this.jsonObject != null) {
			requestContent.put("params", jsonObject);        
		}
		
		if (this.id > 0) {
			requestContent.put("id", id);
		}else {
			throw new Exception("id shuld be int and >0");
		}
		return requestContent.toString();
	}
}
