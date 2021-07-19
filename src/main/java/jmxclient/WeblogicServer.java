package jmxclient;

import java.util.Date;

public class WeblogicServer {
	
	private Date timestamp;
	private String servername;
	private String server_state;
	private int health;
	private long heap_free;
	private int thread_idle;
	private final String type = "weblogicServer";
	
	
	/**
	 * Constructs a WeblogicServer using given parameters. This object provides a unique, immutable, Weblogic server set of metrics.
	 * 
	 * @param timestamp
	 * @param servername Name of the server registered in the Weblogic host
	 * @param server_state State of the server registered in the Weblogic host
	 * @param health Health of the server registered in the Weblogic host
	 * @param heap_free Free Heap space of the server registered in the Weblogic host
	 * @param thread_idle Number of idle threads in the server registered in the Weblogic host
	 */
	public WeblogicServer(Date timestamp, String servername, String server_state, int health, long heap_free,int thread_idle) {
		super();
		this.timestamp = timestamp;
		this.servername = servername;
		this.server_state = server_state;
		this.health = health;
		this.heap_free = heap_free;
		this.thread_idle = thread_idle;
	}

	@Override
	public String toString() {
		return "WeblogicServer [timestamp=" + timestamp + ", servername=" + servername + ", server_state="
				+ server_state + ", health=" + health + ", heap_free=" + heap_free + ", thread_idle=" + thread_idle
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
	public int getHealth() {
		return health;
	}
	public long getHeap_free() {
		return heap_free;
	}
	public int getThread_idle() {
		return thread_idle;
	}
	public String getType() {
		return type;
	}
}

