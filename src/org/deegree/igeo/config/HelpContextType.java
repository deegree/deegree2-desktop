//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.15 at 10:29:06 AM MESZ 
//


package org.deegree.igeo.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				As described above help pages can be registered to an application as itself as well as to each registered module. A help
 * 				page is a reference (URL) to a HTML page containing help, informations etc.. For each page there can be a list of keywords
 * 				that describe the content of the page and that will be made available to a user to find the help he needs. 
 * 				If more than one page	is registered to a module or a application one must be defined as default. The default page will be 
 * 				presented to a user if he request help for a modul or the application itself. The other pages are available through their 
 * 				keywords. 
 * 				Each page can be assigned to a language to enable multi language support; ISO code must be used for language description
 * 			
 * 
 * <p>Java class for HelpContextType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HelpContextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.deegree.org/coremapmodel}OnlineResource"/>
 *                   &lt;element name="keyword" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="mainPage" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                 &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HelpContextType", namespace = "http://www.deegree.org/settings", propOrder = {
    "page"
})
public class HelpContextType {

    @XmlElement(namespace = "http://www.deegree.org/settings", required = true)
    protected HelpContextType.Page page;

    /**
     * Gets the value of the page property.
     * 
     * @return
     *     possible object is
     *     {@link HelpContextType.Page }
     *     
     */
    public HelpContextType.Page getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *     allowed object is
     *     {@link HelpContextType.Page }
     *     
     */
    public void setPage(HelpContextType.Page value) {
        this.page = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.deegree.org/coremapmodel}OnlineResource"/>
     *         &lt;element name="keyword" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="mainPage" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "onlineResource",
        "keyword"
    })
    public static class Page {

        @XmlElement(name = "OnlineResource", namespace = "http://www.deegree.org/coremapmodel", required = true)
        protected OnlineResourceType onlineResource;
        @XmlElement(namespace = "http://www.deegree.org/settings")
        protected List<String> keyword;
        @XmlAttribute
        protected Boolean mainPage;
        @XmlAttribute
        protected String language;

        /**
         * Gets the value of the onlineResource property.
         * 
         * @return
         *     possible object is
         *     {@link OnlineResourceType }
         *     
         */
        public OnlineResourceType getOnlineResource() {
            return onlineResource;
        }

        /**
         * Sets the value of the onlineResource property.
         * 
         * @param value
         *     allowed object is
         *     {@link OnlineResourceType }
         *     
         */
        public void setOnlineResource(OnlineResourceType value) {
            this.onlineResource = value;
        }

        /**
         * Gets the value of the keyword property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the keyword property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getKeyword().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getKeyword() {
            if (keyword == null) {
                keyword = new ArrayList<String>();
            }
            return this.keyword;
        }

        /**
         * Gets the value of the mainPage property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isMainPage() {
            if (mainPage == null) {
                return false;
            } else {
                return mainPage;
            }
        }

        /**
         * Sets the value of the mainPage property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMainPage(Boolean value) {
            this.mainPage = value;
        }

        /**
         * Gets the value of the language property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Sets the value of the language property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLanguage(String value) {
            this.language = value;
        }

    }

}