//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.12.17 at 04:13:52 PM PST
//

package org.geoserver.mapml.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for axisType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="axisType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="latitude"/>
 *     &lt;enumeration value="longitude"/>
 *     &lt;enumeration value="easting"/>
 *     &lt;enumeration value="northing"/>
 *     &lt;enumeration value="x"/>
 *     &lt;enumeration value="y"/>
 *     &lt;enumeration value="row"/>
 *     &lt;enumeration value="column"/>
 *     &lt;enumeration value="i"/>
 *     &lt;enumeration value="j"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "axisType")
@XmlEnum
public enum AxisType {
    @XmlEnumValue("latitude")
    LATITUDE("latitude"),
    @XmlEnumValue("longitude")
    LONGITUDE("longitude"),
    @XmlEnumValue("easting")
    EASTING("easting"),
    @XmlEnumValue("northing")
    NORTHING("northing"),
    @XmlEnumValue("x")
    X("x"),
    @XmlEnumValue("y")
    Y("y"),
    @XmlEnumValue("row")
    ROW("row"),
    @XmlEnumValue("column")
    COLUMN("column"),
    @XmlEnumValue("i")
    I("i"),
    @XmlEnumValue("j")
    J("j");
    private final String value;

    AxisType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AxisType fromValue(String v) {
        for (AxisType c : AxisType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
