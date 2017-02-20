/*
 * Copyright (c) 2016. RCDTS. All Rights Reserved.
 */

package uk.gov.gsi.hmrc.rest.dms.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.documentum.rest.http.UriInfo;
import com.emc.documentum.rest.http.annotation.RequestUri;
import com.emc.documentum.rest.model.batch.annotation.TransactionProhibition;

import uk.gov.gsi.hmrc.rest.dms.model.Document;
import uk.gov.gsi.hmrc.rest.dms.model.Documents;
import uk.gov.gsi.hmrc.rest.dms.model.JsonResponseModel;
import uk.gov.gsi.hmrc.rest.dms.persistent.UploadServiceManager;
import uk.gov.gsi.hmrc.rest.dms.utils.Constants;
import uk.gov.gsi.hmrc.rest.dms.utils.DfcUtils;

/**
 * 
 * @author RC14670 (J Gill)
 *  
 * 	Description – Documentum REST based controller to process capture an ingestion request 
 * 	provided as JSON Input and return the result as a JSON response .
 * 	Input will be triaged to determine if it should be put forward for processing.
 * 
 * 	Modification history
 *	 ------------------------------------------
 * 	Version 1.0 - Initial version August 2016
 * 	Versiion 2.0 - Inclusion of Robotics November 2016
 * 
 *  rc00017 - moved function to populate metaDataAttachmentCount variable to the block where the iForm is processed, so it can be in any order from JSON
 *  RC14670 08/12/2016 Audit :JG_001 EKDMS-1260 Update to allow submitted mail item to be processed manually if submitted with an unsupported Robotics config.
 */

//Controller Name
@Controller("dms-controller") 

//URL definition for the REST service
@RequestMapping("/repositories/{repositoryName}/process_dms_ingestion")

public class UploadServiceController {
	
	@Autowired
	private UploadServiceManager uploadmanager;
    
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @TransactionProhibition
    
