package jmxclient;

import java.util.Date;

public class TomcatServer {
	
	private Date timestamp;
	private String servername;
	private String server_state;
	private long heap_free;
	private int thread_count;
	private final String type = "tomcatServer";
	
	
	/**
	 * Constructs a tomcatServer using given parameters. This object provides a unique, immutable, tomcat server set of metrics.
	 * 
	 * @param timestamp
	 * @param servername Name of the server registered in the tomcat host
	 * @param server_state State of the server registered in the tomcat host
	 * @param health Health of the server registered in the tomcat host
	 * @param heap_free Free Heap space of the server registered in the tomcat host
	 * @param thread_idle Number of idle threads in the server registered in the tomcat host
	 */
	public TomcatServer(Date timestamp, String servername, String server_state, long heap_free,int thread_idle) {
		super();
		this.timestamp = timestamp;
		this.servername = servername;
		this.server_state = server_state;
		this.heap_free = heap_free;
		this.thread_count = thread_idle;
	}

	@Override
	public String toString() {
		return "TomcatServer [timestamp=" + timestamp + ", servername=" + servername + ", server_state="
				+ server_state + ", heap_free=" + heap_free + ", thread_count=" + thread_count
				+ ", type=" + type + "]";
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public String getServername() {
		return servername;
	}
	public String getServer_state() {
		return server_state;
	}
	public long getHeap_free() {
		return heap_free;
	}
	public int getThread_idle() {
		return thread_count;
	}
	public String getType() {
		return type;
	}
}
