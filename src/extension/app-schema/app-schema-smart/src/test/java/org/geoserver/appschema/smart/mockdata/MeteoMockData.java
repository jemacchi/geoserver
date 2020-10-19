package org.geoserver.appschema.smart.mockdata;

import org.geoserver.test.AbstractAppSchemaMockData;

public class MeteoMockData extends AbstractAppSchemaMockData {

    @Override
    public void addContent() {
        putNamespace("mt", "http://www.geo-solutions.it/smartappschema/1.0");
        addFeatureType(
                "mt",
                "BoreholeView",
                "BoreholeView.xml",
                "Gsml32Borehole.properties",
                "geosciml-portrayal.xsd");
    }
}
