package com.nali.mrfclient.callermock;

public class ReadHandler {
	
	public void onMessage(Read read) {
		System.out.println("ReadHandler处理来自readQ的消息：" + read.getId() + " " + read.getReader());
	}

}
