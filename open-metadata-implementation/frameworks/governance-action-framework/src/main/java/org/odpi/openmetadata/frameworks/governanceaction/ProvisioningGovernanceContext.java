/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.governanceaction;

import org.odpi.openmetadata.frameworks.openmetadata.enums.PortType;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.openmetadata.enums.ElementStatus;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;


/**
 * ProvisioningGovernanceContext provides access to the details of the provisioning request along with basic access to the
 * metadata store.
 *
 * A provisioning service is typically using the requestSourceElements to provision new resources at the actionTargetElements.
 * It may also create the lineage to cover its provisioning work.
 *
 * The methods provided to create the lineage are simple.  They include an optional templateGUID to copy the structure of a more complex element
 * if needed.  Alternatively, the provisioning service can specify a guard on completion to initiate a remediation service to fill out the
 * lineage metadata in more detail.
 */
public interface ProvisioningGovernanceContext extends GovernanceContext
{
    /**
     * Create an asset such as a data file, database, API or server.
     *
     * @param assetTypeName the type name of the asset.  This is the name of an open metadata type that inherits from "Asset".
     * @param qualifiedName the unique name of the new asset
     * @param name the technical display name of the resource
     * @param description the description of the resource
     *
     * @return unique identifier of the new asset
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create an asset
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createAsset(String assetTypeName,
                       String qualifiedName,
                       String name,
                       String description) throws InvalidParameterException,
                                                  UserNotAuthorizedException,
                                                  PropertyServerException;


    /**
     * Create an asset such as a data file, database, API or server.
     *
     * @param assetTypeName the type name of the asset.  This is the name of an open metadata type that inherits from "Asset".
     * @param qualifiedName the unique name of the new asset
     * @param name the technical display name of the resource
     * @param versionIdentifier version of the resource
     * @param description the description of the resource
     * @param extendedProperties attributes introduced by subtypes of Asset
     *
     * @return unique identifier of the new asset
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create an asset
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createAsset(String            assetTypeName,
                       String            qualifiedName,
                       String            name,
                       String            versionIdentifier,
                       String            description,
                       ElementProperties extendedProperties) throws InvalidParameterException,
                                                                    UserNotAuthorizedException,
                                                                    PropertyServerException;


    /**
     * Create an asset such as a data file, database, API or server.  This is used if the provisioning
     * governance action service has created a new asset as part of the provisioning process.
     * This interface includes an optional templateGUID to copy the structure of an existing asset of the same type.
     *
     * @param templateGUID the unique identifier of the existing asset to copy (this will copy all the attachments
     *                     such as nested content, schema, connection etc)
     * @param qualifiedName the unique name of the new asset
     * @param name the technical display name of the asset
     * @param description the description of the asset
     *
     * @return unique identifier of the new asset
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create an asset
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createAssetFromTemplate(String templateGUID,
                                   String qualifiedName,
                                   String name,
                                   String description) throws InvalidParameterException,
                                                              UserNotAuthorizedException,
                                                              PropertyServerException;


    /**
     * Create an asset such as a data file, database, API or server.  This is used if the provisioning
     * governance action service has created a new asset as part of the provisioning process.
     * This interface includes an optional templateGUID to copy the structure of an existing asset of the same type.
     *
     * @param assetTypeName      the type name of the asset.  This is the name of an open metadata type that inherits from "Asset".
     * @param templateGUID the unique identifier of the existing asset to copy (this will copy all the attachments
     *                     such as nested content, schema, connection etc)
     * @param qualifiedName the unique name of the new asset
     * @param name the technical display name of the asset
     * @param versionIdentifier version of the resource
     * @param description the description of the asset
     * @param extendedProperties attributes introduced by subtypes of Asset
     *
     * @return unique identifier of the new asset
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create an asset
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createAssetFromTemplate(String            assetTypeName,
                                   String            templateGUID,
                                   String            qualifiedName,
                                   String            name,
                                   String            versionIdentifier,
                                   String            description,
                                   ElementProperties extendedProperties) throws InvalidParameterException,
                                                                                UserNotAuthorizedException,
                                                                                PropertyServerException;


    /**
     * Create a new process to represent the processing of this governance action process.
     *
     * @param processTypeName the type name of the process.  This is the name of an open metadata type that inherits from "Process".
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param description the description of the process
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createProcess(String        processTypeName,
                         ElementStatus initialStatus,
                         String        qualifiedName,
                         String        name,
                         String        description) throws InvalidParameterException,
                                                           UserNotAuthorizedException,
                                                           PropertyServerException;


    /**
     * Create a new process to represent the processing of this governance action process.
     *
     * @param processTypeName the type name of the process.  This is the name of an open metadata type that inherits from "Process".
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param versionIdentifier version of the resource
     * @param description the description of the process
     * @param formula expression that describes the behaviour of the process
     * @param extendedProperties attributes introduced by subtypes of Process
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createProcess(String            processTypeName,
                         ElementStatus     initialStatus,
                         String            qualifiedName,
                         String            name,
                         String            versionIdentifier,
                         String            description,
                         String            formula,
                         ElementProperties extendedProperties) throws InvalidParameterException,
                                                                      UserNotAuthorizedException,
                                                                      PropertyServerException;


    /**
     * Create a new process to represent the processing of this governance action process.
     *
     * @param templateGUID the unique identifier of the existing process to copy (this will copy all the attachments such as ports, nested content,
     *                     schema, connection etc)
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param description the description of the process
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createProcessFromTemplate(String        templateGUID,
                                     ElementStatus initialStatus,
                                     String        qualifiedName,
                                     String        name,
                                     String        description) throws InvalidParameterException,
                                                                       UserNotAuthorizedException,
                                                                       PropertyServerException;


    /**
     * Create a new process to represent the processing of this governance action process.
     *
     * @param templateGUID the unique identifier of the existing process to copy (this will copy all the attachments such as ports, nested content,
     *                     schema, connection etc)
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param versionIdentifier version of the resource
     * @param description the description of the process
     * @param formula expression that describes the behaviour of the process
     * @param extendedProperties attributes introduced by subtypes of Process
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createProcessFromTemplate(String            templateGUID,
                                     ElementStatus     initialStatus,
                                     String            qualifiedName,
                                     String            name,
                                     String            versionIdentifier,
                                     String            description,
                                     String            formula,
                                     ElementProperties extendedProperties) throws InvalidParameterException,
                                                                                  UserNotAuthorizedException,
                                                                                  PropertyServerException;


    /**
     * Create a process that represents the processing instance of this governance action.
     * @param processTypeName the type name of the process.  This is the name of an open metadata type that inherits from "Process".
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param description the description of the process
     * @param parentGUID the unique identifier of the existing process to copy (this will copy all the attachments such as ports, nested content,
     *                     schema, connection etc)
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createChildProcess(String        processTypeName,
                              ElementStatus initialStatus,
                              String        qualifiedName,
                              String        name,
                              String        description,
                              String        parentGUID) throws InvalidParameterException,
                                                               UserNotAuthorizedException,
                                                               PropertyServerException;


    /**
     * Create a process that represents the processing instance of this governance action.
     * @param processTypeName the type name of the process.  This is the name of an open metadata type that inherits from "Process".
     * @param initialStatus status value of the process
     * @param qualifiedName the unique name of the new process
     * @param name the technical display name of the process
     * @param versionIdentifier version of the resource
     * @param description the description of the process
     * @param formula expression that describes the behaviour of the process
     * @param extendedProperties attributes introduced by subtypes of Process
     * @param parentGUID the unique identifier of the existing process to copy (this will copy all the attachments such as ports, nested content,
     *                     schema, connection etc)
     *
     * @return unique identifier of the new process
     *
     * @throws InvalidParameterException the type name or qualified name is null or invalid
     * @throws UserNotAuthorizedException this governance action service is not authorized to create a process
     * @throws PropertyServerException there is a problem connecting to the metadata store
     */
    String createChildProcess(String            processTypeName,
                              ElementStatus     initialStatus,
                              String            qualifiedName,
                              String            name,
                              String            versionIdentifier,
                              String            description,
                              String            formula,
                              ElementProperties extendedProperties,
                              String            parentGUID) throws InvalidParameterException, 
                                                                   UserNotAuthorizedException, 
                                                                   PropertyServerException;


