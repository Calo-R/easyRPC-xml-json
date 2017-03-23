package com.calo.server;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.StringTokenizer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonResolver extends AbstractResolver{
	
	public JsonResolver(ChannelHandlerContext ctx, FullHttpRequest request) {
		this.ctx = ctx;
		this.request = request;
	}

	@Override
	public void response(String requestContent, RpcServer rpcServer) throws Exception {

		JSONObject jObject = JSONObject.fromObject(requestContent);
		if (!jObject.containsKey("id")) {
			String contentType = "application/json; charset=UTF-8";
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, "", contentType);
			return;
		}

		int id = jObject.getInt("id");
		String jsonRpcVersion = jObject.getString("jsonrpc");
		if (!jsonRpcVersion.equals("2.0")) {
			String content = "jsonRpcVersion is wrong.";
			String contentType = "application/json; charset=UTF-8";
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultErr(content, -32001, id), contentType);
			return;
		}

		String call = jObject.getString("method");
		if (!rpcServer.containKey(call)) {
			String content = "Method not found";
			String contentType = "application/json; charset=UTF-8";
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultErr(content, -32601, id), contentType);
			return;
		}

		StringTokenizer stringTokenizer = new StringTokenizer(call, ".");
		String[] classAndMethodName = new String[2];
		int i = 0;
		while (stringTokenizer.hasMoreTokens()) {
			classAndMethodName[i] = stringTokenizer.nextToken();
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
		Object resultRaw = null;
		if (!jObject.containsKey("params")) {
			resultRaw = want.invoke(object);
			resultHanlder(returnType, resultRaw, id);
			return;
		}

		String paramStr = jObject.getString("params");
		if (!paramStr.contains("[") && !paramStr.contains("{")) {

			String paramType = pts[0].getName().toLowerCase();
			if (paramType.equals("int")) {// parameter is int
				resultRaw = want.invoke(object, jObject.getInt("params"));
			}
			if (paramType.contains("string")) {// parameter is string
				resultRaw = want.invoke(object, jObject.getString("params"));
			}
			if (paramType.contains("bool")) {// parameter is boolean
				resultRaw = want.invoke(object, jObject.getBoolean("params"));
			}
			if (paramType.contains("double")) {// parameter is double
				resultRaw = want.invoke(object, jObject.getDouble("params"));
			}
			if (paramType.contains("long")) {// parameter is long
				resultRaw = want.invoke(object, jObject.getLong("params"));
			}
			if (paramType.contains("char")) {// parameter is char
				resultRaw = want.invoke(object, jObject.getString("params").charAt(0));
			}
			if (paramType.contains("float")) {// parameter is float
				resultRaw = want.invoke(object, (float)jObject.getDouble("params"));
			}
			if (paramType.contains("short")) {// parameter is short
				resultRaw = want.invoke(object, (short)jObject.getInt("params"));
			}
			resultHanlder(returnType, resultRaw, id);
			return;
		}

		int rawLen = pts.length;
		Object[] objects = new Object[rawLen];
		if (paramStr.contains("[")) {// parameters is array
			JSONArray jArr = JSONArray.fromObject(paramStr);
			for (int j = 0; j < objects.length; j++) {
				String paramType = pts[j].getName().toLowerCase();
				if (paramType.contains("int")) {
					objects[j] = jArr.getInt(j);
				}
				if (paramType.contains("string")) {
					objects[j] = jArr.getString(j);
				}
				if (paramType.contains("bool")) {
					objects[j] = jArr.getBoolean(j);
				}
				if (paramType.contains("double")) {
					objects[j] = jArr.getDouble(j);
				}
				if (paramType.contains("long")) {
					objects[j] = jArr.getLong(j);
				}
				if (paramType.contains("char")) {
					objects[j] = jArr.getString(j).charAt(0);
				}
				if (paramType.contains("float")) {
					objects[j] = (float)jArr.getDouble(j);
				}
				if (paramType.contains("short")) {
					objects[j] = (short)jArr.getInt(j);
				}
				
			}
			resultRaw = want.invoke(object, objects);
			resultHanlder(returnType, resultRaw, id);
			return;
		}

		if (paramStr.contains("{")) {// parameters is object
			JSONObject jObj = JSONObject.fromObject(paramStr);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = jObj.keys();
			int j = 0;
			while (iterator.hasNext()) {
				String paramType = pts[j].getName().toLowerCase();
				String key = iterator.next();
				if (paramType.contains("int")) {
					objects[j] = jObj.getInt(key);
				}
				if (paramType.contains("string")) {
					objects[j] = jObj.getString(key);
				}
				if (paramType.contains("bool")) {
					objects[j] = jObj.getBoolean(key);
				}
				if (paramType.contains("double")) {
					objects[j] = jObj.getDouble(key);
				}
				if (paramType.contains("long")) {
					objects[j] = jObj.getLong(key);
				}
				if (paramType.contains("char")) {
					objects[j] = jObj.getString(key).charAt(0);
				}
				if (paramType.contains("float")) {
					objects[j] = (float)jObj.getDouble(key);
				}
				if (paramType.contains("short")) {
					objects[j] = (short)jObj.getInt(key);
				}
				j++;
			}
			resultRaw = want.invoke(object, objects);
			resultHanlder(returnType, resultRaw, id);
			return;
		}

		String content = "please check json of your request";
		String contentType = "application/json; charset=UTF-8";
		super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR, resultErr(content, -32002, id), contentType);
		return;
	}

	private void resultHanlder(int retureTag, Object resultRaw, int id) {

		String contentType = "application/json; charset=UTF-8";
		if (retureTag == TAG_SINGLE) {
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(resultRaw, id), contentType);
		}
		if (retureTag == TAG_ARRAY) {
			super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(JSONArray.fromObject(resultRaw), id), contentType);
		}
		if (retureTag == TAG_OBJECT) {
		super.returnResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, resultRight(JSONObject.fromObject(resultRaw), id), contentType);
		}
	}

	private String resultErr(String content, int code, int id) {
		JSONObject object = new JSONObject();
		JSONObject error = new JSONObject();
		object.put("jsonrpc", "2.0");
		error.put("code", code);// -32001
		error.put("message", content);
		object.put("error", error);
		object.put("id", id);
		return object.toString();
	}

	private String resultRight(Object ob, int id) {
		JSONObject object = new JSONObject();
		object.put("jsonrpc", "2.0");
		object.put("result", ob);
		object.put("id", id);
		return object.toString();
	}
}
