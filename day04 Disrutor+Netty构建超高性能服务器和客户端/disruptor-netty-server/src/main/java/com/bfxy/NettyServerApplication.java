package com.bfxy;



import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.server.MessageConsumerForServer;
import com.bfxy.server.NettyServer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;


public class NettyServerApplication {
	public static void main(String[] args) {
		MessageConsumer[] consumers=new MessageConsumer[4];
		for(int i =0;i<consumers.length;i++){
			String id ="ServerConsumer:"+i;
			MessageConsumer messageConsumer = new MessageConsumerForServer(id);
			consumers[i] = messageConsumer;
		}
		RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI
				, 1024*1024,
				new BlockingWaitStrategy(), consumers);
		new NettyServer();
		
	}
	
}
