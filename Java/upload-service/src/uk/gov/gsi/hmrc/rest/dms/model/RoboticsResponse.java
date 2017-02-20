
package uk.gov.gsi.hmrc.rest.dms.model;


import com.documentum.fc.common.DfLogger;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.documentum.rest.binding.SerializableType;
import com.emc.documentum.rest.binding.SerializableType.FieldVisibility;

/**
 * 
 * @author RC14670 (J Gill)
 *  
 * 	Description – Model to represent a Robotics Response json 
 * 
 * 	Modification history
 *	 ------------------------------------------
 * 	Version 1.0 - Initial version August 2016
 * 	Versiion 2.0 - Inclusion of Robotics November 2016
 * 
 *  rc00017 - moved function to populate metaDataAttachmentCount variable to the block where the iForm is processed, so it can be in any order from JSON
 *  RC14670 - Audit JG_001 EKDMS 1249 Update to getNote to ensure max note size is 800 characters after DMS Primary Process adds username and date time.
 *  RC14670 - JG_001b - EDT 593 increase substring value
 */


@SerializableType(value = "message",  fieldVisibility = FieldVisibility.PUBLIC ,fieldOrder = {
		  "source",
		    "target",
		    "transaction_id",
		    "action",
		    "note",
		    "category",
		    "retention_policy"
	})
public class RoboticsResponse{

    private String source;
    private String target;
    private String transaction_id;
    private String action;
    private String note;
    private String category;
    private String retention_policy;
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the transaction_id
	 */
	public String getTransaction_id() {
		return transaction_id;
	}
	/**
	 * @param transaction_id the transaction_id to set
	 */
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		
		//JG_001 EDKMS 1249,JG_001b - EDT 593 - increase substring value
		if (note.length()>800)
		{
			DfLogger.debug(this,"triming note size to 800 in GET as its  " + note.length() ,null,null);
			
			note = note.substring(0, 800);
		}
		
		//END JG_001 EDKMS 1249,JG_001b - EDT 593
		
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		
		
		this.note = note;
		
	}

	 public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getRetention_policy() {
		return retention_policy;
	}
	public void setRetention_policy(String retention_policy) {
		this.retention_policy = retention_policy;
	}
	public boolean isValid()
	 
	 {
		 boolean valid = true;
		 
		 if(StringUtil.isEmptyOrNull(this.getAction()))
			{
	    		DfLogger.error(this,"No Value for Action: "  ,null,null);
	    		valid = false;
			}
		 
		 if(StringUtil.isEmptyOrNull(this.getTransaction_id()))
			{
	    		DfLogger.error(this,"No Value for Unique Transaction Id: "  ,null,null);
	    		valid = false;
			}
		 
		 if(StringUtil.isEmptyOrNull(this.getSource()))
			{
	    		DfLogger.error(this,"No Value for Source: "  ,null,null);
	    		valid = false;
			}
		 return valid;
	 }

}
