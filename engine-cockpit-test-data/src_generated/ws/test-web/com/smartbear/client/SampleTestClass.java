
package com.smartbear.client;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Java class for SampleTestClass complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SampleTestClass"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="X" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="Y" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IntArray" type="{http://smartbear.com}ArrayOfInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SampleTestClass", propOrder = {
    "x",
    "y",
    "name",
    "intArray"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
public class SampleTestClass
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    private final static long serialVersionUID = 1L;
    @XmlElement(name = "X")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    protected double x;
    @XmlElement(name = "Y")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    protected double y;
    @XmlElement(name = "Name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    protected String name;
    @XmlElement(name = "IntArray")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    protected ArrayOfInt intArray;

    /**
     * Gets the value of the x property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public void setY(double value) {
        this.y = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the intArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public ArrayOfInt getIntArray() {
        return intArray;
    }

    /**
     * Sets the value of the intArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public void setIntArray(ArrayOfInt value) {
        this.intArray = value;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