    public JsonResponseModel createJsonResponse(
            @PathVariable("repositoryName") final String repositoryName,
            @RequestBody final Documents docs,
            @RequestUri final UriInfo uriInfo)
            throws Exception {
         
	    	//Create a default  JSON response 
	   		JsonResponseModel response = new JsonResponseModel();
	   		
	   		Document doc = null;
	   		String metaDataAttachmentCount = "";

	   		boolean process = true;
	   		boolean secondaryTriage = false;
	   		boolean reconIds= true;
	   		boolean robotics=false;
	   		boolean roboticsRejected = false; //JG_001 EKDMS-1260
	   		
	   		String roboticsType="";
	   		String roboticsFormat="";
	   		String customerId = "";
	   		
	   		int  primaryContent =0;
	   		int mailAttachmentCount=0;
	   		int xmlContentCount=0;

	   		
	   	//Set up a generic response
			
    		response.setResult("GENERIC");
   			response.setReconciliation_id("NONE");
   			response.setDescription("Generic Response Description");
   			response.setSource("NONE");
	   		
	    	List<Document> docList = docs.getDocuments();
	    	//Get the reconciliation ID of the first document which will be used for comparison
			String primaryReconciliationId = docList.get(0).getHeader().getReconciliationId();
			
			DfLogger.info(this,"======== v2.0 Start DFS Ingestion REQUEST with Reconciliation ID: " + primaryReconciliationId +" ========" ,null,null);
			String responseDescription = Constants.ERROR_DESCRIPTION_PART1 +primaryReconciliationId + Constants.ERROR_DESCRIPTION_PART2 +Constants.BLANK_INPUT_DESCRIPTION ;
    		
			String errorCode ="";
			String exception="";
			boolean headerTriage = true;
			boolean attachmentTriage = true;
			boolean breakTriage = false;
			

			//Triage input to check that all mandatory fields are populated. If any test fails Ingestion will not occur	
			
		if (docList.size()>0)
			{
				DfLogger.info(this,"@@@ Begin Triage of input content @@@" ,null,null); 				 

			    	//for (int j = 0; j < docList.size(); j++) 
			    		
				 int j= 0;
				 
				 while (!breakTriage)
			    	{
					 DfLogger.debug(this,"*******Process Doc  " + j + " *******",null,null);
			    		doc = docList.get(j); 
			
			    		//Check that all Header information is empty or null
			    		headerTriage = doc.getHeader().isValid();
			    		attachmentTriage = doc.getAttachment().isValid();
			    		 
			    		 if (headerTriage&&attachmentTriage)
			    		 {	
			    			 
			    			 DfLogger.debug(this,"Primary content value =  " + doc.getHeader().getPrimary_content() + " for document "+ j,null,null);
			    			 j++;
			    			 DfLogger.debug(this,"BASIC TRIAGE PASSED for document: " + j,null,null);
			    			 secondaryTriage = true;

			    			 //TBC are we using iform or priamry content
			    			 if ((doc.getHeader().getPrimary_content()).equalsIgnoreCase(Constants.BOOLEAN_AS_STRING_TRUE))
			    			 //if ((doc.getHeader().getType()).equalsIgnoreCase(Constants.PRIMARY_CONTENT_TYPE))
					    		{
					    			
					    			DfLogger.debug(this,"Primary Content Value is TRUE so do FORMAT Checks " + j,null,null);
					    			primaryContent++;    
					    			metaDataAttachmentCount = DfcUtils.getMetadataValueFromJson(doc.getMetadata(), "attachment_count");
					    			customerId = DfcUtils.getMetadataValueFromJson(doc.getMetadata(), "customer_id");
					    			DfLogger.debug(this,"Customer ID is " + customerId ,null,null);					    								    			
					    		
					    			if (!(doc.getHeader().getFormat()).equalsIgnoreCase(Constants.MAIL_ATTACHMENT_FORMAT_PDF))
					    			{
					    				
					    				DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " -Content format for the primary content is incorrect. Expected format is : " + Constants.MAIL_ATTACHMENT_FORMAT_PDF  ,null,null);
					    				
					    				process = false;
					    				secondaryTriage = false;
					    				reconIds= false;
					    				errorCode = Constants.ERROR_CODE_WRONG_MAIL_ITEM_TYPE;
					    				response.setResult(Constants.RESPONSE_ERROR);
							   			response.setReconciliation_id(primaryReconciliationId);
							   			responseDescription = responseDescription.concat(Constants.INCORRECT_PRIMARY_CONTENT  + Constants.SEPARATOR );
							   			//trim description		
							   	    	if (responseDescription.length()>999)
							   	    	{
							   	    		responseDescription = responseDescription.substring(0, 998);
							   	    	}
							   			response.setDescription(responseDescription);
							   			response.setSource(doc.getHeader().getSource());
							   			DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " is marked as primary content but format is wrong for document " + j,null,null);
							   			breakTriage = true;
						    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
					    			}//end 	if (!(doc.getHeader().getFormat()).equalsIgnoreCase(Constants.MAIL_ATTACHMENT_FORMAT_PDF))					    								    				

					    		}//end if ((doc.getHeader().getType()).equalsIgnoreCase(Constants.PRIMARY_CONTENT_TYPE))
			    			 
			    			 //Check the recon Ids
			    			 
			    			 if (reconIds&&process)
					    		{
			    				 	DfLogger.debug(this,"primaryReconciliationId = " + primaryReconciliationId  ,null,null);
					    			//only do this check once if reconciliation Id check has previously failed
					    			if (!doc.getHeader().getReconciliationId().equals(primaryReconciliationId)) 
					    			{
						    			DfLogger.error(this,"Inconsistency in submitted Reconciliation IDs"  ,null,null);
						    			response.setResult(Constants.RESPONSE_ERROR);
							   			response.setReconciliation_id(primaryReconciliationId);
							   			responseDescription = responseDescription.concat( Constants.MULTIPLE_RECONCILIATION_IDS  + Constants.SEPARATOR );
							   			process = false;
							   			secondaryTriage = false;							   			
							   			reconIds = false;
							   			errorCode = Constants.ERROR_CODE_MULTI_RECON_ID;
							   			if (responseDescription.length()>999)
							   	    	{
							   	    		responseDescription = responseDescription.substring(0, 998);
							   	    	}
							   			response.setDescription(responseDescription);
							   			response.setSource(doc.getHeader().getSource());
							   			
							   			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
							   			
							   			breakTriage = true;
							   			
					    			}
					    		}
			    			 
			    			 if (reconIds&&process)
					    		{
			    				 //Check for mail attachment
						    		if(((doc.getHeader().getFormat()).equalsIgnoreCase(Constants.MAIL_ATTACHMENT_FORMAT_PDF)||(doc.getHeader().getFormat()).equalsIgnoreCase(Constants.MAIL_ATTACHMENT_FORMAT_JPG )) && (doc.getHeader().getType()).equalsIgnoreCase(Constants.MAIL_ATTACHMENT_TYPE) ) 
						    		{	
						    			mailAttachmentCount++;						    			
						    		}
						    		
						    		//Check for Robotics
						    		if(doc.getHeader().getFormat().equalsIgnoreCase(Constants.MAIL_ATTACHMENT_FORMAT_XML))
						    		{
						    			roboticsType=doc.getHeader().getType();
						    			roboticsFormat=doc.getHeader().getFormat();
						    			xmlContentCount++;
						    		}
					    		}	
	 
			    		 }//end if (headerTriage&&attachmentTriage)
			    		 
			    		 else
			    		 {
			    			 
			    			breakTriage = true; 
			    			secondaryTriage = false;
			    			DfLogger.error(this,"!@@@@Failed Basic triage for reconciliation ID: "+ primaryReconciliationId+" update audit table@@@@!",null,null);
			    			response.setResult(Constants.RESPONSE_ERROR);
				   	    	response.setReconciliation_id(primaryReconciliationId);
					   	    response.setDescription(Constants.INPUT_DATA_FAILURE);
					   	   	response.setSource(doc.getHeader().getSource());
					   	   	uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
			    				
			    		 }	
			    				    		 
			    		 if (j==docList.size())
			    		 {			    			
			    			 breakTriage= true;
			    		 }
			    		 
			    		 
			    	}// End While Loop
				 DfLogger.debug(this,"END DOC LIST ITERATION",null,null);
				 
