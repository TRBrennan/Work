/*
 * CB CodeReview 08/Dec/16
 * Code Review by Chris Brennan.  If clarification is needed, please contact me directly
 */
package uk.gov.gsi.hmrc.rest.dms.robotics.roboticinterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.tools.RegistryPasswordUtils;

import uk.gov.gsi.hmrc.rest.dms.robotics.soapCall.RoboticsFormDataBean;
import uk.gov.gsi.hmrc.rest.dms.utils.DfcUtils;

/**
 * 
 * @author RC11907 (T Brennan)
 * 
 *         Description – Class to build up the required information for Robotics
 *         SOAP Call.
 * 
 * 
 *         Modification history ------------------------------------------
 *         Version 1.0 - Initial version October 2016
 * 
 * 			CB - 8th Feb 2017 - check for null pointer when reading from roboticAuthProps file from docbase
 */

public class RobotSOAPDataSetup {

	public RoboticsFormDataBean data = new RoboticsFormDataBean();

	public void formDataSetup(String formType, IDfSession session)
			throws DfException, IOException {
		IDfCollection coll = null;
		String encrpytedPassword = null;
		String decrpytedPassword = null;
		try {

			DfLogger.debug(this, "Entered formSOAPData Method for Form Type: "
					+ formType, null, null);

			// Query robotic config table to get needed information to build
			// Robotic SOAP Call.
			String dql = "select form_type,robotic_category,robotic_endpoint, message_solution, message_workflow_id from dm_dbo.dgms_robotic_config where form_type = '"
					+ formType + "'";

			DfLogger.debug(this, "Running Config DQL: " + dql, null, null);

			coll = DfcUtils.executeQuery(dql, session, true);
			/*
			 * CB CodeReview 08/Dec/16 Never assume that a collection will have
			 * values. Always wrap IDfCollection.next() in either an if or while
			 * block - TB Added while loop.
			 */
			while (coll.next()) {
				data.setEndpointURL(coll.getString("robotic_endpoint"));
				data.setSolutionID(coll.getString("message_solution"));
				data.setWorkflowID(coll.getString("message_workflow_id"));
				data.setExtInvReq("DMS_01");
				data.setOsUID("DMS");
				data.setInitator_typeD("THIRD_PARTY_APP");
				data.setRoboticCategory(coll.getString("robotic_category"));
			}
			// Get Authentication Properties from the Prop files on Tomcat
			// Password to be encrypted by DFC encryption.
			Properties prop = new Properties();
			/*ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
*/
			DfLogger.debug(this,
					"Loading Properties File to get Tomcat USer and Password.",
					null, null);

			// CB - getting this now from the docbase, as otherwise we need to
			// maintain it in 2 locations.
			IDfDocument idfDocProps = (IDfDocument) session
					.getObjectByPath("/DMS/Properties/RoboticAuthProps");
			//CB - 8th Feb 2017
			// if we can't read the roboticAutProps file, DON'T do the next bit, then set username to be ""
			if(null != idfDocProps){
				InputStream input = idfDocProps.getContent();
	
				/*
				 * InputStream input = classLoader
				 * .getResourceAsStream("RoboticAuthProps.properties"); // load a
				 * properties file
				 */
				DfLogger.debug(this, "Properties File Loaded as a Stream.", null,
						null);
	
				prop.load(input);
	
				DfLogger.debug(this, "Properties File Loaded", null, null);
	
				// Decrypt DFC Encrypted password from Auth Prop Files
				encrpytedPassword = prop.getProperty("password");
	
				// DfLogger.debug(this,"Got Encrytped Password: " +
				// encrpytedPassword, null, null);
	
				decrpytedPassword = decryptPassword(encrpytedPassword);
	
				// get the property value and print it out
				data.setUserName(prop.getProperty("username"));
	
				DfLogger.debug(this, "Got and set Username ", null, null);
	
				data.setPassword(decrpytedPassword);
	
				DfLogger.debug(this, "Set Password for the SOAP Call.", null, null);
			} else {
				data.setUserName("");
				DfLogger.error(this, "FILE_NOT_FOUND_EXCEPTION: PROPERTIES FILE /DMS/Properties/RoboticAuthProps COULD NOT BE READ.", null, null);
			}
		} finally {
			if (coll != null) {
				coll.close();
				DfLogger.debug(this, "Collection Closed.", null, null);
			}
		}
	}

	private String decryptPassword(String encryptedPassword) {

		String password = encryptedPassword;

		try {
			password = RegistryPasswordUtils.decrypt(encryptedPassword);
		} catch (DfException dfe) {
			DfLogger.warn(this, "Error decrypting password", null, dfe);
		}

		return password;
	}

}
