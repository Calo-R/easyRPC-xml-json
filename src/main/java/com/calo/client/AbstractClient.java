package com.calo.client;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public abstract class AbstractClient {
	protected String url;
	protected String clientTag;
	protected String requestMethod;
	
	/**
	 * RPC server URL</BR>
	 */
	public abstract AbstractClient setRequestURL(String url);
	
	/**
	 * the method in server which client called</BR>
	 * example   : User.sleep </BR>
	 * explain   : User is class,sleep is function</BR>
	 * Attention : Case Sensitive</BR>
	 */
	public abstract AbstractClient setRequestMethod(String requestMethod);
	
	/**
	 * callback by parent</BR>
	 */
	public abstract String setRequetContent() throws Exception;
	
	/**
	 * @return map<statusCode,httpEntityString></BR>
	 * example : 200 , string </BR>
	 */
	public Map<Integer, String> sendRequest() throws Exception {
		
		String requestContent = setRequetContent();
		StringEntity reqEntity = new StringEntity(requestContent.toString(), Charset.forName("UTF-8"));
		HttpPost post = new HttpPost(url);
		post.setEntity(reqEntity);
		if (clientTag.equals("json")) {
			post.setHeader("Content-type", "application/json; charset=utf-8");
		}else {
			post.setHeader("Content-type", "text/xml; charset=utf-8");
		}
		Map<Integer, String> resultMap = new HashMap<Integer,String >();
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpResponse response = httpClient.execute(post);
			HttpEntity respEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			String respString = EntityUtils.toString(respEntity);
			resultMap.put(statusCode, respString);
		} finally {
			post.releaseConnection();
		}
		return resultMap;
	}
}
