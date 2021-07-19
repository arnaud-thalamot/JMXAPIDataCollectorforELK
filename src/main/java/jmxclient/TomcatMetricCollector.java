package jmxclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class TomcatMetricCollector {

	private final String hostname;
	private final String hostPort;
	private final String user;
	private final String password;
	private MBeanServerConnection connection;
	private JMXConnector jmxConnection = null;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * Constructs a TomcatMetricCollector using given parameters. This object provides a unique, immutable, connection.
	 * 
	 * @param hostname The hostname of the Tomcat server to connect, using JMX protocol
	 * @param hostPort The port of the Tomcat server to connect, using JMX protocol
	 * @param user The user of the Tomcat server to connect, using JMX protocol (must have proper access rights)
	 * @param password The password of the Tomcat server to connect, using JMX protocol
	 */
	public TomcatMetricCollector(String hostname, String hostPort, String user, String password) {
		Validate.notNull(hostname, "hostname can't be null");
		Validate.notNull(hostPort, "hostPort can't be null");
		Validate.notNull(user, "user can't be null");
		Validate.notNull(password, "password can't be null");
		this.hostname = hostname;
		this.hostPort = hostPort;
		this.user = user;
		this.password = password;
	}


	/**
	 * Open a connection to JMX Tomcat Server using constructor parameters. Use before using getMetrics method.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		if (hostname != null & hostPort != null & user != null & password != null) {
			LOGGER.info("####################");
			LOGGER.info("Request initiate on "+new Date());
			LOGGER.info("hostname = "+hostname);
			LOGGER.info("hostPort = "+hostPort);
			LOGGER.info("user = "+user);
			LOGGER.info("password = "+StringUtils.repeat("*", password.length()));

			JMXServiceURL serviceUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostname + ":" + hostPort
					+ "/jmxrmi");
			HashMap<String, String[]> env;

			String[] credentials = new String[2];
			credentials[0] = user;
			credentials[1] = password;
			env = new HashMap<String, String[]>();
			env.put(JMXConnector.CREDENTIALS, credentials);	

			jmxConnection = JMXConnectorFactory.newJMXConnector(serviceUrl, env);
			LOGGER.info("Connecting to "+hostname+"on port "+hostPort+" ...");			
			jmxConnection.connect();
			connection = jmxConnection.getMBeanServerConnection();
			LOGGER.info("Connection succesful");
		}
		else {
			throw new IllegalArgumentException("At least one the given parameter is missing or have null value");
		}
	}

	/**
	 * Given a successful JMX connection, retrieves various metrics stored in custom objects : TomcatServer, TomcatApp, TomcatDatastore, TomcatJMSServer . See object class for further details
	 * 
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ArrayList<Object> getMetrics() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {

		ArrayList<Object> list = null;

		// ********************************************************************
		// Retrieves data for each Tomcat server registered in the given host
		// ********************************************************************

		if (jmxConnection != null) {
			
			LOGGER.info("Getting Weblogic Metrics for hostname "+hostname);

			list = new ArrayList<Object>();

			ObjectName host = new ObjectName("Catalina:type=Host,host=localhost");

			String serverName = (String) connection.getAttribute(host, "name");
			String serverState = (String) connection.getAttribute(host, "stateName");

			ObjectName memory = new ObjectName("java.lang:type=Memory");

			CompositeData heapMemoryUsage = (CompositeData) connection.getAttribute(memory, "HeapMemoryUsage");

			long max_heap = (long) heapMemoryUsage.get("max");
			long used_heap = (long) heapMemoryUsage.get("used");

			long heapFree = max_heap - used_heap;

			ObjectName threading = new ObjectName("java.lang:type=Threading");
			int threadCount = (int) connection.getAttribute(threading, "ThreadCount");

			TomcatServer currentServer = new TomcatServer(new Date(), serverName, serverState,heapFree, threadCount);
			
			LOGGER.finest(currentServer.toString());
			list.add(currentServer);

			ObjectName application = new ObjectName("Catalina:type=Manager,host=localhost,context=/probe");

			String appName = (String) connection.getAttribute(application, "name");
			String state = (String) connection.getAttribute(application, "stateName");
			int activeSessions = (int) connection.getAttribute(application, "activeSessions");

			TomcatApp currentapp = new TomcatApp(new Date(), serverName, appName,state, activeSessions);

			LOGGER.finest(currentapp.toString());
			list.add(currentapp);

		}
		LOGGER.info("Tomcat Metrics retrieved successfully");
		return list;
	}

	/**
	 * Method to close the JMX connection to the given host. Always insure this method is called at the end of operations.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (jmxConnection != null)
			jmxConnection.close();
		LOGGER.info("Connection sucessfully closed");
		LOGGER.info("####################");
	}
}
