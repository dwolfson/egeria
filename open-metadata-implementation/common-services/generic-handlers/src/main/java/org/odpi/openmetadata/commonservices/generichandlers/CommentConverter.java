/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.generichandlers;


import org.odpi.openmetadata.frameworks.openmetadata.enums.CommentType;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.CommentElement;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.FeedbackTargetElement;
import org.odpi.openmetadata.frameworks.openmetadata.properties.feedback.CommentProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EnumPropertyValue;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstancePropertyValue;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefCategory;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * CommentConverter provides common methods for transferring relevant properties from an Open Metadata Repository Services (OMRS)
 * EntityDetail object into an CommentElement bean.
 */
public class CommentConverter<B> extends OMFConverter<B>
{
    /**
     * Constructor
     *
     * @param repositoryHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public CommentConverter(OMRSRepositoryHelper repositoryHelper,
                            String               serviceName,
                            String               serverName)
    {
        super(repositoryHelper, serviceName, serverName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean. This is used for beans that
     * contain a combination of the properties from an entity and that of a connected relationship.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param relationship relationship containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>     beanClass,
                        EntityDetail entity,
                        Relationship relationship,
                        String       methodName) throws PropertyServerException
    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.getDeclaredConstructor().newInstance();

            if (returnBean instanceof CommentElement bean)
            {
                CommentProperties properties = new CommentProperties();

                bean.setElementHeader(super.getMetadataElementHeader(beanClass, entity, methodName));

                InstanceProperties instanceProperties;

                /*
                 * The initial set of values come from the entity.
                 */
                if (entity != null)
                {
                    instanceProperties = new InstanceProperties(entity.getProperties());

                    properties.setQualifiedName(this.removeQualifiedName(instanceProperties));
                    properties.setAdditionalProperties(this.removeAdditionalProperties(instanceProperties));
                    properties.setCommentText(this.removeCommentText(instanceProperties));
                    properties.setCommentType(this.removeCommentTypeFromProperties(instanceProperties));

                    /*
                     * Any remaining properties are returned in the extended properties.  They are
                     * assumed to be defined in a subtype.
                     */
                    properties.setTypeName(bean.getElementHeader().getType().getTypeName());
                    properties.setExtendedProperties(this.getRemainingExtendedProperties(instanceProperties));
                }
                else
                {
                    handleMissingMetadataInstance(beanClass.getName(), TypeDefCategory.ENTITY_DEF, methodName);
                }

                bean.setProperties(properties);

                if (relationship != null)
                {
                    FeedbackTargetElement feedbackTargetElement = new FeedbackTargetElement();

                    instanceProperties = new InstanceProperties(relationship.getProperties());

                    feedbackTargetElement.setRelationshipHeader(super.getMetadataElementHeader(beanClass, relationship, null, methodName));
                    feedbackTargetElement.setRelatedElement(super.getElementStub(beanClass, relationship.getEntityOneProxy(), methodName));
                    feedbackTargetElement.setIsPublic(this.getIsPublic(instanceProperties));

                    bean.setFeedbackTargetElement(feedbackTargetElement);
                }

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
     * Using the supplied instances, return a new instance of the bean. This is used for beans that
     * contain a combination of the properties from an entity and that of a connected relationship.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>     beanClass,
                        EntityDetail entity,
                        String       methodName) throws PropertyServerException
    {
        return getNewBean(beanClass, entity, null, methodName);
    }


    /**
     * Retrieve and delete the CommentType enum property from the instance properties of an entity
     *
     * @param properties  entity properties
     * @return CommentType  enum value
     */
    private CommentType removeCommentTypeFromProperties(InstanceProperties   properties)
    {
        CommentType commentType = this.getCommentTypeFromProperties(properties);

        if (properties != null)
        {
            Map<String, InstancePropertyValue> instancePropertiesMap = properties.getInstanceProperties();

            if (instancePropertiesMap != null)
            {
                instancePropertiesMap.remove(OpenMetadataProperty.COMMENT_TYPE.name);
            }

            properties.setInstanceProperties(instancePropertiesMap);
        }

        return commentType;
    }


    /**
     * Retrieve the CommentType enum property from the instance properties of an entity
     *
     * @param properties  entity properties
     * @return CommentType  enum value
     */
    private CommentType getCommentTypeFromProperties(InstanceProperties   properties)
    {
        CommentType commentType = CommentType.STANDARD_COMMENT;

        if (properties != null)
        {
            Map<String, InstancePropertyValue> instancePropertiesMap = properties.getInstanceProperties();

            if (instancePropertiesMap != null)
            {
                InstancePropertyValue instancePropertyValue = instancePropertiesMap.get(OpenMetadataProperty.COMMENT_TYPE.name);

                if (instancePropertyValue instanceof EnumPropertyValue enumPropertyValue)
                {
                    commentType = switch (enumPropertyValue.getOrdinal())
                    {
                        case 0 -> CommentType.STANDARD_COMMENT;
                        case 1 -> CommentType.QUESTION;
                        case 2 -> CommentType.ANSWER;
                        case 3 -> CommentType.SUGGESTION;
                        case 4 -> CommentType.USAGE_EXPERIENCE;
                        case 99 -> CommentType.OTHER;
                        default -> commentType;
                    };
                }
            }
        }

        return commentType;
    }
}
