
package uk.gov.gsi.hmrc.rest.dms.model;

import java.util.ArrayList;
import java.util.List;


import com.emc.documentum.rest.binding.SerializableType;

@SerializableType(value = "documents",  fieldOrder = {
	    "header",
	    "metadata",
	    "attachment"
	})
public class Document {

    private Header header;
    private List<Metadata> metadata = new ArrayList<Metadata>();
    private Attachment attachment;


    /**
     * 
     * @return
     *     The header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * 
     * @param header
     *     The header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * 
     * @return
     *     The metadata
     */
    public List<Metadata> getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The attachment
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * 
     * @param attachment
     *     The attachment
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

}
