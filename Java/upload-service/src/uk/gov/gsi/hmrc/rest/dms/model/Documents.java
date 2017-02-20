
package uk.gov.gsi.hmrc.rest.dms.model;

import java.util.ArrayList;
import java.util.List;


import com.emc.documentum.rest.binding.SerializableType;


@SerializableType(value = "object",  fieldOrder = {
	    "documents"
	})
public class Documents {

    private List<Document> documents = new ArrayList<Document>();

    /**
     * 
     * @return
     *     The documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * 
     * @param documents
     *     The documents
     */
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }



}
