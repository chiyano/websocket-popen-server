package org.websocket_popen.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class Main {

	private final Logger log = Log.getLogger(getClass());

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}

	public Main() throws Exception {
		Server server = new Server(9999);
		server.setStopAtShutdown(true);
		server.setGracefulShutdown(1000);

		ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

		root.setResourceBase("./");
		root.addServlet(DefaultServlet.class, "/*");

		ServletHolder holder = new ServletHolder(new PopenServlet());
		root.addServlet(holder, "/ws/*");

		log.info("Start server");

		server.start();
		server.join();
		
		log.info("Done");
	}

}
