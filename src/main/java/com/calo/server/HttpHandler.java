package com.calo.server;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleStateEvent;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	private Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		
		if (!request.method().toString().toLowerCase().equals("post")) {//post
			String content = "HTTP Method must be POST.";
			String contentType = "text/html; charset=UTF-8";
			commonResponse(ctx, request.protocolVersion(), HttpResponseStatus.METHOD_NOT_ALLOWED, content, contentType);
			return;
		}
		
		RpcServer rpcServer = new RpcServer();
		if (!request.uri().equals(rpcServer.getURI())) {//uri
			String content = "URI is wrong.";
			String contentType = "text/html; charset=UTF-8";
			commonResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK,content, contentType);
			return;
		}
		
		ByteBuf buf = request.content();
		byte[] contentBytes = new byte[buf.readableBytes()];
		buf.readBytes(contentBytes);
		
		logger.log(Level.INFO, "\nServer received:\n"+new String(contentBytes,"UTF-8"));
		
		String reqContentType = request.headers().get("content-type").toLowerCase();
		boolean json = reqContentType.contains("json");
		if (json) {
			JsonResolver JR = new JsonResolver(ctx, request);
			JR.response(new String(contentBytes,"UTF-8"), rpcServer);
		}
		
		boolean xml = reqContentType.contains("xml");
		if (xml) {
			XmlResolver XR = new XmlResolver(ctx, request);
			XR.response(new String(contentBytes,"UTF-8"), rpcServer);
		}
		
		if (!json || !xml) {
			String content = "Content-type is wrong!";
			String contentType = "text/html; charset=UTF-8";
			commonResponse(ctx, request.protocolVersion(), HttpResponseStatus.OK, content, contentType);
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		commonResponse(ctx, HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Server internal error", "text/plain;charset=utf-8");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		IdleStateEvent event = (IdleStateEvent)evt;
		switch (event.state()) {
		    case READER_IDLE:
		    	break;
		    case ALL_IDLE:
		    	ctx.channel().close();
		    	break;
		    default:	
		    	break;
		}
	}
	
	private void commonResponse(ChannelHandlerContext ctx, HttpVersion version,HttpResponseStatus status, String content, String contentType){
		FullHttpResponse response = new DefaultFullHttpResponse(version, status,Unpooled.wrappedBuffer(content.getBytes()));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
		ctx.channel().writeAndFlush(response);
		ctx.channel().close();
	}	
}
