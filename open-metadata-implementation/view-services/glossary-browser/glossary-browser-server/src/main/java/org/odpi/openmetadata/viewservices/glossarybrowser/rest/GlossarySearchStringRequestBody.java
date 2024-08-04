/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.viewservices.glossarybrowser.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.commonservices.ffdc.rest.SearchStringRequestBody;
import org.odpi.openmetadata.frameworks.openmetadata.enums.GlossaryTermStatus;

import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;


/**
 * GlossarySearchStringRequestBody is the request body structure used on GlossaryCategory/Term REST API calls that passes a regular expression
 * to use as a search string and the scope of the results can be optionally restricted by glossary.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GlossarySearchStringRequestBody extends SearchStringRequestBody
{
    private String                   glossaryGUID         = null;
    private List<GlossaryTermStatus> limitResultsByStatus = null;

    /**
     * Default constructor
     */
    public GlossarySearchStringRequestBody()
    {
        super();
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public GlossarySearchStringRequestBody(GlossarySearchStringRequestBody template)
    {
        super(template);

        if (template != null)
        {
            glossaryGUID = template.getGlossaryGUID();
            limitResultsByStatus = template.getLimitResultsByStatus();
        }
    }


    /**
     * Return the unique identifier of the glossary scope.
     *
     * @return string guid
     */
    public String getGlossaryGUID()
    {
        return glossaryGUID;
    }


    /**
     * Set up the unique identifier of the glossary scope.
     *
     * @param glossaryGUID string
     */
    public void setGlossaryGUID(String glossaryGUID)
    {
        this.glossaryGUID = glossaryGUID;
    }


    /**
     * Return the list of statuses to return (null for all).
     *
     * @return list of statuses (terms only)
     */
    public List<GlossaryTermStatus> getLimitResultsByStatus()
    {
        return limitResultsByStatus;
    }


    /**
     * Set up the list of statuses to return (null for all).
     *
     * @param limitResultsByStatus list of statuses (terms only)
     */
    public void setLimitResultsByStatus(List<GlossaryTermStatus> limitResultsByStatus)
    {
        this.limitResultsByStatus = limitResultsByStatus;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "GlossarySearchStringRequestBody{" +
                       "glossaryGUID='" + glossaryGUID + '\'' +
                       ", limitResultsByStatus=" + limitResultsByStatus +
                       ", searchString='" + getSearchString() + '\'' +
                       ", searchStringParameterName='" + getSearchStringParameterName() + '\'' +
                       ", effectiveTime=" + getEffectiveTime() +
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
        if (! super.equals(objectToCompare))
        {
            return false;
        }
        GlossarySearchStringRequestBody that = (GlossarySearchStringRequestBody) objectToCompare;
        return Objects.equals(glossaryGUID, that.glossaryGUID) &&
                       Objects.equals(limitResultsByStatus, that.limitResultsByStatus);
    }


    /**
     * Create a hash code for this element type.
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), glossaryGUID, limitResultsByStatus);
    }
}
