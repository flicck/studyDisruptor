package testDisruptor3;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;
//多消费者一定要实现WorkHandler接口
public class Consumer implements WorkHandler<Order> {
	private String consumerId;
	private int count = 0;
	private Random r = new Random();
	public Consumer(String consumerId){
		this.consumerId = consumerId;
	}
	@Override
	public void onEvent(Order event) throws Exception {
		Thread.sleep(1*r.nextInt(5));
		System.out.println("当前消费者："+this.consumerId
				+",消费信息："+event.getId());
		count++;		
	}
	public int getCount(){
		return count;
	}
}
