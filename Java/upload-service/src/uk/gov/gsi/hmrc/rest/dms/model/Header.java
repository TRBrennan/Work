
package uk.gov.gsi.hmrc.rest.dms.model;


import com.emc.documentum.rest.binding.SerializableType;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.impl.util.StringUtil;

@SerializableType(value = "header",  fieldOrder = {
		"primary_content",
		"title",
		    "format",
		    "type",
		    "mime_type",
		    "store",
		    "source",
		    "target",
		    "reconciliation_id"
	})
public class Header {

	private String primary_content;
	private String title;
    private String format;
    private String type;
    private String mime_type;
    private String store;
    private String source;
    private String target;
    private String reconciliation_id;

    /**
	 * @return the primary_content
	 */
	public String getPrimary_content() {
		return primary_content;
	}

	/**
	 * @param primary_content the primary_content to set
	 */
	public void setPrimary_content(String primary_content) {
		this.primary_content = primary_content;
		
		DfLogger.error(this,"Setting primary content =  " + primary_content ,null,null);
	}

	/**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The format
     */
    public String getFormat() {
        return format;
    }

    /**
     * 
     * @param format
     *     The format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The mimeType
     */
    public String getMimeType() {
        return mime_type;
    }

    /**
     * 
     * @param mimeType
     *     The mime_type
     */
    public void setMimeType(String mime_type) {
        this.mime_type = mime_type;
    }

    /**
     * 
     * @return
     *     The store
     */
    public String getStore() {
        return store;
    }

    /**
     * 
     * @param store
     *     The store
     */
    public void setStore(String store) {
        this.store = store;
    }

    /**
     * 
     * @return
     *     The source
     */
    public String getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The target
     */
    public String getTarget() {
        return target;
    }

    /**
     * 
     * @param target
     *     The target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 
     * @return
     *     The reconciliationId
     */
    public String getReconciliationId() {
        return reconciliation_id;
    }

    /**
     * 
     * @param reconciliationId
     *     The reconciliation_id
     */
    public void setReconciliationId(String reconciliation_id) {
        this.reconciliation_id = reconciliation_id;
    }
    
    public boolean isValid()
    {
    	boolean valid = true;
    	
    	if(StringUtil.isEmptyOrNull(this.getPrimary_content()))
		{
    		DfLogger.error(this,"No Value for Primary Content in document: "  ,null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getTitle()))
		{
    		DfLogger.error(this,"No Value for Title in document: "  ,null,null);
    		valid = false;
		}
		
    	if(StringUtil.isEmptyOrNull(this.getFormat()))
		{
    		DfLogger.error(this,"No Value for Format in document: ",null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getType()))
		{
    		DfLogger.error(this,"No Value for Type in document: ",null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getMimeType()))
		{
    		DfLogger.error(this,"No Value for Mime Type in document: ",null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getStore()))
		{
    		DfLogger.error(this,"No Value for Store in document: ",null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getSource()))
		{
    		DfLogger.error(this,"No Value for Source in document: " ,null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getTarget()))
		{
    		DfLogger.error(this,"No Value for Target in document:" ,null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getReconciliationId()))
		{
    		DfLogger.error(this,"No Value for Reconciliation ID in document: " ,null,null);
    		valid = false;
		}
    	
    
    	return valid;
    }
}
