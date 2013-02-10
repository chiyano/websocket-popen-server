package org.websocket_popen.server;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class PopenServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		// FIXME: move following block to another package
		List<String> list = Arrays.asList(request.getPathInfo().split("/"));
		
		list = list.subList(1, list.size());
		
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s).append("/");
		}
		sb.setLength(sb.length() - 1);
		
		String[] cmds = sb.toString().split("\\+");
		
		return new PopenWebSocket(cmds);
	}

}
