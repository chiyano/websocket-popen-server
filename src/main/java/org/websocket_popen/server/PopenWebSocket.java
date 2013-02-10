package org.websocket_popen.server;

import java.io.IOException;
import java.nio.Buffer;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocket;

import org.websocket_popen.popen3.Popen3;
import org.websocket_popen.popen3.PopenReaderProc;

public class PopenWebSocket implements WebSocket.OnTextMessage {
	private Connection connection = null;
	private Popen3 popen3;
	private final Logger log = Log.getLogger(getClass());

	public PopenWebSocket(String[] cmds) {
		popen3 = new Popen3(cmds, new PopenReaderProc() {

			public void call(Buffer buf) {
				String s = buf.toString();
				try {
					log.debug("Message \"" + s + "\" received from popen3 stdout");
					connection.sendMessage(s);
					try {
						if (!popen3.isProcessRunning()) {
							popen3.close();
							connection.close();
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, new PopenReaderProc() {

			public void call(Buffer buf) {
				String s = buf.toString();
				try {
					log.debug("Message \"" + s + "\" received from popen3 stderr");
					connection.sendMessage(s);
					try {
						if (!popen3.isProcessRunning()) {
							popen3.close();
							connection.close();
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void onClose(int arg0, String arg1) {
		log.debug("Connection closed");
		try {
			log.debug("Close popen3");
			popen3.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onOpen(Connection conn) {
		log.debug("Connection associated");
		connection = conn;
	}

	public void onMessage(String mesg) {
		log.debug("Message \"" + mesg + "\" received");
		try {
			// Start popen3 threads when the first message received.
			if (!popen3.isStarted()) {
				popen3.start();
			}

			char c[] = mesg.toCharArray();
			if (c.length > 0 && c[0] == '\0') {
				// Close popen3 when the first charactor of 
				// received message is '\0' assumed as EOF.
				log.debug("Pseudo EOF received");
				popen3.close();
				connection.close();
			} else {
				if (popen3.isProcessRunning()) {
					log.debug("Write message \"" + mesg + "\" to popen3");
					popen3.write(mesg);
				} else {
					popen3.close();
					connection.close();
				}
			}
		} catch (IOException e) {
			log.debug("IOException received");
			try {
				// Close popen3 correctly because the process popen 
				// started is no longer running.
				log.debug("Close popen3");
				popen3.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			log.debug("Close connection");
			connection.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
