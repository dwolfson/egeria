/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.converters;

import org.odpi.openmetadata.frameworks.openmetadata.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.CollectionElement;
import org.odpi.openmetadata.frameworks.openmetadata.properties.OpenMetadataElement;
import org.odpi.openmetadata.frameworks.openmetadata.search.PropertyHelper;

import java.lang.reflect.InvocationTargetException;


/**
 * CollectionConverter generates a CollectionElement from a Collection entity
 */
public class CollectionConverter<B> extends AttributedElementConverterBase<B>
{
    /**
     * Constructor
     *
     * @param propertyHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public CollectionConverter(PropertyHelper propertyHelper,
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
     * @param openMetadataElement openMetadataElement containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>            beanClass,
                        OpenMetadataElement openMetadataElement,
                        String              methodName) throws PropertyServerException
    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.getDeclaredConstructor().newInstance();

            if (returnBean instanceof CollectionElement bean)
            {
                if (openMetadataElement != null)
                {
                    bean.setElementHeader(super.getMetadataElementHeader(beanClass, openMetadataElement, methodName));
                    bean.setProperties(super.getCollectionProperties(openMetadataElement));
                }
                else
                {
                    handleMissingMetadataInstance(beanClass.getName(), OpenMetadataElement.class.getName(), methodName);
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
}
