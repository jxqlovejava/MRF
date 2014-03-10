package com.nali.mrfclient.callermock;

import com.nali.mrfclient.message.MessageContext;
import com.nali.mrfclient.message.RecoverCallback;

public class MyRecoverCallback implements RecoverCallback {
	
	@Override
	public void doRecoverCallback(MessageContext context) {
		System.out.println("Do recover callback: " + context.getMessage());
	}

}
