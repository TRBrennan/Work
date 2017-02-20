package uk.gov.gsi.hmrc.rest.dms.robotics.soapCall;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RoboticsFormDataBean implements Serializable {
	
	private String solutionID;
	private String workflowID;
	private String osUID;
	private String extInvReq;
	private String userName;
	private String password;
	private String initator_type;
	private String endpointURL;
	private String roboticCategory;
	private String status_text;
	
	public String getSolutionID() {
		return solutionID;
	}

	public void setSolutionID(String solutionID) {
		this.solutionID = solutionID;
	}

	public String getWorkflowID() {
		return workflowID;
	}

	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}

	public String getOsUID() {
		return osUID;
	}

	public void setOsUID(String osUID) {
		this.osUID = osUID;
	}

	public String getExtInvReq() {
		return extInvReq;
	}

	public void setExtInvReq(String extInvReq) {
		this.extInvReq = extInvReq;
	}

	public String getUserName() {
		return userName;		
	}
	
	public String getStatus_text() {
		return status_text;
	}

	public void setStatus_text(String status_text) {
		this.status_text = status_text;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}
	
	public String getRoboticCategory() {
		return roboticCategory;
	}

	public void setRoboticCategory(String roboticCategory) {
		this.roboticCategory = roboticCategory;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInitator_type() {
		return initator_type;
	}

	public void setInitator_typeD(String initator_type) {
		this.initator_type = initator_type;
	}
	
	public String getEndpointURL() {
		return endpointURL;
	}

	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}
}