				 DfLogger.debug(this,"value of SECONDARY TRIAGE is  : " + secondaryTriage ,null,null);
			}// end initial doclist iteration		
		
			//Initial checks done do Secondary triage to ensure gathered data is valid	
			if (secondaryTriage)
			{
				
				DfLogger.debug(this,"SECONDARY TRIAGE ANALYSIS" ,null,null);
				
				DfLogger.debug(this,"primaryContent count = " + primaryContent ,null,null);
				DfLogger.debug(this,"xmlContentCount " + xmlContentCount ,null,null);
				DfLogger.debug(this,"mailAttachmentCount" + mailAttachmentCount,null,null);
				//Check that only one primary content object has been passed in.
		    	if(primaryContent==0)
		    	{
		    		DfLogger.error(this,"Failed Triage, doc with reconcilaition ID: "  + primaryReconciliationId + " -No Primary content submitted " ,null,null);
		    		response.setResult(Constants.RESPONSE_ERROR);
		   			response.setReconciliation_id(primaryReconciliationId);
		   			responseDescription = responseDescription.concat(Constants.NO_PRIMARY_CONTENT + Constants.SEPARATOR);
		   			//trim description		
		   	    	if (responseDescription.length()>999)
		   	    	{
		   	    		responseDescription = responseDescription.substring(0, 998);
		   	    	}
		   			response.setDescription(responseDescription);
		   			response.setSource(doc.getHeader().getSource());
		    		process = false;
		    		errorCode = Constants.ERROR_CODE_MAIL_ITEM_NULL;
	    			
	    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
		    	}
		    	else if(primaryContent>1)
		    	{
		    		DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " - Multiple primary content objects in input" ,null,null);
		    		response.setResult(Constants.RESPONSE_ERROR);
		   			response.setReconciliation_id(primaryReconciliationId);
		   			responseDescription = responseDescription.concat(Constants.MULTIPLE_PRIMARY_CONTENT + Constants.SEPARATOR);
		   			//trim description		
		   	    	if (responseDescription.length()>999)
		   	    	{
		   	    		responseDescription = responseDescription.substring(0, 998);
		   	    	}
		   			response.setDescription(responseDescription);
		   			response.setSource(doc.getHeader().getSource());
		    		process = false;
		    		errorCode = Constants.ERROR_CODE_MULTI_MAIL_ITEM;
		    				    			
	    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
		    		
		    	}
		    	if(process)
		    	{
		    	//Check if number of attachments in Input document matches the attachment count in primary document metadata
			    	if(!String.valueOf(mailAttachmentCount).equals(metaDataAttachmentCount))
			    	{
			    		DfLogger.error(this,"Attachment counts dont match for submission with reconciliation ID: " + primaryReconciliationId ,null,null);
			    		DfLogger.debug(this,"Attachments defined in metadata for mail item = " +metaDataAttachmentCount  ,null,null);
			    		DfLogger.debug(this,"Valid attachments found in the input = " +mailAttachmentCount  ,null,null);
			    		response.setResult(Constants.RESPONSE_ERROR);
			   			response.setReconciliation_id(primaryReconciliationId);
			   			responseDescription = responseDescription.concat(Constants.ATTACHMENT_COUNT_MISMATCH_1 + metaDataAttachmentCount + Constants.ATTACHMENT_COUNT_MISMATCH_2 +mailAttachmentCount + Constants.SEPARATOR );
			   			//trim description		
			   	    	if (responseDescription.length()>999)
			   	    	{
			   	    		responseDescription = responseDescription.substring(0, 998);
			   	    	}
			   			response.setDescription(responseDescription);
			   			response.setSource(doc.getHeader().getSource());
			    		process = false;
			    		errorCode = Constants.ERROR_CODE_ATTACHMENT_COUNT;
		    			
		    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
			    	}// end if(!String.valueOf(mailAttachmentCount).equals(metaDataAttachmentCount))
		    	}
		    	
		    	if(process)
		    	{
		    		//Check number of input documents that are xml
			    	if(xmlContentCount>1)
			    	{
			    		DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " - Multiple XML content objects in input" ,null,null);
			    		response.setResult(Constants.RESPONSE_ERROR);
			   			response.setReconciliation_id(primaryReconciliationId);
			   			responseDescription = responseDescription.concat(Constants.MULTIPLE_XML_CONTENT  + Constants.SEPARATOR);
			   			//trim description		
			   	    	if (responseDescription.length()>999)
			   	    	{
			   	    		responseDescription = responseDescription.substring(0, 998);
			   	    	}
			   			response.setDescription(responseDescription);
			   			response.setSource(doc.getHeader().getSource());
			    		process = false;
			    		errorCode = Constants.ERROR_CODE_MULTI_MAIL_ITEM;	
		    			
		    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
			    	}// end (xmlContentCount>1)
			    	
			    	else if (xmlContentCount ==1)
			    	{
			    		
			    		if(!StringUtil.isEmptyOrNull(customerId))			    			
			    		{
				    		if ( uploadmanager.checkRoboticsType(roboticsType,roboticsFormat))
				    			
				    		{
				    			DfLogger.debug(this,"Permitted Robotics document",null,null);
				    			robotics = true;
				    		}
				    		
				    		else 
				    		{
				    			/* JG_001 EKDMS-1260
				    			 * Code to set process as false and generate response error commented out to allow processing to continue
				    			
				    			//DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " XML HAS BEEN SUBMITTED BUT TYPE IS NOT PERMITTED",null,null);
				    			
				    			
				    			process = false;
				    			response.setResult(Constants.RESPONSE_ERROR);
					   			response.setReconciliation_id(primaryReconciliationId);
					   			responseDescription = responseDescription.concat( Constants.UNSUPPORTED_ROBITICS_CONFIGURATION );
					   			//trim description		
					   	    	if (responseDescription.length()>999)
					   	    	{
					   	    		responseDescription = responseDescription.substring(0, 998);
					   	    	}
					   			response.setDescription(responseDescription);
					   			response.setSource(doc.getHeader().getSource());
					   			errorCode = Constants.ERROR_CODE_UNSUPPORTED_ROBOTICS_CONFIG;
				    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
				    			*/
				    			
				    			
				    			roboticsRejected = true;
				    			//END JG_001 EKDMS-1260
				    			
				    			
				    			//set robotics = false XML HAS BEEN SUBMITTED BUT TYPE IS NOT PERMITTED PROCESS AS A STANDARD DOC
				    			
				    			robotics = false;
				    			
				    			DfLogger.debug(this,"Robotics has been submitted for: "  + primaryReconciliationId + "  BUT TYPE IS NOT PERMITTED, Procss as standard ingestion, robotics rejected = true",null,null);
				    			
				    		}
			    		}
		    		
			    		else
			    		{
			    			//Customer ID is empty process as standard doc
			    			DfLogger.error(this,"Failed Triage as doc with reconcilaition ID: "  + primaryReconciliationId + " XML HAS BEEN SUBMITTED BUT CUSTOMERID IS BLANK, PROCESS AS STANDARD DOC",null,null);
			    			robotics = false;
			    		}	
			    	}// end  (xmlContentCount ==1)
			    	
		    	}
		    	
		    	//iF input is valid check to see if the Reconciliation id has previously been processes
		    	if(process)
		    	{
		    		if(uploadmanager.checkIfRecIdProcessed(primaryReconciliationId,true))
		    		{
		    			DfLogger.error(this,"Reconciliation ID " + primaryReconciliationId + " has been processed before, do not process",null,null);
		    			process = false;
		    			response.setResult(Constants.RESPONSE_ERROR);
		    			response.setReconciliation_id(primaryReconciliationId);
		    			responseDescription = responseDescription.concat(Constants.PREVIOUSLY_PROCESSED_DESC + Constants.SEPARATOR + primaryReconciliationId);
		    			//trim description		
			   	    	if (responseDescription.length()>999)
			   	    	{
			   	    		responseDescription = responseDescription.substring(0, 998);
			   	    	}
		    			response.setDescription(responseDescription);
		    			response.setSource(doc.getHeader().getSource());
		    			errorCode = Constants.ERROR_CODE_DUPLICATION;
		    			
		    			uploadmanager.updateDMSAuditTableFailure(doc,errorCode,response.getDescription(),exception,null, true);
		    						    			 
		    		} //if(uploadmanager.checkIfRecIdProcessed(primaryReconciliationId,true))
		    	}
		    	
		    	if(process)
		    	{	
		    		DfLogger.info(this,"@@@@@ Triage completed - Input is Valid @@@@" ,null,null);
		    		response.setReconciliation_id(primaryReconciliationId);
	    			response.setSource(doc.getHeader().getSource());
	    			
	    			try	
			   	       {
		        		 DfLogger.info(this,"Begin Ingestion" ,null,null);
			   	    	   //Process the Input and Create DMS objects in Documentum
			   	    	   response = uploadmanager.processDfsIngestion(docList, primaryReconciliationId, response,robotics);
			   	    	   
			   	       	}
			   	      
			   	     catch (DfException dfe)
			   	       {
				   	 			   	    	
				   	    	   response.setResult(Constants.RESPONSE_ERROR);
				   	    	   response.setReconciliation_id(primaryReconciliationId);
					   	       response.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
					   	       DfLogger.error(this,"Ingestion Process Failed for Reconcialition ID : "+ primaryReconciliationId +" return Default Response. See DfException  stack for furhter details" ,null,dfe);
					   	       response.setSource(doc.getHeader().getSource());
					   	       dfe.printStackTrace();
					   	       uploadmanager.updateDMSAuditTableFailure(doc,Constants.CODE_EXECUTION_ERROR,response.getDescription(),dfe.getLocalizedMessage() ,null, true);//true if need to release session    
			   	       }       	 
	        	 
		        	 catch (JAXBException jxe)
		        	 {
				   	    	
				   	    	   response.setResult(Constants.RESPONSE_ERROR);
				   	    	   response.setReconciliation_id(primaryReconciliationId);
					   	       response.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
					   	       response.setSource(doc.getHeader().getSource());
					   	       DfLogger.error(this,"Ingestion Process Failed for Reconcialition ID : "+ primaryReconciliationId +" return Default Response. See JAXBException  stack for furhter details" ,null,jxe);
					   	       jxe.printStackTrace();
					   	       uploadmanager.updateDMSAuditTableFailure(doc,Constants.CODE_EXECUTION_ERROR,response.getDescription(),jxe.getLocalizedMessage() ,null, true);//true if need to release session  
		        	 }
		        	 
		        	   
		        	 
		        	 catch (IOException ioe)
		        	 {
				   	    	
				   	    	   response.setResult(Constants.RESPONSE_ERROR);
				   	    	   response.setReconciliation_id(primaryReconciliationId);
					   	       response.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
					   	       response.setSource(doc.getHeader().getSource());
					   	       DfLogger.error(this,"Ingestion Process Failed for Reconcialition ID : "+ primaryReconciliationId +"return Default Response. See IOException  stack for furhter details" ,null,ioe);
					   	       ioe.printStackTrace();
						   	   uploadmanager.updateDMSAuditTableFailure(doc,Constants.CODE_EXECUTION_ERROR,response.getDescription(),ioe.getLocalizedMessage() ,null, true);//true if need to release session  
		        	 }
		        	 
		        	 catch (ParseException prse)
		        	 {
				   	    	
				   	    	   response.setResult(Constants.RESPONSE_ERROR);
				   	    	   response.setReconciliation_id(primaryReconciliationId);
					   	       response.setDescription(Constants.DEFAULT_INGESTION_DESCRIPTION);
					   	   	   response.setSource(doc.getHeader().getSource());
					   	       DfLogger.error(this,"Ingestion Process Failed for Reconcialition ID : "+ primaryReconciliationId +" return Default Response. See ParseException stack for furhter details" ,null,prse);
					   	       prse.printStackTrace();
					   	       uploadmanager.updateDMSAuditTableFailure(doc,Constants.CODE_EXECUTION_ERROR,response.getDescription(),prse.getLocalizedMessage() ,null, true);//true if need to release session  
		        	 }
		        	 
		        	 finally
		        	 {
		        		 DfLogger.debug(this,"End Ingestion Processing" ,null,null);	 
		        		 
		        	 }
	    					    		 
		    	}
		    	
			}
					  				
    		DfLogger.info(this,"! ========END DFS Ingestion Request with Reconciliation ID: " + primaryReconciliationId +" ======== !" ,null,null);
       //return the Json Response       
      
    		
    	
    	//JG_001 EKDMS 1260	
    	if(response.getResult().equalsIgnoreCase(Constants.RESPONSE_SUCCESS))
    		
    	{
    		if (roboticsRejected)
    		{
    			DfLogger.debug(this,"Append Invalid Robotics Config message" ,null,null);
    			response.setDescription(response.getDescription() + ". Invalid Robotics Config submitted , Mail Item will be processed manually");
    		}
    	}
    	//END JG_001 EKDMS 1260
    	
    	return response;
    }
    

    
    }
