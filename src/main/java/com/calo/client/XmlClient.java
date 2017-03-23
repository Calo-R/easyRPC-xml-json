package com.calo.client;
import java.util.ArrayList;

public class XmlClient extends AbstractClient{

	private Object xmlObject;
	private ArrayList<Object> xmlArray;
	
	public XmlClient() {
		this.clientTag = "xml";
	}
	
	@Override
	public XmlClient setRequestURL(String url) {
		this.url  = url;
		return this;
	}

	@Override
	public XmlClient setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}
	/**
	 * @param object should be the java basic type or String Type,but not byte([])</BR>
	 * Not Necessary,because some method may have no parameters</BR>
	 */
	public XmlClient setXmlClientParamObject(Object object) {
		this.xmlObject = object;
		return this;
	}

	/**
	 * elements in ArrayList should be the java basic type or String type,but not byte([])</BR>
	 * Not Necessary,because some method may have no parameters</BR> 
	 */
	public XmlClient setXmlClientParamsArray(ArrayList<Object> objects) {
		this.xmlArray = objects;
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
		
		String requestContent = 
				"<?xml version='1.0'?>"+
				"<methodCall>"+
				"<methodName>"+ requestMethod +"</methodName>";
		
		if (xmlObject != null) {
			requestContent += "<params>" +getMidStr(xmlObject) +"</params>";
		}
		
		if (xmlArray != null) {
			String midStr = "<params>";
			for (int i = 0; i < xmlArray.size(); i++) {
				Object object = xmlArray.get(i);
				midStr += getMidStr(object);
			}
			midStr += "</params>"; 
			requestContent += midStr;
		}
		requestContent += "</methodCall>";
		return requestContent;
	}
	
	private String getMidStr(Object object){
		String midStr = "";
		String typeName =  object.getClass().getName().toLowerCase();
		if (typeName.contains("int")) {
			midStr += "<param><value><int>" +(Integer)object +"</int></value></param>";	
		}
		if (typeName.contains("string")) {
			midStr +="<param><value><string>"+object.toString() +"</string></value></param>";
		}
		if (typeName.contains("boolean")) {
			midStr += "<param><value><boolean>" +(Boolean)object +"</boolean></value></param>";	
		}
		if (typeName.contains("long")) {
			midStr += "<param><value><long>"+(Long)object +"</long></value></param>";
		}
		if (typeName.contains("char")) {
			midStr += "<param><value><char>"+(Character)object+"</char></value></param>";
		}
		if (typeName.contains("double")) {
			midStr += "<param><value><double>"+(Double)object +"</double></value></param>";
		}
		if (typeName.contains("short")) {
			midStr += "<param><value><short>"+(Short)object +"</short></value></param>";
		}
		return midStr;
	}
}
