package testDisruptor3;

import com.lmax.disruptor.RingBuffer;

public class Producer {
	private RingBuffer<Order> ringBuffer = null;
	public Producer(RingBuffer<Order> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	public void onData(String uuid) {
		long sequence = ringBuffer.next();
		try{
			Order order = ringBuffer.get(sequence);
			order.setId(uuid);
		}finally{
			ringBuffer.publish(sequence);
		}
	}

	

}
