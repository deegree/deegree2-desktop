//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.15 at 10:29:06 AM MESZ 
//


package org.deegree.igeo.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				not supported yet
 * 			
 * 
 * <p>Java class for CertificateAIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateAIType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="certificate" type="{http://www.deegree.org/coremapmodel}OnlineResourceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateAIType", namespace = "http://www.deegree.org/coremapmodel", propOrder = {
    "certificate"
})
public class CertificateAIType {

    @XmlElement(namespace = "http://www.deegree.org/coremapmodel")
    protected OnlineResourceType certificate;

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *     
     */
    public OnlineResourceType getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *     
     */
    public void setCertificate(OnlineResourceType value) {
        this.certificate = value;
    }

}