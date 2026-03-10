
package com.smartbear.client;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="obj" type="{http://smartbear.com}SampleTestClass" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "obj"
})
@XmlRootElement(name = "SetSampleObject")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
public class SetSampleObject
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    private final static long serialVersionUID = 1L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    protected SampleTestClass obj;

    /**
     * Gets the value of the obj property.
     * 
     * @return
     *     possible object is
     *     {@link SampleTestClass }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public SampleTestClass getObj() {
        return obj;
    }

    /**
     * Sets the value of the obj property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleTestClass }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-05-15T08:48:54+02:00", comments = "JAXB RI v2.3.2")
    public void setObj(SampleTestClass value) {
        this.obj = value;
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
