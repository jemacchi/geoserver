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

/** Integration tests for topology-safe App-Schema MultiLineString conversion. */
public class InspireCurveLineMergeTest extends AbstractAppSchemaTestSupport {

    private static final String EX_PREFIX = "exlm";
    private static final String EX_URI = "http://example.com/inspire-line-merge-repro";
    private static final String GML32_URI = "http://www.opengis.net/gml/3.2";

    @Override
    protected AbstractAppSchemaMockData createTestData() {
        return new MockData();
    }

    @Test
    public void testConnectedComponentsAreMerged() {
        assertLineString("connected_forward", "52.5 13.3 52.505 13.31 52.51 13.32 52.505 13.33 52.5 13.34");
    }

    @Test
    public void testReversedComponentsAreMerged() {
        assertLineString("connected_reversed", "52.5 13.4 52.505 13.41 52.51 13.42 52.505 13.43 52.5 13.44");
    }

    @Test
    public void testUnorderedComponentsAreMerged() {
        assertLineString(
                "connected_unordered", "52.5 13.5 52.505 13.51 52.51 13.52 52.505 13.53 52.505 13.54 52.5 13.55");
    }

    @Test
    public void testDisconnectedComponentsAreRejected() {
        assertConversionRejected("disconnected");
    }

    @Test
    public void testBranchedComponentsAreRejected() {
        assertConversionRejected("branched");
    }

    private void assertLineString(String name, String expectedPosList) {
        Document document = getFeature(name);

        assertEquals(
                prettyString(document),
                "FeatureCollection",
                document.getDocumentElement().getLocalName());
        assertEquals(1, document.getElementsByTagNameNS(GML32_URI, "LineString").getLength());
        NodeList posLists = document.getElementsByTagNameNS(GML32_URI, "posList");
        assertEquals(1, posLists.getLength());
        assertEquals(expectedPosList, posLists.item(0).getTextContent());
    }

    private void assertConversionRejected(String name) {
        Document document = getFeature(name);

        assertEquals(
                prettyString(document),
                "ExceptionReport",
                document.getDocumentElement().getLocalName());
        assertTrue(document.getDocumentElement()
                .getTextContent()
                .contains("Cannot convert geometry from MultiLineString to LineString"));
    }

    private Document getFeature(String name) {
        return getAsDOM("wfs?service=WFS&version=2.0.0&request=GetFeature&typeNames=exlm:CurveFeature"
                + "&CQL_FILTER=name%3D%27"
                + name
                + "%27");
    }

    private static final class MockData extends AbstractAppSchemaMockData {

        @Override
        protected void addContent() {
            putNamespace(EX_PREFIX, EX_URI);
            addFeatureType(
                    EX_PREFIX,
                    "CurveFeature",
                    "inspire/curve/line-merge-feature.xml",
                    "inspire/curve/line-merge-feature.xsd",
                    "inspire/curve/line_merge_cases.cpg",
                    "inspire/curve/line_merge_cases.dbf",
                    "inspire/curve/line_merge_cases.prj",
                    "inspire/curve/line_merge_cases.shp",
                    "inspire/curve/line_merge_cases.shx");
        }
    }
}
