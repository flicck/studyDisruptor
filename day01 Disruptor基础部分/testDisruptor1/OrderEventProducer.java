package testDisruptor1;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;

public class OrderEventProducer {
	private RingBuffer<OrderEvent> ringBuffer;
	public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer){
		this.ringBuffer = ringBuffer;	
	}
	public void sendData(ByteBuffer data){
		//1.在生产者发送消息的时候，首先需要从ringbuffer里面获得一个可用的序号
		long sequence=ringBuffer.next();;
		try{		
			//2.根据这个需要找到具体的"OrderEvent"元素
			OrderEvent orderEvent = ringBuffer.get(sequence);
			//3.此时这个对象是一个属性没有被赋值的对象,进行赋值处理就好啦
			orderEvent.setValue(data.getLong(0));	
		}finally{
			//4.还需要提交一下ringbuffer-->不管怎么样都提交
			ringBuffer.publish(sequence);
		}
		
	}
}
