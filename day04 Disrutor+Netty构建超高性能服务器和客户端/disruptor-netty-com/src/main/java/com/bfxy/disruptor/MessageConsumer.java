package com.bfxy.disruptor;

import com.bfxy.entity.TranslatorDataWapper;
import com.lmax.disruptor.WorkHandler;

public abstract class MessageConsumer implements WorkHandler<TranslatorDataWapper>{
	protected String ConsumerId;
	public MessageConsumer(String ConsumerId){
		this.ConsumerId = ConsumerId;
	}
	public String getConsumerId() {
		return ConsumerId;
	}
	public void setConsumerId(String consumerId) {
		ConsumerId = consumerId;
	}
	
}
