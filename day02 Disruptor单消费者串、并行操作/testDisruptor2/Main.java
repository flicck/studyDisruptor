package testDisruptor2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		
		//构建一个线程池用于提交任务
		ExecutorService es = Executors.newFixedThreadPool(4);
		//注意：单消费者模式下，这个线程池要大于监听的handler的个数
		ExecutorService es2 = Executors.newFixedThreadPool(8);
		//1.构建Disruptor
		@SuppressWarnings("deprecation")
		Disruptor<Trade> disruptor = new Disruptor<>(
				new EventFactory<Trade>(){
					@Override
					public Trade newInstance() {					
						return new Trade();
						}
					},
					1024*1024,
					es2,
					ProducerType.SINGLE,
					new BusySpinWaitStrategy()
				);
		//2.把消费者设置到Disruptor中的handleEventsWith-多个消费者
		
		//2.1串行操作
//		disruptor
//			.handleEventsWith(new Handler1())
//			.handleEventsWith(new Handler2())
//			.handleEventsWith(new Handler3());
		//2.2并行操作
//		disruptor.handleEventsWith(new Handler1(),new Handler2(),new Handler3());
		//2.3菱形操作（一）,对一个对象同时进行Handler1和Handler2的操作，随后对这个对象进行Handler3的操作
		//类似CyclicBarrier
//		disruptor.handleEventsWith(new Handler1(),new Handler2())
//			.handleEventsWith(new Handler3());
		//2.3菱形操作（二） -->效果和1一样
//		EventHandlerGroup<Trade> ehGroup = disruptor.handleEventsWith(new Handler1(),new Handler2());
//		ehGroup.then(new Handler3());
		//2.4六边形操作	/h1->h2\    》》注意现在是单消费者模式，这里disruptor里面的线程池至少要有5个线程，因为有5个事件
		//             s        h3
		//			    \h4-->h5/
		Handler1 h1 = new Handler1();
		Handler2 h2 = new Handler2();
		Handler3 h3 = new Handler3();
		Handler4 h4 = new Handler4();
		Handler5 h5 = new Handler5();
		disruptor.handleEventsWith(h1,h4);
		disruptor.after(h1).handleEventsWith(h2);
		disruptor.after(h4).handleEventsWith(h5);
		disruptor.after(h2,h5).handleEventsWith(h3);
		
		
		//3.启动disruptor
		RingBuffer<Trade> ringBuffer = disruptor.start();
		
		CountDownLatch latch = new CountDownLatch(1);
		
		long begin = System.currentTimeMillis();
		
		//4.异步的提交任务
		es.submit(new TradePublisher(latch,disruptor));
		
		//主线程先堵塞着，等生产者让latch变为0了再走-->这个操作是比较耗时间的几百ms左右
		latch.await();
		es2.shutdown();
		disruptor.shutdown();
		es.shutdown();
		System.err.println("总耗时："+(System.currentTimeMillis()-begin));
	}
}
