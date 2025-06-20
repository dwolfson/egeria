/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.solutions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.properties.ReferenceableProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * SolutionComponentProperties represents a logical architectural component that is part of the digital landscape.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class SolutionComponentProperties extends ReferenceableProperties
{
    private String displayName                       = null;
    private String description                       = null;
    private String version                           = null;
    private String solutionComponentType             = null;
    private String plannedDeployedImplementationType = null;

    /**
     * Default constructor
     */
    public SolutionComponentProperties()
    {
        super();
        super.setTypeName(OpenMetadataType.SOLUTION_COMPONENT.typeName);
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public SolutionComponentProperties(SolutionComponentProperties template)
    {
        super(template);

        if (template != null)
        {
            this.displayName = template.getDisplayName();
            this.description = template.getDescription();
            this.version = template.getVersion();
            this.solutionComponentType = template.getSolutionComponentType();
            this.plannedDeployedImplementationType = template.getPlannedDeployedImplementationType();
        }
    }


    /**
     * Return the display name for this asset (normally a shortened form of the qualified name).
     *
     * @return string name
     */
    public String getDisplayName()
    {
        return displayName;
    }


    /**
     * Set up the display name for this asset (normally a shortened form of the qualified name).
     *
     * @param displayName string name
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }


    /**
     * Return the description for this asset.
     *
     * @return string description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Set up the description for this asset.
     *
     * @param description string
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Return the version identifier for this digital service.
     *
     * @return String
     */
    public String getVersion()
    {
        return version;
    }


    /**
     * Set up the version number for this digital service.
     *
     * @param version String
     */
    public void setVersion(String version)
    {
        this.version = version;
    }


    /**
     * Return the type of the component.
     *
     * @return string
     */
    public String getSolutionComponentType()
    {
        return solutionComponentType;
    }


    /**
     * Set up the type of the component.
     *
     * @param solutionComponentType string
     */
    public void setSolutionComponentType(String solutionComponentType)
    {
        this.solutionComponentType = solutionComponentType;
    }


    /**
     * Return which type of software component is likely to be deployed to implement this solution component.
     *
     * @return string
     */
    public String getPlannedDeployedImplementationType()
    {
        return plannedDeployedImplementationType;
    }


    /**
     * Set up which type of software component is likely to be deployed to implement this solution component.
     *
     * @param plannedDeployedImplementationType string
     */
    public void setPlannedDeployedImplementationType(String plannedDeployedImplementationType)
    {
        this.plannedDeployedImplementationType = plannedDeployedImplementationType;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "SolutionComponentProperties{" +
                "displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", solutionComponentType='" + solutionComponentType + '\'' +
                ", plannedDeployedImplementationType='" + plannedDeployedImplementationType + '\'' +
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
        if (! (objectToCompare instanceof SolutionComponentProperties that))
        {
            return false;
        }
        if (! super.equals(objectToCompare))
        {
            return false;
        }
        return Objects.equals(displayName, that.displayName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(solutionComponentType, that.solutionComponentType) &&
                Objects.equals(plannedDeployedImplementationType, that.plannedDeployedImplementationType) &&
                       Objects.equals(version, that.version);
    }


    /**
     * Return hash code based on properties.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), displayName, description, solutionComponentType,
                            plannedDeployedImplementationType, version);
    }
}
