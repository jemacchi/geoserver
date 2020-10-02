package org.geoserver.appschema.smart;

import org.geoserver.domainmodel.DomainMetadataStore;

public class DomainModelBuilder {
	
	private DomainMetadataStore domainMetadataStore;
	
	public DomainModelBuilder(DomainMetadataStore dms) {
		this.domainMetadataStore = dms;
	}

	public DomainMetadataStore getDomainMetadataStore() {
		return domainMetadataStore;
	}

}
