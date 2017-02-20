/*
* Copyright (c) 2014. EMC Corporation. All Rights Reserved.
*/
package uk.gov.gsi.hmrc.rest.dms.model;

import java.util.ArrayList;
import java.util.List;

import com.emc.documentum.rest.binding.SerializableType;
import com.emc.documentum.rest.model.Link;
import com.emc.documentum.rest.model.Linkable;

/**
* The model for the jsonResponse.
*/
@SerializableType(value = "json_response", // the root name of the marshalled xml and json
fieldOrder = { "result", "source","description", "reconciliation_id"},
xmlNS = "http://www.custom.com/sample", xmlNSPrefix = "cs")

public class JsonResponseModel implements Linkable {     
	private String result;
	private String source;
	private String reconciliation_id;
	private String description;
	protected List<Link> links = new ArrayList<Link>();
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getSource() {
	    return result;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getReconciliation_id() {
		return reconciliation_id;
	}
	public void setReconciliation_id(String reconciliation_id) {
		this.reconciliation_id = reconciliation_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    @Override
    public List<Link> getLinks() {
        return links;
    }
    @Override
    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
