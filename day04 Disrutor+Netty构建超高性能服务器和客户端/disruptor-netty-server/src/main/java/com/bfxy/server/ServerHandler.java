package com.bfxy.server;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.TranslatorData;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
//这里可能有数据持久化操作-->很影响workgroup的性能-->交给一个线程池去异步的调用执行
public class ServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		
//		TranslatorData request = (TranslatorData) msg;
//		System.err.println("Server端"+request.getId()
//		+"name="+request.getName()
//		+",message="+request.getMessage());
//		TranslatorData response = new TranslatorData();
//		response.setId("resp:"+request.getId());
//		response.setName("resp:"+request.getName());
//		response.setMessage("resp:"+request.getMessage());
//		//写出response响应信息 ---》因为有写操作，所以无需释放msg，写操作的时候已经帮忙释放了
//		ctx.writeAndFlush(response);
		TranslatorData request = (TranslatorData) msg;
		//自己的应用服务应该有一个id生成规则
		String producerId = "ServerProducer:001";
		MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
		//把数据扔进去，让生产者生产数据
		messageProducer.onData(request, ctx);
		
	}
}


