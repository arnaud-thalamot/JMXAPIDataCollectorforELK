package jmxclient;

import static spark.Spark.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) throws SecurityException, IOException {

		LOGGER.setLevel(Level.INFO);
		FileHandler weblogicLog = new FileHandler("getWeblogicMetrics.log", true);
		SimpleFormatter formatterTxt = new SimpleFormatter();
		weblogicLog.setFormatter(formatterTxt);
		
		FileHandler tomcatLog = new FileHandler("getTomcatMetrics.log", true);
		tomcatLog.setFormatter(formatterTxt);
		
		//API path serving requests on /weblogic
		get("/weblogic", (request, response) -> {
			LOGGER.addHandler(weblogicLog);
			String target = request.queryParams("target");
			String port = request.queryParams("port");
			String user = request.queryParams("user");
			String password = request.queryParams("password");

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Object> list = null;
			String jsonString = null;

			WeblogicMetricCollector weblogicMC = null;

			long debut = System.currentTimeMillis();
			try {

				weblogicMC = new WeblogicMetricCollector(target, port,user,password);
				weblogicMC.connect();
				list = weblogicMC.getMetrics();
				jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
			}

			catch(Exception e) {
				LOGGER.severe(e.getMessage());
				if (weblogicMC != null) {
					weblogicMC.disconnect();
				}
				LOGGER.info("Request served in "+(System.currentTimeMillis()-debut)+" miliseconds");
			}
			finally {
				if (weblogicMC != null) {
					weblogicMC.disconnect();
				}
				LOGGER.info("Request served in "+(System.currentTimeMillis()-debut)+" miliseconds\n\n");
			}
			return jsonString;
		});
		
		//API path serving requests on /tomcat
		get("/tomcat", (request, response) -> {
			LOGGER.addHandler(weblogicLog);
			String target = request.queryParams("target");
			String port = request.queryParams("port");
			String user = request.queryParams("user");
			String password = request.queryParams("password");

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Object> list = null;
			String jsonString = null;

			TomcatMetricCollector tomcatMC = null;

			long debut = System.currentTimeMillis();
			try {

				tomcatMC = new TomcatMetricCollector(target, port,user,password);
				tomcatMC.connect();
				list = tomcatMC.getMetrics();
				jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
			}

			catch(Exception e) {
				LOGGER.severe(e.getMessage());
				if (tomcatMC != null) {
					tomcatMC.disconnect();
				}
				LOGGER.info("Request served in "+(System.currentTimeMillis()-debut)+" miliseconds");
			}
			finally {
				if (tomcatMC != null) {
					tomcatMC.disconnect();
				}
				LOGGER.info("Request served in "+(System.currentTimeMillis()-debut)+" miliseconds\n\n");
			}
			return jsonString;
		});
	}
}
