/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.schema;

import com.fasterxml.jackson.annotation.*;
import org.odpi.openmetadata.frameworks.openmetadata.properties.ReferenceableProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * SchemaElementProperties carries the common parameters for creating or updating schema elements such as schema types and attributes.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SchemaTypeProperties.class, name = "SchemaTypeProperties"),
        @JsonSubTypes.Type(value = SchemaAttributeProperties.class, name = "SchemaAttributeProperties")
              })
public abstract class SchemaElementProperties extends ReferenceableProperties
{
    private String  displayName  = null;
    private String  description  = null;
    private boolean isDeprecated = false;


    /**
     * Default constructor
     */
    public SchemaElementProperties()
    {
        super();
        super.setTypeName(OpenMetadataType.SCHEMA_ELEMENT.typeName);
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public SchemaElementProperties(SchemaElementProperties template)
    {
        super(template);

        if (template != null)
        {
            displayName  = template.getDisplayName();
            description  = template.getDescription();
            isDeprecated = template.getIsDeprecated();
        }
    }


    /**
     * Return the display name of the schema element
     *
     * @return string name
     */
    public String getDisplayName()
    {
        return displayName;
    }


    /**
     * Set up the display name of the schema element.
     *
     * @param displayName string name
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }


    /**
     * Return the description of the schema element.
     *
     * @return string description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Set up the description of the schema element.
     *
     * @param description string description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Is the schema element deprecated?
     *
     * @return boolean flag
     */
    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }


    /**
     * Set whether the schema element deprecated or not.  Default is false.
     *
     * @param deprecated boolean flag
     */
    public void setIsDeprecated(boolean deprecated)
    {
        isDeprecated = deprecated;
    }


    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "SchemaElementProperties{" +
                "displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", isDeprecated=" + isDeprecated +
                "} " + super.toString();
    }


    /**
     * Return comparison result based on the content of the properties.
     *
     * @param objectToCompare test object
     * @return result of comparison
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
        if (!super.equals(objectToCompare)) return false;
        SchemaElementProperties that = (SchemaElementProperties) objectToCompare;
        return Objects.equals(getDisplayName(), that.getDisplayName()) &&
                Objects.equals(getDescription(), that.getDescription());
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(getDisplayName(), getDescription());
    }
}
