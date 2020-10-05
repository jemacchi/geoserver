package org.geoserver.appschema.smart;

import org.geoserver.domainmodel.DomainModel;
import org.geoserver.domainmodel.DomainModelParameters;
import org.geoserver.domainmodel.jdbc.JdbcDomainModel;
import org.geoserver.domainmodel.jdbc.JdbcDomainModelParameters;

public class DomainModelFactory {

	public DomainModel getDomainModel(DomainModelParameters parameters) {
		if (parameters.getType().equals(JdbcDomainModelParameters.TYPE)) {
			JdbcDomainModelParameters jdmp = (JdbcDomainModelParameters) parameters;
			return new JdbcDomainModel(jdmp);
		}
		return null;
	}
}
