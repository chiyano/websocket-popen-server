package org.websocket_popen.server.restservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestService {
	private Result result = new Result();

	private Process ps = null;

	@Path("/{command}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Result index(@PathParam ("command") String command) throws IOException, InterruptedException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		// FIXME: move following block to another package
		List<String> list = Arrays.asList(command.split("/"));
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s).append("/");
		}
		sb.setLength(sb.length() - 1);
		String[] cmds = sb.toString().split("\\+");

		Thread stdoutThread = null;
		Thread stderrThread = null;

		try {
			ps = Runtime.getRuntime().exec(cmds);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ps.getOutputStream().close();
		stdoutThread = new Thread(new Runnable() {
			public void run() {
				InputStream is = ps.getInputStream();
				final byte[] buffer = new byte[1024];
				int length;
				try {
					while ((length = is.read(buffer)) > 0) {
						result.setStdout(result.getStdout() + new String(buffer, 0, length));
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
					int length;
					while ((length = is.read(buffer)) > 0) {
						result.setStderr(result.getStderr() + new String(buffer, 0, length));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		stdoutThread.start();
		stderrThread.start();
		
		ps.waitFor();
		
		result.setExitValue(ps.exitValue());

		return result;
	}
}
