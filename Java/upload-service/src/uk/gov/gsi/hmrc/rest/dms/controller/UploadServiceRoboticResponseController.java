package uk.gov.gsi.hmrc.rest.dms.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import uk.gov.gsi.hmrc.rest.dms.helper.WorkFlowBean;
import uk.gov.gsi.hmrc.rest.dms.model.JsonResponseModel;
import uk.gov.gsi.hmrc.rest.dms.model.Metadata;
import uk.gov.gsi.hmrc.rest.dms.model.RoboticsResponses;
import uk.gov.gsi.hmrc.rest.dms.persistent.UploadServiceManager;
import uk.gov.gsi.hmrc.rest.dms.utils.Constants;

import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.documentum.rest.http.UriInfo;
import com.emc.documentum.rest.http.annotation.RequestUri;
import com.emc.documentum.rest.model.batch.annotation.TransactionProhibition;



	/**
	 * 
	 * @author RC14670
	 *  
	 * 	Description – Documentum REST based controller to process a response from robotics submitted by GIS 
	 * 	provided as JSON Input and return the result as a JSON response .
	 * 	Input will be triaged to determine if it should be put forward for processing.
	 * 
	 * 	Modification history 
	 *	 ------------------------------------------
	 * 	Version 1.0 - Initial version August 2016
	 * Versiion 2.0 - Inclusion of Robotics November 2016
	 *
	 *  rc00017 - moved function to populate metaDataAttachmentCount variable to the block where the iForm is processed, so it can be in any order from JSON
   	 *	RC14670 15/12/2016 Audit :JG_001 EDT-592 - Ensure that customer IDs are Upper Case.
   	 *  RC14394 21/12/2016 Audit: CM_001 - Add new status (8) Acquired by the user
	 
	 */

	//Controller Name
	@Controller("dmsRobotic-controller") 

	//URL definition for the REST service
	@RequestMapping("/repositories/{repositoryName}/process_robotics_response")

	public class UploadServiceRoboticResponseController {
		
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
	            @RequestBody final RoboticsResponses rResponse,
	            @RequestUri final UriInfo uriInfo)
	            throws Exception {
	         
		    	//Create a default  JSON response 
		   		JsonResponseModel response = new JsonResponseModel();
		   		String workFlowId = "";
		   		
		   		int status = 0;
		   		
		   		String updateStatus = "";
		   		
		   		String updateError = "";
		   		int newStatus=0;
		   		Date updateTrackingTime = new Date();
		   		
		   		boolean updateTracking = false;
		   		boolean ignore = false;
		   		boolean process =  rResponse.getMessage().isValid();
		   		
		   		List<String> CustomerIds = new ArrayList<String>();
		   		
		   		DfLogger.info(this,"========Start Process Robotics Response for mail item V2"+rResponse.getMessage().getTransaction_id() +" ======== " ,null,null);
		   		DfLogger.info(this,"Date is " + updateTrackingTime.toString() ,null,null);
		   		
		   		//JG metadata testing
		   		
		   		List<Metadata> rMetaData = rResponse.getMetadata();
		   		
		   		if (rMetaData.size()>0)
		   		{
		   			DfLogger.debug(this,"We have metadata" ,null,null);	   		 
			   		for (int j = 0; j < rMetaData.size(); j++)
			   		{			   		 
				   		Metadata m = rMetaData.get(j);				   		 
				   		String att_name = m.getAttr_name();
				   		String att_value = m.getAttr_value();
				   		DfLogger.debug(this,"For entry number:"+ j + " Metadata values are : Att Name =  " + att_name + " , att_value = " + att_value  ,null,null);

				   		switch(att_name)
				   		{
				   		
				   			case Constants.CUSTOMER_ID :				   			
				   				//JG_001 - ensure customer Ids are upper case
				   				CustomerIds.add(att_value.toUpperCase());
				   				break;
				   				
				   			default:
				   				DfLogger.debug(this,"Not supported metadata - ignore it " ,null,null);
				   				break;
				   		}
			   		
			   			 
			   		}
		   		}
		   		
		   		else
		   		{
		   			DfLogger.debug(this,"No additional metadata submitted" ,null,null);
		   		}
		   		
		   		DfLogger.debug(this,"End of metadata parsing" ,null,null);
		   		
		   		
		   		
		   		//JG metadata testing
		   		
		   		
		   		
		   		//Check if previously worked		   		
		   		status = uploadmanager.isMailItemWorked(rResponse.getMessage().getTransaction_id());   		
		   		if(status==-1)
		   		{
		   			process= false;
		   			ignore = true;
		   			DfLogger.error(this,"Robotic Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " as no tracking entry can be found for this item" ,null,null);
		   		}
		   		
		   		
		   		if (process)
		   		{
		   			//passed triage and entry found for item, update the tracking table to set the date.
	
			   		switch(status)
			   		{
					
					case 1:
						
						if(uploadmanager.checkmailItemEvents(rResponse.getMessage().getTransaction_id()))
					   		{				   			
					   			
					   			process = true;
					   			
					   		}
						else
						{
							DfLogger.error(this,"Robotic Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " Robotic status is: " +status + " Awaiting Response, however mail item appears to have been processed since robotic request was made" ,null,null);
							response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
							process = false;
							ignore = true;
							updateError = "Triage failure :mail item appears to have been acquired by the user since robotic request was made";
							// CM_001 - Add new status (8) when item has been acquired by the user
							newStatus=8;
							
							
						}
						
						updateTracking= true;
						
						break;
						
					case 2:
						DfLogger.error(this,"Robotic Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " Robotic status is: " +status + " Completed" ,null,null);
						response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
						process = false;
						ignore = true;						
						break;
						
						
					case 3:
						DfLogger.error(this,"Robotic Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " Robotic status is: " +status + " Error – Robotic Call" ,null,null);
						if(uploadmanager.checkmailItemEvents(rResponse.getMessage().getTransaction_id()))
					   		{
							process = true;
							
					   		}
							
						else
							{
							response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
							process = false;
							ignore = true;
							updateError = "Triage failure :mail item appears to have been acquired by the user since robotic request was made";
							// CM_001 - Add new status (8) when item has been acquired by the user
							newStatus=8;
							
							}
						updateTracking= true;
						break;
						
					case 4:
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() +" Robotic status is: " +status + " Error – Robotic Response"  ,null,null);
						if(uploadmanager.checkmailItemEvents(rResponse.getMessage().getTransaction_id()))
					   		{
					   			process = true;
					   			
					   		}
						else
						{
							response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
							process = false;
							ignore = true;
							updateError = "Triage failure :mail item appears to have been acquired by the user since robotic request was made";
							// CM_001 - Add new status (8) when item has been acquired by the user
							newStatus=8;
							
						}
						updateTracking= true;
						break;
						
					case 5:
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " Robotic status is: " +status + " Timed Out (Awaiting Response)" ,null,null);
						response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
						process = false;
						ignore = true;
						updateTracking= true;
						newStatus=status;
						break;
						
					case 6:
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() +" Robotic status is: " +status +" Timed Out (Error – Robotic Call)" ,null,null);
						response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
						process = false;
						ignore = true;
						updateTracking= true;
						newStatus=status;
						break;
						
					case 7:
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() +" Robotic status is: " +status + " Timed Out (Error – Robotic Response)" ,null,null);
						
						process = false;
						ignore = true;
						updateTracking= true;
						newStatus=status;
						break;
						
					// CM_001 - Add new case for status (8) when item has been acquired by the user
						
					case 8:
						
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() +" Robotic status = 8 ",null,null);
						
						process = false;
						ignore = true;
						updateTracking= true;	
						updateError = "Triage failure :mail item appears to have been acquired by the user since robotic request was made";	
						newStatus=8;
						break;
						
					default:
						DfLogger.error(this,"Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() +" Robotic status = 0 or empty, status is: " +status ,null,null);
						
						process = false;
						ignore = true;
						updateTracking= true;					
						break;
						
			   		}
			   		
			   		
	    		}	
		   		if (process)
		   		{
		   			// make sure note is not to long- futue get a stroing util for this sort of stuff
		   			
		   			String note = rResponse.getMessage().getNote();
		   			
		   			if(note.length()>1024)
		   			{
		   				note = note.substring(0,1023);
		   			}
		   			
		   			try
			   		{
				   		workFlowId= uploadmanager.startResttWorkflow(rResponse.getMessage().getSource(),rResponse.getMessage().getTarget(),rResponse.getMessage().getTransaction_id(),rResponse.getMessage().getAction(),note,rResponse.getMessage().getCategory(), rResponse.getMessage().getRetention_policy(), CustomerIds);
				 				   		
			   		}
			   		
			   		catch (DfException dfe)
			   		{
			   			dfe.printStackTrace();
	
			   		}
			   		
			   		if (!StringUtil.isEmptyOrNull(workFlowId))
		   			{
			   			response.setResult(Constants.RESPONSE_SUCCESS);
			   			response.setDescription(Constants.ROBOTICS_RESPONSE_PROCESS_SUCCESS + workFlowId + " for mail item with id: " + rResponse.getMessage().getTransaction_id());
			   			response.setReconciliation_id(rResponse.getMessage().getTransaction_id());
			   			newStatus = 2;
		   			}
			   		else
		   			{
			   			DfLogger.info(this,"Create an error response" ,null,null);
			   			response.setResult(Constants.RESPONSE_ERROR);
			   			response.setDescription(Constants.ROBOTICS_RESPONSE_PROCESS_ERROR + rResponse.getMessage().getTransaction_id());
			   			newStatus = 4;
		   			}
			   		
			
		   		}
		   		else
		   		{
		   			if(ignore)
		   				{
		   				//Ignore already set 
		   				response.setResult(Constants.ROBOTICS_RESPONSE_IGNORE);
			   			response.setDescription(Constants.ROBOTICS_RESPONSE_IGNORE_DESC);
		   				}
		   			else
				   		{
				   			response.setResult(Constants.RESPONSE_ERROR);
				   			response.setDescription(Constants.INPUT_DATA_FAILURE);
				   			DfLogger.error(this,"Robotic Request will be ignored for mailitem "+ rResponse.getMessage().getTransaction_id() + " Input Data is invalid or missing" ,null,null);
							
				   		}	
		   		}
		   		
			   	if(updateTracking)
			   		{
			   			//Update the reobotics tracking table here.
			   			uploadmanager.updateRoboticTrackingTable(newStatus,rResponse.getMessage().getTransaction_id(),updateError,updateTrackingTime);
			   		}
			   		
		   		
			   	//Clean up Metadata lists
			 	DfLogger.info(this,"Clear Up Metadata Lists" ,null,null);
			   	CustomerIds.clear();
			   	
			   	DfLogger.info(this,"!========END Process Robotics Response========!" ,null,null);
		   		
		   		
		   		
	       //return the Json Response       
	       return response;
	    }
	    
	    }
