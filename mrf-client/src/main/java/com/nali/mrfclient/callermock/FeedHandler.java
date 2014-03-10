package com.nali.mrfclient.callermock;

public class FeedHandler {

	public void onMessage(Feed feed) {
		System.out.println("FeedHandler处理消息:" + feed.getId() + "."
							+ feed.getCreator() + " " + feed.getName());
		
		
		// for no retry: retryInterval < 0 or retryTimes = 0
//		throw new IllegalArgumentException("no retry: faked illegal argument exception");
		
		// for immediate infinite retry: retryInterval = 0 and retryTimes < 0
//		throw new IllegalArgumentException("immediate infinite retry: faked illegal argument exception");
		
		// for interval infinite retry: retryInterval > 0 and retryTimes < 0
//		throw new IllegalArgumentException("interval infinite retry: faked illegal argument exception");
		
		// for immediate finite retry and recover: retryInterval = 0 and retryTimes > 0
//		throw new IllegalArgumentException("immediate finite retry: faked illegal argument exception");
		
		// for immediate finite retry and may succeed within retryTimes: retryInterval = 0 and retryTimes > 0(here I suggest make it 10)
/*		int i = (int) (Math.random()*2);
		if(i == 0) {
			throw new IllegalArgumentException("immediate finite retry and no recover: faked illegal argument exception");
		}*/
		
		// for interval finite retry and recover: retryInterval > 0 and retryTimes > 0
		throw new IllegalArgumentException("direct to recover: faked illegal argument exception");
		
		// for interval finite retry and no recover: retryInterval > 0 and retryTimes > 0
//		int i = (int) (Math.random()*2);
//		if(i == 0) {
//			throw new IllegalArgumentException("immediate finite retry and no recover: faked illegal argument exception");
//		}
	}

}

// for retry immediately, see applicationContex.xml for retry configuration
/*		int i = (int) (Math.random()*2);
if(i == 1) {
	throw new IllegalArgumentException("faked illegal argument exception");
}*/