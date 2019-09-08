package testDisruptor2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler1 implements EventHandler<Trade>,WorkHandler<Trade> {
	//针对EventHandler
	@Override
	public void onEvent(Trade event) throws Exception {
		this.onEvent(event);	
	}
	
	//针对WorkHandler
	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.err.println("handler 1 : set name");
		event.setName("H1");
		Thread.sleep(1000);
	}

}
