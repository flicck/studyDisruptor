package testDisruptor2;

import com.lmax.disruptor.EventHandler;

public class Handler4 implements EventHandler<Trade>{

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.err.println("handler 4:set price");
		event.setPrice(17.0);
		
	}

}
