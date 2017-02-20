package uk.gov.gsi.hmrc.rest.dms.helper;


	
	public class WorkFlowBean 
	{
	public String action;
	public String target;
	public String source;
	public String unique_id;
	public String note;
	public String originalCategory;
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
	 * @return the transaction_id
	 */
	public String getUnique_id() {
		return unique_id;
	}
	/**
	 * @param transaction_id the transaction_id to set
	 */
	public void setUnique_id(String unique_id) {
		this.unique_id =unique_id;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the originalCategory
	 */
	public String getOriginalCategory() {
		return originalCategory;
	}
	/**
	 * @param originalCategory the originalCategory to set
	 */
	public void setOriginalCategory(String originalCategory) {
		this.originalCategory = originalCategory;
	}

}
