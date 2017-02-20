
package uk.gov.gsi.hmrc.rest.dms.model;


import com.documentum.fc.common.DfLogger;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.documentum.rest.binding.SerializableType;


@SerializableType(value = "attachment",  fieldOrder = {
		"primary_content",  
		"encoding",
		    "data"
	})
public class Attachment {

	private String primary_content;
	private String encoding;
    private String data;

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
   	}

    /**
     * 
     * @return
     *     The encoded
     */
    public String getEncoded() {
        return encoding;
    }

    /**
     * 
     * @param encoded
     *     The encoded
     */
    public void setEncoded(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 
     * @return
     *     The data
     */
    public String getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(String data) {
        this.data = data;
    }
    
    public boolean isValid()
    {
    	boolean valid = true;
    	
    	if(StringUtil.isEmptyOrNull(this.getEncoded()))
		{
    		DfLogger.error(this,"No Value for Encoded in the Attachment: "  ,null,null);
    		valid = false;
		}
    	if(StringUtil.isEmptyOrNull(this.getData()))
		{
    		DfLogger.error(this,"No Value for Data in the Attachment: "  ,null,null);
    		valid = false;
		}
		
    	
    	
    	return valid;
    }

}
