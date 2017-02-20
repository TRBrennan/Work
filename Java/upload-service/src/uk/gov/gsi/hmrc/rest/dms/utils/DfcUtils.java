package uk.gov.gsi.hmrc.rest.dms.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import uk.gov.gsi.hmrc.rest.dms.model.Document;
import uk.gov.gsi.hmrc.rest.dms.model.Metadata;
import uk.gov.gsi.hmrc.rest.dms.xmlobjects.config.DfsConfigs;
import uk.gov.gsi.hmrc.rest.dms.xmlobjects.config.DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.impl.util.StringUtil;
import com.documentum.fc.common.DfLogger;
import com.documentum.thirdparty.javassist.bytecode.stackmap.TypeData.ClassName;

/**
 * DfcUtils class has all static methods that are being used to help execute DFC calls to interact with Docuemntum repository while processing DFS document creation
 * 
 */
/**
 * @author RC14663
 * 
 * * 	Modification history
 *	 ------------------------------------------
 * 	Version 1.0 - Initial version August 2016
 * 	Versiion 2.0 - Inclusion of Robotics November 2016
 * 	Version 2.1 - EDT-609 03/01/2017 Added Method for String Manipulation around Exceptions message length and Single Quotes 
 * 
 *  
 *  RC14670 14/12/2016 Audit :JG_001 EKDMS-1280 Add new method to generated a sorted list of docs submitted in json. Sorted by primary content value.
 */
public class DfcUtils {

	DfcUtils() {
		// private constructor so this class can not be initialized
	}

	/**
	 * Helper method to execute the DQL queries.
	 * 
	 * @param dql
	 *            DQL query
	 * @param session
	 *            IDfSession
	 * @param selectOnly
	 *            True if the passed in DQL is select query, false if dql is
	 *            update/insert query
	 * @return IDfCollection the Documentum collection.
	 * @throws DfException
	 *             if the execution of DQL encounters issues.
	 */
	public static IDfCollection executeQuery(String dql, IDfSession session,
			boolean selectOnly) throws DfException {
		IDfQuery query = new DfQuery();
		query.setDQL(dql);

		if (selectOnly)
			return query.execute(session, IDfQuery.DF_READ_QUERY);
		else
			return query.execute(session, IDfQuery.DF_EXEC_QUERY);
	}
	
	/**
	 * Helper method to execute update or delete queries.
	 * No Collection is returned, as it is implicitly  closed inline
	 * 
	 * @param sDQL
	 *            DQL query
	 * @param sess
	 *            IDfSession
	 * @return void
	 * @throws DfException
	 *             if the execution of DQL encounters issues.
	 */
	public static void updateDeleteQuery(String sDQL, IDfSession sess) throws DfException{
		IDfQuery qry = new DfQuery();
		qry.setDQL(sDQL);
		qry.execute(sess, IDfQuery.DF_EXEC_QUERY).close();
	}
	/**
	 * Helper method hat return the ByteArrayOutputStream for creating DFC
	 * object's content.
	 * 
	 * @param content
	 *            byte array of content
	 * 
	 * @return ByteArrayOutputStream the ByteArrayOutputStream of passed in byte
	 *         array.
	 * @throws IOException
	 *             if the output stream writing encounters issues.
	 */
	public static ByteArrayOutputStream getContentStream(byte[] content)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(content);
		baos.close();

