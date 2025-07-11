/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.schema;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * EnumSchemaTypeProperties carries the specialized parameters for creating or updating enum schema types.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)

public class EnumSchemaTypeProperties extends SimpleSchemaTypeProperties
{
    private String validValueSetGUID = null;

    /**
     * Default constructor
     */
    public EnumSchemaTypeProperties()
    {
        super();
        super.setTypeName(OpenMetadataType.ENUM_SCHEMA_TYPE.typeName);
    }


    /**
     * Copy/clone Constructor
     *
     * @param template template object to copy.
     */
    public EnumSchemaTypeProperties(EnumSchemaTypeProperties template)
    {
        super(template);

        if (template != null)
        {
            validValueSetGUID = template.getValidValueSetGUID();
        }
    }


    /**
     * Return the unique identifier of the valid value set that describes the enum values for this schema element.
     *
     * @return string guid
     */
    public String getValidValueSetGUID()
    {
        return validValueSetGUID;
    }


    /**
     * Set up the unique identifier of the value set that describes the enum values for this schema element.
     *
     * @param validValueSetGUID string guid
     */
    public void setValidValueSetGUID(String validValueSetGUID)
    {
        this.validValueSetGUID = validValueSetGUID;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "EnumSchemaTypeProperties{" +
                "validValueSetGUID='" + validValueSetGUID + '\'' +
                "} " + super.toString();
    }


    /**
     * Compare the values of the supplied object with those stored in the current object.
     *
     * @param objectToCompare supplied object
     * @return boolean result of comparison
     */
    @Override
    public boolean equals(Object objectToCompare)
    {
        if (this == objectToCompare)
        {
            return true;
        }
        if (objectToCompare == null || getClass() != objectToCompare.getClass())
        {
            return false;
        }
        if (!super.equals(objectToCompare))
        {
            return false;
        }
        EnumSchemaTypeProperties that = (EnumSchemaTypeProperties) objectToCompare;
        return Objects.equals(validValueSetGUID, that.validValueSetGUID);
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), validValueSetGUID);
    }
}
