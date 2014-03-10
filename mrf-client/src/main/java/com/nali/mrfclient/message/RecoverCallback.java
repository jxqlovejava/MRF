package com.nali.mrfclient.message;

/**
 * Client may implement this interface to do something when reach max retry times and to do recover, such as:
 * <li>do some logging</li>
 * <li>notify user(more user friendly)</li>
 * @author will
 *
 */
public interface RecoverCallback {

	public void doRecoverCallback(MessageContext context);
	  
}
