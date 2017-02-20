package uk.gov.gsi.hmrc.rest.dms.persistent;


import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import uk.gov.gsi.hmrc.rest.dms.helper.WorkFlowBean;
import uk.gov.gsi.hmrc.rest.dms.model.Document;
import uk.gov.gsi.hmrc.rest.dms.model.JsonResponseModel;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

/**
 * The custom persistent manager for the DMS Upload Service
 *  *	Modification history
 *	 ------------------------------------------
 * 	Version 1.0 - Initial version August 2016
 * 	Versiion 2.0 - Inclusion of Robotics November 2016
 * 
 *  
 *  RC14670 12/12/2016 Audit :JG_001 EKDMS-1245 set "sent_to_robotics" replace and extend existing method
 */
public interface UploadServiceManager {
	/**
	 * create a JSON response object based on a JsonResponseModel 
	 * @param docList the list of Document object model passed in from parsed JSON request
	 * @param reconcilID the reconsiliation ID passed in from parsed JSON request
	 * @return JsonResponseModel object
	 * @throws DfException
	 * @throws JAXBException 
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public JsonResponseModel processDfsIngestion (List<Document> docList, String reconcilID, JsonResponseModel jsr,boolean robotics) throws DfException, JAXBException, IOException, ParseException;
	
	/**
	 * create a record in DMS Audit Table for a failed object that was not imported in repository
	 * @param jsi a single Document object passed in from parsed JSON request
	 * @param error_id an error ID number
	 * @param exception an exception message
	 * @throws DfException
	 */
	public void updateDMSAuditTableFailure(Document jsi, String error_id, String errorDescription,String exception, IDfSession session,boolean releaseSession) throws DfException;
	
	/**
	 * Checking if a reconciliation ID in current JSON request is already processed previously or not 
	 * @param reconcilID the reconsiliation ID passed in from parsed JSON request
	 * @param successCheck true to check dm_dbo.dgms_upload_success table, false to check dm_dbo.dgms_upload_failure table
	 * @return boolean
	 * @throws DfException
	 */
	public boolean checkIfRecIdProcessed(String reconcilID,  boolean successCheck)throws DfException;
	
	/**
	 * Checking if a reconciliation ID in current JSON request is already processed previously or not 
	 * @param submittedType the value of the type passed in from parsed JSON request for a document marked as XML
	 * @return boolean
	 * @throws DfException
	 */
	public boolean checkRoboticsType(String submittedType, String submittedFormat)throws DfException;
	
	
	/**
	 * Retrurn the Robotics config associate with a particular form type 
	 * @param The form type e.g. R39
	 * @return IDfCollection
	 * @throws DfException
	 */
	public IDfCollection getRoboticsConfig (String formType) throws DfException;
	
	/**
	 * Start The REST Primary Process 
	 * @param Mail Item Id, workflow job type
	 * @return String
	 * @throws DfException
	 */
	public String startResttWorkflow (String source, String target,String mailItemObjId,String action, String note,String category, String retention_policy, List<String> CustomerIds) throws DfException;
	
	/**
	 * Start The REST Primary Process 
	 * @param WorkFlow Bean
	 * @return String
	 * @throws DfException
	 */
	public String startResttWorkflow (WorkFlowBean wfb) throws DfException;
	
	/**
	 * gets object id of a named workflow
	 * @param String workflowName
	 * @return String
	 * @throws DfException
	 */
	public String gettWorkflowID (String workflowName) throws DfException;
	
	/**
	 * determines if mail item has been manually processed
	 * @param String mailItemId
	 * @return String
	 * @throws DfException
	 */
	public int isMailItemWorked (String mailItemId) throws DfException;
	
	/**
	 * determines if mail item has events meet criteria
	 * @param String mailItemId
	 * @return String
	 * @throws DfException
	 */
	public Boolean checkmailItemEvents (String mailItemId) throws DfException;
	
	
	//JG_001 replace this method with the extended setRoboticStatusAndCategory
	/**
	 * sets robotic category on newly created mail item
	 * @param String mailItemId, String roboticType
	 * @return Boolean
	 * @throws DfException
	 */
	//public void setRoboticCategory(String mailItemId, String roboticType)throws DfException;
	
	
	/**
	 * sets robotic category and status on newly created mail item
	 * @param String mailItemId, String roboticType
	 * @return Boolean
	 * @throws DfException
	 */
	public void setRoboticStatusAndCategory(String mailItemId, String roboticType)throws DfException;
	
	//END JG_001
	
	
	/**
	 * Update RoboticTracking after robotic response received
	 * @param int status ,String mailItemId, String error, Date now
	 * @throws DfException
	 */
	public void updateRoboticTrackingTable(int status, String mailItemId ,String error, Date now) throws DfException;
}
