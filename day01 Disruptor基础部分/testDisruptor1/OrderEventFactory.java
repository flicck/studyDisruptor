package testDisruptor1;

import com.lmax.disruptor.EventFactory;

public class OrderEventFactory implements EventFactory<OrderEvent> {

	@Override
	public OrderEvent newInstance() {
		//这个方法就是为了返回空的event
		return new OrderEvent();
	}
	
}
