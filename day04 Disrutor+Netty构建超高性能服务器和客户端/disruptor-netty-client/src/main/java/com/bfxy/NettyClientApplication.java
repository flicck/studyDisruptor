package com.bfxy;


import com.bfxy.client.MessageConsumerForClient;
import com.bfxy.client.NettyClient;
import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;


public class NettyClientApplication {
	public static void main(String[] args) {
		MessageConsumer[] consumers=new MessageConsumer[4];
		for(int i =0;i<consumers.length;i++){
			String id ="ClientConsumer:"+i;
			MessageConsumer messageConsumer = new MessageConsumerForClient(id);
			consumers[i] = messageConsumer;
		}
		RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI
				, 1024*1024,
				new BlockingWaitStrategy(), consumers);
		new NettyClient().sendData();
		
	}
}
