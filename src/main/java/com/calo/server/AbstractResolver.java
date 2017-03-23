package com.calo.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public abstract class AbstractResolver {

	//return Tag
	protected static final int TAG_SINGLE = 1;//CHAR,SHORT,INT,LONG,DOUBLE,FLOAT,BYTE,BOOLEAN--> raw data, do nothing
	protected static final int TAG_OBJECT = 2;//json object
	protected static final int TAG_ARRAY = 3;//json array
	protected ChannelHandlerContext ctx;
	protected FullHttpRequest request;
	
	/**
	 * return response</BR>
	 */
	public abstract void response(String requestContent, RpcServer rpcServer) throws Exception;
	
	
	public static void returnResponse(ChannelHandlerContext ctx,HttpVersion version, HttpResponseStatus status, String content, String contentType) {
		FullHttpResponse response = new DefaultFullHttpResponse(version, status,Unpooled.wrappedBuffer(content.getBytes()));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		ctx.channel().writeAndFlush(response);
		ctx.channel().close();
	}
	
	public static int getReturnTypeTag(String name) {

		if (name.contains("char")) {
			return TAG_SINGLE;
		}
		if (name.contains("short")) {
			return TAG_SINGLE;
		}
		if (name.contains("int")) {
			return TAG_SINGLE;
		}
		if (name.contains("long")) {
			return TAG_SINGLE;
		}
		if (name.contains("double")) {
			return TAG_SINGLE;
		}
		if (name.contains("float")) {
			return TAG_SINGLE;
		}
		if (name.contains("bool")) {
			return TAG_SINGLE;
		}
		if (name.contains("byte")) {
			return TAG_SINGLE;
		}
		if (name.contains("string")) {
			return TAG_SINGLE;
		}
		if (name.contains("map")) {
			return TAG_OBJECT;
		}
		if (name.contains("list")) {
			return TAG_ARRAY;
		}
		return TAG_OBJECT;
	}
}
