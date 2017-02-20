	/*
	 * CB CodeReview 08/Dec/16
	 * Code Review by Chris Brennan.  If clarification is needed, please contact me directly
	 */
package uk.gov.gsi.hmrc.rest.dms.robotics.roboticinterface;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.rpc.ServiceException;

import uk.gov.gsi.hmrc.rest.dms.robotics.soapCall.RoboticsCall;
import uk.gov.gsi.hmrc.rest.dms.utils.DfcUtils;


import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

/**
 * 
 * @author RC11907 (T Brennan)
 * 
 *         Description – Class to call the Robotics SOAP Call and handle any
 *         expections.
 * 
 * 
 *         Modification history ------------------------------------------
 *         Version 1.0 - Initial version October 2016
 *         Version 1.1 - EDT-609 03/01/2017 Added Util Method for String Manipulation around Exception message length and Single Quotes 
 * 
 * 		  CB - 8th Feb 2017 - check for "" robotics username; don't call robot if not set
 */

public class RobotInterface extends RoboticsCall {

	/*
	 * CB CodeReview 08/Dec/16
	 * These variables do not need global scope - move their definitions into the appropriate method
	 */
	RobotSOAPDataSetup robotDataSetup = null;
	
	private static final String insertIntoTrackingDQL = "INSERT INTO dm_dbo.dgms_robotic_tracking (identifier, creation_date,r_object_id,robotic_form_type,robotic_category,real_category,status,error_condition,robotic_request_date,robotic_call_retry_count) VALUES";
	private static final String InsertValuesDQL = "('IDPARAM',DATE('CREATIONDATEPARAM','MM/dd/yyyy hh:mi:ss'),'ROBJPARAM','FORMTYPEPARAM','ROBOTCATPARAM','REALCATPARAM',STATUSPARAM,'ERRORCONPARAM',DATE('ROBOTICREQUESTDATEPARAM','MM/dd/yyyy hh:mi:ss'),RETRYCOUNTPARAM)";
	
	private String modifiedPayload = null;

	public void RoboticRequest(String formType, String xmlB64Payload,
			String r_object_id, String real_Category, IDfSession session) {
		String robotResponse = null;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String setTrackingDataDQL = "";
		Date currentTime = new Date();
		String expectionMessageHandler = null;

		String creationDate = dateFormat.format(currentTime);
		String RobotRequestDate = dateFormat.format(currentTime);

		DfLogger.info(this, "Entered Robotic Request", null, null);

		try {
			robotDataSetup = new RobotSOAPDataSetup();
			DfLogger.debug(this, "New SOAP Data Method Set up: "
					+ robotDataSetup.data, null, null);
			// Build up Data for the Robotic SOAP Call.
			DfLogger.debug(this, "New SOAP Form Data Type for: " + formType,
					null, null);
			robotDataSetup.formDataSetup(formType, session);
			
			//CB - 8th Feb 2017
			// if we can't read the roboticAutProps file, username will be ""; throw an IEException
			if(robotDataSetup.data.getUserName().equals("")){
				throw new IOException("Properties file /DMS/Properties/RoboticAuthProps could not be read.");
			}
			
			DfLogger.debug(this, "Data Set up for Form Type: " + formType
					+ " Data: " + robotDataSetup.data, null, null);
			// Configure the XML Payload for the Robotic SOAP Call
			xmlConfig(xmlB64Payload, r_object_id);

			DfLogger.debug(
					this,
					"Robotic category is: "
							+ robotDataSetup.data.getRoboticCategory(), null,
					null);

			// Call the Robot to Process the Form.
			robotResponse = formSubmission(robotDataSetup, modifiedPayload);

			DfLogger.debug(this, "After Form Submission: " + robotResponse,
					null, null);

			// Check the Response Status of the Robotics SOAP Call, if Success
			// set the values in the table to be Awaiting Response
			if (robotResponse.equalsIgnoreCase("Success")) {
				// Error Status of 1 Awaiting Response from Robot
				setTrackingDataDQL = insertIntoTrackingDQL
						+ buildDQLStatement(r_object_id, r_object_id,
								creationDate, formType,
								robotDataSetup.data.getRoboticCategory(),
								real_Category, "1", "", RobotRequestDate, "0");
			}
			// If Response is not a success set the values in the table to be
			// Error Robotics Call with Error Message.
			else {
				//EDT-609 Call Util Method for handling Exceptions Strings to not contain Single Quotes and not be over 500 Characters
				expectionMessageHandler = DfcUtils.expectionStringBuilder(robotDataSetup.data.getStatus_text());
				// Error Status of 3 Error in Robotics Call
				setTrackingDataDQL = insertIntoTrackingDQL
						+ buildDQLStatement(r_object_id, r_object_id,
								creationDate, formType,
								robotDataSetup.data.getRoboticCategory(),
								real_Category, "3",
								expectionMessageHandler,
								RobotRequestDate, "0");
			}

			DfLogger.info(this,
					"If then Else Statement passed SOAP Call was made", null,
					null);

		} catch (DfException | ServiceException | IOException e) {
			DfLogger.error(
					this,
					"Error trying to get Robotic Config Informatiom from dm_dbo.dgms_robotic_config table",
					null, e);
			//EDT-609 Call Util Method for handling Exceptions Strings to not contain Single Quotes and not be over 500 Characters
			expectionMessageHandler = DfcUtils.expectionStringBuilder(e.getMessage());
			
			// Error Status of 3 Error in Robotics Call
			setTrackingDataDQL = insertIntoTrackingDQL
					+ buildDQLStatement(
							r_object_id,
							r_object_id,
							creationDate,
							formType,
							robotDataSetup.data.getRoboticCategory(),
							real_Category,
							"3",
							expectionMessageHandler,
							RobotRequestDate, "0");
		} finally {

			if (setTrackingDataDQL.isEmpty()) {
				// Error Status of 3 Error in Robotics Call
				setTrackingDataDQL = insertIntoTrackingDQL
						+ buildDQLStatement(r_object_id, r_object_id,
								creationDate, formType,
								robotDataSetup.data.getRoboticCategory(),
								real_Category, "3", "", RobotRequestDate, "0");
			}
			insertTrackingEntry(setTrackingDataDQL, session);
		}
	}

