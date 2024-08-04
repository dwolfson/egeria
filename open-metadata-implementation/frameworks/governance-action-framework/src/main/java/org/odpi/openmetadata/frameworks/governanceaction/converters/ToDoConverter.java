/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.governanceaction.converters;

import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ToDoActionTargetElement;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ToDoElement;
import org.odpi.openmetadata.frameworks.openmetadata.properties.actions.ToDoActionTargetProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.actions.ToDoProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ElementStub;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.frameworks.governanceaction.properties.OpenMetadataElement;
import org.odpi.openmetadata.frameworks.governanceaction.properties.RelatedMetadataElement;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;
import org.odpi.openmetadata.frameworks.governanceaction.search.PropertyHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * ToDoConverter generates a ToDoElement from a "To Do" entity
 */
public class ToDoConverter<B> extends OpenMetadataConverterBase<B>
{
    /**
     * Constructor
     *
     * @param propertyHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public ToDoConverter(PropertyHelper propertyHelper,
                         String         serviceName,
                         String         serverName)
    {
        super(propertyHelper, serviceName, serverName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean.  It is used for beans such as
     * an Annotation or To Do bean which combine knowledge from the element and its linked relationships.
     *
     * @param beanClass name of the class to create
     * @param primaryElement element that is the root of the collection of entities that make up the
     *                      content of the bean
     * @param relationships relationships linking the entities
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewComplexBean(Class<B>                     beanClass,
                               OpenMetadataElement          primaryElement,
                               List<RelatedMetadataElement> relationships,
                               String                       methodName) throws PropertyServerException

    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.getDeclaredConstructor().newInstance();

            if (returnBean instanceof ToDoElement bean)
            {
                ToDoProperties toDoProperties = new ToDoProperties();

                bean.setElementHeader(super.getMetadataElementHeader(beanClass, primaryElement, methodName));

                ElementProperties elementProperties;

                /*
                 * The initial set of values come from the toDoMetadataElement.
                 */
                if (primaryElement != null)
                {
                    elementProperties = new ElementProperties(primaryElement.getElementProperties());

                    toDoProperties.setQualifiedName(this.removeQualifiedName(elementProperties));
                    toDoProperties.setAdditionalProperties(this.removeAdditionalProperties(elementProperties));
                    toDoProperties.setName(this.removeName(elementProperties));
                    toDoProperties.setToDoType(this.removeToDoType(elementProperties));
                    toDoProperties.setDescription(this.removeDescription(elementProperties));
                    toDoProperties.setCreationTime(this.removeCreationTime(elementProperties));
                    toDoProperties.setPriority(this.removeIntPriority(elementProperties));
                    toDoProperties.setToDoStatus(super.removeToDoStatus(elementProperties));
                    toDoProperties.setLastReviewTime(this.removeLastReviewTime(elementProperties));
                    toDoProperties.setDueTime(this.removeDueTime(elementProperties));
                    toDoProperties.setCompletionTime(this.removeCompletionTime(elementProperties));
                    toDoProperties.setEffectiveFrom(primaryElement.getEffectiveFromTime());
                    toDoProperties.setEffectiveTo(primaryElement.getEffectiveToTime());

                    /*
                     * Any remaining properties are returned in the extended properties.  They are
                     * assumed to be defined in a subtype.
                     */
                    toDoProperties.setTypeName(bean.getElementHeader().getType().getTypeName());
                    toDoProperties.setExtendedProperties(this.getRemainingExtendedProperties(elementProperties));

                    bean.setProperties(toDoProperties);

                    if (relationships != null)
                    {
                        List<ElementStub>         assignedActors = new ArrayList<>();
                        List<ElementStub>             sponsors      = new ArrayList<>();
                        List<ToDoActionTargetElement> actionTargets = new ArrayList<>();

                        for (RelatedMetadataElement relatedMetadataElement : relationships)
                        {
                            if (relatedMetadataElement != null)
                            {
                                if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.TO_DO_SOURCE_RELATIONSHIP_TYPE_NAME))
                                {
                                    bean.setToDoSource(super.getElementStub(beanClass, relatedMetadataElement.getElement(), methodName));
                                }
                                else if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.ACTION_ASSIGNMENT_RELATIONSHIP_TYPE_NAME))
                                {
                                    assignedActors.add(super.getElementStub(beanClass, relatedMetadataElement.getElement(), methodName));
                                }
                                else if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.ACTION_SPONSOR_RELATIONSHIP_TYPE_NAME))
                                {
                                    sponsors.add(super.getElementStub(beanClass, relatedMetadataElement.getElement(), methodName));
                                }
                                else if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.ACTION_TARGET_RELATIONSHIP_TYPE_NAME))
                                {
                                    ToDoActionTargetElement actionTargetElement = new ToDoActionTargetElement();

                                    actionTargetElement.setTargetElement(super.getElementSummary(beanClass,
                                                                                                 relatedMetadataElement.getElement(),
                                                                                                 methodName));
                                    actionTargetElement.setRelationshipHeader(super.getMetadataElementHeader(beanClass,
                                                                                                             relatedMetadataElement,
                                                                                                             relatedMetadataElement.getRelationshipGUID(),
                                                                                                             null,
                                                                                                             methodName));

                                    ToDoActionTargetProperties actionTargetProperties = new ToDoActionTargetProperties();
                                    ElementProperties          relationshipProperties = new ElementProperties(relatedMetadataElement.getRelationshipProperties());

                                    actionTargetProperties.setActionTargetName(removeActionTargetName(relationshipProperties));
                                    actionTargetProperties.setStatus(removeToDoStatus(relationshipProperties));
                                    actionTargetProperties.setStartDate(removeStartDate(relationshipProperties));
                                    actionTargetProperties.setCompletionDate(removeCompletionDate(relationshipProperties));
                                    actionTargetProperties.setCompletionMessage(removeCompletionMessage(relationshipProperties));

                                    actionTargetElement.setRelationshipProperties(actionTargetProperties);

                                    actionTargets.add(actionTargetElement);
                                }
                            }
                        }

                        if (! assignedActors.isEmpty())
                        {
                            bean.setAssignedActors(assignedActors);
                        }

                        if (! sponsors.isEmpty())
                        {
                            bean.setSponsors(sponsors);
                        }

                        if (! actionTargets.isEmpty())
                        {
                            bean.setActionTargets(actionTargets);
                        }
                    }
                }
                else
                {
                    handleMissingMetadataInstance(beanClass.getName(), OpenMetadataElement.class.getName(), methodName);
                }

                bean.setProperties(toDoProperties);
            }

            return returnBean;
        }
        catch (IllegalAccessException | InstantiationException | ClassCastException | NoSuchMethodException | InvocationTargetException error)
        {
            super.handleInvalidBeanClass(beanClass.getName(), error, methodName);
        }

        return null;
    }
}
