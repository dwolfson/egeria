/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.governanceaction.converters;

import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.governanceaction.properties.RelatedMetadataElement;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;
import org.odpi.openmetadata.frameworks.governanceaction.search.PropertyHelper;
import org.odpi.openmetadata.frameworks.openmetadata.enums.CollectionMemberStatus;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.CollectionMember;
import org.odpi.openmetadata.frameworks.openmetadata.properties.collections.CollectionMembershipProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;

import java.lang.reflect.InvocationTargetException;


/**
 * CollectionMemberConverter generates a CollectionMember bean from a RelatedMetadataElement.
 */
public class CollectionMemberConverter<B> extends OpenMetadataConverterBase<B>
{
    /**
     * Constructor
     *
     * @param propertyHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public CollectionMemberConverter(PropertyHelper propertyHelper,
                                     String         serviceName,
                                     String         serverName)
    {
        super(propertyHelper, serviceName, serverName);
    }


    /**
     * Using the supplied openMetadataElement, return a new instance of the bean. This is used for most beans that have
     * a one to one correspondence with the repository instances.
     *
     * @param beanClass name of the class to create
     * @param relatedMetadataElement the properties of an open metadata element plus details of the relationship used to navigate to it
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>               beanClass,
                        RelatedMetadataElement relatedMetadataElement,
                        String                 methodName) throws PropertyServerException
    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.getDeclaredConstructor().newInstance();

            if (returnBean instanceof CollectionMember bean)
            {
                CollectionMembershipProperties membershipProperties = new CollectionMembershipProperties();

                bean.setRelationshipHeader(super.getMetadataElementHeader(beanClass,
                                                                          relatedMetadataElement,
                                                                          relatedMetadataElement.getRelationshipGUID(),
                                                                          null,
                                                                          methodName));

                /*
                 * The initial set of values come from the relationship properties.
                 */
                if (relatedMetadataElement.getRelationshipProperties() != null)
                {
                    ElementProperties elementProperties = new ElementProperties(relatedMetadataElement.getRelationshipProperties());

                    membershipProperties.setMembershipRationale(this.removeMembershipRationale(elementProperties));
                    membershipProperties.setCreatedBy(this.removeCreatedBy(elementProperties));
                    membershipProperties.setExpression(this.removeExpression(elementProperties));
                    membershipProperties.setConfidence(this.removeConfidence(elementProperties));
                    membershipProperties.setSteward(this.removeSteward(elementProperties));
                    membershipProperties.setStewardTypeName(this.removeStewardTypeName(elementProperties));
                    membershipProperties.setStewardPropertyName(this.removeStewardPropertyName(elementProperties));
                    membershipProperties.setSource(this.removeSource(elementProperties));
                    membershipProperties.setNotes(this.removeNotes(elementProperties));
                    membershipProperties.setStatus(this.removeCollectionMemberStatus(elementProperties));
                    membershipProperties.setEffectiveFrom(relatedMetadataElement.getEffectiveFromTime());
                    membershipProperties.setEffectiveTo(relatedMetadataElement.getEffectiveToTime());

                    /*
                     * Any remaining properties are returned in the extended properties.  They are
                     * assumed to be defined in a subtype.
                     */
                    membershipProperties.setExtendedProperties(this.getRemainingExtendedProperties(elementProperties));

                    bean.setRelationshipProperties(membershipProperties);
                }

                bean.setElementHeader(super.getElementStub(beanClass, relatedMetadataElement.getElement(), methodName));
            }

            return returnBean;
        }
        catch (IllegalAccessException | InstantiationException | ClassCastException | NoSuchMethodException | InvocationTargetException error)
        {
            super.handleInvalidBeanClass(beanClass.getName(), error, methodName);
        }

        return null;
    }


    /**
     * Extract and delete the CollectionMemberStatus property from the supplied element properties.
     *
     * @param elementProperties properties from entity
     * @return KeyPattern enum
     */
     CollectionMemberStatus removeCollectionMemberStatus(ElementProperties elementProperties)
    {
        final String methodName = "removeCollectionMemberStatus";

        if (elementProperties != null)
        {
            String retrievedProperty = propertyHelper.removeEnumProperty(serviceName,
                                                                         OpenMetadataProperty.MEMBERSHIP_STATUS.name,
                                                                         elementProperties,
                                                                         methodName);

            for (CollectionMemberStatus status : CollectionMemberStatus.values())
            {
                if (status.getName().equals(retrievedProperty))
                {
                    return status;
                }
            }
        }

        return null;
    }
}
