package testDisruptor3;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class Main {
	public static void main(String[] args) {
		//1.创建一个RingBuffer（也可以用Disruptor的）（核心）
		RingBuffer<Order> ringBuffer =
				RingBuffer.create(ProducerType.MULTI, 
						new EventFactory<Order>(){
							@Override
							public Order newInstance() {							
								return new Order();
							}						
						}, 
						1024*1024, 
						new YieldingWaitStrategy());
		//2.通过ringbuffer创建一个屏障（核心）
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		//3.创建多消费者数组--10个消费者
		Consumer[] consumers = new Consumer[10];
		for(int i = 0;i < consumers.length;i++){
			consumers[i] = new Consumer("C"+i);
		}
		//4.构建多消费者工作池（核心）
		WorkerPool<Order> workerPool = new WorkerPool<>(
				ringBuffer,
				sequenceBarrier, 
				new EventExceptionHandler(), 
				consumers);
		//5.设置多个消费者的Sequence序号，用于单独统计消费进度（核心）
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		//6.启动workerPool-->消费者就启动了（核心）-->最好也要为10，不然会有两个消费者消费数量为0
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+2);
		workerPool.start(newFixedThreadPool);
		
		CountDownLatch latch = new CountDownLatch(1);
		//7.创建100个生产者
		for(int i=0;i<100;i++){
			Producer producer = new Producer(ringBuffer);
			new Thread(new Runnable(){
				@Override
				public void run() {	
					try{
					//每创建一个线程都在这儿先阻塞着-->搞100只生产者先关起来
						latch.await();
					}catch(Exception e){
						e.printStackTrace();
					}
					//每一个生产者生产100块肉
					for(int j=0;j<100;j++){
						producer.onData(UUID.randomUUID().toString());
					}
				}			
			}).start();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("---线程创建完毕，开始");
		//开门放生产者
		latch.countDown();
		
		try {
		//主线程等待执行-->当然如果之前的100只生产者是线程池里的，可以shutdown线程池（不允许增加新任务，已有线程继续跑），然后
		//主线程循环awaitTemination，优雅结束主线程
		//这里为了程序简洁，先这么写了
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		newFixedThreadPool.shutdown();
		//随便牵一只消费者，看它一共吃了多少块肉了
		System.err.println("消费者0一共处理的任务总数："+consumers[0].getCount());
		System.err.println("消费者1一共处理的任务总数："+consumers[1].getCount());
		System.err.println("消费者2一共处理的任务总数："+consumers[2].getCount());
		System.err.println("消费者3一共处理的任务总数："+consumers[3].getCount());
		System.err.println("消费者4一共处理的任务总数："+consumers[4].getCount());
		System.err.println("消费者5一共处理的任务总数："+consumers[5].getCount());
		System.err.println("消费者6一共处理的任务总数："+consumers[6].getCount());
		System.err.println("消费者7一共处理的任务总数："+consumers[7].getCount());
		System.err.println("消费者8一共处理的任务总数："+consumers[8].getCount());
		System.err.println("消费者9一共处理的任务总数："+consumers[9].getCount());
	}
	
	static class EventExceptionHandler implements ExceptionHandler<Order>{
		//消费中异常
		@Override
		public void handleEventException(Throwable ex, long sequence, Order event) {
		}
		//启动时异常
		@Override
		public void handleOnStartException(Throwable ex) {
		}
		//关闭时异常
		@Override
		public void handleOnShutdownException(Throwable ex) {		
		}
		
	}
}
