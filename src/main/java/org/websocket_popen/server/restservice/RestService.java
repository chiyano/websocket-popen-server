package org.websocket_popen.server.restservice;

import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.websocket_popen.popen3.Popen3;
import org.websocket_popen.popen3.PopenReaderProc;

@Path("/")
public class RestService {
	private Result result = new Result();

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
		
		Popen3 popen3 = new Popen3(cmds, new PopenReaderProc() {
			public void call(Buffer buf) {
				result.setStdout(buf.toString());
			}
		}, new PopenReaderProc() {

			public void call(Buffer buf) {
				result.setStderr(buf.toString());
			}
		});

		popen3.startWithoutStdin();
		
		popen3.join();
		
		popen3.close();
		
		result.setExitValue(popen3.getProcess().exitValue());
		
		return result;
	}
}
