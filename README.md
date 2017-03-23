# easyRPC-xml-json
This is a new java RPC framework,it is easy to use than the other you find.
It support the way XML or JSON RPC to send request to server at the same time.

## What easyRPC based on?
Based on json-rpc protocol and xml-rpc protocol.</br>
Based on java Reflection,asynchronous programming,network programming</br>

## Why it is easy to use?
1.You don't have to know the servlet,http or web server in java.</br>
2.You just to know how to "getter()","setter()" and new an object!</br>
3.It is ZERO-configure!</br>

## How to use?
There I provided 3 jar in folder myjar in the project
`easy-rpc-all-in-one-1.0.jar`</br>
`easy-rpc-client-1.0.jar`</br>
`easy-rpc-server-1.0.jar`</br>
the `easy-rpc-all-in-one-1.0.jar` include `easy-rpc-client-1.0.jar` and `easy-rpc-server-1.0.jar`.</br>
If you just want a xml/json server,take the `easy-rpc-server-1.0.jar`,client as the same!
You can also directly copy the project to use!

#### define 2 class
`BooK.java`</br>
	<code>
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
</code>
</br>`User.java`</br>
<code>
	public class User {
	   public String say(int i , String st){
	      return String.valueOf(i) +","+ st+"!";
	   }
	   public Book getFromOb( int i, Boolean b, String s){
	      Book book = new  Book();
	      book.setId(i);
	      book.setName(s);
	      book.setB(b);
	      return book;
	   }
	}
</code>


	
