/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.unitycatalog.sync;

import org.odpi.openmetadata.adapters.connectors.unitycatalog.controls.UnityCatalogPlaceholderProperty;
import org.odpi.openmetadata.adapters.connectors.unitycatalog.properties.SchemaInfo;
import org.odpi.openmetadata.adapters.connectors.unitycatalog.resource.OSSUnityCatalogResourceConnector;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.ElementStatus;
import org.odpi.openmetadata.frameworks.governanceaction.controls.PlaceholderProperty;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;
import org.odpi.openmetadata.frameworks.integration.iterator.IntegrationIterator;
import org.odpi.openmetadata.frameworks.integration.iterator.MemberAction;
import org.odpi.openmetadata.frameworks.integration.iterator.MemberElement;
import org.odpi.openmetadata.frameworks.integration.iterator.MetadataCollectionIterator;
import org.odpi.openmetadata.frameworks.openmetadata.enums.PermittedSynchronization;
import org.odpi.openmetadata.frameworks.openmetadata.enums.ServerAssetUseType;
import org.odpi.openmetadata.frameworks.openmetadata.refdata.DeployedImplementationType;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.integrationservices.catalog.connector.CatalogIntegratorContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the specialist methods for working with Unity Catalog (UC) Schema.
 */
public class OSSUnityCatalogInsideCatalogSyncSchema extends OSSUnityCatalogInsideCatalogSyncBase
{
    private String templateGUID = null;

    /**
     * Set up the schema synchronizer.
     *
     * @param connectorName name of this connector
     * @param context context for the connector
     * @param catalogTargetName the catalog target name
     * @param catalogName name of the catalog
     * @param ucFullNameToEgeriaGUID map of full names from UC to the GUID of the entity in Egeria.
     * @param targetPermittedSynchronization the policy that controls the direction of metadata exchange
     * @param ucConnector connector for accessing UC
     * @param ucServerEndpoint the server endpoint used to constructing the qualified names
     * @param templates templates supplied through the catalog target
     * @param configurationProperties configuration properties supplied through the catalog target
     * @param auditLog logging destination
     */
    public OSSUnityCatalogInsideCatalogSyncSchema(String                           connectorName,
                                                  CatalogIntegratorContext         context,
                                                  String                           catalogTargetName,
                                                  String                           catalogName,
                                                  Map<String, String>              ucFullNameToEgeriaGUID,
                                                  PermittedSynchronization         targetPermittedSynchronization,
                                                  OSSUnityCatalogResourceConnector ucConnector,
                                                  String                           ucServerEndpoint,
                                                  Map<String, String>              templates,
                                                  Map<String, Object>              configurationProperties,
                                                  AuditLog                         auditLog)
    {
        super(connectorName,
              context,
              catalogTargetName,
              catalogName,
              ucFullNameToEgeriaGUID,
              targetPermittedSynchronization,
              ucConnector,
              ucServerEndpoint,
              DeployedImplementationType.OSS_UC_SCHEMA,
              templates,
              configurationProperties,
              auditLog);

        if (templates != null)
        {
            this.templateGUID = templates.get(DeployedImplementationType.OSS_UC_SCHEMA.getDeployedImplementationType());
        }
    }



    /**
     * Review all the schemas stored in Egeria.
     *
     * @return MetadataCollectionIterator
     * @throws InvalidParameterException parameter error
     * @throws PropertyServerException repository error
     * @throws UserNotAuthorizedException security error
     */
    protected IntegrationIterator refreshEgeria() throws InvalidParameterException,
                                                         PropertyServerException,
                                                         UserNotAuthorizedException
    {
        final String methodName = "refreshEgeria";

        MetadataCollectionIterator iterator = new MetadataCollectionIterator(this.context.getMetadataSourceGUID(),
                                                                             this.context.getMetadataSourceQualifiedName(),
                                                                             catalogTargetName,
                                                                             connectorName,
                                                                             deployedImplementationType.getAssociatedTypeName(),
                                                                             openMetadataAccess,
                                                                             targetPermittedSynchronization,
                                                                             context.getMaxPageSize(),
                                                                             auditLog);

        while (iterator.moreToReceive())
        {
            MemberElement nextElement = iterator.getNextMember();

            if (nextElement != null)
            {
                SchemaInfo schemaInfo = null;

                String schemaName = propertyHelper.getStringProperty(catalogTargetName,
                                                                     OpenMetadataProperty.NAME.name,
                                                                     nextElement.getElement().getElementProperties(),
                                                                     methodName);

                try
                {
                    schemaInfo = ucConnector.getSchema(schemaName);
                }
                catch (Exception missing)
                {
                    // this is not necessarily an error
                }

                MemberAction memberAction;
                if (schemaInfo == null)
                {
                    memberAction = nextElement.getMemberAction(null, null);
                }
                else
                {
                    memberAction = nextElement.getMemberAction(this.getDateFromLong(schemaInfo.getCreated_at()), this.getDateFromLong(schemaInfo.getUpdated_at()));
                }

                this.takeAction(memberAction, nextElement, schemaInfo);
            }
        }

        return iterator;
    }


