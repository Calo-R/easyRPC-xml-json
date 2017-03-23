package com.calo.server;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class XmlResolver extends AbstractResolver{

	public XmlResolver(ChannelHandlerContext ctx, FullHttpRequest request) {
		this.ctx = ctx;
		this.request = request;
	}
	
	@Override
	public void response(String requestContent, RpcServer rpcServer) throws Exception {
		XMLSerializer xs = new XMLSerializer();
		JSON xmlJson = xs.read(requestContent);
		JSONObject jObject = JSONObject.fromObject(xmlJson);
		
		String call = jObject.getString("methodName");
		if (!rpcServer.containKey(call)) {
			String content = error(-1, "Method not found");
			String contentType = "text/xml; charset=UTF-8";
			super.returnResponse(ctx, request.protocolVersion(),HttpResponseStatus.OK, content, contentType);
			return ;
		}
		
		StringTokenizer st = new StringTokenizer(call, ".");
		String[] classAndMethodName = new String[2];
		int i = 0;
		while (st.hasMoreElements()) {
			classAndMethodName[i] = st.nextToken();
			i++;
		}
		
		String methodName = classAndMethodName[1];
		Method want = null;
		Class<?> cls = rpcServer.getClassByKey(call);
		Method[] method = cls.getMethods();
		for (int j = 0; j < method.length; j++) {
			if (method[j].getName().equals(methodName)) {
				want = method[j];
				break;
			}
		}

		Object object = cls.newInstance();
		Class<?>[] pts = want.getParameterTypes();
		int returnType = super.getReturnTypeTag(want.getReturnType().getName().toLowerCase());
		String returnName = this.getReturnName(want.getReturnType().getName().toLowerCase());
		Object resultRaw = null;
		if (!jObject.containsKey("params")) {
			resultRaw = want.invoke(object);
			resultHanlder(returnType, returnName, resultRaw);
			return;
		}

		String paramStr = jObject.getString("params");
		if (paramStr.contains("[")) {//array
			JSONArray array = JSONArray.fromObject(paramStr);
			Object[] objs = new Object[pts.length];
			for (int j = 0; j < array.size(); j++) {
				JSONObject tmpOj = JSONObject.fromObject(array.get(j));
				String value1 = tmpOj.getString("value");
				if (!value1.contains("{")) {
					objs[j] = value1;
					continue;
				}
				JSONObject value2 = JSONObject.fromObject(value1);
				@SuppressWarnings("unchecked")
				Iterator<String> ki = value2.keys();
				String valueType = ki.next();
				if (valueType.equals("i4") || valueType.equals("int")) {
					objs[j] = value2.getInt(valueType);
					continue;
				}
				if (valueType.equals("boolean")) {
					objs[j] = value2.getBoolean(valueType);
					continue;
				}
				if (valueType.equals("string")) {
					objs[j] = value2.getString(valueType);
					continue;
				}
				if (valueType.equals("double")) {
					objs[j] = value2.getDouble(valueType);
					continue;
				}
				if (valueType.equals("short")) {
					objs[j] = (short)value2.getInt(valueType);
					continue;
				}
				if (valueType.equals("char")) {
					objs[j] = value2.getString(valueType).charAt(0);
					continue;
				}
				if (valueType.equals("long")) {
					objs[j] = value2.getLong(valueType);
					continue;
				}
				if (valueType.equals("float")) {
					objs[j] = (float)value2.getLong(valueType);
					continue;
				}
				
			}
			resultRaw = want.invoke(object, objs);
			resultHanlder(returnType, returnName, resultRaw);
			return;
		}
		
		//else json
		JSONObject jvalue = JSONObject.fromObject(paramStr);
		String value1 = JSONObject.fromObject(jvalue.getString("param")).getString("value") ;
		if (!value1.contains("{")) {
			resultRaw = want.invoke(object, value1);
			resultHanlder(returnType, returnName, resultRaw);
			return;
		}
		
		JSONObject value2 = JSONObject.fromObject(value1);
		@SuppressWarnings("unchecked")
		Iterator<String> ki = value2.keys();
		String valueType = ki.next();
		if (valueType.equals("i4") || valueType.equals("int")) {
			int it = value2.getInt(valueType);
			resultRaw = want.invoke(object, it);
		}
		
		if (valueType.equals("boolean")) {
			boolean b = value2.getBoolean(valueType);
			resultRaw = want.invoke(object, b);
		}

		if (valueType.equals("string")) {
			String s = value2.getString(valueType);
			resultRaw = want.invoke(object, s);
		}
		if (valueType.equals("double")) {
			double d = value2.getDouble(valueType);
			resultRaw = want.invoke(object, d);
		}
		if (valueType.equals("short")) {
			short s = (short) value2.getInt(valueType);
			resultRaw = want.invoke(object, s);
		}
		if (valueType.equals("char")) {
			char c = value2.getString(valueType).charAt(0);
			resultRaw = want.invoke(object, c);
		}
		if (valueType.equals("long")) {
			long l = value2.getLong(valueType);
			resultRaw = want.invoke(object, l);
		}
		if (valueType.equals("float")) {
			float l = (float)value2.getLong(valueType);
			resultRaw = want.invoke(object, l);
		}
		resultHanlder(returnType, returnName, resultRaw);
		return;
	}
	
	private void resultHanlder(int returnType, String returnName, Object resultRaw) {

		String contentType = "application/json; charset=UTF-8";
		if (returnType == TAG_SINGLE) {
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(returnType, returnName, resultRaw), contentType);
		}
		if (returnType == TAG_ARRAY) {
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(returnType, returnName, resultRaw), contentType);
		}
		if (returnType == TAG_OBJECT) {
		super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(returnType, returnName, resultRaw), contentType);
		}
	}
	
	private String resultRight(int returnTag, String returnName,Object ob) {

		if (returnTag == TAG_SINGLE ) {
			if (returnName.equals("boolean")) {
				boolean b = (Boolean) ob;
				if (b) {
					return assembleXml("<param><value><"+returnName +">"+1+"</" +returnName +"></value></param>");
				}
				return assembleXml("<param><value><"+returnName +">"+0+"</" +returnName +"></value></param>");
			}
			return assembleXml("<param><value><"+returnName +">"+String.valueOf(ob)+"</" +returnName +"></value></param>");
		}
		
		String result = "<param><value><array><data>";
		if (returnTag == TAG_ARRAY) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) ob;	
			int i = 0;
			while (i < list.size()) {
				result += "<value>"+list.get(i)+"</value>";
				i ++;
			}
		}
		
		if (returnTag == TAG_OBJECT) {
			JSONObject jObject = JSONObject.fromObject(ob);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = jObject.keys();
			while (keys.hasNext()) {
				result += "<value>"+jObject.get(keys.next())+"</value>";
			}
		}
		result += "</data></array></value><param>";
		return assembleXml(result);	
	}
	
	private String assembleXml(String result){
		return "<?xml version='1.0'?>"+
		        "<methodResponse>"+
					"<params>"+
		                result+
					"</params>"+
		        "</methodResponse>";
	}
	
	private String getReturnName(String name){
		if (name.contains("int")) {
			return "int";
		}
		if (name.contains("bool")) {
			return "boolean";
		}
		if (name.contains("double")) {
			return "double";
		}
		return "string";
	}
	private String error(int errCode,String errStr){
		return "<?xml version='1.0'?>"+
			   "<methodResponse>"+
			      "<fault>"+
				     "<value>"+
			            "<struct>"+
				           "<member>"+
				              "<name>"+"errCode"+"</name>"+
				              "<value>"+"<int>"+errCode+"</int>"+"</value>"+
				           "</member>"+
				           "<member>"+
				              "<name>"+"errStr"+"</name>"+
				               "<value>"+"<string>"+errStr+"</string>"+"</value>"+
				           "</member>"+
				        "</struct>"+
				     "</value>"+
				  "</fault>"+
				"</methodResponse>";
	}
}
