/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/** Integration tests for conversion of App-Schema curve geometries. */
public class InspireCurveConversionTest extends AbstractAppSchemaTestSupport {

    private static final String EX_PREFIX = "ex";
    private static final String EX_URI = "http://example.com/inspire-curve-repro";
    private static final String GML32_URI = "http://www.opengis.net/gml/3.2";

    @Override
    protected AbstractAppSchemaMockData createTestData() {
        return new MockData();
    }

    @Test
    public void testSinglePartLineIsEncoded() {
        Document document = getFeature("single_part_control");

        NodeList lineStrings = document.getElementsByTagNameNS(GML32_URI, "LineString");
        assertEquals(1, lineStrings.getLength());
        NodeList posLists = document.getElementsByTagNameNS(GML32_URI, "posList");
        assertEquals(1, posLists.getLength());
        assertEquals("13.35 52.5 13.36 52.505 13.37 52.51", posLists.item(0).getTextContent());
    }

    @Test
    public void testTwoPartLineIsRejected() {
        assertMultipartConversionRejected("multipart_problem_candidate");
    }

    @Test
    public void testThreePartLineIsRejected() {
        assertMultipartConversionRejected("multipart_three_parts");
    }

    private Document getFeature(String name) {
        return getAsDOM("wfs?service=WFS&version=2.0.0&request=GetFeature&typeNames=ex:CurveFeature"
                + "&CQL_FILTER=name%3D%27"
                + name
                + "%27");
    }

    private void assertMultipartConversionRejected(String name) {
        Document document = getFeature(name);

        assertEquals(
                prettyString(document),
                "ExceptionReport",
                document.getDocumentElement().getLocalName());
        assertTrue(document.getDocumentElement()
                .getTextContent()
                .contains("Cannot convert geometry from MultiLineString to LineString"));
    }

    private static final class MockData extends AbstractAppSchemaMockData {

        @Override
        protected void addContent() {
            putNamespace(EX_PREFIX, EX_URI);
            addFeatureType(
                    EX_PREFIX,
                    "CurveFeature",
                    "inspire/curve/curve-feature.xml",
                    "inspire/curve/curve-feature.xsd",
                    "inspire/curve/multipart_curve.cpg",
                    "inspire/curve/multipart_curve.dbf",
                    "inspire/curve/multipart_curve.prj",
                    "inspire/curve/multipart_curve.shp",
                    "inspire/curve/multipart_curve.shx");
        }
    }
}
