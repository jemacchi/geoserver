package org.geoserver.appschema.smart;

import org.geoserver.domain.model.DomainModel;
import org.geoserver.domain.model.DomainModelParameters;
import org.geoserver.domain.model.jdbc.JdbcDomainModel;
import org.geoserver.domain.model.jdbc.JdbcDomainModelParameters;

public class DomainModelFactory {

    public DomainModel getDomainModel(DomainModelParameters parameters) {
        if (parameters.getType().equals(JdbcDomainModelParameters.TYPE)) {
            JdbcDomainModelParameters jdmp = (JdbcDomainModelParameters) parameters;
            return new JdbcDomainModel(jdmp);
        }
        return null;
    }
}
