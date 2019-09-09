package com.bfxy.server;
import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.entity.TranslatorData;
import com.bfxy.entity.TranslatorDataWapper;
public class MessageConsumerForServer extends MessageConsumer{

	public MessageConsumerForServer(String ConsumerId) {
		super(ConsumerId);
	}

	@Override
	public void onEvent(TranslatorDataWapper event) throws Exception {
		//实际业务处理
		TranslatorData request = event.getData();
		System.err.println("Server端"+request.getId()
		+"name="+request.getName()
		+",message="+request.getMessage());
		//回送响应
		TranslatorData response = new TranslatorData();
		response.setId("resp:"+request.getId());
		response.setName("resp:"+request.getName());
		response.setMessage("resp:"+request.getMessage());
		//写出response响应信息 ---》因为有写操作，所以无需释放msg，写操作的时候已经帮忙释放了
		event.getCtx().writeAndFlush(response);
	}

}
