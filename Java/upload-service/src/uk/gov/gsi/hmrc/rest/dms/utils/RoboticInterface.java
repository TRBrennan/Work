package uk.gov.gsi.hmrc.rest.dms.utils;

import uk.gov.gsi.hmrc.rest.dms.robotics.roboticinterface.RobotInterface;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfLogger;

public class RoboticInterface extends RobotInterface {

	public RoboticInterface() {
		DfLogger.error(this,"Robotics interface Iam here",null,null);
	}
	

	public void RoboticRequestInterface(String roboticType, String roboticData,
			String mainObjCreated, String realcategory, IDfSession session) {

		DfLogger.error(this,"IN Robotic Request, set up the RobotInterface",null,null);
		
		
		// !!!! CARLOS IT FAILS ON THIS CALL!!! SEE LOG FILE

		
		DfLogger.error(this,"RobotInterface Created call the method",null,null);
		
		RoboticRequest(roboticType, roboticData, mainObjCreated, realcategory, session);

		DfLogger.error(this,"Robotics interface I finished",null,null);
		
	}
	
}
