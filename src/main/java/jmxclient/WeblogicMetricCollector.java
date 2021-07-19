package jmxclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import weblogic.health.HealthState;

public class WeblogicMetricCollector {

	private final String hostname;
	private final String hostPort;
	private final String user;
	private final String password;
	private MBeanServerConnection connection;
	private JMXConnector jmxConnection = null;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * Constructs a WeblogicMetricCollector using given parameters. This object provides a unique, immutable, connection.
	 * 
	 * @param hostname The hostname of the Weblogic server to connect, using JMX protocol
	 * @param hostPort The port of the Weblogic server to connect, using JMX protocol
	 * @param user The user of the Weblogic server to connect, using JMX protocol (must have proper access rights)
	 * @param password The password of the Weblogic server to connect, using JMX protocol
	 */
	public WeblogicMetricCollector(String hostname, String hostPort, String user, String password) {
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
	 * Open a connection to JMX Weblogic Server using constructor parameters. Use before using getMetrics method.
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

			JMXServiceURL serviceUrl = new JMXServiceURL("service:jmx:iiop://" + hostname + ":" + hostPort
					+ "/jndi/weblogic.management.mbeanservers.domainruntime");
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
			env.put(javax.naming.Context.SECURITY_PRINCIPAL, user);
			env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);

			//System.setProperty("javax.net.ssl.keyStore","DemoIdentity.jks");
			//System.setProperty("javax.net.ssl.keyStoreType", "JKS");
			//System.setProperty("javax.net.ssl.keyStorePassword", "storepass");
			//System.setProperty("javax.net.ssl.trustStore","DemoTrust.jks");
			//System.setProperty("javax.net.ssl.trustStoreType", "JKS");
			//System.setProperty("javax.net.ssl.trustStorePassword", "storepass");
			//env.put("com.sun.jndi.rmi.factory.socket", new SslRMIClientSocketFactory());

