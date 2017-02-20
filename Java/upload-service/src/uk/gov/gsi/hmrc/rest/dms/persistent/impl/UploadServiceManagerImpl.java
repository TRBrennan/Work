/*
 * Copyright (c) 2014. EMC Corporation. All Rights Reserved.
 */

/*
 * CB CodeReview 08/Dec/16
 * Code Review by Chris Brennan.  If clarification is needed, please contact me directly
 */
package uk.gov.gsi.hmrc.rest.dms.persistent.impl;

//JG_002 Remove Unused Imports
import static com.emc.documentum.rest.dfc.util.DfcSessions.release;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.gsi.hmrc.rest.dms.helper.WorkFlowBean;
import uk.gov.gsi.hmrc.rest.dms.model.Document;
import uk.gov.gsi.hmrc.rest.dms.model.JsonResponseModel;
import uk.gov.gsi.hmrc.rest.dms.persistent.UploadServiceManager;
import uk.gov.gsi.hmrc.rest.dms.robotics.roboticinterface.RobotInterface;
import uk.gov.gsi.hmrc.rest.dms.utils.Constants;
import uk.gov.gsi.hmrc.rest.dms.utils.DfcUtils;
import uk.gov.gsi.hmrc.rest.dms.xmlobjects.config.DfsConfigs;
import uk.gov.gsi.hmrc.rest.dms.xmlobjects.config.DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData;

import com.documentum.bpm.IDfWorkflowEx;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.documentum.rest.dfc.ContextSessionManager;
import com.emc.documentum.rest.dfc.SessionAwareAbstractManager;
import com.emc.documentum.rest.dfc.util.DfcSessions;

/**
 * * @author INDARS SPARRINS
 * 
 * The Upload Service Manager Implementation to handle the Ingestion process
 * through REST services. The class will interrogate the JSON input (request).
 * For each document in JSON request, the program will extract the encoded
 * content and all associated metadata required to create documents (Mail Item
 * and its attachments). The extracted content and metadata will then be mapped
 * to Documentum attributes through XML Configuration file found in repository
 * and then used to create DMS objects in Documentum. Finally, is the document
 * creation in repository was successful, the program will start the workflow on
 * ingested Mail Item. For any exception thrown or error caught, the system will
 * clean clean the created documents, if any, thus mimicing creating all or
 * nothing (transaction based).
 * 
 ** 
 * Modification history ------------------------------------------ Version 1.0 -
 * Initial version August 2016 Versiion 2.0 - Inclusion of Robotics November
 * 2016
 * 
 * 
 * RC14670 12/12/2016 Audit :JG_001 EKDMS-1245 set "sent_to_robotics" variable
 * on mail item. Initial version to utilise an update statment, should be moved
 * to mail item creation code. RC14670 12/12/2016 Audit :JG_002 EKDMS-1258 -
 * Implement Code review changes relating to collections and queries RC14670
 * 13/12/2016 Audit :JG_003 EKDMS-1255 - Include active check in robotics config
 * lookup RC14670 14/12/2016 Audit :JG_004 EKDMS-1280 - Update to ensuer that
 * the primary content is always the first item created
 */

