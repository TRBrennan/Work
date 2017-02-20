/*
 * CB CodeReview 08/Dec/16
 * Code Review by Chris Brennan.  If clarification is needed, please contact me directly
 */
package uk.gov.gsi.hmrc.rest.dms.robotics.soapCall;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import uk.gov.gsi.hmrc.rest.dms.robotics.roboticinterface.RobotSOAPDataSetup;
import uk.gov.hmrc.digitalmail.robotics.RoboticAutomation.RoboticAutomationBindingStub;
import uk.gov.hmrc.digitalmail.robotics.RoboticAutomation.RoboticAutomationService;
import uk.gov.hmrc.digitalmail.robotics.invoke.Invoke_server_req;
import uk.gov.hmrc.digitalmail.robotics.invoke.Invoke_server_resp;
import uk.gov.hmrc.digitalmail.robotics.invoke.Req_data;
import uk.gov.hmrc.digitalmail.robotics.invoke.Req_header;
import uk.gov.hmrc.digitalmail.robotics.invoke.Workflow_header;
import uk.gov.hmrc.digitalmail.robotics.soapHeader.User_auth;

import com.documentum.fc.common.DfLogger;

/**
 * 
 * @author RC11907 (T Brennan)
 * 
 *         Description – Builds up and handles SOAP Call to the Robotic system
 *         for automatic DMS Mail Item Processing.
 * 
 *         Modification history ------------------------------------------
 *         Version 1.0 - Initial version September 2016
 * 
 * 
 */

public abstract class RoboticsCall {

	/*
	 * CB CodeReview 08/Dec/16 These variables should have private, protected or
	 * public visibility
	 */
	private RoboticAutomationService service;
	private RobotSOAPDataSetup robotSOAPData = null;
	// Instantiate new Request Header Class
	private Req_header req_header = new Req_header();
	// Instantiate Invoke Server Req Header Class
	private Invoke_server_req invoke_server_req = new Invoke_server_req();
	// Instantiate User_Auth Class
	private User_auth auth = new User_auth();

	/*
	 * CB CodeReview 08/Dec/16 These variables do not need global scope - move
	 * their definitions into the appropriate method
	 */
	// Instantiate Req Data Class
	private Req_data req_data = new Req_data();
	// Instantiate New Workflow Header Class
	private Workflow_header wf_header = new Workflow_header();
	// Instantiate Response
	private Invoke_server_resp resp = new Invoke_server_resp();

	/*
	 * CB CodeReview 08/Dec/16 This variable does not appear to be used in the
	 * code unit - consider removing it
	 */
	String xmlAppended = null;

	public String formSubmission(RobotSOAPDataSetup robotData, String in_wf_data)
			throws ServiceException, MalformedURLException, RemoteException {
		Req_data[] wf_data = null;
		String[] workflow_data_xml = null;
		String responseCode = null;
		// Instantiate the Binding Stub to call the Robotics SOAP Service
		RoboticAutomationBindingStub formbuilder;

		// Call the Data building logic based on form ID.
		// Set up new Binding Stub Class for SOAP Call
		// Get the Endpoint for the Service
		robotSOAPData = robotData;
		URL endpoint = new URL(robotSOAPData.data.getEndpointURL());
		// Start a new Form based on the endpoint and service.
		formbuilder = new RoboticAutomationBindingStub(endpoint, service);
		// Set up Auth Credentials
		userAuth_Builder(robotSOAPData.data.getUserName(),
				robotSOAPData.data.getPassword());
		// Build up the header of the request
		reqHeaderBuilder();
		workflow_data_xml = new String[] { in_wf_data };
		// Set the workflow ID, solution and xml to be processed.
		wf_data = reqDataBuilder(workflow_data_xml);
		// Add everything needed together to the request.
		invokeServerReqBuilder(wf_data);

		// Process the SOAP Call to the Robotic System.
		resp = formbuilder.asyncInvokeOperation(invoke_server_req, auth);
		responseCode = resp.getResp_header().getStatus_type().toString();
		robotSOAPData.data.setStatus_text(resp.getResp_header()
				.getStatus_text());

		return responseCode;
	}

	// Set up the Req_Header Values
	private void reqHeaderBuilder() {
		DfLogger.info(this, "Starting to build Req Header", null, null);
		req_header.setGuid("");
		req_header.setOs_uid(robotSOAPData.data.getOsUID());
		req_header.setExternal_invoker_request_id(robotSOAPData.data
				.getExtInvReq());
		req_header.setInitiator_type(robotSOAPData.data.getInitator_type());
		req_header.setInitiator_id("");
		DfLogger.info(this, "Built Req Header", null, null);
	}

	// Set up Server Reqeust Values
	private void invokeServerReqBuilder(Req_data[] wf_data) {
		DfLogger.info(this, "Starting to build Reqeust", null, null);
		invoke_server_req.setReq_header(req_header);
		invoke_server_req.setSequence(wf_data);
		DfLogger.info(this, "Request Built", null, null);
	}

	// Set up the Workflow data to be processed by Robotics.
	private Req_data[] reqDataBuilder(String[] workflow_data) {
		DfLogger.info(this, "Starting to build Reqeust Data", null, null);
		wf_header.setSolution(robotSOAPData.data.getSolutionID());
		wf_header.setWorkflow_id(robotSOAPData.data.getWorkflowID());
		req_data.setWorkflow_header(wf_header);
		req_data.setWorkflow_data(workflow_data);
		Req_data[] reqdata = { req_data };
		DfLogger.info(this, "Request Data Built", null, null);
		return reqdata;
	}

	// Set Up User Authentication for the SOAP Env Header.
	private void userAuth_Builder(String UserName, String Password) {
		DfLogger.info(this, "Starting to build Authenitcation Header", null,
				null);
		auth.setUser_name(UserName);
		auth.setPassword(Password);
		DfLogger.info(this, "Authenitcation Header Built", null, null);
	}

}
