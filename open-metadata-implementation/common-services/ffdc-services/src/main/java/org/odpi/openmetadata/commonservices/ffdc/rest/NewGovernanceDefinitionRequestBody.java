/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.ffdc.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.enums.GovernanceDefinitionStatus;
import org.odpi.openmetadata.frameworks.openmetadata.properties.governance.GovernanceDefinitionProperties;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * NewGovernanceDefinitionRequestBody provides a structure used when creating governance definitions.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class NewGovernanceDefinitionRequestBody extends NewElementOptionsRequestBody
{
    private GovernanceDefinitionProperties properties    = null;
    private GovernanceDefinitionStatus     initialStatus = null;


    /**
     * Default constructor
     */
    public NewGovernanceDefinitionRequestBody()
    {
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public NewGovernanceDefinitionRequestBody(NewGovernanceDefinitionRequestBody template)
    {
        super(template);

        if (template != null)
        {
            this.properties = template.getProperties();
            this.initialStatus = template.getInitialStatus();
        }
    }


    /**
     * Return the properties of the governance definition.
     *
     * @return properties
     */
    public GovernanceDefinitionProperties getProperties()
    {
        return properties;
    }


    /**
     * Set up the properties of the governance definition.
     *
     * @param properties properties
     */
    public void setProperties(GovernanceDefinitionProperties properties)
    {
        this.properties = properties;
    }


    /**
     * Return the initial status of the governance definition.
     *
     * @return instance status
     */
    public GovernanceDefinitionStatus getInitialStatus()
    {
        return initialStatus;
    }


    /**
     * Set up the initial status of the governance definition.
     *
     * @param initialStatus instance status
     */
    public void setInitialStatus(GovernanceDefinitionStatus initialStatus)
    {
        this.initialStatus = initialStatus;
    }


    /**
     * JSON-style toString.
     *
     * @return list of properties and their values.
     */
    @Override
    public String toString()
    {
        return "NewGovernanceDefinitionRequestBody{" +
                "properties=" + properties +
                ", initialStatus=" + initialStatus +
                "} " + super.toString();
    }


    /**
     * Equals method that returns true if containing properties are the same.
     *
     * @param objectToCompare object to compare
     * @return boolean result of comparison
     */
    @Override
    public boolean equals(Object objectToCompare)
    {
        if (this == objectToCompare)
        {
            return true;
        }
        if (! (objectToCompare instanceof NewGovernanceDefinitionRequestBody that))
        {
            return false;
        }
        if (! super.equals(objectToCompare))
        {
            return false;
        }
        return Objects.equals(properties, that.properties) && initialStatus == that.initialStatus;
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), properties, initialStatus);
    }
}