		return baos;
	}

	/**
	 * Helper method to map attribute/value pairs taking JSON attributes and
	 * values and mapping those attributes to corresponding attributes in
	 * Documentum. This will be used to populate a newly created Docuemntum
	 * object.
	 * 
	 * @param contentValuesFromJson
	 *            List of metadata passed in within JSON request
	 * @param configValuesXML
	 *            Map of mapping metadata from XML mapping config object in
	 *            repository
	 * @param isAttachment
	 *            True if the mapping is for DFS attachment, false if mapping is
	 *            for DFS mail item
	 * @return HashMap<String, Object> returns mapped attribute/value pairs to
	 *         be populated on Docuemntum object
	 * @throws ParseException
	 *             if the XML parsing encounters issues.
	 */
	public static HashMap<String, Object> setAttributesFromMap(
			List<Metadata> contentValuesFromJson,
			List<MetaData> configValuesXML, boolean isAttachment)
			throws ParseException {

		Metadata attrInJson = null;
		String attrNameFromJson = "";
		String attrNameDMS = "";
		String attrValue;
		String configMetadata = "";
		String attrType = "";

		HashMap<String, Object> retAttributeValues = new HashMap<String, Object>();
		retAttributeValues.put("isAttachment", isAttachment);

		// int configValSize = ;
		Iterator<Metadata> contAttrIterator = contentValuesFromJson.iterator();
		while (contAttrIterator.hasNext()) {
			attrInJson = contAttrIterator.next();
			attrNameFromJson = attrInJson.getAttr_name();
			DfLogger.debug(DfcUtils.class, "Document Metadata Attr Name: "
					+ attrNameFromJson, null, null);
			DfLogger.debug(DfcUtils.class, "Document Metadata Attr Value: "
					+ attrInJson.getAttr_value(), null, null);

			// check if attribute name has mapping
			for (int i = 0; i < configValuesXML.size(); i++) {
				attrValue = "";
				configMetadata = configValuesXML.get(i).getMetaDataName();

				if (attrNameFromJson.equalsIgnoreCase(configMetadata)) {
					// matching attribute name from JSON to that of incoming xml
					// mapping config file to determine what value goes in
					// Documentum
					// this is the value as it is in Documentum
					attrNameDMS = configValuesXML.get(i).getAttributeName();
					attrType = configValuesXML.get(i).getAttributeType();
					attrValue = attrInJson.getAttr_value();

					// check Type
					if (attrType.equalsIgnoreCase("string"))
						retAttributeValues.put(attrNameDMS,
								attrValue.toString());
					else if (attrType.equalsIgnoreCase("integer"))
						retAttributeValues.put(attrNameDMS,
								Integer.parseInt(attrValue));
					else if (attrType.equalsIgnoreCase("boolean")) {
						// support also values 0 for false and 1 for true
						if (attrValue.equalsIgnoreCase("0"))
							attrValue = "false";
						if (attrValue.equalsIgnoreCase("1"))
							attrValue = "true";

						retAttributeValues.put(attrNameDMS,
								Boolean.parseBoolean(attrValue.toLowerCase()));

					} else if (attrType.equalsIgnoreCase("time")
							|| attrType.equalsIgnoreCase("date")) {
						String dateFormat = configValuesXML.get(i)
								.getAttributeDateFormat();
						if (StringUtil.isEmptyOrNull(dateFormat))
							dateFormat = Constants.DMS_DATE_FORMAT_IF_NOT_SET_IN_XML;

						// this block might throw ParseException - handled by
						// controller class
						DateFormat formatter = new SimpleDateFormat(dateFormat);
						Date fDate = (Date) formatter.parse(attrValue);
						retAttributeValues.put(attrNameDMS, fDate);
					}

				}
			}
		}

		return retAttributeValues;
	}

	/**
	 * Helper method to get a particular value for a particular attribute in
	 * request JSON file.
	 * 
	 * @param contentValuesFromJson
	 *            attribute/value pairs from JSON request
	 * @param requiredAttrNameFromJson
	 *            Attribute name in JSON file for which we want the value to be
	 *            returned
	 * 
	 * @return String attribute value for specified attribute name.
	 * @throws ParseException
	 *             if the XML parsing encounters issues.
	 */
	public static String getMetadataValueFromJson(
			List<Metadata> contentValuesFromJson,
			String requiredAttrNameFromJson) throws ParseException {

		Metadata attrInJson = null;
		String attrNameFromJson = "";
		String attrValue = "";

		Iterator<Metadata> contAttrIterator = contentValuesFromJson.iterator();
		DfLogger.debug(DfcUtils.class, "Looking for "
				+ requiredAttrNameFromJson + " attribute value", null, null);
		while (contAttrIterator.hasNext()) {
			attrInJson = contAttrIterator.next();
			attrNameFromJson = attrInJson.getAttr_name();
			if (attrNameFromJson.equalsIgnoreCase(requiredAttrNameFromJson)) {
				DfLogger.debug(DfcUtils.class, "Document Metadata Attr Found: "
						+ attrNameFromJson, null, null);
				attrValue = attrInJson.getAttr_value();
				break;
			}

		}

		return attrValue;
	}

	/**
	 * Helper method to update object metadata that is being created.
	 * 
	 * @param doc
	 *            IDfDocument
	 * @param attributeVal
	 *            Hashmap of attribute/value mapping
	 * 
	 * @throws DfException
	 *             if the setting of object values encounters issues.
	 */
	public static void updateObjMetadata(IDfDocument doc,
			HashMap<String, Object> attributeVal) throws DfException {

		Set<String> keySet = attributeVal.keySet();
		Iterator<String> keySetIterator = keySet.iterator();
		DfLogger.debug(
				DfcUtils.class,
				"-----------------DfcUtils.updateObjMetadata()----------------",
				null, null);

		while (keySetIterator.hasNext()) {

			String key = keySetIterator.next();
			Object value = attributeVal.get(key);
			DfLogger.debug(DfcUtils.class, "Attribute: " + key
					+ " --value-->: " + value, null, null);
			DfLogger.debug(DfcUtils.class, value.getClass().getName(), null,
					null);
			if (key.equalsIgnoreCase("isAttachment"))
				continue;
			if (key.equalsIgnoreCase("format")) {
				doc.setContentType(attributeVal.get(key).toString());
				continue;
			}

			if (value instanceof Integer) {

				DfLogger.debug(DfcUtils.class, "Integer", null, null);
				doc.setInt(key, (Integer) value);
				continue;
			} else if (value instanceof String) {

				DfLogger.debug(DfcUtils.class, "String", null, null);
				String sValue = value.toString();
				if (sValue.contains(",")) {
					// this is multivalue - JASON might never come in as
					// multivalue, but left it here in case need in future
					String[] multiVal = sValue.split(",");
					for (int availableIndex = 0; availableIndex < multiVal.length; availableIndex++) {

						DfLogger.debug(DfcUtils.class,
								"Setting repaeting String for attribute: "
										+ key + "[" + availableIndex
										+ "] value: "
										+ multiVal[availableIndex], null, null);
						doc.setRepeatingString(key, availableIndex,
								multiVal[availableIndex]);
					}
					continue;
				} else {
					doc.setString(key, sValue);
					continue;
				}
			} else if (value instanceof Boolean) {
				DfLogger.debug(DfcUtils.class, "Boolean", null, null);
				doc.setBoolean(key, (Boolean) value);
				continue;
			} else if (value instanceof Date) {
				DfLogger.debug(DfcUtils.class, "Date", null, null);
				IDfTime dfcTime = new DfTime((Date) (value));
				doc.setTime(key, dfcTime);
				continue;
			}
		}
		
	}

	/**
	 * Helper method to create an object in repository with DFC calls, setting
	 * some common attributes. Backscan feature is commented out, since not
	 * being used by current JSON request
	 * 
	 * @param attVal
	 *            mapped attribute/value pairs to be populated on Documentum
	 *            object
	 * @param parsedXMLConfig
	 *            parsed XML mapping config file object from repository
	 * @param content
	 *            content to be set on created object
	 * @param documentModel
	 *            XML Config object's Document model
	 * @param mailItemDocID
	 *            Mail Item Document ID, if exists
	 * @param _session
	 *            IDfSession
	 * 
	 * @return String[] returns string array (size = 2 always) of created object
	 *         ID and the folder this object has been linked to
	 * 
	 * @throws NullPointerException
	 *             null pointer exception.
	 * @throws DfException
	 *             if the setting of object values and creating it encounters
	 *             issues.
	 */
	public static String[] createDocsWithDFC(HashMap<String, Object> attVal,
			DfsConfigs parsedXMLConfig, ByteArrayOutputStream content,
			Document documentModel, String mailItemDocID, IDfSession _session)
			throws DfException, NullPointerException {

		String linkTo = "";
		IDfId folder = null;
		String[] objectID_FolderIDArray = new String[2];

		String attachmentObjType = parsedXMLConfig.getDfsConfig()
				.getImportRules().getTargetAttachmentType();
		String attachmentFolderType = parsedXMLConfig.getDfsConfig()
				.getImportRules().getTargetAttachmentFolderType();
		String attachmentTargetFolder = parsedXMLConfig.getDfsConfig()
				.getImportRules().getTargetFolder();
		String aclDomain = parsedXMLConfig.getDfsConfig().getImportRules()
				.getTargetAclDomain();
		String acl = parsedXMLConfig.getDfsConfig().getImportRules()
				.getTargetAcl();

		if ((Boolean) attVal.get("isAttachment")
				&& !StringUtil.isEmptyOrNull(mailItemDocID)) {
			DfLogger.debug(DfcUtils.class,
					"About to create Attachment document...", null, null);
			IDfDocument documentAttachm = (IDfDocument) _session
					.newObject(attachmentObjType);
			documentAttachm.setObjectName(documentModel.getHeader().getTitle());
			documentAttachm.setTitle(documentModel.getHeader().getTitle());
			documentAttachm.setACLDomain(aclDomain);
			documentAttachm.setACLName(acl);
			documentAttachm.setContentType(documentModel.getHeader()
					.getFormat().toLowerCase());
			documentAttachm.setContent(content);
			linkTo = attachmentTargetFolder.concat(mailItemDocID.trim());
			folder = FolderUtils.createFolderPath(_session, linkTo,
					Constants.FOLDER_TITLE, acl, attachmentFolderType);
			DfLogger.debug(DfcUtils.class,
					"Saving Attachment to the following location: " + linkTo,
					null, null);
			documentAttachm.link(folder.getId());
			documentAttachm.save();
			objectID_FolderIDArray[0] = documentAttachm.getObjectId().getId();
			objectID_FolderIDArray[1] = folder.getId();
		} else {
			String mainMailObjType = parsedXMLConfig.getDfsConfig()
					.getImportRules().getTargetType();
			String mailItemDestinationFolder = parsedXMLConfig.getDfsConfig()
					.getImportRules().getTargetMailitemFolderAttrJson();
			String dateForCreateFolder = new SimpleDateFormat(
					Constants.DATE_FORMAT, Locale.ENGLISH).format(new Date());
			// first determine the backscan value. backScanDocument is NOT used
			// as per latest JSON document changes, yet leaving it here if needs
			// to be user in future
			/*
			 * boolean backScanDocument = false; try { if
			 * (!StringUtil.isEmptyOrNull
			 * (attVal.get("scanned_backfile").toString())) backScanDocument =
			 * (Boolean) attVal.get("scanned_backfile"); } catch
			 * (NullPointerException nlEx){ DfLogger.error(DfcUtils.class,
			 * "NullPointerException occured while casting backscan attaribute as Boolean. Make sure backscan has value."
			 * ,null,null); throw new NullPointerException(
			 * "NullPointerException in createDocsWithDFC(): Not able to process scanned_backfile attribute for BackScan value provided in JSON"
			 * ); } catch (ClassCastException ccEx){
			 * 
			 * DfLogger.error(DfcUtils.class,
			 * "ClassCastException occured while casting backscan attaribute as Boolean. Will attempt cast to String..."
			 * ,null,null); String strNotBackScanDocument =
			 * attVal.get("scanned_backfile").toString(); backScanDocument =
			 * (strNotBackScanDocument.equalsIgnoreCase("0") ||
			 * strNotBackScanDocument.equalsIgnoreCase("false")); }
			 */

			// craete main document
			DfLogger.debug(DfcUtils.class,
					"About to create Mail Item document...", null, null);
			IDfDocument documentMain = (IDfDocument) _session
					.newObject(mainMailObjType);
			documentMain.setObjectName(documentModel.getHeader().getTitle());
			documentMain.setTitle(documentModel.getHeader().getTitle());
			documentMain.setACLDomain(aclDomain);
			documentMain.setACLName(acl);
			DfcUtils.updateObjMetadata(documentMain, attVal);
			documentMain.setContentType(documentModel.getHeader().getFormat()
					.toLowerCase());
			
			documentMain.setContent(content);

			// backscan true, got to do a few different bits, if
			// notBackScanDocument returns TRUE
			/*
			 * if (backScanDocument) { documentMain.setString("mail_state",
			 * "Closed"); linkTo =
			 * Constants.BACKSCAN_FOLDER.concat(dateForCreateFolder.substring(0,
			 * 4
			 * )).concat("/").concat(attVal.get("scanned_envelope_id").toString(
			 * )); folder = FolderUtils.createFolderPath(_session, linkTo,
			 * Constants.FOLDER_TITLE, acl, Constants.DMFOLDER);
			 * DfLogger.debug(DfcUtils.class,"BackScan Document: " +
			 * backScanDocument + " Linking document to Backscan folder: " +
			 * folder.getId() ,null,null); documentMain.link(folder.getId()); }
			 * else {
			 */
			documentMain.setString("mail_state", "Unassigned");

			linkTo = Constants.CABINET.concat(dateForCreateFolder).concat("/")
					.concat(attVal.get(mailItemDestinationFolder).toString());

			folder = FolderUtils.createFolderPath(_session, linkTo,
					Constants.FOLDER_TITLE, acl, Constants.DMFOLDER);
			
			DfLogger.debug(DfcUtils.class, "Linking document to the folder: "
					+ folder.getId(), null, null);
			documentMain.link(folder.getId());
			DfLogger.debug(DfcUtils.class, "SAVE THE DOC: "
					+ folder.getId(), null, null);
			documentMain.save();
			objectID_FolderIDArray[0] = documentMain.getObjectId().getId();
			objectID_FolderIDArray[1] = folder.getId();
		}

		return objectID_FolderIDArray;
	}

	/**
	 * Helper method update dgms_upload_failure table on object creation failure
	 * 
	 * @param reconciliation_id
	 *            Reconciliation ID
	 * @param source
	 *            Source
	 * @param error_id
	 *            Error ID
	 * @param errorDescription
	 *            Error Description
	 * @param exeception
	 *            Exception message
	 * @param session
	 *            IDfSession
	 * 
	 * @throws DfException
	 *             if the setting of object values and creating it encounters
	 *             issues.
	 */

	// needs refactored to be slicker, needs name changed to be more
	// descriptive, its not a handle all method for upload failuire, its a
	// insert into a DB and should say as much
	public static void uploadError(String reconciliation_id, String source,
			String error_id, String errorDescription, String exeception,
			IDfSession session) throws DfException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String timestamp = dateFormat.format(date);
		IDfCollection coll = null;

		if (!StringUtil.isEmptyOrNull(exeception)) {
			if (exeception.length() > 500) {
				exeception = exeception.substring(0, 500);
			}
			if (exeception.indexOf("'") > -1) {
				exeception = exeception.replaceAll("'", "''");
			}
		}

		String dql = "INSERT INTO dm_dbo.dgms_upload_failure(reconciliation_id,source, timestamp, error_id, error_description, exception) VALUES('"
				+ reconciliation_id
				+ "','"
				+ source
				+ "','"
				+ timestamp
				+ "','"
				+ error_id
				+ "','"
				+ errorDescription
				+ "','"
				+ exeception + "')";
		DfLogger.debug(DfcUtils.class, "update_error_dql + @" + dql + "@",
				null, null);

		/*
		 * CB CodeReview 08/Dec/16
		 * call to new method updateDeleteQuery()
		 * removes the need to instantiate / close the collection
		 */
		try {
			coll = DfcUtils.executeQuery(dql, session, false);
		} finally {
			if (coll != null) {
				coll.close();
			}
		}
	}

	/**
	 * Helper method to update dgms_upload_success table in the event of
	 * successfully creating all required documetns in JSON request and starting
	 * workflow.
	 * 
	 * @param reconciliation_id
	 *            Reconciliation ID
	 * @param source
	 *            Source
	 * @param r_object_id
	 *            r_object_id of created Document
	 * @param session
	 *            IDfSession
	 * 
	 * @throws DfException
	 *             if the setting of object values and creating it encounters
	 *             issues.
	 */
	public static void updateDMSAuditTableSuccess(String reconciliation_id,
			String r_object_id, String source, IDfSession session)
			throws DfException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String timestamp = dateFormat.format(date);
		IDfCollection coll = null;

		String dql = "INSERT INTO dm_dbo.dgms_upload_success(reconciliation_id,r_object_id, source, timestamp) VALUES('"
				+ reconciliation_id
				+ "','"
				+ r_object_id
				+ "','"
				+ source
				+ "','" + timestamp + "')";
		DfLogger.debug(DfcUtils.class, "update_success dql: + @" + dql + "@",
				null, null);

		try {
			coll = DfcUtils.executeQuery(dql, session, false);
		}

		finally {
			if (coll != null)
				coll.close();
		}

	}

	/**
	 * Helper method to get the ordered map where iform is the first in TreeMap.
	 * 
	 * @param docList
	 *            List of Document objects from JSON
	 * 
	 * @return TreeMap<String, Document> Returns sorted TreeMap with the element
	 *         iform as the first element. This is achieved by prefixing letter
	 *         A to iform
	 */
	public static TreeMap<String, Document> getOrderedDocMap(
			List<Document> docList) {
		// this is constructed assumptively needs changes, questions as to
		// value?
		// Document document = null; -- removed as no needed

		HashMap<String, Document> documentMap = new HashMap<String, Document>();
		// Get through the list
		for (Document element : docList) {

			
			if (element.getHeader().getType().equalsIgnoreCase("iform")) {
				documentMap.put("AAA_iform", element);
			} else {
				// still needs addressed, why are we bothering, lets just
				// re-order the list putting the iform first, which appears the
				// be the intend here anyway?
				documentMap.put(
						element.getHeader()
								.getType()
								.concat(Integer.toString(docList
										.indexOf(element))), element);
			}
			
		
		}
		

		return new TreeMap<String, Document>(documentMap);
	}

	
	//JG_001 
	public static List<Document> getSortedList(List<Document> docList)
	{
			
		int origSize = docList.size();
		List<Document> sortedList = new ArrayList();
		
		DfLogger.debug(DfcUtils.class, "Look for Primary Content first and add only that to the sorted list in position 0" , null, null);
		
		// CM CodeReview: Would not work iterating only once and doing all the job there?
		// If not, replace For by While to avoid multiple iterations
		
		for(int i =0;i<origSize;i++)
		{
			if (docList.get(i).getHeader().getPrimary_content().equalsIgnoreCase("true"))
			{
				sortedList.add(0,docList.get(i));
			}
			else
			{
				sortedList.add(docList.get(i));
			}
			
		}
		
/*		DfLogger.debug(DfcUtils.class, "Add Non Primary Content to the sortedlist" , null, null);
		for(int i =0;i<origSize;i++)
		{
			if (docList.get(i).getHeader().getPrimary_content().equalsIgnoreCase("false"))
			{
				sortedList.add(docList.get(i));
			}
			
		}
*/				
		return sortedList;
	}
	
	//END JG_001 
	
	/**
	 * @param List
	 *            String List of items
	 * @return String List as comma seperated string
	 */
	public static String getDocListAsCSV(List<String> list) {
		String docsCSV = "";
		for (String element : list) {
			docsCSV = docsCSV.concat(element).concat(",");
		}
		if (docsCSV.endsWith(",")) {
			docsCSV = docsCSV.substring(0, docsCSV.length() - 1);
		}
		return docsCSV;
	}
	
	/**
	 * Helper method to trim a string to 500 characters and replace single qoutes with a star to allow them to be inserted into Databases EDT-609.
	 * 
	 * @param expection
	 *            String of Exception Message
	 *            
	 * @return
	 * 			String of 500 charcater length and no single qoutes.  
	 */
	public static String expectionStringBuilder(String expection){
		
		String expectionMessageHandler = null;
		int iLength = 0;
		int iMath = 0;
		DfLogger.debug(DfcUtils.class,"Getting Expection Message Length", null, null);
		iLength = expection.length();
		DfLogger.debug(DfcUtils.class, "Legnth is: " + iLength + " Getting Math.min." , null, null);
		iMath = Math.min(iLength, 499); 
		DfLogger.debug(DfcUtils.class,"Math.min is " + iMath, null, null);

		DfLogger.debug(DfcUtils.class, "Trimming Erorr Message to 500 characters to fit in database.", null, null);
		expectionMessageHandler = expection.substring(0,iMath);
		
		DfLogger.debug(DfcUtils.class, "Replacing ' with * to stop SQL message errors.", null, null);
		expectionMessageHandler = expectionMessageHandler.replaceAll("'", "*");
		
		DfLogger.debug(DfcUtils.class, "Returned Value is: " + expectionMessageHandler, null, null);
		return expectionMessageHandler;
	}
}
