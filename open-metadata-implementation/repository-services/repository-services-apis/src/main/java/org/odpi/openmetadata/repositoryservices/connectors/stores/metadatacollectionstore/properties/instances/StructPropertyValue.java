/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * StructPropertyValue supports the value part of property that is defined as a complex structure.
 * It manages a list of properties that cover the fields in the structure.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class StructPropertyValue extends InstancePropertyValue
{
    @Serial
    private static final long serialVersionUID = 1L;

    private InstanceProperties  attributes = null;


    /**
     * Default constructor set StructPropertyValue to null.
     */
    public StructPropertyValue()
    {
        super(InstancePropertyCategory.STRUCT);
    }


    /**
     * Copy/clone constructor sets up the values based on the template.
     *
     * @param template StructPropertyValue to copy.
     */
    public StructPropertyValue(StructPropertyValue template)
    {
        super(template);

        if (template != null)
        {
            attributes = template.getAttributes();
        }
    }


    /**
     * Delegate the process of cloning to the subclass.
     *
     * @return subclass of InstancePropertyValue
     */
    @Override
    public  InstancePropertyValue cloneFromSubclass()
    {
        return new StructPropertyValue(this);
    }


    /**
     * Return the string version of the value - used for error logging.
     *
     * @return string value
     */
    @Override
    public String valueAsString()
    {
        if (attributes != null)
        {
            return mapValuesAsString(attributes.getInstanceProperties()).toString();
        }

        return null;
    }


    /**
     * Return the object version of the value - used for comparisons.
     *
     * @return object value
     */
    @Override
    public Object valueAsObject()
    {
        if (attributes != null)
        {
            return mapValuesAsObject(attributes.getInstanceProperties());
        }

        return null;
    }


    /**
     * Return the attributes that make up the fields of the struct.
     *
     * @return attributes InstanceProperties iterator
     */
    public InstanceProperties getAttributes()
    {
        if (attributes == null)
        {
            return null;
        }
        else
        {
            return new InstanceProperties(attributes);
        }
    }


    /**
     * Set up the attributes that make up the fields of the struct.
     *
     * @param attributes InstanceProperties iterator
     */
    public void setAttributes(InstanceProperties attributes) { this.attributes = attributes; }


    /**
     * Standard toString method.
     *
     * @return JSON style description of variables.
     */
    @Override
    public String toString()
    {
        return "StructPropertyValue{" +
                "attributes=" + attributes +
                ", instancePropertyCategory=" + getInstancePropertyCategory() +
                ", typeGUID='" + getTypeGUID() + '\'' +
                ", typeName='" + getTypeName() + '\'' +
                '}';
    }


    /**
     * Validate that an object is equal depending on their stored values.
     *
     * @param objectToCompare object
     * @return boolean result
     */
    @Override
    public boolean equals(Object objectToCompare)
    {
        if (this == objectToCompare)
        {
            return true;
        }
        if (! (objectToCompare instanceof StructPropertyValue that))
        {
            return false;
        }
        if (! super.equals(objectToCompare))
        {
            return false;
        }

        return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;
    }


    /**
     * Return a hash code based on the property values
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
