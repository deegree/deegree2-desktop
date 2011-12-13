//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.15 at 10:29:06 AM MESZ 
//


package org.deegree.igeo.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for ToolbarEntryValueType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ToolbarEntryValueType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ToggleButton"/>
 *     &lt;enumeration value="PushButton"/>
 *     &lt;enumeration value="RadioButton"/>
 *     &lt;enumeration value="CheckBox"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum ToolbarEntryValueType {

    @XmlEnumValue("ToggleButton")
    TOGGLE_BUTTON("ToggleButton"),
    @XmlEnumValue("PushButton")
    PUSH_BUTTON("PushButton"),
    @XmlEnumValue("RadioButton")
    RADIO_BUTTON("RadioButton"),
    @XmlEnumValue("CheckBox")
    CHECK_BOX("CheckBox");
    private final String value;

    ToolbarEntryValueType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ToolbarEntryValueType fromValue(String v) {
        for (ToolbarEntryValueType c: ToolbarEntryValueType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}