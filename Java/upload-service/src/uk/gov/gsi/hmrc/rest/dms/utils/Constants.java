package uk.gov.gsi.hmrc.rest.dms.utils;

public class Constants {

		//METADATA VALUES
		public static final String PRIMARY_CONTENT_TYPE = "iform";
		public static final String MAIL_ATTACHMENT_TYPE = "attachment";
		public static final String ROBOTIC_ATTACHMENT_TYPE = "Robotic Form";
		public static final String MAIL_ATTACHMENT_FORMAT_PDF = "pdf";
		public static final String MAIL_ATTACHMENT_FORMAT_JPG = "jpeg";
		public static final String MAIL_ATTACHMENT_FORMAT_XML = "xml";
		public static final String BOOLEAN_AS_STRING_TRUE = "true";
		public static final String BOOLEAN_AS_STRING_FALSE = "false";
		public static final String CUSTOMER_ID = "customer_id";
		
		//JSON RESPONSE Values
	  	public static final String DEFAULT_INGESTION_STATUS = "DEFAULT RESPONSE:INGESTION FAILED";
	  	public static final String DEFAULT_INGESTION_DESCRIPTION = "DEFAULT RESPONSE:INGESTION WAS NOT PROCESSED";
	  	public static final String BLANK_INPUT_DESCRIPTION = " THE FOLLOWING INPUT(s) ARE NOT PROVIDED OR VALID:";
	  	public static final String BLANK_SOURCE_DESCRIPTION = " <SOURCE> ";
		public static final String BLANK_TITLE_DESCRIPTION = " <TITLE> ";
		public static final String BLANK_FORMAT_DESCRIPTION = " <FORMAT> ";
		public static final String UNSUPPORTED_FORMAT_DESCRIPTION = " CONTENT SUBMITTED IS NOT A SUPPORTED FORMAT  ";
		public static final String BLANK_ENCODING_DESCRIPTION = " <ENCODING> ";
	  	public static final String BLANK_DOCUMENT_CONTENT_STREAM_DESCRIPTION = " <DOCUMENT_CONTENT_STREAM> ";
	  	public static final String BLANK_RECONCILIATION_ID_DESCRIPTION = " <RECONCILIATION_ID> ";
	  	public static final String INPUT_DATA_FAILURE = "INGESTION FAILED:INPUT DATA ERROR: Required fields are null or empty";
	  	public static final String BLANK_TYPE_DESCRIPTION =  " <TYPE> ";
	  	public static final String NO_PRIMARY_CONTENT =  " PRIMARY CONTENT NOT DEFINED ";
	  	public static final String INCORRECT_PRIMARY_CONTENT =  " PRIMARY CONTENT IS NOT OF THE REQUIRED FORMAT ";
	  	public static final String MULTIPLE_PRIMARY_CONTENT =  " MORE THAT ONE OBJECT IDENTIFIED AS PRIMARY CONTENT ";
		public static final String MULTIPLE_XML_CONTENT =  " MORE THAT ONE OBJECT IDENTIFIED AS XML CONTENT ";
	  	public static final String MULTIPLE_RECONCILIATION_IDS =  " MORE THAN ONE RECONCILAITION ID IN INPUT ";
	  	public static final String ATTACHMENT_COUNT_MISMATCH_1 = "NUMBER OF ATTACHMENTS DEFINED IN THE METADATA :" ;
	  	public static final String ATTACHMENT_COUNT_MISMATCH_2	=" DOESNT MATCH NUMBER OF DFS ATTACHMENTS FOUND IN INPUT :  " ;
	  	public static final String SEPARATOR =  " : ";	
	  	public static final String DOC_DESCRIPTOR =  " FOR DOCUMENT : ";	
	  	public static final String WHITESPACE =  " ";
	  	public static final String NOT_PROCESSED_RESULT =  " INGESTION WILL NOT BE PROCESSED ";
	  	public static final String PREVIOUSLY_PROCESSED_DESC =  " THIS RECONCILLIATION ID HAS BEEN PROCESSED PREVIOUSLY ";
	  	public static final String UNSUPPORTED_ROBITICS_CONFIGURATION =  " THE SUBMITTED ROBTICS CONFIG IS NOT PERMITTED ";
	  	
	  	public static final String ERROR_DESCRIPTION_PART1 =  "The ingestion failed while processing the document: ";
	  	public static final String ERROR_DESCRIPTION_PART2 =  " and execution stopped. ";
	  	public static final String EMPTY_SOURCE =  "NO SOURCE";
	  	public static final String RESPONSE_SUCCESS =  "SUCCESS";
	  	public static final String RESPONSE_ERROR =  "ERROR";
	  	
		public static final String ROBOTICS_RESPONSE_PROCESS_ERROR =  " Failed to process Robotics response as workflow could not be started.";
		public static final String ROBOTICS_RESPONSE_PROCESS_SUCCESS =  "Robotics response processed via workflow with id:  ";
		public static final String ROBOTICS_RESPONSE_IGNORE="Ignore";
		public static final String ROBOTICS_RESPONSE_IGNORE_DESC="Robotics response has been assessed: tracking data for the item determined that it will not be proessed for mail item:";
	  	
