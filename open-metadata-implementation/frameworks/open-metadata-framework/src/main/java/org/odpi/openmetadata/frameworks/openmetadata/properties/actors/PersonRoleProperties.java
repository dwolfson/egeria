/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.actors;


import com.fasterxml.jackson.annotation.*;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * PersonRoleProperties covers a role that has been defined in an organization.  One of more people
 * can be assigned to a role.  The optional headCount determines the maximum number of people that should be
 * appointed (open metadata does not enforce this level but sends a notification if the headCount
 * level is breached.) It also returns how many people are currently appointed to the role.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = GovernanceRoleProperties.class, name = "GovernanceRoleProperties"),
        })
public class PersonRoleProperties extends ActorRoleProperties
{
    private boolean headCountLimitSet = false;
    private int     headCount         = 0;


    /**
     * Default constructor
     */
    public PersonRoleProperties()
    {
        super();
        super.setTypeName(OpenMetadataType.PERSON_ROLE.typeName);
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public PersonRoleProperties(PersonRoleProperties template)
    {
        super(template);

        if (template != null)
        {
            this.headCountLimitSet = template.getHeadCountLimitSet();
            this.headCount = template.getHeadCount();
        }
    }


    /**
     * Return the indicator whether the head count limit is set for a person role.
     *
     * @return boolean flag
     */
    public boolean getHeadCountLimitSet()
    {
        return headCountLimitSet;
    }


    /**
     * Set up the indicator whether the head count limit is set for a person role.
     *
     * @param headCountLimitSet boolean flag
     */
    public void setHeadCountLimitSet(boolean headCountLimitSet)
    {
        this.headCountLimitSet = headCountLimitSet;
    }


    /**
     * Return the head count limit (or zero if not set).
     *
     * @return int
     */
    public int getHeadCount()
    {
        return headCount;
    }


    /**
     * Set up the head count limit (or zero if not set).
     *
     * @param headCount int
     */
    public void setHeadCount(int headCount)
    {
        this.headCount = headCount;
    }



    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "PersonRoleProperties{" +
                "headCountLimitSet=" + headCountLimitSet +
                ", headCount=" + headCount +
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
        if (!super.equals(objectToCompare))
        {
            return false;
        }
        PersonRoleProperties that = (PersonRoleProperties) objectToCompare;
        return getHeadCountLimitSet() == that.getHeadCountLimitSet() &&
                getHeadCount() == that.getHeadCount();
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), getHeadCountLimitSet(), getHeadCount());
    }
}