    /**
     * Add a port to a process.
     *
     * @param processGUID unique identifier of the process
     * @param qualifiedName unique name for the port
     * @param displayName display name for the port
     * @param portType type of port (direction of data flow)
     *
     * @return unique identifier of the new port
     *
     * @throws InvalidParameterException the processGUID or qualified name is null or is invalid
     * @throws UserNotAuthorizedException the governance action service is not authorized to create this type of element
     * @throws PropertyServerException there is a problem with the metadata store
     */
    String createPort(String   processGUID,
                      String   qualifiedName,
                      String   displayName,
                      PortType portType) throws InvalidParameterException,
                                                UserNotAuthorizedException,
                                                PropertyServerException;


    /**
     * Create a LineageMapping relationship between a source and target element.  This could be between two assets, two process ports or
     * two schema elements.
     *
     * @param sourceElementGUID unique identifier of the element that describes the source of the data.
     * @param targetElementGUID unique identifier of the element that describes the destination of the data.
     *
     * @return unique identifier of the relationship
     *
     * @throws InvalidParameterException one of the GUIDs is null or is invalid
     * @throws UserNotAuthorizedException the governance action service is not authorized to create this type of relationship
     * @throws PropertyServerException there is a problem with the metadata store
     */
    String createLineageRelationship(String sourceElementGUID,
                                     String targetElementGUID) throws InvalidParameterException,
                                                                 UserNotAuthorizedException,
                                                                 PropertyServerException;


    /**
     * Create a lineage relationship between a source and target element.  This could be between two assets, two process ports or
     * two schema elements.
     *
     * @param relationshipName either LineageMapping, ProcessCall, DataFlow, ControlFlow.
     * @param sourceElementGUID unique identifier of the element that describes the source of the data.
     * @param qualifiedName qualifiedName of the information supply chain
     * @param label label for when the lineage relationship is visualized
     * @param description description of the lineage activity
     * @param formula expression summary
     * @param formulaType language used to express the formula
     * @param queryId identifier for the process query - DataMapping Relationship
     * @param query the process query - DataMapping Relationship
     * @param guard value that indicated a control path to take - ControlFlow relationship
     * @param lineNumber line number where the call is made - ProcessCall Relationship
     * @param targetElementGUID unique identifier of the element that describes the destination of the data.
     *
     * @return unique identifier of the relationship
     *
     * @throws InvalidParameterException one of the GUIDs is null or is invalid
     * @throws UserNotAuthorizedException the governance action service is not authorized to create this type of relationship
     * @throws PropertyServerException there is a problem with the metadata store
     */
    String createLineageRelationship(String relationshipName,
                                     String sourceElementGUID,
                                     String qualifiedName,
                                     String label,
                                     String description,
                                     String formula,
                                     String formulaType,
                                     String queryId,
                                     String query,
                                     String guard,
                                     String lineNumber,
                                     String targetElementGUID) throws InvalidParameterException,
                                                                      UserNotAuthorizedException,
                                                                      PropertyServerException;
}
