package org.geoserver.appschema.smart;

import org.geoserver.appschema.smart.utils.Node;
import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.DomainMetadataStore;
import org.geoserver.domainmodel.DomainModel;
import org.geoserver.domainmodel.DomainModelParameters;

public class DomainModelBuilder {
	
	public DomainMetadataStore getDomainMetadataStore(DomainModelParameters parameters) {
		DomainModelFactory factory = new DomainModelFactory();
		DomainModel dm = factory.getDomainModel(parameters);
		return dm;
	}
	
	public Node<AbstractDomainObject> buildTreeFrom(DomainModel domain, AbstractDomainObject root) {
		return null;
	}
	
	public Node<AbstractDomainObject> buildTreeFrom(DomainModel domain) {
		return null;
	}

}
