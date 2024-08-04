/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.viewservices.glossarymanager.rest;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.enums.GlossaryTermStatus;
import org.odpi.openmetadata.frameworks.openmetadata.properties.glossaries.GlossaryTermProperties;

import java.util.Date;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;


/**
 * ControlledGlossaryTermRequestBody describes the request body used to create/update controlled glossary term properties.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ControlledGlossaryTermRequestBody
{
    private GlossaryTermProperties elementProperties = null;
    private GlossaryTermStatus     initialStatus     = null;
    private Date                   effectiveTime     = null;
    private String                 updateDescription = null;

    /**
     * Default constructor
     */
    public ControlledGlossaryTermRequestBody()
    {
        super();
    }


    /**
     * Copy/clone constructor.
     *
     * @param template object to copy
     */
    public ControlledGlossaryTermRequestBody(ControlledGlossaryTermRequestBody template)
    {
        if (template != null)
        {
            elementProperties = template.getElementProperties();
            initialStatus = template.getInitialStatus();
            effectiveTime = template.getEffectiveTime();
            updateDescription = template.getUpdateDescription();
        }
    }


    /**
     * Return the properties for the element.
     *
     * @return properties object
     */
    public GlossaryTermProperties getElementProperties()
    {
        return elementProperties;
    }


    /**
     * Set up the properties for the element.
     *
     * @param elementProperties properties object
     */
    public void setElementProperties(GlossaryTermProperties elementProperties)
    {
        this.elementProperties = elementProperties;
    }


    /**
     * Return the initial status for the controlled glossary term.  By default, it is "DRAFT".
     *
     * @return glossary term status enum
     */
    public GlossaryTermStatus getInitialStatus()
    {
        return initialStatus;
    }


    /**
     * Set up the initial status for the controlled glossary term.  By default, it is "DRAFT".
     *
     * @param initialStatus glossary term status enum
     */
    public void setInitialStatus(GlossaryTermStatus initialStatus)
    {
        this.initialStatus = initialStatus;
    }


    /**
     * Return the date/time to use for the query.
     *
     * @return date object
     */
    public Date getEffectiveTime()
    {
        return effectiveTime;
    }


    /**
     * Set up  the date/time to use for the query.
     *
     * @param effectiveTime date object
     */
    public void setEffectiveTime(Date effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }



    /**
     * Return the string that describes details of the update.
     *
     * @return description
     */
    public String getUpdateDescription()
    {
        return updateDescription;
    }


    /**
     * Set up the string that describes details of the update.
     *
     * @param updateDescription description
     */
    public void setUpdateDescription(String updateDescription)
    {
        this.updateDescription = updateDescription;
    }


    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "ControlledGlossaryTermRequestBody{" +
                       "elementProperties=" + elementProperties +
                       ", initialStatus=" + initialStatus +
                       ", effectiveTime=" + effectiveTime +
                       ", updateDescription='" + updateDescription + '\'' +
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
        if (this == objectToCompare)
        {
            return true;
        }
        if (objectToCompare == null || getClass() != objectToCompare.getClass())
        {
            return false;
        }
        ControlledGlossaryTermRequestBody that = (ControlledGlossaryTermRequestBody) objectToCompare;
        return Objects.equals(getElementProperties(), that.getElementProperties()) &&
                       Objects.equals(effectiveTime, that.effectiveTime) &&
                       Objects.equals(updateDescription, that.updateDescription);
    }



    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(elementProperties, initialStatus, effectiveTime, updateDescription);
    }
}
