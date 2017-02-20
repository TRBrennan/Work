
package uk.gov.gsi.hmrc.rest.dms.model;


import java.util.ArrayList;
import java.util.List;

import com.emc.documentum.rest.binding.SerializableType;


@SerializableType(value = "object",  fieldOrder = {
	    "message","metadata"
	})
public class RoboticsResponses  {

    private RoboticsResponse message = new RoboticsResponse();
    private List<Metadata> metadata = new ArrayList<Metadata>();

	/**
	 * @return the message
	 */
	public RoboticsResponse getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(RoboticsResponse message) {
		this.message = message;
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
    


}
