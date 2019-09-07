package testDisruptor1;

import com.lmax.disruptor.EventHandler;

//监听者--就是消费者
public class OrderEventHandler implements EventHandler<OrderEvent> {

	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		//当有事件发过来的时候，就能被监听到
		System.out.println("消费者1:"+event.getValue());
	}
	
}
