package com.bfxy.client;

import com.bfxy.codec.MarshallingCodeCFactory;
import com.bfxy.entity.TranslatorData;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient {
	public static final String HOST ="127.0.0.1";
	public static final int PORT = 8765;
	//以后记得要池化channel--》放到一个ConcurrentHashMap中  ConcurrentHashMap<key->String,value->Channel>
	private Channel channel = null;
	private EventLoopGroup workGroup =new NioEventLoopGroup();;
	private ChannelFuture cf =null;
	public NettyClient(){
		this.connect(HOST,PORT);
	}
	private void connect(String host, int port) {
		//1.一个用于实际处理业务的线程组-->客户端没有sync和accept队列
		 //见全局变量
		//2.建立辅助类-->类似disruptor类-->辅助类改为Bootsrap
		Bootstrap bootstrap = new Bootstrap();
		
		try {
			bootstrap.group(workGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)  //自适应每次的数据的大小
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)     //表示缓冲池--用到buffer直接从池里取-->提升性能
			.handler(new LoggingHandler(LogLevel.INFO))			//配置日志
			.handler(new ChannelInitializer<SocketChannel>() {	//处理实际的数据--》接收到数据并异步处理,但是不要在这里写业务逻辑代码，会影响性能
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					
					sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					sc.pipeline().addLast(new ClientHandler());		
				}		
			});		
			
			//3.建立连接--》可以connect到不同端口获得不同channel
			this.cf = bootstrap.connect(host,port).sync();
			System.err.println("Client connected...");
			//接下来进行数据的发送,首先要获取通道--》如果这里给channel一个key（id）和value（对应的channel），然后缓存到一个ConcurrentHashMap
			//中可以实现channel的池化，这样可以多线程操作，channel也可以
			this.channel = cf.channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			
		}

	}
	public void sendData(){
		
		for(int i=0;i<10;i++){
			TranslatorData data = new TranslatorData();
			data.setId(""+i);
			data.setName("请求消息名称"+i);
			data.setMessage("请求消息内容"+i);
			this.channel.writeAndFlush(data);
		}
	}
	public void close() throws Exception{
		cf.channel().closeFuture().sync();
		//优雅停机
		workGroup.shutdownGracefully();
		System.out.println("Server ShutDown...");
	}
}
