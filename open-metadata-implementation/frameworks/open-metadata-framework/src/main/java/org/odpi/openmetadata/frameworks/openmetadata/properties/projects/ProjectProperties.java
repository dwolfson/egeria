/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.projects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.properties.ReferenceableProperties;

import java.util.Date;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * ProjectProperties describes a project.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProjectProperties extends ReferenceableProperties
{
    private String identifier     = null;
    private String name           = null;
    private String description    = null;
    private String projectPhase   = null;
    private String projectHealth  = null;
    private String projectStatus  = null;
    private int    priority       = 0;
    private Date   startDate      = null;
    private Date   plannedEndDate = null;


    /**
     * Default constructor
     */
    public ProjectProperties()
    {
        super();
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public ProjectProperties(ProjectProperties template)
    {
        super(template);

        if (template != null)
        {
            this.identifier = template.getIdentifier();
            this.name = template.getName();
            this.description   = template.getDescription();
            this.projectPhase = template.getProjectPhase();
            this.projectHealth = template.getProjectHealth();
            this.projectStatus = template.getProjectStatus();
            this.priority = template.getPriority();
            this.startDate     = template.getStartDate();
            this.plannedEndDate = template.getPlannedEndDate();
        }
    }


    /**
     * Return the code value or symbol used to identify the project - typically unique.
     *
     * @return string identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }


    /**
     * Set up the code value or symbol used to identify the project - typically unique.
     *
     * @param identifier string identifier
     */
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }



    /**
     * Return the name of the project.
     *
     * @return string name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Set up the name of the project.
     *
     * @param name string name
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Return the description of the project.
     *
     * @return text
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Set up the description of the project.
     *
     * @param description text
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Return the current phase in the project lifecycle.
     *
     * @return string
     */
    public String getProjectPhase()
    {
        return projectPhase;
    }


    /**
     * Set up the current phase in the project lifecycle.
     *
     * @param projectPhase string
     */
    public void setProjectPhase(String projectPhase)
    {
        this.projectPhase = projectPhase;
    }


    /**
     * Return the current health of the project.
     *
     * @return string
     */
    public String getProjectHealth()
    {
        return projectHealth;
    }


    /**
     * Set up the current health of the project.
     *
     * @param projectHealth string
     */
    public void setProjectHealth(String projectHealth)
    {
        this.projectHealth = projectHealth;
    }


    /**
     * Return the status for this project.
     *
     * @return string
     */
    public String getProjectStatus()
    {
        return projectStatus;
    }


    /**
     * Set up the status for this project.
     *
     * @param projectStatus string
     */
    public void setProjectStatus(String projectStatus)
    {
        this.projectStatus = projectStatus;
    }


    /**
     * Return the priority of this project.
     *
     * @return int
     */
    public int getPriority()
    {
        return priority;
    }


    /**
     * Set up the priority of this project.
     *
     * @param priority int
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Return the date that the project was created.
     *
     * @return date
     */
    public Date getStartDate()
    {
        if (startDate == null)
        {
            return null;
        }
        else
        {
            return new Date(startDate.getTime());
        }
    }


    /**
     * Set up the date that the project was created.
     *
     * @param startDate date
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }


    /**
     * Return the date that the project is expected to complete.
     *
     * @return date
     */
    public Date getPlannedEndDate()
    {
        if (plannedEndDate == null)
        {
            return null;
        }
        else
        {
            return new Date(plannedEndDate.getTime());
        }
    }


    /**
     * Set up the date that the project is expected to complete.
     *
     * @param plannedEndDate date
     */
    public void setPlannedEndDate(Date plannedEndDate)
    {
        this.plannedEndDate = plannedEndDate;
    }


    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "ProjectProperties{" +
                       "identifier='" + identifier + '\'' +
                       ", name='" + name + '\'' +
                       ", description='" + description + '\'' +
                       ", phase='" + projectPhase + '\'' +
                       ", health='" + projectHealth + '\'' +
                       ", status='" + projectStatus + '\'' +
                       ", startDate=" + startDate +
                       ", priority=" + priority +
                       ", plannedEndDate=" + plannedEndDate +
                       ", qualifiedName='" + getQualifiedName() + '\'' +
                       ", additionalProperties=" + getAdditionalProperties() +
                       ", effectiveFrom=" + getEffectiveFrom() +
                       ", effectiveTo=" + getEffectiveTo() +
                       ", vendorProperties=" + getVendorProperties() +
                       ", typeName='" + getTypeName() + '\'' +
                       ", extendedProperties=" + getExtendedProperties() +
                       '}';
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
        if (this == objectToCompare) return true;
        if (objectToCompare == null || getClass() != objectToCompare.getClass()) return false;
        if (!super.equals(objectToCompare)) return false;
        ProjectProperties that = (ProjectProperties) objectToCompare;
        return priority == that.priority && Objects.equals(identifier, that.identifier) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(projectPhase, that.projectPhase) && Objects.equals(projectHealth, that.projectHealth) && Objects.equals(projectStatus, that.projectStatus) && Objects.equals(startDate, that.startDate) && Objects.equals(plannedEndDate, that.plannedEndDate);
    }

    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), identifier, name, description, projectPhase, projectHealth, projectStatus, priority, startDate, plannedEndDate);
    }
}