    /**
     * Review all the schemas stored in UC.
     *
     * @param iterator  Metadata collection iterator
     *
     * @throws InvalidParameterException parameter error
     * @throws PropertyServerException repository error
     * @throws UserNotAuthorizedException security error
     */
    protected void refreshUnityCatalog(IntegrationIterator iterator) throws InvalidParameterException,
                                                                            PropertyServerException,
                                                                            UserNotAuthorizedException
    {
        List<SchemaInfo> ucSchemaList = ucConnector.listSchemas(catalogName);

        if (ucSchemaList != null)
        {
            for (SchemaInfo schemaInfo : ucSchemaList)
            {
                if (schemaInfo != null)
                {
                    if (ucFullNameToEgeriaGUID.get(schemaInfo.getFull_name()) == null)
                    {
                        String ucSchemaQualifiedName = this.getQualifiedName(schemaInfo.getFull_name());

                        MemberElement memberElement = iterator.getMemberByQualifiedName(ucSchemaQualifiedName);
                        MemberAction memberAction   = memberElement.getMemberAction(this.getDateFromLong(schemaInfo.getCreated_at()),
                                                                                    this.getDateFromLong(schemaInfo.getUpdated_at()));
                        this.takeAction(memberAction, memberElement, schemaInfo);
                    }
                }
            }
        }
    }


    /**
     * Use the member action enum to request that the correct action is taken.
     *
     * @param memberAction enum
     * @param memberElement element from egeria
     * @param schemaInfo element from UC
     */
    private void takeAction(MemberAction  memberAction,
                            MemberElement memberElement,
                            SchemaInfo    schemaInfo) throws InvalidParameterException,
                                                             PropertyServerException,
                                                             UserNotAuthorizedException
    {
        switch (memberAction)
        {
            case CREATE_INSTANCE_IN_OPEN_METADATA -> this.createElementInEgeria(schemaInfo);
            case UPDATE_INSTANCE_IN_OPEN_METADATA -> this.updateElementInEgeria(schemaInfo, memberElement);
            case DELETE_INSTANCE_IN_OPEN_METADATA -> this.deleteElementInEgeria(memberElement);
            case CREATE_INSTANCE_IN_THIRD_PARTY   -> this.createElementInThirdParty(memberElement);
            case UPDATE_INSTANCE_IN_THIRD_PARTY   -> this.updateElementInThirdParty(schemaInfo, memberElement);
            case DELETE_INSTANCE_IN_THIRD_PARTY   -> this.deleteElementInThirdParty(schemaInfo);
        }
    }


    /**
     * Create an element in open metadata.
     *
     * @param schemaInfo object from UC
     * @throws InvalidParameterException parameter error
     * @throws PropertyServerException repository error
     * @throws UserNotAuthorizedException authorization error
     */
    private void createElementInEgeria(SchemaInfo schemaInfo) throws InvalidParameterException,
                                                                     PropertyServerException,
                                                                     UserNotAuthorizedException
    {
        final String parentLinkTypeName = OpenMetadataType.SERVER_ASSET_USE_RELATIONSHIP.typeName;
        final boolean parentAtEnd1 = true;

        String ucSchemaGUID;

        if (templateGUID != null)
        {
            ucSchemaGUID = openMetadataAccess.createMetadataElementFromTemplate(deployedImplementationType.getAssociatedTypeName(),
                                                                                context.getAssetManagerGUID(),
                                                                                false,
                                                                                null,
                                                                                null,
                                                                                templateGUID,
                                                                                this.getElementProperties(super.getQualifiedName(schemaInfo.getFull_name()), schemaInfo),
                                                                                this.getPlaceholderProperties(schemaInfo),
                                                                                context.getAssetManagerGUID(),
                                                                                parentLinkTypeName,
                                                                                propertyHelper.addEnumProperty(null,
                                                                                                               OpenMetadataProperty.USE_TYPE.name,
                                                                                                               ServerAssetUseType.getOpenTypeName(),
                                                                                                               ServerAssetUseType.OWNS.getName()),
                                                                                parentAtEnd1);
        }
        else
        {
            ucSchemaGUID = openMetadataAccess.createMetadataElementInStore(deployedImplementationType.getAssociatedTypeName(),
                                                                           ElementStatus.ACTIVE,
                                                                           null,
                                                                           context.getAssetManagerGUID(),
                                                                           false,
                                                                           null,
                                                                           null,
                                                                           this.getElementProperties(schemaInfo),
                                                                           context.getAssetManagerGUID(),
                                                                           parentLinkTypeName,
                                                                           propertyHelper.addEnumProperty(null,
                                                                                                          OpenMetadataProperty.USE_TYPE.name,
                                                                                                          ServerAssetUseType.getOpenTypeName(),
                                                                                                          ServerAssetUseType.OWNS.getName()),
                                                                           parentAtEnd1);
        }

        context.addExternalIdentifier(ucSchemaGUID,
                                      deployedImplementationType.getAssociatedTypeName(),
                                      this.getExternalIdentifierProperties(schemaInfo, schemaInfo.getName()));

        ucFullNameToEgeriaGUID.put(schemaInfo.getFull_name(), ucSchemaGUID);
    }