			jmxConnection = JMXConnectorFactory.newJMXConnector(serviceUrl, env);
			LOGGER.info("Connecting to "+hostname+" on port "+hostPort+" ...");			
			jmxConnection.connect();
			connection = jmxConnection.getMBeanServerConnection();
			LOGGER.info("Connection successfull");
		}
		else {
			throw new IllegalArgumentException("At least one the given parameter is missing or have null value");
		}
	}

	/**
	 * Given a successful JMX connection, retrieves various metrics stored in custom objects : WeblogicServer, WeblogicApp, WeblogicDatastore, WeblogicJMSServer . See object class for further details
	 * 
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ArrayList<Object> getMetrics() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {


		// ********************************************************************
		// Retrieves data for each Weblogic server registered in the given host
		// ********************************************************************

		ArrayList<Object> list = null;

		if (jmxConnection != null) {

			LOGGER.info("Getting Weblogic Metrics for hostname "+hostname);

			list = new ArrayList<Object>();

			ObjectName service = new ObjectName(
					"com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
			ObjectName[] servers = (ObjectName[]) connection.getAttribute(service, "ServerRuntimes");

			for (int i = 0; i < servers.length; i++) {
				String serverName = (String) connection.getAttribute(servers[i], "Name");
				String serverState = (String) connection.getAttribute(servers[i], "State");
				HealthState serverHealthState = (HealthState) connection.getAttribute(servers[i], "HealthState");
				int serverHealth = serverHealthState.getState();
				ObjectName jvmRuntime = (ObjectName) connection.getAttribute(servers[i], "JVMRuntime");
				long heap_free = (long) connection.getAttribute(jvmRuntime, "HeapFreeCurrent");
				ObjectName threadPoolRuntime = (ObjectName) connection.getAttribute(servers[i], "ThreadPoolRuntime");
				int threadIdle = (int) connection.getAttribute(threadPoolRuntime, "ExecuteThreadIdleCount");

				WeblogicServer currentServer = new WeblogicServer(new Date(), serverName, serverState, serverHealth,
						heap_free, threadIdle);
				LOGGER.finest(currentServer.toString());
				list.add(currentServer);

				ObjectName domainRuntime = (ObjectName) connection.getAttribute(service, "DomainRuntime");
				ObjectName apps = (ObjectName) connection.getAttribute(domainRuntime, "AppRuntimeStateRuntime");
				String[] appIds = (String[]) connection.getAttribute(apps, "ApplicationIds");
				ObjectName[] appRuntimes = (ObjectName[]) connection.getAttribute(servers[i], "ApplicationRuntimes");

				// **************************************************************************************
				// Retrieves current state for each application ID registered and stores it into a HashMap
				// **************************************************************************************
				HashMap<String, String> states = new HashMap<String, String>();
				for (int x = 0; x < appIds.length; x++) {
					String app_name = (appIds[x].split("#"))[0];
					String state = (String) connection.invoke(apps, "getCurrentState",
							new String[] { appIds[x], currentServer.getServername() },
							new String[] { "java.lang.String", "java.lang.String" });
					states.put(app_name, state);
				}

				// ***********************************************************************************************
				// Retrieves data for each Weblogic application contained in the HashMap retrieved before
				// Because all applications in ApplicationRuntimes are not necessarily a WebApplication
				// ***********************************************************************************************
				for (int x = 0; x < appRuntimes.length; x++) {

					try {
						String appliName = (String) connection.getAttribute(appRuntimes[x], "Name");

						ObjectName[] componentRuntimes = (ObjectName[]) connection.getAttribute(appRuntimes[x],
								"ComponentRuntimes");

						for (int j = 0; j < componentRuntimes.length; j++) {

							String appName = (String) connection.getAttribute(appRuntimes[x], "Name");
							String componentType = (String) connection.getAttribute(componentRuntimes[j], "Type");

							if (componentType.toString().equals("WebAppComponentRuntime")){

								int openSessions = (int)connection.getAttribute(componentRuntimes[j],"OpenSessionsCurrentCount");
								String state = states.get(appliName);
								WeblogicApp currentapp = new WeblogicApp(new Date(), currentServer.getServername(), appName,
										state, openSessions);
								LOGGER.finest(currentapp.toString());
								list.add(currentapp);
							}
						}
					} catch (Exception e) {
						// Nothing to do
					}

					ObjectName jdbcRuntime = (ObjectName) connection.getAttribute(servers[i], "JDBCServiceRuntime");
					ObjectName[] datastores = (ObjectName[]) connection.getAttribute(jdbcRuntime,
							"JDBCDataSourceRuntimeMBeans");

					// *******************************************************************************
					// Retrieves datastores for each Weblogic application registered in the given host
					// *******************************************************************************

					for (int k = 0; k < datastores.length; k++) {
						String datastoreName = (String) connection.getAttribute(datastores[k], "Name");
						String datastoreState = (String) connection.getAttribute(datastores[k], "State");
						int datastoreConnectionTotalCount = (int) connection.getAttribute(datastores[k],
								"ConnectionsTotalCount");
						int datastoreActiveConnectionsCurrentCount = (int) connection.getAttribute(datastores[k],
								"ActiveConnectionsCurrentCount");
						int datastoreActiveConnectionsHighCount = (int) connection.getAttribute(datastores[k],
								"ActiveConnectionsHighCount");
						WeblogicDatastore currentDatastore = new WeblogicDatastore(new Date(),
								currentServer.getServername(), datastoreName, datastoreConnectionTotalCount,
								datastoreActiveConnectionsCurrentCount, datastoreActiveConnectionsHighCount,
								datastoreState);
						LOGGER.finest(currentDatastore.toString());
						list.add(currentDatastore);
					}

					ObjectName jmsRuntime = (ObjectName) connection.getAttribute(servers[i], "JMSRuntime");
					ObjectName[] jmsServers = (ObjectName[]) connection.getAttribute(jmsRuntime, "JMSServers");

					// ********************************************************************************
					// Retrieves destinations for each Weblogic JMS server registered in the given host
					// ********************************************************************************

					for (int l = 0; l < jmsServers.length; l++) {
						ObjectName[] destinations = (ObjectName[]) connection.getAttribute(jmsServers[l],
								"Destinations");
						for (int m = 0; m < destinations.length; m++) {
							long jmsPendingMessages = (long) connection.getAttribute(destinations[m],
									"MessagesPendingCount");
							String jmsName = (String) connection.getAttribute(destinations[m], "Name");
							WeblogicJMSServer currentJMSServer = new WeblogicJMSServer(new Date(),
									currentServer.getServername(), jmsName, jmsPendingMessages);
							LOGGER.finest(currentJMSServer.toString());
							list.add(currentJMSServer);
						}
					}
				}
			} 
		}
		LOGGER.info("Weblogic Metrics retrieved successfully");
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
		LOGGER.info("####################\n\n");
	}
}
