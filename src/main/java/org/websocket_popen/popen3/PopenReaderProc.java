package org.websocket_popen.popen3;

import java.nio.Buffer;

public interface PopenReaderProc {
	public void call(Buffer reader);
}
