package org.websocket_popen.popen3;

import java.io.BufferedWriter;

public interface PopenWriterProc {
	public void call(BufferedWriter writer);
}
