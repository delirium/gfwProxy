/**
 * 
 */
package org.malstream;

/**
 * @author delirium
 * 
 */
public class GfwProxyServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GfwProxyServer server = new GfwProxyServer();
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.shutDown();
	}

}
