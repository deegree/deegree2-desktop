//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.10.22 at 02:21:41 PM MESZ 
//


package org.deegree.igeo.modules.bookmarks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnvelopeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnvelopeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="minx" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="miny" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxx" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxy" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="crs" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnvelopeType")
public class EnvelopeType {

    @XmlAttribute(required = true)
    protected double minx;
    @XmlAttribute(required = true)
    protected double miny;
    @XmlAttribute(required = true)
    protected double maxx;
    @XmlAttribute(required = true)
    protected double maxy;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String crs;

    /**
     * Gets the value of the minx property.
     * 
     */
    public double getMinx() {
        return minx;
    }

    /**
     * Sets the value of the minx property.
     * 
     */
    public void setMinx(double value) {
        this.minx = value;
    }

    /**
     * Gets the value of the miny property.
     * 
     */
    public double getMiny() {
        return miny;
    }

    /**
     * Sets the value of the miny property.
     * 
     */
    public void setMiny(double value) {
        this.miny = value;
    }

    /**
     * Gets the value of the maxx property.
     * 
     */
    public double getMaxx() {
        return maxx;
    }

    /**
     * Sets the value of the maxx property.
     * 
     */
    public void setMaxx(double value) {
        this.maxx = value;
    }

    /**
     * Gets the value of the maxy property.
     * 
     */
    public double getMaxy() {
        return maxy;
    }

    /**
     * Sets the value of the maxy property.
     * 
     */
    public void setMaxy(double value) {
        this.maxy = value;
    }

    /**
     * Gets the value of the crs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrs() {
        return crs;
    }

    /**
     * Sets the value of the crs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrs(String value) {
        this.crs = value;
    }

}