/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworkservices.gaf.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.governanceaction.properties.ExternalIdentifierProperties;
import org.odpi.openmetadata.frameworks.governanceaction.properties.MetadataCorrelationProperties;

import java.util.Date;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * EffectiveTimeQueryRequestBody carries the date/time for a query.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EffectiveTimeQueryRequestBody extends OMAGGAFAPIResponse
{
    private String externalScopeGUID = null;
    private String externalScopeName = null;
    private Date   effectiveTime = null;


    /**
     * Default constructor
     */
    public EffectiveTimeQueryRequestBody()
    {
        super();
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public EffectiveTimeQueryRequestBody(EffectiveTimeQueryRequestBody template)
    {
        super(template);

        if (template != null)
        {
            externalScopeGUID = template.getExternalScopeGUID();
            externalScopeName = template.getExternalScopeName();
            effectiveTime = template.getEffectiveTime();
        }
    }


    /**
     * Return the unique identifier of the software server capability that represents the asset manager.
     *
     * @return string guid
     */
    public String getExternalScopeGUID()
    {
        return externalScopeGUID;
    }


    /**
     * Set up the unique identifier of the software server capability that represents the asset manager.
     *
     * @param externalScopeGUID string guid
     */
    public void setExternalScopeGUID(String externalScopeGUID)
    {
        this.externalScopeGUID = externalScopeGUID;
    }


    /**
     * Return the qualified name of the software server capability that represents the asset manager.
     *
     * @return string name
     */
    public String getExternalScopeName()
    {
        return externalScopeName;
    }


    /**
     * Set up the qualified name of the software server capability that represents the asset manager.
     *
     * @param externalScopeName string name
     */
    public void setExternalScopeName(String externalScopeName)
    {
        this.externalScopeName = externalScopeName;
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
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "EffectiveTimeQueryRequestBody{" +
                "externalScopeGUID='" + externalScopeGUID + '\'' +
                ", externalScopeName='" + externalScopeName + '\'' +
                ", effectiveTime=" + effectiveTime +
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
        if (! super.equals(objectToCompare))
        {
            return false;
        }
        EffectiveTimeQueryRequestBody that = (EffectiveTimeQueryRequestBody) objectToCompare;
        return Objects.equals(effectiveTime, that.effectiveTime) &&
                Objects.equals(externalScopeGUID, that.externalScopeGUID) &&
                Objects.equals(externalScopeName, that.externalScopeName);
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), externalScopeGUID, externalScopeName, effectiveTime);
    }
}
