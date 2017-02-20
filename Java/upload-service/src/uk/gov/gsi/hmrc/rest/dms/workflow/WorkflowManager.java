package uk.gov.gsi.hmrc.rest.dms.workflow;



import com.documentum.bpm.IDfWorkflowEx;
import com.documentum.com.DfClientX;
import com.documentum.fc.client.IDfActivity;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import uk.gov.gsi.hmrc.rest.dms.utils.Constants;
import uk.gov.gsi.hmrc.rest.dms.utils.DfcUtils;

/**
 * Automatically starts the HMRC MailPoT Workflow for items which have been imported into the Repository.
 * 
 */
public class WorkflowManager
{
	/**
	 * The First Item in a List.
	 */
	private static final int FIRST_ITEM = 0;

	/**
	 * The Mail Item Workflow Note.
	 */
	private static final String WORKFLOW_NOTE = null;

	/**
	 * The Mail Item Process ID.
	 */
	private IDfId theMailItemProcessID = null;

	private static WorkflowManager instance = null;
	
	protected WorkflowManager() {
	      // Exists only to defeat instantiation.
	   }
	
	public static WorkflowManager getInstance() {
	      if(instance == null) {
	         instance = new WorkflowManager();
	      }
	      return instance;
	   }

	/**
	 * Starts the MailPoT Mail Item Workflow against the Mail Item.
	 * 
	 * @param theMailItem
	 *            the Mail Item that the Workflow should be started with.
	 * @return IDfID
	 *         the Object ID of the Workflow which has been created.
	 * @throws DfException
	 *             if the Workflow could not be started for the Mail Item.
	 */
	public IDfId startMailItemWorkflow(final IDfSysObject theMailItem, IDfSession s) throws DfException	{
		
		DfLogger.debug(this,"JG _ RUN TH EINGESTED MAIL PROCESS NOT NEW ONE", null,null);
		IDfWorkflowBuilder theBuilder = s.newWorkflowBuilder(new DfId("4b0186a28000236c"));
		theBuilder.initWorkflow();
		 System.out.println("builder.getWorkflow().getObjectId().toString() = " + theBuilder.getWorkflow().getObjectId().toString());
		
		
		DfLogger.debug(this,"JG _ GET A WORKFLOWEX", null,null);
		
		IDfWorkflowEx wfe  =  null;
		//IDfWorkflowEx wfe = (IDfWorkflowEx)  theBuilder.getWorkflow().getObjectId();
		DfLogger.debug(this,"WE HAVE AA Workflow ex", null,null);
		
		//DfLogger.debug(this,"set process varaibles with workflowex", null,null);

		//wfe.setPrimitiveObjectValue("request_source", "robotics");
		//wfe.setPrimitiveObjectValue("mail_item_id", "0000000000000000");
		//wfe.setPrimitiveObjectValue("robotic_identifier", "testrobotic_identifier");}
		
		
		
		
		IDfId theWorkflowID = theBuilder.runWorkflow();
		
		DfLogger.debug(this,"workflow id in the workflow manager = " +theWorkflowID , null,null);
		// Obtain the Start Activity
		final IDfActivity theStartActivity = getStartActivity(theBuilder, s);

		// Populate the Workflow Package
		final IDfList thePackages = new DfClientX().getList();
		thePackages.append(theMailItem.getObjectId());

		// Start the Workflow
		theBuilder.addPackage(theStartActivity.getObjectName(), theStartActivity.getPortName(FIRST_ITEM), Constants.WORKFLOW_PACKAGE_NAME, Constants.WORKFLOW_PACKAGE_TYPE, WORKFLOW_NOTE, Constants.WORKFLOW_NOTE_PERSIST, thePackages);
		return theWorkflowID; 
	}

   	/**
	 * The Mail Item Process ID.
	 */
	private boolean doTryAndGetMailId = true;

   	public boolean isValid() {
        return theMailItemProcessID != null;
    }

	/**
	 * Obtains the Mail Item Process ID from the Repository.
	 * 
	 * @return IDfId
	 *         the Documentum Object ID of the Process.
	 * @throws DfException
	 *             if the Process ID could not be obtained.
	 */
	public IDfId getMailItemProcessID(IDfSession s) throws DfException	{
		// Obtain the Mail Item Process ID
		if (theMailItemProcessID == null && doTryAndGetMailId) {
			DfLogger.debug(this, "Initialize workflow template: " + Constants.REST_PROCESS_NAME, null, null);
			//final String theDQL = Constants.PROCESS_QUERY.replace("REST_PROCESS_NAME", Constants.REST_PROCESS_NAME);
			
			final String theDQL = "select r_object_id from dm_process where object_name = 'DMS REST Primary Process'" ;
			IDfCollection col = null;

			// Iterate through the Results
			try {
				col = DfcUtils.executeQuery(theDQL, s, true);
	
				// Obtain the Process ID
				if (col!=null && col.next())
					theMailItemProcessID = col.getId(Constants.OBJECT_ID);
			}catch (DfException e) {
				DfLogger.error(this,  theDQL, null, null);
				throw e;
			} finally {
				if(col!=null)
					col.close();
			}

            if(theMailItemProcessID!=null)
                DfLogger.debug(this, "Initializes workflow template: " + theMailItemProcessID.getId(), null, null);
            else
                DfLogger.error(this, Constants.PROCESS_NAME + " workflow template does not exist", null, null);
            doTryAndGetMailId = false;
		}
		// Return the Process Identifier
		return theMailItemProcessID;
	}

	/**
	 * Obtains the Mail Item Workflow Start Activity from the Repository.
	 * 
	 * @param theWorkflowBuilder
	 *            a Mail Item Workflow Builder
	 * @return IDfActivity
	 *         the Start Activity of the Workflow.
	 * @throws DfException
	 *             if the Start Activity could not be obtained.
	 */
	private IDfActivity getStartActivity(final IDfWorkflowBuilder theWorkflowBuilder, final IDfSession s) throws DfException
	{
		// Log the Method Invocation
		DfLogger.debug(this, "Get Start Activity for Workflow: " + theWorkflowBuilder.getProcess().getObjectName(), null, null);

		// Get the Start Activity ID
		final IDfId theStartActivityID = theWorkflowBuilder.getStartActivityIds().getId(FIRST_ITEM);

		// Get the Start Activity
		final IDfActivity theActivity = (IDfActivity) s.getObject(theStartActivityID);

		// Return the Start Activity
		DfLogger.debug(this, "Got Start Activity. Name: " + theActivity.getObjectName(), null, null);
		return theActivity;
	}
}
