package org.websocket_popen.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocket;


public class PopenWebSocket implements WebSocket.OnTextMessage {
	private final Logger log = Log.getLogger(getClass());

	private Process ps = null;

	private Thread stdoutThread = null;
	private Thread stderrThread = null;

	private Connection con = null;

	private PrintWriter stdin;

	private boolean isStarted;

	public PopenWebSocket(String[] cmds) {
		try {
			ps = Runtime.getRuntime().exec(cmds);
		} catch (IOException e) {
			e.printStackTrace();
		}
		stdin = new PrintWriter(ps.getOutputStream());
		stdoutThread = new Thread(new Runnable() {
			public void run() {
				InputStream is = ps.getInputStream();
				final byte[] buffer = new byte[1024];
				int length;
				try {
					while ((length = is.read(buffer)) > 0) {
						String mesg = new String(buffer, 0, length);
						log.debug("send:" + mesg);
						con.sendMessage(mesg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		stderrThread = new Thread(new Runnable() {
			public void run() {
				InputStream is = ps.getErrorStream();
				final byte[] buffer = new byte[1024];
				try {
					while (is.read(buffer) > 0) {
						con.sendMessage(new String(buffer));
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
			ps.getOutputStream().close();
			ps.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onOpen(Connection conn) {
		log.debug("Connection associated");
		con = conn;
	}

	public void onMessage(String mesg) {
		log.debug(mesg + "\" received");

		try {
			if (!isStarted) {
				start();
			}

			char c[] = mesg.toCharArray();
			
			if (c.length > 0 && c[0] == '\0') {
				// Close popen3 when the first charactor of 
				// received message is '\0' assumed as EOF.
				log.debug("Pseudo EOF received");
				ps.getOutputStream().close();
				ps.waitFor();
				con.close();
			} else {
				if (isProcessRunning()) {
					stdin.print(mesg);
					stdin.flush();
				} else {
					ps.getOutputStream().close();
					ps.waitFor();
					con.close();		
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private void start() {
		stdoutThread.start();
		stderrThread.start();
		isStarted = true;
	}

	private boolean isProcessRunning() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<? extends Process> klass = ps.getClass();
		Field field = klass.getDeclaredField("hasExited");
		field.setAccessible(true);
		return !field.getBoolean(ps);
	}

}
