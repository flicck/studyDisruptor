package com.bfxy.client;

import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.entity.TranslatorData;
import com.bfxy.entity.TranslatorDataWapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerForClient extends MessageConsumer{

	public MessageConsumerForClient(String ConsumerId) {
		super(ConsumerId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEvent(TranslatorDataWapper event) throws Exception {
		TranslatorData response = event.getData();
		ChannelHandlerContext ctx = event.getCtx();
		//业务逻辑处理
		try{
		
			System.out.println("Client端：id"+response.getId()
			+",name="+response.getName()
			+".message="+response.getMessage());
		}finally{
			ReferenceCountUtil.release(response);
		}
	}

}
