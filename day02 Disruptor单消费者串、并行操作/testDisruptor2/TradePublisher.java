package testDisruptor2;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class TradePublisher implements Runnable {
	private Disruptor<Trade> disruptor;
	private CountDownLatch latch;
	private static int PUBLISH_COUNT=1;
	public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor){
		this.disruptor = disruptor;
		this.latch = latch;
	}
	@Override
	public void run() {
		//新的方式提交方式，以前是获得序号后获得对象用RingBuffer的publish提交的
		//也是官方推荐的提交方式,直接通过Translator造Event
		//多次提交，正常的话需要在Main里面写的
		for(int i=0;i<PUBLISH_COUNT;i++){
		disruptor.publishEvent(new TradeEventTranslator());
		}
		//让主线程接着往下走
		latch.countDown();
	}
}
class TradeEventTranslator implements EventTranslator<Trade>{
	private Random random = new Random();
	//Disruptor会给一个空的event和对应的序号
	@Override
	public void translateTo(Trade event, long sequence) {
		this.generateTrade(event);		
	}
	private void generateTrade(Trade event) {
		event.setPrice(random.nextDouble()*9999);
	}
		
}