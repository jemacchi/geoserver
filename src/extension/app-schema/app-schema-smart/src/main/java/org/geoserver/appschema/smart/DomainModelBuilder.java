package org.geoserver.appschema.smart;

import org.geoserver.domainmodel.DomainMetadataStore;

public class DomainModelBuilder {
	
	private DomainMetadataStore domain;
	
	public DomainModelBuilder(DomainMetadataStore dms) {
		this.domain = dms;
	}

	public DomainMetadataStore getDomain() {
		return domain;
	}

}
