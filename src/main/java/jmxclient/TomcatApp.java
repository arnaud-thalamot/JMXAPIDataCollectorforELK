package jmxclient;

import java.util.Date;

public class TomcatApp {

	private Date timestamp;
	private String servername;
	private String appname;
	private String app_state;
	private int activeSessions;
	private final String type = "tomcatApp";	
	
	/**
	 * Constructs a tomcatApp using given parameters. This object provides a unique, immutable, tomcat application set of metrics.
	 * 
	 * @param timestamp
	 * @param servername Name of the server where the application is registered
	 * @param appname Name of the application
	 * @param app_state State of the application. See tomcat documentation on ApplicationStateRuntimeMBean.getCurrentState() for possible values.
	 * @param opensessions_currentcount Number of currently open sessions for the application.
	 */
	public TomcatApp(Date timestamp, String servername, String appname, String app_state,
			int opensessions_currentcount) {
		super();
		this.timestamp = timestamp;
		this.servername = servername;
		this.appname = appname;
		this.app_state = app_state;
		this.activeSessions = opensessions_currentcount;
	}



	@Override
	public String toString() {
		return "TomcatApp [timestamp=" + timestamp + ", servername=" + servername + ", appname=" + appname
				+ ", app_state=" + app_state + ", active_sessions=" + activeSessions + "]";
	}



	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getServername() {
		return servername;
	}
	public void setServername(String servername) {
		this.servername = servername;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getApp_state() {
		return app_state;
	}
	public void setApp_state(String app_state) {
		this.app_state = app_state;
	}
	public int getOpensessions_currentcount() {
		return activeSessions;
	}
	public void setOpensessions_currentcount(int i) {
		this.activeSessions = i;
	}
	public String getType() {
		return type;
	}
	
	
}
