//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.01 at 02:47:35 PM GMT-03:00 
//


package org.cemantika.testing.cxg.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://drools.org/drools-5.0/process}imports"/>
 *         &lt;element ref="{http://drools.org/drools-5.0/process}functionImports"/>
 *         &lt;element ref="{http://drools.org/drools-5.0/process}globals"/>
 *         &lt;element ref="{http://drools.org/drools-5.0/process}variables"/>
 *         &lt;element ref="{http://drools.org/drools-5.0/process}swimlanes"/>
 *         &lt;element ref="{http://drools.org/drools-5.0/process}exceptionHandlers"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "importsOrFunctionImportsOrGlobals"
})
@XmlRootElement(name = "header")
public class Header {

    @XmlElements({
        @XmlElement(name = "functionImports", type = FunctionImports.class),
        @XmlElement(name = "globals", type = Globals.class),
        @XmlElement(name = "swimlanes", type = Swimlanes.class),
        @XmlElement(name = "variables", type = Variables.class),
        @XmlElement(name = "imports", type = Imports.class),
        @XmlElement(name = "exceptionHandlers", type = ExceptionHandlers.class)
    })
    protected List<Object> importsOrFunctionImportsOrGlobals;

    /**
     * Gets the value of the importsOrFunctionImportsOrGlobals property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importsOrFunctionImportsOrGlobals property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportsOrFunctionImportsOrGlobals().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FunctionImports }
     * {@link Globals }
     * {@link Swimlanes }
     * {@link Variables }
     * {@link Imports }
     * {@link ExceptionHandlers }
     * 
     * 
     */
    public List<Object> getImportsOrFunctionImportsOrGlobals() {
        if (importsOrFunctionImportsOrGlobals == null) {
            importsOrFunctionImportsOrGlobals = new ArrayList<Object>();
        }
        return this.importsOrFunctionImportsOrGlobals;
    }

}
