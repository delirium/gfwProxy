package org.malstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class GfwProxyServer {
	private static final int[] ports = { 4444, 5555 };

	private Vector<ServerSocket> serverSocket;
	private List<Socket> sockets;
	private boolean running;

	public GfwProxyServer() {
		serverSocket = new Vector<ServerSocket>();
		sockets = new LinkedList<Socket>();

		for (int i = 0; i < ports.length; i++) {
			try {
				serverSocket.add(new ServerSocket(ports[i]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		running = true;
		for (int i = 0; i < serverSocket.size(); i++) {
			Thread receiverThread = new Thread(new ReceiverThreadRunner(
					serverSocket.get(i)));
			receiverThread.start();
		}
	}

	private synchronized void addSocket(Socket socket) {

		sockets.add(socket);
	}

	public synchronized boolean hasNewConnection() {
		return sockets.size() > 0;
	}

	public void shutDown() {
		running = false;
		for (int i = 0; i < serverSocket.size(); i++) {
			try {
				ServerSocket socket = serverSocket.get(i);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ReceiverThreadRunner implements Runnable {
		private ServerSocket socket;

		public ReceiverThreadRunner(ServerSocket s) {
			socket = s;
		}

		public void run() {
			System.out.println("Run Receiver thread... port "
					+ socket.getLocalPort());
			while (running) {
				try {
					if (running) {
						Socket s = socket.accept();
						System.out.println("Add new socket on port "
								+ s.getLocalPort() + "...");
						s.setKeepAlive(true);
						addSocket(s);
						while (running) {
							InputStream input = s.getInputStream();
							OutputStream output = s.getOutputStream();
							output.write(input.read());
						}
					}
				} catch (IOException e) {
					// e.printStackTrace();
				}
				// Thread.yield();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			System.out.println("Receiver stopped");
		}
	}

}