    /**
     * Update an element in open metadata.
     *
     * @param info object from UC
     * @param memberElement existing element in egeria
     *
     * @throws InvalidParameterException parameter error
     * @throws PropertyServerException repository error
     * @throws UserNotAuthorizedException authorization error
     */
    protected void updateElementInEgeria(SchemaInfo    info,
                                         MemberElement memberElement) throws InvalidParameterException,
                                                                             PropertyServerException,
                                                                             UserNotAuthorizedException
    {
        String egeriaSchemaGUID = memberElement.getElement().getElementGUID();

        openMetadataAccess.updateMetadataElementInStore(egeriaSchemaGUID,
                                                        false, getElementProperties(info));

        context.updateExternalIdentifier(egeriaSchemaGUID,
                                         deployedImplementationType.getAssociatedTypeName(),
                                         this.getExternalIdentifierProperties(info, info.getName()));

        context.confirmSynchronization(egeriaSchemaGUID,
                                       deployedImplementationType.getAssociatedTypeName(),
                                       info.getName());
    }


    /**
     * Create a schema in UC.
     *
     * @param memberElement elements from Egeria
     * @throws PropertyServerException problem communicating with UC
     */
    private void createElementInThirdParty(MemberElement memberElement) throws PropertyServerException
    {
        ucConnector.createSchema(super.getUCNameFromMember(memberElement),
                                 catalogName,
                                 super.getUCCommentFomMember(memberElement),
                                 super.getUCPropertiesFomMember(memberElement));
    }


    /**
     * Update the schema in UC.
     *
     * @param schemaInfo existing schema in UC
     * @param memberElement elements from Egeria
     *
     * @throws PropertyServerException  problem communicating with UC
     */
    private void updateElementInThirdParty(SchemaInfo    schemaInfo,
                                           MemberElement memberElement) throws PropertyServerException
    {
        SchemaInfo newInfo = ucConnector.updateSchema(schemaInfo.getFull_name(),
                                                      this.getUCNameFromMember(memberElement),
                                                      this.getUCCommentFomMember(memberElement),
                                                      this.getUCPropertiesFomMember(memberElement));
    }


    /**
     * Delete the UC element.
     *
     * @param info info object describing the element to delete.
     *
     * @throws PropertyServerException problem connecting to UC
     */
    private void deleteElementInThirdParty(SchemaInfo    info) throws PropertyServerException
    {
        ucConnector.deleteSchema(info.getFull_name(), false);
    }


    /**
     * Return the template's placeholder properties populated with the info object's values.
     *
     * @param info object from UC
     * @return map of placeholder values
     */
    private Map<String, String> getPlaceholderProperties(SchemaInfo info)
    {
        Map<String, String> placeholderProperties = new HashMap<>();

        placeholderProperties.put(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getName(), ucServerEndpoint);
        placeholderProperties.put(UnityCatalogPlaceholderProperty.CATALOG_NAME.getName(), catalogName);
        placeholderProperties.put(UnityCatalogPlaceholderProperty.SCHEMA_NAME.getName(), info.getName());
        placeholderProperties.put(PlaceholderProperty.VERSION_IDENTIFIER.name, null);
        placeholderProperties.put(PlaceholderProperty.DESCRIPTION.name, info.getComment());

        return placeholderProperties;
    }


    /**
     * Set up the element properties for an asset from the info object.
     *
     * @param info  information extracted from UC
     *
     * @return element properties suitable for create or update
     */
    private ElementProperties getElementProperties(SchemaInfo info)
    {
        ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                               OpenMetadataProperty.NAME.name,
                                                                               info.getFull_name());

        elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                             OpenMetadataProperty.DESCRIPTION.name,
                                                             info.getComment());

        elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                             OpenMetadataProperty.DEPLOYED_IMPLEMENTATION_TYPE.name,
                                                             DeployedImplementationType.OSS_UC_SCHEMA.getDeployedImplementationType());

        elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                info.getProperties());

        return elementProperties;
    }



    /**
     * Set up the element properties for an asset from the info object and qualified name.
     *
     * @param qualifiedName calculated qualified name
     * @param info  information extracted from UC
     *
     * @return element properties suitable for create or update
     */
    private ElementProperties getElementProperties(String     qualifiedName,
                                                   SchemaInfo info)
    {
        ElementProperties elementProperties = this.getElementProperties(info);

        elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                             OpenMetadataProperty.QUALIFIED_NAME.name,
                                                             qualifiedName);

        return elementProperties;
    }

}