public class UploadServiceManagerImpl extends SessionAwareAbstractManager
		implements UploadServiceManager {

	private static final String ATTACHMENT = "attachment";
//	private static final String IFORM = "iform";
	private static final String XML_RETRIEVE_QUERY = "dm_document where FOLDER('/DFS/config') and object_name = 'xml_attribute_matching.xml'";
	private static final String WORKFLOW_STARTED_FOR_OBJECT = "Workflow started for object: ";
	private static final String NULL_ID = "0000000000000000";
	private static final String UPDATE_AUDIT_FAILED_GET_WORKFLOW = ". Inserting values in DMS Audit Trail Failure table done by method failing get workflow";
	private static final String WORKFLOW_NOT_STARTED_FOR_OBJECT = "Workflow NOT started for object: ";
	private static final String WORKFLOW_NOT_STARTED_NO_OBJECT_ID_FOR_MAIL_ITEM = "Workflow NOT started because there is no valid ObjectID for Mail Item: ";
	private static final String ENTERED_FAILURE_BLOCK = "Entered Failure block";
	private static final String WORKFLOW_ID = " :WorkflowID: ";
	private static final String DFS_ATTACHMENT_PROCESS_FAILED_BAD_JSON = "DFS Attachment process failed because the JSON request documents are in unexpected order or some unexpected issues in request body.";
	private static final String RELEASING_SESSION_IN_PROCESS_DFS_INGESTION = "Releasing session in processDfsIngestion()...";
	private static final String DOCUMENT_CLEANUP_COMPLETED_DELETED_THE_FOLLOWING_OBJECT_I_DS = "Document cleanup completed. Deleted the following object IDs: ";
	private static final String DOCUMENTS_CLEANED_DELETING_EMPTY_FOLDER = "Documents cleaned. Deleting empty folder: ";

	/*
	 * CB CodeReview 08/Dec/16 These variables should have private, protected or
	 * public visibility (also ContextSessionManager below)
	 */
	IDfSession session = null;
	DfsConfigs parsedXMLConfig = null;
	List<MetaData> configValues;

	boolean isSubmissionIdSet = false;

	// If want transaction support in custom persistent layer, we MUST use
	// ContextSessionManager. Otherwise, there will be problems.
	@Autowired
	ContextSessionManager contextSessionManager;

	/**
	 * Main method for processing JSON request after initial validation has been
	 * done in Controller class. processDfsIngestion() method parses XML
	 * configuration file for mapping metadata. For each document in JSON
	 * request, methods are invoked to create the object, assing metadata and
	 * content. In case of success, dgms_upload_success table is updated and
	 * JSON response object is created and set. For failure, dgms_upload_failure
	 * table is updated and JSON response object is created and set. if
	 * documents has been successfully created, code performs validation to
	 * check the number of documents created vs number of documents requested to
	 * be created. If validation passed, the workflow is invoked on Mail Item
	 * created. Finally, if there were any errors during the above process
	 * execution, the cleanup is perform to create a transaction-based approach,
	 * where all or none documents are created in repository and workflow
	 * initiated on Mail item.
	 * 
	 * @param docList
	 *            list of document objects from JSON request file
	 * @param reconsiliationID
	 *            value of Reconciliation ID
	 * @param jsr
	 *            JsonResponseModel object to be populated with message
	 * 
	 * 
	 * @throws DfException
	 *             if the settings of object values and creating it encounters
	 *             issues.
	 * @throws JAXBException
	 *             JAXBException exception while parsing XML config file
	 * @throws IOException
	 *             IOException exception.
	 * @throws ParseException
	 *             parse exception while parsing XML config file
	 * 
	 * @return JsonResponseModel JSON response model
	 * 
	 */
	/*
	 * CB CodeReview 08/Dec/16 This method is over 300 lines long - that's too
	 * big; it becomes unwieldy, and hard to read. We need to consider how /
	 * where to break some sections out into new methods
	 */
	public JsonResponseModel processDfsIngestion(List<Document> docList,
			String reconsiliationID, JsonResponseModel jsr, boolean robotics)
			throws DfException {
		DfLogger.debug(this, "ROBOTICS = : " + robotics, null, null);

		String mainObjCreated = "";
		Document doc = null;
		boolean failed = true;
		boolean continueProcessing = true;

		int totalDocListSize = 0;
		int totalDocListCreatedSize = 0;
		List<String> docListCreated = new ArrayList<String>();
		// JG_002 Remove rogue ;

		// this will be for keeping all docs created in this request and
		// compare it against what came in to make sure all docs are created
		// and none missed
		// IDfId returnedWorkflow = null;
		String docsCSV = "none"; // This is validation variable
		String mailObjName = "";
		String workflowIDstr = "";
		String requestSource = "";
		String realcategory = "";
		boolean wfCheck = false;
		boolean unsupportedType = false;

		String roboticType = "";
		String roboticData = "";
		// removed superfious line
		// TreeMap<String, Document> documentOrderedMap = new TreeMap<String,
		// Document>();

		try {

			DfLogger.debug(this, "About to get session", null, null);
			session = getSessionRepository().getSession();// if we re-using
															// existing
															// one that has been
															// been released, we
															// getting error
			DfLogger.debug(this, "Session acquired....", null, null);
			DfLogger.debug(this, "Process Ingestion data", null, null);
			DfLogger.debug(this, "GET ATT XML from docbase", null, null);
			IDfSysObject configFileRepo = (IDfSysObject) session
					.getObjectByQualification(UploadServiceManagerImpl.XML_RETRIEVE_QUERY);
			DfLogger.debug(this, "GET content as byte streamL", null, null);
			ByteArrayInputStream stream = configFileRepo.getContent();
			DfLogger.debug(this, "GET jaxb", null, null);
			JAXBContext jaxbContext = JAXBContext.newInstance(DfsConfigs.class);
			DfLogger.debug(this, "create unmarshaller", null, null);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			DfLogger.debug(this, "Get parsedXMLConfig ", null, null);
			parsedXMLConfig = (DfsConfigs) jaxbUnmarshaller.unmarshal(stream);
			DfLogger.debug(this, "Get configValues ", null, null);
			configValues = parsedXMLConfig.getDfsConfig().getImportRules()
					.getMetaDataMappings().getMetaData();

			DfLogger.debug(
					this,
					"Processing Input data to create DMS objects in Documentum",
					null, null);
			// point of this?
			totalDocListSize = docList.size();

			// JG_004

			DfLogger.debug(this,
					"Sort the doclist to ensure primary content is first ",
					null, null);

			List<Document> sortedList;

			sortedList = DfcUtils.getSortedList(docList);

			// JG_004
			// DfLogger.debug(this,"Going to mutate and order the list into a tree hashmap",null,
			// null);
			// JG_004 TreeMap<String, Document> documentOrderedMap =
			// DfcUtils.getOrderedDocMap(docList);

			// entry or element of?
			/*
			 * CB CodeReview 08/Dec/16 Consider breaking this for() loop out
			 * into a new method
			 */

			// JG_004 for (Map.Entry<String, Document> entry :
			// documentOrderedMap.entrySet()) {

			// JG_004
			// DfLogger.debug(this,"Getting value of sorted TreeMap for key ["+
			// entry.getKey()+
			// "] --> key name can be ignored as they are not exact same as passed in JSON",null,
			// null);

			DfLogger.debug(this,
					"Value of Primary Content for first item in the list is :  "
							+ sortedList.get(0).getHeader()
									.getPrimary_content(), null, null);

			for (int i = 0; i < sortedList.size(); i++) {

				// why are be bothering with an ordered Hash Tree Map list when
				// we receive a perfectly good List that we just want 1 element
				// first?
				// JG_004 doc = entry.getValue();

				doc = sortedList.get(i);

				totalDocListCreatedSize++;

				// JG_004 DfLogger.debug(this, "Getting document of type ["+
				// doc.getHeader().getType() + "]", null, null);
				// END JG_004

				// JG_XYZ Change for Primary Content

				if (doc.getHeader().getPrimary_content()
						.equalsIgnoreCase(Constants.BOOLEAN_AS_STRING_TRUE)) {

					DfLogger.debug(this,
							"About to process Main mail item document", null,
							null);

					requestSource = doc.getHeader().getSource();
					realcategory = DfcUtils.getMetadataValueFromJson(
							doc.getMetadata(), "classification_type");
					DfLogger.debug(this, "realcategory = " + realcategory,
							null, null);

					mainObjCreated = processContent(doc, true, false, null);
					if (StringUtil.isEmptyOrNull(mainObjCreated)) {
						DfLogger.debug(
								this,
								"Main Mail document NOT created because of exception",
								null, null);
						// Update Ingestion result tables
						updateDMSAuditTableFailure(doc,
								Constants.ERROR_CODE_MAILITEM_NOT_CREATED, "",
								Constants.ERROR_MESSAGE_MAILITEM_NOT_CREATED,
								session, false);
						// setting JSON response if failed due to Applicaiton
						// Error
						jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
								+ reconsiliationID
								+ Constants.ERROR_DESCRIPTION_PART2
								+ Constants.ERROR_MESSAGE_MAILITEM_NOT_CREATED);
						continueProcessing = false;
					} else {
						mailObjName = doc.getHeader().getTitle();
						DfLogger.debug(this, "Mail Item " + mailObjName
								+ " created Successfully with ID: "
								+ mainObjCreated, null, null);
						// docListCreated.size();
						docListCreated.add(mainObjCreated);
					}

				} else {
					// Create additional objects
					switch (doc.getHeader().getType().toLowerCase()) {

					case UploadServiceManagerImpl.ATTACHMENT:

						String objCreated = processContent(doc, false, false,
								mainObjCreated);
						if (StringUtil.isEmptyOrNull(objCreated)) {
							DfLogger.debug(
									this,
									"Attachment document NOT created because of exception",
									null, null);
							// Update Ingestion result tables
							updateDMSAuditTableFailure(
									doc,
									Constants.ERROR_CODE_ATTACHMENT_NOT_CREATED,
									"",
									Constants.ERROR_MESSAGE_ATTACHMENT_NOT_CREATED,
									session, false);
							// setting JSON response if failed due to
							// Applicaiton
							// Error
							jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
									+ reconsiliationID
									+ Constants.ERROR_DESCRIPTION_PART2
									+ Constants.ERROR_MESSAGE_ATTACHMENT_NOT_CREATED);
							continueProcessing = false;
							break;
						} else {
							DfLogger.debug(
									this,
									"Attachment document created with ID: "
											+ objCreated
											+ " and attached to mail item with ID: "
											+ mainObjCreated, null, null);
							docListCreated.add(objCreated);

						}
						break;
					default:

						if (doc.getHeader()
								.getFormat()
								.equalsIgnoreCase(
										Constants.MAIL_ATTACHMENT_FORMAT_XML)) {
							DfLogger.debug(
									this,
									"FOUND ROBOTICS XML CREATE XML DOC, add to doclist size  ",
									null, null);
							String xmlObjCreated = processContent(doc, false,
									true, mainObjCreated);
							docListCreated.add(xmlObjCreated);

							roboticType = doc.getHeader().getType();
							roboticData = doc.getAttachment().getData();
						}

						else {
							DfLogger.debug(this, "Unrecognised Document Type: "
									+ doc.getHeader().getType()
									+ " --> SKIPPING...", null, null);
							totalDocListCreatedSize--;
							unsupportedType = true;
						}

						break;
					}

				}
				// END JG_XYZ Change for Primary Content

			} // end FOR loop

			// Validate the number of documents created

			/*
			 * CB CodeReview 08/Dec/16 Consider breaking this validation step
			 * out into a new method
			 */
			int totalDocListSizeOriginal = totalDocListSize;

			if (unsupportedType) {
				DfLogger.info(this,
						"Validation... Total documents requested: ["
								+ totalDocListSize
								+ "] --> Total valid Document types: ["
								+ totalDocListCreatedSize + "]", null, null);
				totalDocListSize = totalDocListCreatedSize;
			}
			// so we know that doc list size will always equal so why check?
			if (totalDocListSize == docListCreated.size() && continueProcessing) {
				docsCSV = DfcUtils.getDocListAsCSV(docListCreated);
				DfLogger.info(
						this,
						"Validation PASSED ----> Total Documents requested: ["
								+ totalDocListSizeOriginal
								+ "] -->  Total Documents with SUPPORTED Document types found: ["
								+ totalDocListCreatedSize
								+ "] -----> Total Documents created: ["
								+ docListCreated.size() + "] Doc IDs: ["
								+ docsCSV + "]", null, null);
			} else {
				if (continueProcessing) {
					updateDMSAuditTableFailure(doc,
							Constants.ERROR_CODE_VALIDATION_FAILED, "",
							Constants.ERROR_MESSAGE_VALIDATION_FAILED, session,
							false);
				}

				continueProcessing = false;
			}

			// Update Ingestion result tables
			/*
			 * CB CodeReview 08/Dec/16 Consider breaking this if() block out
			 * into a new method
			 */
			if (continueProcessing) {

				if (robotics && !(roboticType.equals(""))) {

					// DfLogger.debug(this,
					// "ROBOTICS variables are , roboticType " + roboticType +
					// "MAIL ITEM OBJ ID  " + mainObjCreated + "roboticData  " +
					// roboticData + "Real category =" + realcategory ,null,
					// null);

					// JG_001

					DfLogger.debug(this,
							"Set Robotic category and Status on Mail Item ",
							null, null);
					// Set robotics status on the mail item - replace previous
					// method call
					// setRoboticCategory(mainObjCreated, roboticType);

					setRoboticStatusAndCategory(mainObjCreated, roboticType);

					// END JG_001
					RobotInterface robotIF = new RobotInterface();
					robotIF.RoboticRequest(roboticType, roboticData,
							mainObjCreated, realcategory, session);
					DfLogger.debug(this, "Robotics Call SUCCESSFUL ", null,
							null);

				}

				else {
					DfLogger.debug(this,
							"Robotics was false , process as normal ", null,
							null);
				}

				if (!StringUtil.isEmptyOrNull(mainObjCreated)) {// start worflow

					DfLogger.debug(this, "Starting workflow for object: "
							+ mainObjCreated, null, null);
					workflowIDstr = startResttWorkflow(requestSource,
							Constants.WORKFLOW_TARGET_DEFAULT, mainObjCreated,
							Constants.WORKFLOW_SOURCE_INGESTION, "", "", "",
							null);

					// workflow
					wfCheck = true;

					DfLogger.debug(this, "Checking Workflow started "
							+ mainObjCreated, null, null);

					if (StringUtil.isEmptyOrNull(workflowIDstr)
							|| workflowIDstr
									.equalsIgnoreCase(UploadServiceManagerImpl.NULL_ID)
							|| StringUtil.isEmptyOrNull(workflowIDstr)) {
						DfLogger.debug(
								this,
								UploadServiceManagerImpl.WORKFLOW_NOT_STARTED_FOR_OBJECT
										+ mainObjCreated
										+ " ::: "
										+ workflowIDstr
										+ ". Inserting values in DMS Audit Trail Failure table",
								null, null);
						DfLogger.debug(this,
								"Update Ingestion Audit table :Failure", null,
								null);

						// Update Ingestion result tables
						DfcUtils.uploadError(reconsiliationID, doc.getHeader()
								.getSource(),
								Constants.ERROR_CODE_WORKFLOW_NOT_STARTED, "",
								Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED,
								session);
						// setting JSON response if failed due to Applicaiton
						// Error
						jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
								+ reconsiliationID
								+ Constants.ERROR_DESCRIPTION_PART2
								+ Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED);
						// failed = true;
					}

					else {

						// Update Ingestion result tables
						DfcUtils.updateDMSAuditTableSuccess(reconsiliationID,
								mainObjCreated, doc.getHeader().getSource(),
								session);
						DfLogger.info(
								this,
								"Validation PASSED ----> Total documents requested: ["
										+ totalDocListSizeOriginal
										+ "] --> Total valid Documents found to be created: ["
										+ totalDocListSize
										+ "] --> Total Documents created: ["
										+ docListCreated.size()
										+ "] Workflow ID: [" + workflowIDstr
										+ "]", null, null);
						// setting JSON response if Successfully created all
						// docs and started workflow

						jsr.setResult(Constants.RESPONSE_SUCCESS);
						jsr.setDescription("The form " + mailObjName
								+ " (r_object_id: " + mainObjCreated
								+ ") has been successfully processed");
						DfLogger.debug(
								this,
								UploadServiceManagerImpl.WORKFLOW_STARTED_FOR_OBJECT
										+ mainObjCreated
										+ " ::: "
										+ workflowIDstr, null, null);
						// Workflow started. PRocess completed successfully
						failed = false;
					}

				} else {

					DfLogger.error(
							this,
							UploadServiceManagerImpl.WORKFLOW_NOT_STARTED_NO_OBJECT_ID_FOR_MAIL_ITEM
									+ mainObjCreated, null, null);
					// failed = true;
					// Update Ingestion result tables
					DfcUtils.uploadError(
							reconsiliationID,
							doc.getHeader().getSource(),
							Constants.ERROR_CODE_WORKFLOW_NOT_STARTED_NO_MAILITEM,
							"",
							Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED_NO_MAILITEM_ID,
							session);
					// setting JSON response if failed due to Applicaiton Error
					jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
							+ reconsiliationID
							+ Constants.ERROR_DESCRIPTION_PART2
							+ Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED_NO_MAILITEM_ID);
				}
			}

			// Clean up everything in case of any errors/exceptions
			// clean up has fault in code, you attempt to clean up on a
			// potentially null session, this will cause a spew of errors.

			if (failed) {
				cleanUpOnFailure(workflowIDstr, wfCheck, mainObjCreated,
						reconsiliationID, jsr, doc, docsCSV, docListCreated);
			}

		}

		/*
		 * CB CodeReview 08/Dec/16 It looks like each of these catch statements
		 * does exactly the same thing, except for the 'See xxx stack' Consider
		 * rationalising this to catch "Exception" to save code duplication
		 */
		catch (DfException e1) {
			cleanUpOnFailure(workflowIDstr, wfCheck, mainObjCreated,
					reconsiliationID, jsr, doc, docsCSV, docListCreated);

			jsr.setResult(Constants.RESPONSE_ERROR);
			jsr.setReconciliation_id(reconsiliationID);
			jsr.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
			DfLogger.error(
					this,
					"Ingestion Process Failed, return Default Response. See DfException  stack for furhter details",
					null, e1);
			jsr.setSource(doc.getHeader().getSource());
			e1.printStackTrace();
			updateDMSAuditTableFailure(doc, Constants.CODE_EXECUTION_ERROR,
					jsr.getDescription(), e1.getLocalizedMessage(), null, true);// true
																				// if
																				// need
																				// to
																				// release
																				// session
		} catch (IOException e1) {
			cleanUpOnFailure(workflowIDstr, wfCheck, mainObjCreated,
					reconsiliationID, jsr, doc, docsCSV, docListCreated);

			jsr.setResult(Constants.RESPONSE_ERROR);
			jsr.setReconciliation_id(reconsiliationID);
			jsr.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
			DfLogger.error(
					this,
					"Ingestion Process Failed, return Default Response. See IOException  stack for furhter details",
					null, e1);
			jsr.setSource(doc.getHeader().getSource());
			e1.printStackTrace();
			updateDMSAuditTableFailure(doc, Constants.CODE_EXECUTION_ERROR,
					jsr.getDescription(), e1.getLocalizedMessage(), null, true);// true
																				// if
																				// need
																				// to
																				// release
																				// session
		} catch (ParseException e1) {
			cleanUpOnFailure(workflowIDstr, wfCheck, mainObjCreated,
					reconsiliationID, jsr, doc, docsCSV, docListCreated);

			jsr.setResult(Constants.RESPONSE_ERROR);
			jsr.setReconciliation_id(reconsiliationID);
			jsr.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
			DfLogger.error(
					this,
					"Ingestion Process Failed, return Default Response. See ParseException  stack for furhter details",
					null, e1);
			jsr.setSource(doc.getHeader().getSource());
			e1.printStackTrace();
			updateDMSAuditTableFailure(doc, Constants.CODE_EXECUTION_ERROR,
					jsr.getDescription(), e1.getLocalizedMessage(), null, true);// true
																				// if
																				// need
																				// to
																				// release
																				// session
		} catch (JAXBException e1) {
			cleanUpOnFailure(workflowIDstr, wfCheck, mainObjCreated,
					reconsiliationID, jsr, doc, docsCSV, docListCreated);

			jsr.setResult(Constants.RESPONSE_ERROR);
			jsr.setReconciliation_id(reconsiliationID);
			jsr.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
			DfLogger.error(
					this,
					"Ingestion Process Failed, return Default Response. See JAXBException  stack for furhter details",
					null, e1);
			e1.printStackTrace();
			updateDMSAuditTableFailure(doc, Constants.CODE_EXECUTION_ERROR,
					jsr.getDescription(), e1.getLocalizedMessage(), null, true);// true
																				// if
																				// need
																				// to
																				// release
																				// session
		}

		finally {

			// releasing the session
			DfLogger.debug(this, RELEASING_SESSION_IN_PROCESS_DFS_INGESTION,
					null, null);
			if (session != null) {
				release(session);
			}
		}

		// reset submission folder check to false to ensure next submission is
		// processed correctly
		isSubmissionIdSet = false;

		return jsr;
	}

	/*
	 * (List<Document> docList, String reconsiliationID, JsonResponseModel jsr
	 */

	public void cleanUpOnFailure(String workflowIDstr, boolean wfCheck,
			String mainObjCreated, String reconsiliationID,
			JsonResponseModel jsr, Document doc, String docsCSV,
			List<String> docListCreated) throws DfException {

		DfLogger.error(this, UploadServiceManagerImpl.ENTERED_FAILURE_BLOCK,
				null, null);
		// String objID = "";

		// if (returnedWorkflow == null && wfCheck) {

		if (StringUtil.isEmptyOrNull(workflowIDstr) && wfCheck) {
			DfLogger.error(
					this,
					UploadServiceManagerImpl.WORKFLOW_NOT_STARTED_FOR_OBJECT
							+ mainObjCreated
							+ UploadServiceManagerImpl.WORKFLOW_ID
							+ workflowIDstr
							+ UploadServiceManagerImpl.UPDATE_AUDIT_FAILED_GET_WORKFLOW,
					null, null);
			// setting JSON response if failed due to Applicaiton Error
			jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
					+ reconsiliationID + Constants.ERROR_DESCRIPTION_PART2
					+ Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED_UNINSTALLED);
		}

		// else if (returnedWorkflow.getId().equalsIgnoreCase(
		else if (workflowIDstr
				.equalsIgnoreCase(UploadServiceManagerImpl.NULL_ID)
				|| StringUtil.isEmptyOrNull(workflowIDstr)) {

			DfLogger.error(
					this,
					"Hit this as failed is false but failed check on returnedWorkflowID which we no longer set",
					null, null);
			// Update Ingestion result tables
			DfcUtils.uploadError(reconsiliationID, doc.getHeader().getSource(),
					Constants.ERROR_CODE_WORKFLOW_NOT_STARTED, "",
					Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED, session);
			// setting JSON response if failed due to Applicaiton Error
			jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
					+ reconsiliationID + Constants.ERROR_DESCRIPTION_PART2
					+ Constants.ERROR_MESSAGE_WORKFLOW_NOT_STARTED);
		} else {

			// can this ever happen? JSON is assumed out of order, we
			// then proceed to order it ourselves?
			DfLogger.error(this, DFS_ATTACHMENT_PROCESS_FAILED_BAD_JSON, null,
					null);
			// Update Ingestion result tables
			DfcUtils.uploadError(reconsiliationID, doc.getHeader().getSource(),
					Constants.ERROR_CODE_UNEXPECTED_REQUEST_BODY, "",
					Constants.ERROR_MESSAGE_UNEXPECTED_REQUEST_BODY, session);
			jsr.setDescription(Constants.ERROR_DESCRIPTION_PART1
					+ reconsiliationID + Constants.ERROR_DESCRIPTION_PART2
					+ Constants.ERROR_MESSAGE_UNEXPECTED_REQUEST_BODY);
		}

		jsr.setResult(Constants.RESPONSE_ERROR);
		IDfId forlderID = null;

		// should be using true length loops For(element:
		// itterateList){do stuff}
		for (String element : docListCreated) {
			// cleaning the failed objects - must have DELETE permission
			IDfSysObject sysObj = (IDfSysObject) session.getObject(new DfId(
					element));
			forlderID = sysObj.getFolderId(0);
			sysObj.destroyAllVersions();
		}
		docsCSV = DfcUtils.getDocListAsCSV(docListCreated);
		// if any folders for Attachments have been created, clean that
		// one too
		if (forlderID != null && forlderID.getId().startsWith("0b0")) {
			DfLogger.debug(this, DOCUMENTS_CLEANED_DELETING_EMPTY_FOLDER
					+ forlderID.getId(), null, null);
			IDfFolder folderObj = (IDfFolder) session.getObject(forlderID);
			folderObj.destroyAllVersions();
		}

		DfLogger.debug(this,
				DOCUMENT_CLEANUP_COMPLETED_DELETED_THE_FOLLOWING_OBJECT_I_DS
						+ docsCSV, null, null);

		// move to front of finaly block, if it always happens do cheap fast
		// ops first
		// setting JSON response reconsilioationID in all the scenarios
		jsr.setReconciliation_id(reconsiliationID);
	}

	/**
	 * Method to update dgms_upload_failure table in the event of failure
	 * creating any of required documetns in JSON request OR starting a
	 * workflow.
	 * 
	 * @param jsi
	 *            Java object of JSON request for a single document
	 * @param error_id
	 *            Error ID
	 * @param errorDescription
	 *            Description of an error
	 * @param exception
	 *            exception thrown
	 * @param _session
	 *            IDfSession
	 * @param releaseSession
	 *            true if release session required, false if no release of
	 *            session required
	 * 
	 * 
	 * @throws DfException
	 *             if running a query encounters issues.
	 * 
	 * 
	 */
	public void updateDMSAuditTableFailure(Document jsi, String error_id,
			String errorDescription, String exception, IDfSession _session,
			boolean releaseSession) throws DfException

	{

		// CM CodeReview: Description error must be reduces as it may be too
		// long.
		if (errorDescription.length() > 999) {
			errorDescription = errorDescription.substring(0, 998);
		}
		if (exception.length() > 999) {
			exception = exception.substring(0, 998);
		}

		try {

			if (_session == null) {
				_session = getSessionRepository().getSession();
				DfLogger.debug(
						this,
						"Acquired session in updateDMSAuditTableFailure() called from Controller class",
						null, null);
			}
			// Call to update either the error or success table pending the
			// result of processing
			// Use the response object to determine what to do
			DfLogger.debug(this, "Update Ingestion Audit table :Failure", null,
					null);
			DfcUtils.uploadError(jsi.getHeader().getReconciliationId(), jsi
					.getHeader().getSource(), error_id, errorDescription,
					exception, _session);
		} finally {
			if (_session != null && releaseSession) {
				release(_session);
			}
		}
	}

	/**
	 * Method to process an object creation in repository and setting its
	 * content and metadata.
	 * 
	 * @param doc
	 *            An object representation of a single document from JSON
	 *            request
	 * @param isMainMailDoc
	 *            true if this is Mail Item, false if this is attachments
	 *            document
	 * @param mailItemID
	 *            String representation of created r_object_id of a mail Item,
	 *            if any.
	 * 
	 * @return String returns r_object_id value of an object created
	 * 
	 * @throws DfException
	 *             if DfException encountered while executing method throws
	 *             IOException if IOException encountered while executing method
	 * @throws ParseException
	 *             if ParseException encountered while executing method
	 * 
	 */
	private String processContent(Document doc, boolean isMainMailDoc,
			boolean isRobotics, String mailItemID) throws DfException,
			IOException, ParseException {

		ByteArrayOutputStream objectContent = null;
		String[] idObjectFolder;
		String objId = "";
		byte[] decodeByte;
		HashMap<String, Object> attributeValuesMap = null;

		String encodeType = doc.getAttachment().getEncoded();
		String encodeData = doc.getAttachment().getData().trim();
		String title = doc.getHeader().getTitle();

		if (StringUtil.isEmptyOrNull(encodeType)
				|| encodeType.equalsIgnoreCase("none")) {
			decodeByte = encodeData.getBytes(StandardCharsets.UTF_8);
			objectContent = DfcUtils.getContentStream(decodeByte);
		} else if (encodeType.contains("64")) {
			decodeByte = Base64.decodeBase64(encodeData
					.getBytes(StandardCharsets.UTF_8));
			objectContent = DfcUtils.getContentStream(decodeByte);

		} else {
			DfLogger.error(this, "Encoding:: " + encodeType
					+ " discovered that is NOT supported.", null, null);
			throw new DfException(
					"Encoding:: "
							+ encodeType
							+ " discovered that is NOT supported by current DFS Attachments services.");
		}

		if (isMainMailDoc) {

			DfLogger.debug(this, "Processing Main mail item document: " + title
					+ " with mime Type: " + doc.getHeader().getMimeType(),
					null, null);
			// set metadata for main document
			attributeValuesMap = DfcUtils.setAttributesFromMap(
					doc.getMetadata(), configValues, false);
			idObjectFolder = DfcUtils.createDocsWithDFC(attributeValuesMap,
					parsedXMLConfig, objectContent, doc, null, session);
			objId = idObjectFolder[0];
			DfLogger.debug(this, "Main mail document:: " + title
					+ " created with ID: " + objId, null, null);

		}

		else if (isRobotics) {
			DfLogger.debug(this, "Processing Robotics document: " + title
					+ " with mime Type: " + doc.getHeader().getMimeType(),
					null, null);
			IDfDocument mailItem = (IDfDocument) session.getObject(new DfId(
					mailItemID));
			IDfFolder mailItemFolder = (IDfFolder) session.getObject(mailItem
					.getFolderId(0));
			IDfDocument roboticsDoc = (IDfDocument) session
					.newObject("dm_document");
			roboticsDoc.setObjectName(mailItemID + "_XML");
			roboticsDoc.setACLDomain("DMS");
			roboticsDoc.setACLName("dgms_dms_general");
			roboticsDoc.setContentType(doc.getHeader().getFormat()
					.toLowerCase());
			roboticsDoc.setContent(objectContent);
			roboticsDoc.link(mailItemFolder.getFolderPath(0));
			roboticsDoc.save();

			objId = roboticsDoc.getObjectId().toString();
			DfLogger.debug(this,
					"XML DOCUMENT CREATED : " + roboticsDoc.getObjectName()
							+ " created with ID: " + objId
							+ " and is stored here " + mailItem.getFolderId(0),
					null, null);
		}

		else {

			DfLogger.debug(this,
					"About to process Attachment document,  isSubmissionIdSet  =  "
							+ isSubmissionIdSet, null, null);
			// set metadata for attachments
			attributeValuesMap = DfcUtils.setAttributesFromMap(
					doc.getMetadata(), configValues, true);
			// set create Attachment doc with DFC
			idObjectFolder = DfcUtils.createDocsWithDFC(attributeValuesMap,
					parsedXMLConfig, objectContent, doc, mailItemID, session);
			objId = idObjectFolder[0];
			DfLogger.debug(this, "Attachment document:: " + title
					+ " created with ID: " + objId, null, null);
			// set submissions_folder_id on mail item

			if (!isSubmissionIdSet) {
				// IDfCollection collUpd = null;
				String folderId = idObjectFolder[1];
				String updateSubmissionFldrDQL = "UPDATE "
						+ parsedXMLConfig.getDfsConfig().getImportRules()
								.getTargetType()
						+ " OBJECT SET submissions_folder_id = '" + folderId
						+ "' WHERE r_object_id = '" + mailItemID + "'";
				DfLogger.debug(this,
						"update_submissionFolderForMailItem_dql + @"
								+ updateSubmissionFldrDQL + "@", null, null);
				try {

					// JG_002 Change utility method
					// collUpd =
					// DfcUtils.executeQuery(updateSubmissionFldrDQL,session,
					// false);
					DfcUtils.updateDeleteQuery(updateSubmissionFldrDQL, session);
					// END JG_002

					// JG_002 - Used elsewhere in the code as part of the loop
					// for ensuring that mail item is updated to reflect
					// attachment status- Part of a previous bug fix
					isSubmissionIdSet = true;
				} finally {
					// if (collUpd != null)
					// collUpd.close();
				}
			}
		}

		return objId;
	}

	/**
	 * Method to check if Reconsiliation ID already has been processed and
	 * record of that is found in table (success/failure).
	 * 
	 * @param reconcilID
	 *            Java object of JSON request for a single document
	 * @param successCheck
	 *            true if release session required, false if no release of
	 *            session required
	 * 
	 * @return Boolean returns true if any number of results running query
	 *         returned. False - no results returned running query
	 * 
	 * @throws DfException
	 *             if running a query encounters issues.
	 * 
	 */
	@Override
	public boolean checkIfRecIdProcessed(String reconcilID, boolean successCheck)
			throws DfException {

		boolean prevProcessed = false;
		String timeStamp = "";
		IDfSession triageSession = null;
		IDfCollection coll = null;
		String dql = "";
		try {
			triageSession = getSessionRepository().getSession();
			if (successCheck)
				dql = "select reconciliation_id, timestamp from dm_dbo.dgms_upload_success where reconciliation_id = '"
						+ reconcilID + "'";
			else
				dql = "select reconciliation_id, timestamp from dgms_upload_failure where reconciliation_id = '"
						+ reconcilID + "'";

			coll = DfcUtils.executeQuery(dql, triageSession, true);
			int processed = 0;

			while (coll.next()) {
				timeStamp = coll.getTime("timestamp").toString();
				processed++;
			}

			if (processed > 0) {
				DfLogger.debug(this, " Input for " + reconcilID
						+ " was previously processed on " + timeStamp, null,
						null);
				prevProcessed = true;
			}

		} finally {

			// JG_002 move collection close from try to finally block
			if (coll != null)
				coll.close();
			// END JG_002

			if (triageSession != null)
				DfcSessions.release(triageSession);
		}
		return prevProcessed;
	}

	/**
	 * 
	 * @param submittedType
	 *            the value of the type passed in from parsed JSON request for a
	 *            document marked as XML
	 * @param successCheck
	 *            true to check robotics config lookup table
	 * @return boolean
	 * @throws DfException
	 */
	public boolean checkRoboticsType(String submittedType,
			String submittedFormat) throws DfException {
		boolean permitted = false;

		String roboticCategory = "";
		IDfSession triageSession = null;
		IDfCollection coll = null;
		String dql = "";
		try {
			triageSession = getSessionRepository().getSession();

			// JG_003 - include active check in the lookup
			dql = "select robotic_category from dm_dbo.dgms_robotic_config where active = true and form_type = '"
					+ submittedType
					+ "' and form_format = '"
					+ submittedFormat
					+ "'";

			// dql =
			// "select robotic_category from dm_dbo.dgms_robotic_config where  form_type = '"
			// + submittedType
			// + "' and form_format = '"
			// + submittedFormat
			// + "'";
			// END JG_003

			coll = DfcUtils.executeQuery(dql, triageSession, true);
			int valid = 0;

			while (coll.next()) {
				roboticCategory = coll.getString("robotic_category");
				valid++;
			}

			if (valid > 0) {
				DfLogger.debug(this,
						" Valid Robotics configuration, category is "
								+ roboticCategory, null, null);
				permitted = true;
			}

		} finally {

			// JG_002 move collection close from try to finally block
			if (coll != null)
				coll.close();
			// END JG_002

			if (triageSession != null)
				DfcSessions.release(triageSession);
		}
		return permitted;
	}

	/**
	 * 
	 * @param submittedType
	 *            the value of the type passed in from parsed JSON request for a
	 *            document marked as XML
	 * @param successCheck
	 *            true to check robotics config lookup table
	 * @return boolean
	 * @throws DfException
	 */

	public IDfCollection getRoboticsConfig(String formType) throws DfException

	{
		IDfCollection coll = null;
		IDfSession querySession = null;

		try {
			querySession = getSessionRepository().getSession();
			String dql = "select form_type,form_format,form_source,robotic_category,robotic_endpoint from dm_dbo.dgms_robotic_config where form_type = '"
					+ formType + "'";
			coll = DfcUtils.executeQuery(dql, querySession, true);
		}

		catch (DfException dfe) {
			DfLogger.error(this, " Unable to get Robotics Cinfig Collection",
					null, null);
		}
		return coll;
	}

	// tobe replaced with workflow bean version
	public String startResttWorkflow(String source, String target,
			String mailItemObjId, String action, String note, String category,
			String retention_policy, List<String> CustomerIds)
			throws DfException {
		DfLogger.debug(this, " Starting REST Primary Process", null, null);
		IDfSession testSession = null;
		IDfId wfId = null;
		String workflowId = "";
		/*
		 * CB CodeReview 08/Dec/16 For type safety, only declare IDobjectList
		 * here, and initialise it later using CustomerIds as a parameter
		 * List<Object> IdobjectList = null;
		 */
		List<Object> IdobjectList = new ArrayList<Object>();

		try {
			DfLogger.debug(this, "Get the session", null, null);
			testSession = getSessionRepository().getSession();

			DfLogger.debug(this, " use workflow builder to get the workflow",
					null, null);

			String workFlowID = gettWorkflowID(Constants.REST_PROCESS_NAME);

			DfLogger.debug(this, " The workflow process template  ID is :"
					+ workFlowID, null, null);

			IDfWorkflowBuilder builder = testSession
					.newWorkflowBuilder(new DfId(workFlowID));
			builder.initWorkflow();

			DfLogger.debug(this,
					"builder.getWorkflow().getObjectId().toString() = "
							+ builder.getWorkflow().getObjectId().toString(),
					null, null);

			DfLogger.debug(this, " associate workflow with workflowex", null,
					null);
			IDfWorkflowEx wfe = (IDfWorkflowEx) builder.getWorkflow();

			DfLogger.debug(
					this,
					" set process varaibles with workflowex update when new process deployed",
					null, null);
			wfe.setPrimitiveObjectValue("request_source", source);
			wfe.setPrimitiveObjectValue("mail_item_id", mailItemObjId);
			wfe.setPrimitiveObjectValue("action", action);
			wfe.setPrimitiveObjectValue("note", note);
			wfe.setPrimitiveObjectValue("robot_submitted_category", category);
			if (action.equals("Close")) {
				retention_policy = calculateRetentionPolicy(mailItemObjId,retention_policy);
				wfe.setPrimitiveObjectValue("retention_policy",retention_policy);
				wfe.setPrimitiveObjectValue("retention_period",calculateRetentionPeriod(retention_policy));
			}
			if (CustomerIds != null) {
				DfLogger.debug(this,
						" CustomerIds size = " + CustomerIds.size(), null, null);
				if (CustomerIds.size() > 0) {
					DfLogger.debug(this, " Convert the list", null, null);

					/*
					 * CB CodeReview 08/Dec/16 For type safety, initialise
					 * IdobjectList here using CustomerIds as a parameter
					 * IdobjectList = new ArrayList<Object>(CustomerIds);
					 */
					IdobjectList = new ArrayList<Object>(CustomerIds);//(List) CustomerIds;
					DfLogger.debug(this, " set the repeating attributes ",
							null, null);
					wfe.setRepeatingPrimitiveObjectValues("customer_ids",
							IdobjectList);
				}

				else {
					DfLogger.debug(
							this,
							"Custom IDS not null but No repeating atts to set, just instantiate the process variable ",
							null, null);
					// set empty value of repeating process variable so that it
					// still instantiated in the workflow

					// IdobjectList.add("");
					wfe.setRepeatingPrimitiveObjectValues("customer_ids",
							IdobjectList);
				}
			}

			else {
				DfLogger.debug(
						this,
						"No repeating atts to set, just instantiate the process variable ",
						null, null);
				// set empty value of repeating process variable so that it
				// still instantiated in the workflow

				// IdobjectList.add("");
				wfe.setRepeatingPrimitiveObjectValues("customer_ids",
						IdobjectList);
			}

			wfId = (IDfId) builder.runWorkflow();
			DfLogger.debug(this, " running workflow id = " + wfId.getId(),
					null, null);

			workflowId = wfId.getId();
			wfe = null;

		}

		catch (DfException dfe) {
			DfLogger.error(this, " Unable to getWORKFLOWEX", null, null);
			dfe.printStackTrace();
		}

		finally {
			if (testSession != null)
				DfcSessions.release(testSession);
		}

		IdobjectList.clear();
		return workflowId;
	}

	private int calculateRetentionPeriod(String retention_policy) throws DfException {
		
		int retentionPeriod =0;

		IDfSession dqlSession = null;
		IDfCollection coll = null;
		String getRetentionPeroidDQL = "";

		getRetentionPeroidDQL = "select retention_period from dm_dbo.dgms_retention_policy where retention_name = '"
				+ retention_policy + "'";

		DfLogger.debug(this, "DQL TO BE RUN =:" + getRetentionPeroidDQL + ";", null, null);

		try {
			dqlSession = getSessionRepository().getSession();
			coll = DfcUtils.executeQuery(getRetentionPeroidDQL, dqlSession, true);
			while (coll.next()) {

				retentionPeriod = coll.getInt("retention_period");
			}

		} finally {
			if (coll != null)
				coll.close();

			if (dqlSession != null)
				DfcSessions.release(dqlSession);
		}
	
		DfLogger.debug(this, "RETURNED RETENTION PERIOD IS " + retentionPeriod, null,
				null);
		return retentionPeriod;
	}

	// Method stub to pass workflow info as a bean- doenst work come back to
	// this
	public String startResttWorkflow(WorkFlowBean wfbean) throws DfException {
		DfLogger.debug(this, " Starting REST Primary Process BEAN VERSION",
				null, null);
		IDfSession testSession = null;
		IDfId wfId = null;

		if (wfbean == null) {
			DfLogger.debug(this, "BEAN IS NULL ", null, null);
		}

		else {
			DfLogger.debug(
					this,
					"TESTING THE BEAN for r_object_id " + wfbean.getUnique_id(),
					null, null);

			try {
				testSession = getSessionRepository().getSession();

				DfLogger.debug(this,
						" use workflow builder to get the workflow", null, null);
				IDfWorkflowBuilder builder = testSession
						.newWorkflowBuilder(new DfId(
								gettWorkflowID(Constants.REST_PROCESS_NAME)));
				builder.initWorkflow();

				DfLogger.debug(this,
						"builder.getWorkflow().getObjectId().toString() = "
								+ builder.getWorkflow().getObjectId()
										.toString(), null, null);

				DfLogger.debug(this, " associate workflow with workflowex",
						null, null);
				IDfWorkflowEx wfe = (IDfWorkflowEx) builder.getWorkflow();

				DfLogger.debug(this, " set process varaibles with workflowex",
						null, null);

				wfe.setPrimitiveObjectValue("request_source",
						wfbean.getSource());
				wfe.setPrimitiveObjectValue("mail_item_id",
						wfbean.getUnique_id());
				wfe.setPrimitiveObjectValue("robotic_action",
						wfbean.getAction());
				wfe.setPrimitiveObjectValue("note", wfbean.getNote());

				wfId = (IDfId) builder.runWorkflow();
				DfLogger.debug(this, " running workflow id = " + wfId.getId(),
						null, null);

				DfLogger.debug(this, " Set wfe null = " + wfId.getId(), null,
						null);
				wfe = null;

			}

			catch (DfException dfe) {
				DfLogger.debug(this, " Unable to getWORKFLOWEX", null, null);
				dfe.printStackTrace();
			}

			finally {
				if (testSession != null)
					DfcSessions.release(testSession);
			}

		}
		return wfId.getId();
	}

	public String gettWorkflowID(String workflowName) throws DfException {
		String workFlowId = "";

		IDfSession dqlSession = null;
		IDfCollection coll = null;
		String workflowdql = "";

		workflowdql = "select r_object_id from dm_process where object_name  = '"
				+ workflowName + "'";

		DfLogger.debug(this, "DQL TO BE RUN =:" + workflowdql + ";", null, null);

		/*
		 * CB CodeReview 08/Dec/16 There should only be one dm_process with this
		 * name. Therefore, use DFC/API rather than DQL: IDfId idWF =
		 * dqlSession.getIdByQualification("dm_process where object_name  = '"+
		 * workflowName + "'") workflowId = idWF.toString() removes the
		 * requirement for IDfCollection, and is quicker and easier.
		 */
		try {
			dqlSession = getSessionRepository().getSession();
			coll = DfcUtils.executeQuery(workflowdql, dqlSession, true);
			while (coll.next()) {

				workFlowId = coll.getString("r_object_id");
			}

		} finally {
			// JG_002 move collection close from try to finally block
			if (coll != null)
				coll.close();
			// END JG_002

			if (dqlSession != null)
				DfcSessions.release(dqlSession);
		}

		DfLogger.debug(this, "RETURNED WORKFLOW ID IS " + workFlowId, null,
				null);
		return workFlowId;
	}

	public Boolean checkmailItemEvents(String mailItemId) throws DfException {

		// Determines if the latest events for the mail item are one of the
		// intial 3 allowed
		boolean events = false;
		IDfSession dqlSession = null;
		IDfCollection coll = null;

		String eventName = "";

		String eventsDql = "select event_name from dgms_dms_event where source_object_id = '"
				+ mailItemId
				+ "' order by r_creation_date desc enable (return_top 1)";

		DfLogger.debug(this, "Events DQL is " + eventsDql, null, null);

		try {
			dqlSession = getSessionRepository().getSession();
			coll = DfcUtils.executeQuery(eventsDql, dqlSession, true);

			// JG_002 Remove unused variable

			while (coll.next()) {
				eventName = coll.getString("event_name");

			}

			// Check values

			if (eventName.equalsIgnoreCase("Created in Documentum")
					|| eventName.equalsIgnoreCase("Initial Receipt")
					|| eventName.equalsIgnoreCase("Scanned")) {
				events = true;
			}

		} finally {
			// JG_002 move collection close from try to finally block
			if (coll != null)
				coll.close();
			// END JG_002

			if (dqlSession != null)
				DfcSessions.release(dqlSession);
		}

		return events;
	}

	public int isMailItemWorked(String mailItemId) throws DfException {
		IDfSession dqlSession = null;
		IDfCollection coll = null;

		String statusDql = "select status from dm_dbo.dgms_robotic_tracking where r_object_id = '"
				+ mailItemId + "'";

		// Get status from tracking table
		int status = -1;

		// JG_002 Remove duplicate calls to execute query

		try {
			dqlSession = getSessionRepository().getSession();
			coll = DfcUtils.executeQuery(statusDql, dqlSession, true);

			while (coll.next()) {
				status = coll.getInt("status");

			}

		} finally {

			// JG_002 move collection close from try to finally block
			if (coll != null)
				coll.close();
			// END JG_002

			if (dqlSession != null)
				DfcSessions.release(dqlSession);
		}

		return status;
	}

	// TB - Added in New Method to check Retention Policy exists, if it does not
	// replace with default one on Mail Item.
	// This is to stop mail items being closed with an invalid policy and
	// setting disposal data to one day before the mail item was closed.
	private String calculateRetentionPolicy(String mailitemId, String retention_policy) throws DfException {
			
		IDfSession dqlSession = null;
		IDfCollection checkRetentionColl = null;
		IDfCollection getMailItemRetentionColl = null;
		IDfCollection invalidRetentionNoteColl = null;
		IDfCollection validRetentionNoteColl = null;
		IDfCollection baDefaultRetentionColl = null;
		IDfCollection defaultRetentionColl = null;
		String returnedRetention = null;		
        String checkRetentionDQL = "SELECT retention_name FROM dm_dbo.dgms_retention_policy WHERE retention_name = '" + retention_policy + "'";
        String checkforBADefaultRetentionDQL = "SELECT retention_name FROM dm_dbo.dgms_retention_policy WHERE default_for_ba IN (SELECT business_area FROM dgms_mail_item WHERE r_object_id = '" + mailitemId + "')";
        String getDefaultRetentionPolicyDQL = "SELECT retention_name FROM dm_dbo.dgms_retention_policy WHERE retention_isdefault = true";
        //Get Now.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
         try {
        	DfLogger.debug(this, "Getting a Session for running DQL for Calculating Retention Policy",null,null);
			dqlSession = getSessionRepository().getSession();
			DfLogger.debug(this, "Running Query to check if the Retention Policy Exists. Query is: " + checkRetentionDQL, null, null);
			checkRetentionColl = DfcUtils.executeQuery(checkRetentionDQL, dqlSession, true);
			
         if (checkRetentionColl.next())      {
        	 DfLogger.debug(this,"Retention Policy was valid, using this policy.",null,null);
        	 String correctRetentionNote = "Retention Policy determined by Robotics and changed to : "+ retention_policy + "|| Added by Robot On: " + timeStamp;
        	 String validRetentionNoteDQL = "UPDATE dgms_mail_item OBJECT APPEND notes = '"+ correctRetentionNote +"' WHERE r_object_id ='"+mailitemId+"'";
        	 validRetentionNoteColl = DfcUtils.executeQuery(validRetentionNoteDQL,dqlSession,true);
        	 DfLogger.debug(this,"Setting Retention Policy Sent by Robotics: " + retention_policy,null,null);
        	 returnedRetention =  retention_policy;
        }
        else
        {
        	  DfLogger.debug(this,"Retention Policy is empty or invalid, using default policy from mail item.",null,null);
               String getMailItemRetentnionDQL = "SELECT retention_policy FROM dgms_mail_item WHERE r_object_id ='"+mailitemId+"'";
               DfLogger.debug(this,"Running Query to get Mail Items Default Retention Policy. " + getMailItemRetentnionDQL,null,null);
               getMailItemRetentionColl = DfcUtils.executeQuery(getMailItemRetentnionDQL, dqlSession, true);
               if (getMailItemRetentionColl.next())
               {      
            	  String mailItemRetention = getMailItemRetentionColl.getString ("retention_policy");
				  if (mailItemRetention!=null && !mailItemRetention.equals(""))
				  {
					  String REJECTION_NOTE = "Retention Policy submitted by Robotics empty or invalid, maintain existing: " + mailItemRetention + "|| Added by Robot On: " + timeStamp;
					  String invalidRetentionNoteDQL = "UPDATE dgms_mail_item OBJECT APPEND notes = '"+REJECTION_NOTE+"' WHERE r_object_id ='"+mailitemId+"'";
					  DfLogger.debug(this,"Running Query to Add Note about invalid Retention Policy. " + invalidRetentionNoteDQL,null,null);
					  invalidRetentionNoteColl = DfcUtils.executeQuery(invalidRetentionNoteDQL, dqlSession, true);
					  DfLogger.debug(this, "Setting Retention Policy to Mail Item Retention Policy" + mailItemRetention, null, null);
				      returnedRetention = mailItemRetention;
				  } 
				  else 
				  {
					  String DEFAULT_NOTE = null;
					  DfLogger.debug(this,"Retention Policy was null on Mail Item Checking for BA Default Policy", null, null);
					  DfLogger.debug(this,"Running Query to get BA default Retention Policy: " + checkforBADefaultRetentionDQL,null,null);
					  baDefaultRetentionColl = DfcUtils.executeQuery(checkforBADefaultRetentionDQL,dqlSession,true);
					  if (baDefaultRetentionColl.next()){
						  DEFAULT_NOTE = "Retention Policy submitted by Robotics empty or invalid,setting default BA Retention Policy || Added by Robot On: " + timeStamp;
						  DfLogger.debug(this, "Setting Retention Policy to BA Default Policy: " + baDefaultRetentionColl.getString("retention_name"), null, null);
						  returnedRetention = baDefaultRetentionColl.getString("retention_name");
					  } 
					  else 
					  {
						  DEFAULT_NOTE = "Retention Policy submitted by Robotics empty or invalid,setting default Retention Policy || Added by Robot On: " + timeStamp;
						  DfLogger.debug(this,"No Default Policy for BA using Default Policy for System", null,null);
						  DfLogger.debug(this,"Running Query to get Default Retetnion Policy for System: " + getDefaultRetentionPolicyDQL,null,null);
						  defaultRetentionColl = DfcUtils.executeQuery(getDefaultRetentionPolicyDQL,dqlSession,true);
						  if (defaultRetentionColl.next())
						  {
							  DfLogger.debug(this, "Setting Retention Policy to System Default Retention Policy" + defaultRetentionColl.getString (retention_policy), null, null);
						      returnedRetention = defaultRetentionColl.getString (retention_policy);
						  }
					  }
					   String invalidRetentionNoteDQL = "UPDATE dgms_mail_item OBJECT APPEND notes = '"+DEFAULT_NOTE+"' WHERE r_object_id ='"+mailitemId+"'";
					   DfLogger.debug(this,"Running Query to Add Note about invalid Retention Policy. " + invalidRetentionNoteDQL,null,null);
					   invalidRetentionNoteColl = DfcUtils.executeQuery(invalidRetentionNoteDQL, dqlSession, true);
  
				  }
                      
               }               
        } 
        }  finally {
			if (checkRetentionColl !=null){
				checkRetentionColl.close();
			}
			if (getMailItemRetentionColl !=null){
				getMailItemRetentionColl.close();
			}
			if (invalidRetentionNoteColl !=null){
				invalidRetentionNoteColl.close();
			}
			if (validRetentionNoteColl !=null){
				validRetentionNoteColl.close();
			}

			if (baDefaultRetentionColl !=null){
				baDefaultRetentionColl.close();
			}
			if (defaultRetentionColl !=null){
				validRetentionNoteColl.close();
			}
			
			if(dqlSession !=null){
				DfcSessions.release(dqlSession);
			}
        	
        }
        	
        
        return returnedRetention;
  }

	// JG_001 - Note this should be eventually be moved to be included as part
	// of the mail item creation code.

	/*
	 * public void setRoboticCategory(String mailItemId, String
	 * roboticType)throws DfException { IDfSession dqlSession = null;
	 * IDfCollection coll = null;
	 * 
	 * DfLogger.debug(this, "setRoboticCategory: MailID:  "+ mailItemId
	 * +"Type: "+ roboticType, null, null);
	 * 
	 * String updateCategoryDql=
	 * "update dgms_mail_item object set category =(select robotic_category from dm_dbo.dgms_robotic_config where form_type = '"
	 * + roboticType +"') where r_object_id = '"+mailItemId+"'";
	 * 
	 * DfLogger.debug(this, "setRoboticCategory: DQL:  "+ updateCategoryDql,
	 * null, null);
	 * 
	 * try{ dqlSession = getSessionRepository().getSession();
	 * 
	 * CB CodeReview 08/Dec/16 Consider using the new method
	 * DfcUtils.updateDeleteQuery(sDQL, dqlSession) Removes the requirement for
	 * IDfCollection, as the update query is closed inline
	 * 
	 * coll = DfcUtils.executeQuery(updateCategoryDql, dqlSession, true);
	 * 
	 * 
	 * CB CodeReview 08/Dec/16 Collection should be closed in the finally block
	 * (moot point if you use DfcUtils.updateDeleteQuery(sDQL, dqlSession)
	 * 
	 * if (coll != null) coll.close(); } finally { if (dqlSession != null)
	 * DfcSessions.release(dqlSession); }
	 * 
	 * }
	 */

	public void setRoboticStatusAndCategory(String mailItemId,
			String roboticType) throws DfException {
		IDfSession dqlSession = null;

		DfLogger.debug(this, "setRoboticStatusAndCategory: MailID:  "
				+ mailItemId, null, null);

		String updateRoboticStatusAndCategory = "update dgms_mail_item object set sent_to_robotics = TRUE, set category =(select robotic_category from dm_dbo.dgms_robotic_config where form_type = '"
				+ roboticType + "') where r_object_id = '" + mailItemId + "'";

		DfLogger.debug(this, "setRoboticStatusAndCategory: DQL:  "
				+ updateRoboticStatusAndCategory, null, null);

		try {
			dqlSession = getSessionRepository().getSession();

			DfcUtils.updateDeleteQuery(updateRoboticStatusAndCategory,
					dqlSession);

		} finally {

			if (dqlSession != null)
				DfcSessions.release(dqlSession);
		}

	}

	// END JG_001

	public void updateRoboticTrackingTable(int status, String mailItemId,
			String error, Date now) throws DfException {

		IDfSession dqlSession = null;
		// IDfCollection coll = null;

		DfLogger.debug(this, "Updating the tracking table", null, null);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

		String sNow = dateFormat.format(now);

		String queryBase = "update  dm_dbo.dgms_robotic_tracking ";
		String whereClause = " where r_object_id = '" + mailItemId + "'";

		String qualification = " set robot_response_date = date('" + sNow
				+ "')";

		if (!StringUtil.isEmptyOrNull(error)) {
			qualification = qualification.concat(" , error_condition = '"
					+ error + "'");
		}

		if (status == -1) {
			// do nothing
		}

		else {
			qualification = qualification.concat(" ,  status = " + status);
		}

		String updateDql = queryBase + qualification + whereClause;

		DfLogger.info(this, "Updating statement = " + updateDql, null, null);

		try {

			dqlSession = getSessionRepository().getSession();

			// JG_002 Change utility method
			DfcUtils.updateDeleteQuery(updateDql, dqlSession);
			// END JG_002

		} finally {
			// JG_002 move collection close from try to finally block
			// if (coll != null)
			// coll.close();
			// END JG_002

			if (dqlSession != null)

				DfcSessions.release(dqlSession);
		}
	}
}
