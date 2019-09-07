package testDisruptor1;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class Main {
	public static void main(String[] args) {
		OrderEventFactory orf =new OrderEventFactory();
		int ringBufferSize = 1024*1024;
		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		/**
		 * 1、消息event工厂对象
		 * 2、容器的大小
		 * 3、executor-》自定义线程池 RejectedExecutionHandler 自定义线程池需要有拒绝策略
		 * 4、waitStrategy 阻塞的策略
		 */
		//1.实例化disruptor对象-->将消息通过工厂类投递给disruptor
		Disruptor<OrderEvent> disruptor = new Disruptor<>(orf, ringBufferSize, 
				threadPool,ProducerType.SINGLE,new BlockingWaitStrategy()); 
		//2.添加消费者的监听-->构建好消费者和disruptor的关联关系
		disruptor.handleEventsWith(new OrderEventHandler());
		//3.启动disruptor
		disruptor.start();
		//4.获取实际存储数据的容器-->ringbuffer disruptor中包含了ringbuffer，是一个辅助类
		RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
		OrderEventProducer producer = new OrderEventProducer(ringBuffer);
		ByteBuffer bb = ByteBuffer.allocate(8);
		for(long i=0;i<100;i++){
			bb.putLong(0,i);
			producer.sendData(bb);
		}
		disruptor.shutdown();
		threadPool.shutdown();
	}
}
