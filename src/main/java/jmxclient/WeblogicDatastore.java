package jmxclient;

import java.util.Date;

public class WeblogicDatastore {

	private Date timestamp;
	private String servername;
	private String datastore_name;
	private int datastore_connections_totalcount;
	private int datastore_connections_currentcount;
	private int datastore_connections_highcount;
	private String datastore_state;
	private final String type = "weblogicDatastore";
	
	
	/**
	 * Constructs a WeblogicDatastore using given parameters. This object provides a unique, immutable, Weblogic datastore set of metrics.
	 * 
	 * @param timestamp
	 * @param servername Name of the server where the datastore is registered
	 * @param datastore_name Name of the datastore
	 * @param datastoreConnectionTotalCount Number of connections on this datastore
	 * @param datastoreActiveConnectionsCurrentCount Number of active connections on this datastore
	 * @param datastoreActiveConnectionsHighCount Highest number of active connections on this datastore
	 * @param datastore_state State of this datastore
	 */
	public WeblogicDatastore(Date timestamp, String servername, String datastore_name,int datastoreConnectionTotalCount, int datastoreActiveConnectionsCurrentCount,int datastoreActiveConnectionsHighCount, String datastore_state) {
		super();
		this.timestamp = timestamp;
		this.servername = servername;
		this.datastore_name = datastore_name;
		this.datastore_connections_totalcount = datastoreConnectionTotalCount;
		this.datastore_connections_currentcount = datastoreActiveConnectionsCurrentCount;
		this.datastore_connections_highcount = datastoreActiveConnectionsHighCount;
		this.datastore_state = datastore_state;
	}
	
	@Override
	public String toString() {
		return "WeblogicDatastore [timestamp=" + timestamp + ", servername=" + servername + ", datastore_name="
				+ datastore_name + ", datastore_connections_totalcount=" + datastore_connections_totalcount
				+ ", datastore_connections_currentcount=" + datastore_connections_currentcount
				+ ", datastore_connections_highcount=" + datastore_connections_highcount + ", datastore_state="
				+ datastore_state + ", type=" + type + "]";
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getServername() {
		return servername;
	}

	public String getDatastore_name() {
		return datastore_name;
	}

	public int getDatastore_connections_totalcount() {
		return datastore_connections_totalcount;
	}

	public int getDatastore_connections_currentcount() {
		return datastore_connections_currentcount;
	}

	public int getDatastore_connections_highcount() {
		return datastore_connections_highcount;
	}

	public String getDatastore_state() {
		return datastore_state;
	}

	public String getType() {
		return type;
	}
}
