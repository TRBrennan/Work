package uk.gov.gsi.hmrc.robotics.soapCall;

import java.io.IOException;

import javax.xml.rpc.ServiceException;

import org.junit.Test;


public class CallTest {
	
	@Test
	public void RoboticsCallTest() throws ServiceException, IOException{
		//Endpoint endpoint = Endpoint.publish("http://25ek-7-d1-004:8088/mockRoboticAutomationBinding", new RoboticsCall());
		//assertTrue(endpoint.isPublished());
		//assertEquals("", endpoint.getBinding().getBindingID());
 
		//RoboticsCall rc = new RoboticsCall();
		String wf_Data = "Test";
		//String response = rc.formSubmission(null, wf_Data);
		
		//System.out.println("Repsone Data " + response);
				
	}
}
