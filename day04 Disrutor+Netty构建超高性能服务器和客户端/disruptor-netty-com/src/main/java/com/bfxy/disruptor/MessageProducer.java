package com.bfxy.disruptor;

import com.bfxy.entity.TranslatorData;
import com.bfxy.entity.TranslatorDataWapper;
import com.lmax.disruptor.RingBuffer;

import io.netty.channel.ChannelHandlerContext;

public class MessageProducer {
	protected String producerId;
	private RingBuffer<TranslatorDataWapper> ringBuffer;
	public MessageProducer(String producerId,RingBuffer<TranslatorDataWapper> ringBuffer){
		this.ringBuffer = ringBuffer;
		this.producerId = producerId;
	}
	//生产一个wapper
	public void onData(TranslatorData data,ChannelHandlerContext ctx){
		long sequence = ringBuffer.next();
		try {
			TranslatorDataWapper wapper = ringBuffer.get(sequence);
			wapper.setData(data);
			wapper.setCtx(ctx);
		} finally {
			ringBuffer.publish(sequence);
		}
	}
}
