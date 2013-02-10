package org.websocket_popen.popen3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.util.Arrays;

public class Popen3 {
	private String[] cmds;
	private PopenWriterProc stdinProc;
	private PopenReaderProc stdoutProc;
	private PopenReaderProc stderrProc;
	private Process process;
	private Thread stdoutThread;
	private Thread stderrThread;
	private Thread stdinThread;
	private BufferedWriter stdinWriter;
	private BufferedReader stdoutReader;
	private BufferedReader stderrReader;
	private boolean isStarted = false;
	private volatile boolean isRunning = false;

	public Popen3(String[] cmds, PopenWriterProc stdinProc, PopenReaderProc stdoutProc, PopenReaderProc stderrProc) {
		this.cmds = cmds;
		this.stdinProc = stdinProc;
		this.stdoutProc = stdoutProc;
		this.stderrProc = stderrProc;
	}

	public Popen3(String[] cmds, PopenReaderProc stdoutProc, PopenReaderProc stderrProc) {
		this.cmds = cmds;
		this.stdinProc = null;
		this.stdoutProc = stdoutProc;
		this.stderrProc = stderrProc;
	}

	public void start() throws IOException, InterruptedException {
		if (isStarted) return;
		
		isStarted = true;
		isRunning = true;
		
		process = Runtime.getRuntime().exec(cmds);

		if (stdinProc != null) {
			stdinWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			stdinThread = new Thread(new Runnable() {
				public void run() {
					try {
						while (isProcessRunning() && !Thread.interrupted()) {
							stdinProc.call(stdinWriter);
						}
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});		
		}

		if (stdoutProc != null) {
			stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			stdoutThread = new Thread(new Runnable() {
				CharBuffer buf = CharBuffer.allocate(1024);
				
				public void run() {
					while (!Thread.interrupted()) {
						try {
							if (stdoutReader.ready()) {
								if (stdoutReader.read(buf) > -1) {
									stdoutProc.call(buf.flip());
									buf.clear();
								} else {
									break;
								}
							}
							if (!isRunning) {
								break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}

		if (stderrProc != null) {
			stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			stderrThread = new Thread(new Runnable() {
				CharBuffer buf = CharBuffer.allocate(1024);

				public void run() {
					while (!Thread.interrupted()) {
						try {
							if (stderrReader.ready()) {
								if (stderrReader.read(buf) > -1) {
									stderrProc.call(buf.flip());
									buf.clear();
								} else {
									break;
								}
							}
							if (!isRunning) {
								break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}

		startAll();
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public boolean isProcessRunning() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<? extends Process> klass = process.getClass();
		Field field = klass.getDeclaredField("hasExited");
		field.setAccessible(true);
		return !field.getBoolean(process);
	}

	public void close() throws IOException, InterruptedException {
		if (process != null) {
			process.getOutputStream().close();
			process.waitFor();
		}
		isRunning = false;
	}
	
	public void waitFor() throws InterruptedException {
		if (process != null)
			process.waitFor();
		joinAll();
	}
	
	public Process getProcess() {
		return process;
	}

	private void startAll() {
		for (Thread t : Arrays.asList(stdoutThread, stderrThread, stdinThread))
			if (t != null) t.start();
	}

	private void joinAll() throws InterruptedException {
		for (Thread t : Arrays.asList(stdoutThread, stderrThread, stdinThread))
			if (t != null) t.join();
	}

	public void write(String s) throws IOException {
		if (stdinWriter == null) {
			stdinWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		}
		stdinWriter.write(s);
		stdinWriter.flush();
	}

}
