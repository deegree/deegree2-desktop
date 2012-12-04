//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.07.29 at 09:58:26 AM MESZ 
//


package org.deegree.igeo.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 			A 	DirectStyle contains a description of how data accessed from a layers datasource should be rendered as SLD document.
 * 			An embedded SLD document must be XML encoded or included into a CDATA element.
 * 			
 * 
 * <p>Java class for DirectStyleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.deegree.org/coremapmodel}DefinedStyleType">
 *       &lt;sequence>
 *         &lt;element name="sld" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectStyleType", namespace = "http://www.deegree.org/coremapmodel", propOrder = {
    "sld"
})
public class DirectStyleType
    extends DefinedStyleType
{

    @XmlElement(namespace = "http://www.deegree.org/coremapmodel", required = true)
    protected String sld;

    /**
     * Gets the value of the sld property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSld() {
        return sld;
    }

    /**
     * Sets the value of the sld property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSld(String value) {
        this.sld = value;
    }

}