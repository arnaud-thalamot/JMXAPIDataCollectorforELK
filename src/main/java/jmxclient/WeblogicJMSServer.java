package jmxclient;

import java.util.Date;

public class WeblogicJMSServer {
	
	private Date timestamp;
	private String servername;
	private String jms_server_name;
	private long jms_destination_messages_pendingcount;
	private final String type = "weblogicJMSServer";
	
	/**
	 * Constructs a WeblogicJMSServer using given parameters. This object provides a unique, immutable, Weblogic JMS Server set of metrics.
	 * 
	 * @param timestamp
	 * @param servername Name of the server where the JMS server is registered
	 * @param jms_server_name Name of the JMS server
	 * @param jmsPendingMessages Number of pending messages to this JMS Server
	 */
	public WeblogicJMSServer(Date timestamp, String servername, String jms_server_name,long jmsPendingMessages) {
		super();
		this.timestamp = timestamp;
		this.servername = servername;
		this.jms_server_name = jms_server_name;
		this.jms_destination_messages_pendingcount = jmsPendingMessages;
	}
	
	@Override
	public String toString() {
		return "WeblogicJMSServer [timestamp=" + timestamp + ", servername=" + servername + ", jms_server_name="
				+ jms_server_name + ", jms_destination_messages_pendingcount=" + jms_destination_messages_pendingcount
				+ ", type=" + type + "]";
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public String getServername() {
		return servername;
	}
	public String getJms_server_name() {
		return jms_server_name;
	}
	public long getJms_destination_messages_pendingcount() {
		return jms_destination_messages_pendingcount;
	}
	public String getType() {
		return type;
	}
}
