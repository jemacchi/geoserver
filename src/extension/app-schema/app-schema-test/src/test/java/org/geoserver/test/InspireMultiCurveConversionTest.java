/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/** Integration tests for lossless App-Schema MultiCurve geometry encoding. */
public class InspireMultiCurveConversionTest extends AbstractAppSchemaTestSupport {

    private static final String EX_PREFIX = "exmc";
    private static final String EX_URI = "http://example.com/inspire-multicurve-repro";
    private static final String GML32_URI = "http://www.opengis.net/gml/3.2";

    @Override
    protected AbstractAppSchemaMockData createTestData() {
        return new MockData();
    }

    @Test
    public void testSinglePartLineIsEncodedAsOneMember() {
        Document document = getFeature("single_part_control");

        assertMultiCurve(document, 1);
        assertPosList(document, 0, "13.35 52.5 13.36 52.505 13.37 52.51");
    }

    @Test
    public void testTwoPartLinePreservesBothMembers() {
        Document document = getFeature("multipart_problem_candidate");

        assertMultiCurve(document, 2);
        assertPosList(document, 0, "13.4 52.5 13.41 52.505 13.42 52.51");
        assertPosList(document, 1, "13.43 52.5 13.44 52.505 13.45 52.51");
    }

    @Test
    public void testThreePartLinePreservesAllMembers() {
        Document document = getFeature("multipart_three_parts");

        assertMultiCurve(document, 3);
        assertPosList(document, 0, "13.5 52.5 13.51 52.505");
        assertPosList(document, 1, "13.52 52.5 13.53 52.505");
        assertPosList(document, 2, "13.54 52.5 13.55 52.505");
    }

    private Document getFeature(String name) {
        return getAsDOM("wfs?service=WFS&version=2.0.0&request=GetFeature&typeNames=exmc:MultiCurveFeature"
                + "&CQL_FILTER=name%3D%27"
                + name
                + "%27");
    }

    private void assertMultiCurve(Document document, int expectedMembers) {
        assertEquals(
                prettyString(document),
                "FeatureCollection",
                document.getDocumentElement().getLocalName());
        assertEquals(1, document.getElementsByTagNameNS(GML32_URI, "MultiCurve").getLength());
        assertEquals(
                expectedMembers,
                document.getElementsByTagNameNS(GML32_URI, "curveMember").getLength());
        assertEquals(
                expectedMembers,
                document.getElementsByTagNameNS(GML32_URI, "LineString").getLength());
    }

    private void assertPosList(Document document, int index, String expected) {
        NodeList posLists = document.getElementsByTagNameNS(GML32_URI, "posList");
        assertEquals(expected, posLists.item(index).getTextContent());
    }

    private static final class MockData extends AbstractAppSchemaMockData {

        @Override
        protected void addContent() {
            putNamespace(EX_PREFIX, EX_URI);
            addFeatureType(
                    EX_PREFIX,
                    "MultiCurveFeature",
                    "inspire/curve/multicurve-feature.xml",
                    "inspire/curve/multicurve-feature.xsd",
                    "inspire/curve/multipart_curve.cpg",
                    "inspire/curve/multipart_curve.dbf",
                    "inspire/curve/multipart_curve.prj",
                    "inspire/curve/multipart_curve.shp",
                    "inspire/curve/multipart_curve.shx");
        }
    }
}