	// TODO tidy this up, consider a true Util implementation
	// /Implement DfcExecution correctly so no collection returned
	private void insertTrackingEntry(String inserTracinkTableDQL, IDfSession session) {
		
		try {
			/*
			 * CB CodeReview 08/Dec/16
			 * Should change this to use the new Dfcutils.updateDeleteQuery()
			 * makes IDfCollection col redundant, so remove it
			 */
			DfLogger.debug(this, "Testing for Session", null, null);
			if (null != session) {
				DfLogger.debug(this, "Session is valid, Executing Query " + inserTracinkTableDQL, null, null);
				DfcUtils.updateDeleteQuery(inserTracinkTableDQL, session);
			} else {
				DfLogger.warn(this,"Failed to execute DQL as session is null. DQL Query: " + inserTracinkTableDQL, null, null);
			}
		} catch (DfException e) {
			// Exception caught trying to run DQL into Table
			DfLogger.error(this, "executing DQL statement: "
					+ inserTracinkTableDQL + ":", null, e);
		} finally {
			DfLogger.debug(this, "Enter Finally Block of DQL Run", null, null);
		}

	}

	/*
	 * Method to complete String Replace of the XML payload to match with the
	 * Robotic Data Guide. Decode the Base 64 Encoded Payload. Add the DMS
	 * Submission Reference to the XML Payload. Replace < with &lt;, > with
	 * &gt;, Replace & with &#38. Add CDATA contaimnet tag to start and end of
	 * payload
	 */
	private void xmlConfig(String xmlPayload, String r_object_id) {
		String decodedPayload = null;
		String subRefDecodedPayload = null;
		String submissionRef = "<DMS_Submission_Reference>" + r_object_id
				+ "</DMS_Submission_Reference>";
		// Decode the XML Payload from Base64 Encoding
		decodedPayload = new String(
				DatatypeConverter.parseBase64Binary(xmlPayload));

		// Add Submission Referneceto the Payload
		subRefDecodedPayload = decodedPayload + submissionRef;

		modifiedPayload = "<![CDATA[" + subRefDecodedPayload + "]]>";
	}
	
	

	private String buildDQLStatement(String IDPARAM, String ROBJIDPARAM,
			String CREATIONDATEPARAM, String FORMTYPEPARAM,
			String ROBOTCATPARAM, String REALCATPARAM, String STATUSPARAM,
			String ERRORCONPARAM, String ROBOTICREQUESTPARAM,
			String RETRYCOUNTPARAM) {
		String InsertValuesDQL1;

		DfLogger.info(this, "Building DQL Statement and replacing values in "
				+ InsertValuesDQL, null, null);
		InsertValuesDQL1 = InsertValuesDQL.replace("IDPARAM", IDPARAM)
				.replace("ROBJPARAM", ROBJIDPARAM)
				.replace("CREATIONDATEPARAM", CREATIONDATEPARAM)
				.replace("FORMTYPEPARAM", FORMTYPEPARAM)
				.replace("ROBOTCATPARAM", ROBOTCATPARAM)
				.replace("REALCATPARAM", REALCATPARAM)
				.replace("STATUSPARAM", STATUSPARAM)
				.replace("ERRORCONPARAM", ERRORCONPARAM)
				.replace("ROBOTICREQUESTDATEPARAM", ROBOTICREQUESTPARAM)
				.replace("RETRYCOUNTPARAM", RETRYCOUNTPARAM);

		return InsertValuesDQL1;
	}
}
