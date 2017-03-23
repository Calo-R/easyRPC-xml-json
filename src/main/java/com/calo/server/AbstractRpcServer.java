package com.calo.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class AbstractRpcServer {

	public static volatile String URI = "/";
	public static Map<String, Class<?>> handlerMap = new ConcurrentHashMap<String,Class<?>>();
	protected int port = 8080;
	private Logger logger = Logger.getLogger(AbstractRpcServer.class.getName());
	/**
	 * DEFAULT 8080</BR>
	 */
	public abstract RpcServer bind(int port);
	
	public void start(){
		EventLoopGroup boss = new NioEventLoopGroup(3);
		EventLoopGroup worker = new NioEventLoopGroup(20);
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 3);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				ChannelPipeline pipeline = sc.pipeline();
				pipeline.addLast(new IdleStateHandler(0,0,30));
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new HttpObjectAggregator(1024*1024));
				pipeline.addLast(new HttpHandler());
			}
		});
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		try {
			Set<String> keySet = handlerMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			String methodList ="\n";
			while (iterator.hasNext()) {
				 methodList += iterator.next()+"\n";
			}
			bootstrap.bind(port).sync();
			logger.log(Level.INFO, 
					"\nServer registered following method:"+methodList+
					"\nServer listen on port:"+port+
					"\nServer URL:"+URI+
					"\n\nRPC SERVER START SUCCESS.\n");
		} catch (InterruptedException e) {
			
		}
	};
}
