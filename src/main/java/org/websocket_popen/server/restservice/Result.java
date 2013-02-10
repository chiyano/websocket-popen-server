package org.websocket_popen.server.restservice;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Result {
	private String stdout;
	private String stderr;
	private Integer exitValue;

	public String getStdout() {
		return stdout;
	}

	public String getStderr() {
		return stderr;
	}

	public Integer getExitValue() {
		return exitValue;
	}

	public void setStdout(String val) {
		this.stdout = val;
	}

	public void setStderr(String val) {
		this.stderr = val;
	}

	public void setExitValue(Integer val) {
		this.exitValue = val;
	}
}
