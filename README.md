# easyRPC-xml-json 
* For Java server
* This is a new java RPC framework
* It is easy to use than the other you find.
* It support the way XML or JSON RPC to send request to server at the same time.
* It supports all the programming languages client which support json,xml and http.

## What easyRPC based on?
* json-rpc protocol. 
* xml-rpc protocol.
* java Reflection.
* asynchronous programming
* network programming.

## Why it is easy to use?
* Don't have to know the servlet,http or web server in java.
* Just need to know how to "getter()","setter()" and new an object!
* ZERO-configure!

## How to use?
There I provided 3 jar in folder myjar in the project:
* `easy-rpc-all-in-one-1.0.jar`for server and client 
* `easy-rpc-server-1.0.jar`just for server
* `easy-rpc-client-1.0.jar`just for client
* You can also directly copy the project to use!
#### define 2 class
`BooK.java`</br>
```java
	public class Book {
	   private int id;
	   private String name;
	   private boolean b;
	   public int getId() {
	      return id;
	   }
	   public void setId(int id) {
	      this.id = id;
	   }
	   public String getName() {
	      return name;
	   }
	   public void setName(String name) {
	      this.name = name;
	   }
	   public boolean isB() {
	      return b;
	   }
	   public void setB(boolean b) {
	      this.b = b;
	   }	
	}
```
`User.java`</br>
```java
public class User {
	
	public String sleep (int i){
		return "sleep "+String.valueOf(i) +" hour(s)"; 
	}
	
	public String say(int i, String st){
		return String.valueOf(i) +","+ st+"!";
	}
	
	public Book getBook( int i, Boolean b, String s){
		Book book = new  Book();
		book.setId(i);
		book.setName(s);
		book.setB(b);
		return book;
	}
}
```
#### create server and register service
```java
import com.calo.server.RpcServer;

public class Server {

	public static void main(String[] args) {
		
		/**
		 * create a server,receive xml or json request
		 */
		RpcServer rpcServer = new RpcServer();
		
		/**
		 * URI is default '/', but you can change it as you want
		 */
		rpcServer.setURI("/rpc");
		
		/**
		 * register service "User.say","User.getBook" and User.sleep
		 * Service name is Case Sensitive!!!
		 */
		rpcServer.addHanlder("User.say", User.class);
		rpcServer.addHanlder("User.getBook", User.class);
		rpcServer.addHanlder("User.sleep", User.class);
		
		/**
		 * server default listen on port 8080,but you can change it as you want
		 * then,start the server 
		 */
		rpcServer.bind(9999).start();
		/**
		 * server start message:
		 * 
		 * Server registered following method:
		 * User.sleep
		 * User.say
		 * User.getBook
		 * 
		 * Server listen on port:9999
		 * Server URL:/rpc
		 * 
		 * RPC SERVER START SUCCESS.
		 */
	}
}

```
#### create a rpc-json client, and call service "User.sleep"
```java
import java.util.Map;
import com.calo.client.JsonClient;

public class Client {

	public static void main(String[] args) throws Exception {
		/**
		 * create a json rpc client based on json-rpc protocol
		 */
		JsonClient jClient = new JsonClient();
		
		//set request url
		jClient.setRequestURL("http://localhost:9999/rpc");
		
		//set request id,it is needed!
		jClient.setRequestId(1);
		
		//set method called on server 
		jClient.setRequestMethod("User.sleep");
		
		/**
		 * set request parameter.
		 * when the method on server just has one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		jClient.setJsonClientParamObject(10);
		
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = jClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * {"jsonrpc":"2.0","result":"sleep 10 hour(s)","id":1}
		 */
	}
}
```
#### create a rpc-json client, and call service "User.say"
```java
import java.util.Map;
import com.calo.client.JsonClient;

public class Client {

	public static void main(String[] args) throws Exception {
		/**
		 * create a json rpc client based on json-rpc protocol
		 */
		JsonClient jClient = new JsonClient();
		
		//set request url
		jClient.setRequestURL("http://localhost:9999/rpc");
		
		//set request id,it is needed!
		jClient.setRequestId(1);
		
		//set method called on server 
		jClient.setRequestMethod("User.say");
		
		/**
		 * set request parameter.
		 * when the method on server just has MORE THAN one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		ArrayList<Object> parametersArray = new ArrayList<Object>();
		parametersArray.add(2017);
		parametersArray.add("hello word");
		jClient.setJsonClientParamsArray(parametersArray);
		
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = jClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * {"jsonrpc":"2.0","result":"2017,hello word!","id":1}
		 */
	}
}
```
#### create a rpc-json client, and call service "User.getBook"
```java
import java.util.Map;
import com.calo.client.JsonClient;

public class Client {

	public static void main(String[] args) throws Exception {
		/**
		 * create a json rpc client based on json-rpc protocol
		 */
		JsonClient jClient = new JsonClient();
		
		//set request url
		jClient.setRequestURL("http://localhost:9999/rpc");
		
		//set request id,it is needed!
		jClient.setRequestId(1);
		
		//set method called on server 
		jClient.setRequestMethod("User.getBook");
		
		/**
		 * set request parameter.
		 * when the method on server just has MORE THAN one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		JSONObject parametersObject = new JSONObject();
		parametersObject.put("i", 10);
		parametersObject.put("b", true);
		parametersObject.put("s", "Linux");
		jClient.setJsonClientParamsJsonObject(parametersObject);
		
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = jClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * {"jsonrpc":"2.0","result":{"b":true,"id":10,"name":"Linux"},"id":1}
		 */
	}
}
```
#### create a rpc-XML client, and call service "User.sleep"
```java
import java.util.Map;

public class Client {

	public static void main(String[] args) throws Exception {

		/**
		 * create a xml client
		 */
		XmlClient xClient = new XmlClient();
		xClient.setRequestURL("http://localhost:9999/rpc");
		xClient.setRequestMethod("User.sleep");
		
		/**
		 * set request parameter.
		 * when the method on server just has one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		xClient.setXmlClientParamObject(10);
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = xClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * <?xml version='1.0'?><methodResponse><params><param><value><string>sleep 10 hour(s)</string></value></param></params></methodResponse>
		 */
	}
}
```
#### create a rpc-XML client, and call service "User.say"
```java
import java.util.Map;

public class Client {

	public static void main(String[] args) throws Exception {

		/**
		 * create a xml client
		 */
		XmlClient xClient = new XmlClient();
		xClient.setRequestURL("http://localhost:9999/rpc");
		xClient.setRequestMethod("User.say");
		
		ArrayList<Object> objects = new ArrayList<>();
		objects.add(2017);
		objects.add("hello world");
		/**
		 * set request parameter.
		 * when the method on server just has MORE THAN one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		xClient.setXmlClientParamsArray(objects);
		
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = xClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * <?xml version='1.0'?>
		 * <methodResponse><params><param>
		 * <value><string>2017,hello world!</string></value>
		 * </param></params></methodResponse>
		 */
	}
}
```
#### create a rpc-XML client, and call service "User.getBook"
```java
import java.util.Map;

public class Client {

	public static void main(String[] args) throws Exception {

		/**
		 * create a xml client
		 */
		XmlClient xClient = new XmlClient();
		xClient.setRequestURL("http://localhost:9999/rpc");
		xClient.setRequestMethod("User.getBook");
		
		ArrayList<Object> objects = new ArrayList<>();
		objects.add(2017);
		objects.add(true);
		objects.add("Linux");
		/**
		 * set request parameter.
		 * when the method on server just has MORE THAN one parameter,you can use this method
		 * the parameter's type should be just the java base type or String,
		 * but not byte([]) and customized!!!
		 */
		xClient.setXmlClientParamsArray(objects);
		
		/**
		 * send request
		 * @return map<statusCode,httpEntityString></BR>
		 * example : 200 , string </BR>
		 */
		Map<Integer, String> map = xClient.sendRequest();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		
		/**
		 * return result:
		 * 200
		 * <?xml version='1.0'?><methodResponse><params><param>
		 * <value><array><data>
		 * <value>true</value><value>2017</value>
		 * <value>Linux</value>
		 * </data></array></value>
		 * <param></params></methodResponse>
		 */
	}
}
```
