/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.frameworks.governanceaction.properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ElementHeader;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * GovernanceActionProcessElement contains the properties and header for a governance action process entity retrieved from the metadata repository.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GovernanceActionProcessElement
{
    private ElementHeader                          elementHeader     = null;
    private GovernanceActionProcessProperties      processProperties = null;
    private Map<String, List<Map<String, String>>> specification     = null;


    /**
     * Default constructor
     */
    public GovernanceActionProcessElement()
    {
        super();
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public GovernanceActionProcessElement(GovernanceActionProcessElement template)
    {
        if (template != null)
        {
            elementHeader     = template.getElementHeader();
            processProperties = template.getProcessProperties();
            specification     = template.getSpecification();
        }
    }


    /**
     * Return the element header associated with the properties.
     *
     * @return element header object
     */
    public ElementHeader getElementHeader()
    {
        return elementHeader;
    }


    /**
     * Set up the element header associated with the properties.
     *
     * @param elementHeader element header object
     */
    public void setElementHeader(ElementHeader elementHeader)
    {
        this.elementHeader = elementHeader;
    }

    /**
     * Return details of the process
     *
     * @return process properties
     */
    public GovernanceActionProcessProperties getProcessProperties()
    {
        return processProperties;
    }


    /**
     * Set up process properties
     *
     * @param processProperties process properties
     */
    public void setProcessProperties(GovernanceActionProcessProperties processProperties)
    {
        this.processProperties = processProperties;
    }

    /**
     * Return the specification for the governance action.
     *
     * @return specification map
     */
    public Map<String, List<Map<String, String>>> getSpecification()
    {
        return specification;
    }


    /**
     * Set up the specification for the governance action.
     *
     * @param specification specification map
     */
    public void setSpecification(Map<String, List<Map<String, String>>> specification)
    {
        this.specification = specification;
    }


    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "GovernanceActionProcessElement{" +
                       "elementHeader=" + elementHeader +
                       ", processProperties=" + processProperties +
                       ", specification=" + specification +
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
        if (!super.equals(objectToCompare))
        {
            return false;
        }
        GovernanceActionProcessElement that = (GovernanceActionProcessElement) objectToCompare;
        return Objects.equals(elementHeader, that.elementHeader) &&
                Objects.equals(processProperties, that.processProperties) &&
                Objects.equals(specification, that.specification);
    }


    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), elementHeader, processProperties, specification);
    }
}
