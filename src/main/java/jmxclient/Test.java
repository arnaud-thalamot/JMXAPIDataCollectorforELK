package jmxclient;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

public class Test {

	public static void main(String[] args) throws IOException, MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {

		WeblogicMetricCollector weblogicMC = null;
		
		long debut = System.currentTimeMillis();
		try {
			Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			logger.setLevel(Level.INFO);
			FileHandler fileTxt = new FileHandler("getWeblogicMetrics.log", true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
	        fileTxt.setFormatter(formatterTxt);
	        logger.addHandler(fileTxt);
			
		    //weblogicMC = new WeblogicMetricCollector("mrs-px-00085", "7201","btweb", "cs2014*!");
	        //curl 'localhost:4567/weblogic?target=mrs-pr-00018&port=7001&user=btweb&password=3DGthJK43!'
	        
	        weblogicMC = new WeblogicMetricCollector("mrs-pr-00018", "7001","btweb", "3DGthJK43!");
			weblogicMC.connect();
			weblogicMC.getMetrics();
		}

		finally {
			if (weblogicMC != null) {
				weblogicMC.disconnect();
			}
			System.out.println(System.currentTimeMillis()-debut);
		}
	}
}
