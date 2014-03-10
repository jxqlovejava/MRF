package com.nali.mrfclient.callermock;

public class FeedHandler2 {

	public void onMessage(Feed feed) {
		System.out.println("FeedHandler2处理来自feedQ2的消息: " + feed.getId() + "."
				+ feed.getCreator() + "." + feed.getName());
		
		// used to test interval retry message retry, nomrally will succeed within max retry times
		int i = (int) (Math.random()*2);
		if(i == 1) {
			throw new IllegalArgumentException("faked illegal argument exception");
		}
		
		// used to test interval retry message retry and recover
//		throw new IllegalArgumentException("faked illegal argument exception");
	}

}