	  //ERROR_CODES
	  	public static final String ERROR_CODE_NULL_INPUT = "001";
	  	public static final String ERROR_CODE_NULL_SOURCE = "002";
	  	public static final String ERROR_CODE_NULL_RECON_ID = "003";
	  	public static final String ERROR_CODE_NULL_TYPE = "004";
	  	public static final String ERROR_CODE_NULL_CONTENT = "005";
	  	public static final String ERROR_CODE_MULTI_RECON_ID = "006";
	  	public static final String ERROR_CODE_ATTACHMENT_COUNT = "007";	  	
	  	public static final String ERROR_CODE_WRONG_MAIL_ITEM_TYPE = "008";
	  	public static final String ERROR_CODE_MAIL_ITEM_NULL = "009";
	  	public static final String ERROR_CODE_MULTI_MAIL_ITEM = "010";
	  	public static final String ERROR_CODE_DUPLICATION = "011";
	  	public static final String ERROR_CODE_UNSUPPORTED_ROBOTICS_CONFIG = "012";
	  
	  	public static final String ERROR_CODE_MAILITEM_NOT_CREATED = "100";
	  	public static final String ERROR_CODE_ATTACHMENT_NOT_CREATED = "101";
	  	public static final String ERROR_CODE_VALIDATION_FAILED =  "103";
	  	public static final String ERROR_CODE_WORKFLOW_NOT_STARTED = "110";
	  	public static final String ERROR_CODE_WORKFLOW_NOT_STARTED_UNINSTALLED = "111";
	  	public static final String ERROR_CODE_WORKFLOW_NOT_STARTED_NO_MAILITEM = "112";
	  	public static final String ERROR_CODE_UNEXPECTED_REQUEST_BODY = "113"; 
	  	
	  	public static final String CODE_EXECUTION_ERROR = "150";

	  	
	  	//Documentum Values
	  	
	  	//DQL Values
	  	
	  	//FolderUtils static DQL values
	  	public static final String DQL_GetAcl = "dm_acl WHERE object_name = ''{0}''";
	  	public static final String DQL_GetCabient = "dm_cabinet WHERE object_name = ''{0}''";
	  	public static final String DQL_GetFolder = "dm_folder WHERE FOLDER(''{0}'') AND object_name = ''{1}''";
	  	public static final String DQL_CHECK_IF_RECON_ID_PROCESSED = "dm_dbo.dgms_upload_success WHERE reconciliation_id = ''{0}''";
	  	
	  	//workflow related static values
	  	public static final String PROCESS_NAME = "Ingested Mail Process";
	  	public static final String REST_PROCESS_NAME = "DMS REST Primary Process";
	  	public static final String PROCESS_QUERY = "SELECT r_object_id FROM dm_process WHERE object_name = 'PROCESS_NAME'";
	  	public static final String WORKFLOW_PACKAGE_NAME = "mailitem";
	  	public static final String WORKFLOW_PACKAGE_TYPE = "dgms_mail_item";
	  	public static final String OBJECT_ID = "r_object_id";
	  	public static final boolean WORKFLOW_NOTE_PERSIST = false;
	  	public static final String WORKFLOW_SOURCE_INGESTION = "Ingestion";
	  	public static final String WORKFLOW_TARGET_DEFAULT = "DMS";
	  	
	  	//Config XML related values for Ingestion in UploadServiceManagerImpl class
	  	public static final String DATE_FORMAT = "yyyy/MMMM/dd"; 
	  	public static final String CABINET = "DGMS/";
	  	public static final String DMFOLDER = "dm_folder";
	  	public static final String FOLDER_TITLE = "DFS Import Service";
	  	public static final String BACKSCAN_FOLDER = "DGMS/Backscan/";
	  	public static final String DMS_DATE_FORMAT_IF_NOT_SET_IN_XML = "dd/MM/yyyy";
	  	
	  	
	  	
	    //Error messages to insert in FAILED table AND on RESPONSES
	  	public static final String ERROR_MESSAGE_WORKFLOW_NOT_STARTED = "OBJECTS CREATED BUT WORKFLOW NOT STARTED";
	  	public static final String ERROR_MESSAGE_ATTACHMENT_NOT_CREATED = "ATTACHMENTS NOT CREATED";
	  	public static final String ERROR_MESSAGE_MAILITEM_NOT_CREATED = "MAIL ITEM NOT CREATED";
	  	public static final String ERROR_MESSAGE_WORKFLOW_NOT_STARTED_NO_MAILITEM_ID = "WORKFLOW NOT STARTED BECAUSE NO MAIL ITEM ID AVAILABLE";
	  	public static final String ERROR_MESSAGE_WORKFLOW_NOT_STARTED_UNINSTALLED = "WORKFLOW NOT STARTED. PROBALE CAUSE: PROCESS UNINSTALLED";
	  	public static final String ERROR_MESSAGE_VALIDATION_FAILED = "FAILED CREATING REQUESTED OBJECTS - VALIDATION FAILED";
	  	public static final String ERROR_MESSAGE_UNEXPECTED_REQUEST_BODY = "FAILED CREATING REQUESTED OBJECTS - UNEXPECTED JSON REQUEST BODY FORMAT OR ORDER";


}
