package com.calo.server;

public class RpcServer extends AbstractRpcServer{

	public void addHanlder(String methedName, Class<?> cls) {
		synchronized (RpcServer.class) {
			handlerMap.put(methedName, cls);
		}
	}

	public Class<?> getClassByKey(String name) {
		return handlerMap.get(name);
	}
	
	public boolean containKey(String key){
		return handlerMap.containsKey(key);
	}
	
	public String getURI() {
		return URI;
	}

	public RpcServer setURI(String uri) {
		URI = uri;
		return this;
	}

	@Override
	public RpcServer bind(int port) {
		this.port = port;
		return this;
	}
}
