package com.bfxy.client;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.TranslatorData;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		try{
//			TranslatorData response = (TranslatorData) msg;
//			System.out.println("Client端：id"+response.getId()
//			+",name="+response.getName()
//			+".message="+response.getMessage());
//		}finally{
//			//msg对象其实底层是缓存做的,所以一定要注意用完了缓存要进行释放
//			ReferenceCountUtil.release(msg);
//		}
		TranslatorData response = (TranslatorData) msg;
		String id = "ClientProducer:001";
		MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(id);
		messageProducer.onData(response, ctx);
	}
	

}
