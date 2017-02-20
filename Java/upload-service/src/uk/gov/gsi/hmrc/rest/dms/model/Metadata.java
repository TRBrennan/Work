
package uk.gov.gsi.hmrc.rest.dms.model;

import com.emc.documentum.rest.binding.SerializableType;


@SerializableType(value = "metadata",  fieldOrder = {
		"attr_name",
	    "attr_value"
		})
public class Metadata {

    private String attr_name;
    private String attr_value;

    /**
     * 
     * @return
     *     The attrTime1
     */
    public String getAttr_name() {
        return attr_name;
    }

    /**
     * 
     * @param attrTime1
     *     The attr_time1
     */
    public void setAttr_name(String attr_name) {
        this.attr_name = attr_name;
    }

    /**
     * 
     * @return
     *     The attrTime2
     */
    public String getAttr_value() {
        return attr_value;
    }

    /**
     * 
     * @param attrTime2
     *     The attr_time2
     */
    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }


}
