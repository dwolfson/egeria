/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.lineage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.properties.RelationshipProperties;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * LineageMappingProperties describe the properties for a lineage mapping relationship.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class LineageMappingProperties extends RelationshipProperties
{
    private String qualifiedName = null;
    private String label         = null;
    private String description   = null;


    /**
     * Default constructor
     */
    public LineageMappingProperties()
    {
        super();
    }


    /**
     * Copy/clone constructor.  Retrieves values from the supplied template
     *
     * @param template element to copy
     */
    public LineageMappingProperties(LineageMappingProperties template)
    {
        if (template != null)
        {
            qualifiedName = template.getQualifiedName();
            label         = template.getLabel();
            description   = template.getDescription();
        }
    }


    /**
     * Set up the fully qualified name.
     *
     * @param qualifiedName String name
     */
    public void setQualifiedName(String qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }


    /**
     * Returns the stored qualified name property for the relationship.
     * If no qualified name is available then the empty string is returned.
     *
     * @return qualifiedName
     */
    public String getQualifiedName()
    {
        return qualifiedName;
    }


    /**
     * Return the label used when displaying this relationship.
     *
     * @return string
     */
    public String getLabel()
    {
        return label;
    }


    /**
     * Set up the label used when displaying this relationship.
     *
     * @param label string
     */
    public void setLabel(String label)
    {
        this.label = label;
    }


    /**
     * Return the description of the relationship.
     *
     * @return string text
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Set up the description of the relationship.
     *
     * @param description string text
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "LineageMappingProperties{" +
                       "effectiveFrom=" + getEffectiveFrom() +
                       ", effectiveTo=" + getEffectiveTo() +
                       ", qualifiedName='" + qualifiedName + '\'' +
                       ", label='" + label + '\'' +
                       ", description='" + description + '\'' +
                       '}';
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
        LineageMappingProperties that = (LineageMappingProperties) objectToCompare;
        return Objects.equals(getQualifiedName(), that.getQualifiedName()) &&
                Objects.equals(getLabel(), that.getLabel()) &&
                       Objects.equals(getDescription(), that.getDescription());
    }


    /**
     * Return hash code based on properties.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(qualifiedName, label, description);
    }
}