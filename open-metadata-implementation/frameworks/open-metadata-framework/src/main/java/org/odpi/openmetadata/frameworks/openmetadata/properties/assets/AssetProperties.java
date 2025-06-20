/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.assets;

import com.fasterxml.jackson.annotation.*;
import org.odpi.openmetadata.frameworks.openmetadata.properties.SupplementaryProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.assets.apis.APIProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.assets.infrastructure.ITInfrastructureProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.assets.processes.ProcessProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * AssetProperties describes an asset.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = APIProperties.class, name = "APIProperties"),
        @JsonSubTypes.Type(value = DataAssetProperties.class, name = "DataAssetProperties"),
        @JsonSubTypes.Type(value = ProcessProperties.class, name = "ProcessProperties"),
        @JsonSubTypes.Type(value = ITInfrastructureProperties.class, name = "ITInfrastructureProperties"),
})
public class AssetProperties extends SupplementaryProperties
{
    private String name                       = null;
    private String resourceName               = null;
    private String versionIdentifier          = null;
    private String resourceDescription        = null;
    private String deployedImplementationType = null;


    /**
     * Default constructor
     */
    public AssetProperties()
    {
        super();
        super.setTypeName(OpenMetadataType.ASSET.typeName);
    }


    /**
     * Copy/clone constructor.  Note, this is a deep copy
     *
     * @param template object to copy
     */
    public AssetProperties(AssetProperties template)
    {
        super(template);

        if (template != null)
        {
            name                       = template.getName();
            resourceName               = template.getResourceName();
            versionIdentifier          = template.getVersionIdentifier();
            resourceDescription        = template.getResourceDescription();
            deployedImplementationType = template.getDeployedImplementationType();
        }
    }


    /**
     * Convert this object into an AssetProperties object.  This involves packing the properties introduced at this level
     * into the extended properties.
     *
     * @param subTypeName subtype name
     * @return asset properties
     */
    public AssetProperties cloneToAsset(String subTypeName)
    {
        AssetProperties clone = new AssetProperties(this);

        if (clone.getTypeName() == null)
        {
            clone.setTypeName(subTypeName);
        }

        return clone;
    }


    /**
     * Return the short name of the resource that is used when displaying the resource in tables etc.
     *
     * @return string
     */
    public String getName()
    {
        return name;
    }


    /**
     * Set up the short name of the resource that is used in tables etc.
     *
     * @param name string resource name
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Return the full name of the resource as it is known in its owning technology.
     *
     * @return string
     */
    public String getResourceName()
    {
        return resourceName;
    }

    /**
     * Set up the full name of the resource as it is known in its owning technology.
     *
     * @param resourceName string
     */
    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }


    /**
     * Set up the version identifier of the resource.
     *
     * @return string version name
     */
    public String getVersionIdentifier()
    {
        return versionIdentifier;
    }


    /**
     * Set up the version identifier of the resource.
     *
     * @param versionIdentifier string version name
     */
    public void setVersionIdentifier(String versionIdentifier)
    {
        this.versionIdentifier = versionIdentifier;
    }


    /**
     * Returns the stored description property extracted from the resource.
     * If no description is provided then null is returned.
     *
     * @return description String text
     */
    public String getResourceDescription()
    {
        return resourceDescription;
    }


    /**
     * Set up the stored description property extracted from the resource.
     *
     * @param resourceDescription String text
     */
    public void setResourceDescription(String resourceDescription)
    {
        this.resourceDescription = resourceDescription;
    }


    /**
     * This override defaults the display description to the resource description if it is null;
     *
     * @return description
     */
    public String getDisplayDescription()
    {
        return displayDescription;
    }


    /**
     * Retrieve the name of the technology used for this data asset.
     *
     * @return string name
     */
    public String getDeployedImplementationType()
    {
        return deployedImplementationType;
    }


    /**
     * Set up the name of the technology used for this data asset.
     *
     * @param deployedImplementationType string name
     */
    public void setDeployedImplementationType(String deployedImplementationType)
    {
        this.deployedImplementationType = deployedImplementationType;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "AssetProperties{" +
                "name='" + name + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", versionIdentifier='" + versionIdentifier + '\'' +
                ", description='" + resourceDescription + '\'' +
                ", deployedImplementationType='" + deployedImplementationType + '\'' +
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
        if (this == objectToCompare) return true;
        if (objectToCompare == null || getClass() != objectToCompare.getClass()) return false;
        if (!super.equals(objectToCompare)) return false;
        AssetProperties that = (AssetProperties) objectToCompare;
        return Objects.equals(name, that.name) &&
                Objects.equals(versionIdentifier, that.versionIdentifier) &&
                Objects.equals(resourceName, that.resourceName) &&
                Objects.equals(resourceDescription, that.deployedImplementationType) &&
                Objects.equals(resourceDescription, that.resourceDescription);
    }

    /**
     * Return hash code based on properties.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), name, resourceName, versionIdentifier, resourceDescription, deployedImplementationType);
    }
}