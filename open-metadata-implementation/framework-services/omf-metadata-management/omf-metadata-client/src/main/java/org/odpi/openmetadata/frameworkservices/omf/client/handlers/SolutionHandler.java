/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.frameworkservices.omf.client.handlers;

import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.frameworks.openmetadata.mermaid.InformationSupplyChainMermaidGraphBuilder;
import org.odpi.openmetadata.frameworks.openmetadata.mermaid.SolutionBlueprintMermaidGraphBuilder;
import org.odpi.openmetadata.frameworks.openmetadata.mermaid.SolutionComponentMermaidGraphBuilder;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.openmetadata.converters.*;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.openmetadata.properties.*;
import org.odpi.openmetadata.frameworks.openmetadata.enums.ElementStatus;
import org.odpi.openmetadata.frameworks.openmetadata.enums.SequencingOrder;
import org.odpi.openmetadata.frameworks.openmetadata.enums.SolutionPortDirection;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.*;
import org.odpi.openmetadata.frameworks.openmetadata.properties.solutions.SolutionComponentActorProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.solutions.SolutionComponentProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.solutions.SolutionBlueprintProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.solutions.SolutionBlueprintCompositionProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.informationsupplychains.InformationSupplyChainLinkProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.informationsupplychains.InformationSupplyChainProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.informationsupplychains.InformationSupplyChainSegmentProperties;
import org.odpi.openmetadata.frameworks.openmetadata.properties.solutions.*;
import org.odpi.openmetadata.frameworks.openmetadata.search.*;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.frameworkservices.omf.ffdc.OpenMetadataStoreAuditCode;

import java.util.*;

/**
 * SolutionHandler provides methods to define information supply chains, solution components and their supporting objects
 * The interface supports the following types of objects
 *
 * <ul>
 *     <li>InformationSupplyChains</li>
 *     <li>InformationSupplyChainSegments</li>
 *     <li>SolutionBlueprints</li>
 *     <li>SolutionComponents</li>
 *     <li>SolutionPorts</li>
 * </ul>
 */
public class SolutionHandler
{
    private final OpenMetadataStoreHandler openMetadataStoreClient;
    private final InvalidParameterHandler  invalidParameterHandler = new InvalidParameterHandler();
    private final AuditLog                 auditLog;

    private final PropertyHelper propertyHelper = new PropertyHelper();

    private final String serviceName;
    private final String serverName;


    /**
     * Create a new client with no authentication embedded in the HTTP request.
     *
     * @param localServerName       name of this server (view server)
     * @param serverName            name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST services
     * @param auditLog              logging destination
     * @param accessServiceURLMarker which access service to call
     * @param serviceName           local service name
     * @param maxPageSize           maximum number of results supported by this server
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     *                                   REST API calls.
     */
    public SolutionHandler(String   localServerName,
                           String   serverName,
                           String   serverPlatformURLRoot,
                           AuditLog auditLog,
                           String   accessServiceURLMarker,
                           String   serviceName,
                           int      maxPageSize) throws InvalidParameterException
    {
        this.openMetadataStoreClient = new OpenMetadataStoreHandler(serverName, serverPlatformURLRoot, accessServiceURLMarker, maxPageSize);
        this.auditLog = auditLog;
        this.serverName = localServerName;
        this.serviceName = serviceName;
    }


    /**
     * Create a new client that passes userId and password in each HTTP request.  This is the
     * userId/password of the calling server.  The end user's userId is sent on each request.
     *
     * @param serverName            name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST services
     * @param userId                caller's userId embedded in all HTTP requests
     * @param password              caller's userId embedded in all HTTP requests
     * @param maxPageSize           maximum number of results supported by this server
     * @param auditLog              logging destination
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     *                                   REST API calls.
     */
    public SolutionHandler(String   localServerName,
                           String   serverName,
                           String   serverPlatformURLRoot,
                           String   userId,
                           String   password,
                           AuditLog auditLog,
                           String   accessServiceURLMarker,
                           String   serviceName,
                           int      maxPageSize) throws InvalidParameterException
    {
        this.openMetadataStoreClient = new OpenMetadataStoreHandler(serverName, serverPlatformURLRoot, accessServiceURLMarker, userId, password, maxPageSize);
        this.auditLog = auditLog;
        this.serverName = localServerName;
        this.serviceName = serviceName;
    }


    /**
     * Create a new information supply chain.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param anchorGUID unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                   or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor boolean flag to day that the element should be classified as its own anchor once its element
     *                    is created in the repository.
     * @param anchorScopeGUID unique identifier of any anchor scope to use for searching
     * @param properties             properties for the new element.
     * @param parentGUID unique identifier of optional parent entity
     * @param parentRelationshipTypeName type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1 which end should the parent GUID go in the relationship
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     *
     * @return unique identifier of the newly created element
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String createInformationSupplyChain(String                           userId,
                                               String                           externalSourceGUID,
                                               String                           externalSourceName,
                                               String                           anchorGUID,
                                               boolean                          isOwnAnchor,
                                               String                           anchorScopeGUID,
                                               InformationSupplyChainProperties properties,
                                               String                           parentGUID,
                                               String                           parentRelationshipTypeName,
                                               ElementProperties parentRelationshipProperties,
                                               boolean                          parentAtEnd1,
                                               boolean                          forLineage,
                                               boolean                          forDuplicateProcessing,
                                               Date                             effectiveTime) throws InvalidParameterException,
                                                                                                      PropertyServerException,
                                                                                                      UserNotAuthorizedException
    {
        final String methodName = "createInformationSupplyChain";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);
        invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);

        String elementTypeName = OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName;

        if (properties.getTypeName() != null)
        {
            elementTypeName = properties.getTypeName();
        }


        return openMetadataStoreClient.createMetadataElementInStore(userId,
                                                                    externalSourceGUID,
                                                                    externalSourceName,
                                                                    elementTypeName,
                                                                    ElementStatus.ACTIVE,
                                                                    null,
                                                                    anchorGUID,
                                                                    isOwnAnchor,
                                                                    anchorScopeGUID,
                                                                    properties.getEffectiveFrom(),
                                                                    properties.getEffectiveTo(),
                                                                    this.getElementProperties(properties),
                                                                    parentGUID,
                                                                    parentRelationshipTypeName,
                                                                    parentRelationshipProperties,
                                                                    parentAtEnd1,
                                                                    forLineage,
                                                                    forDuplicateProcessing,
                                                                    effectiveTime);
    }


    /**
     * Create a new metadata element to represent an information supply chain using an existing  element as a template.
     * The template defines additional classifications and relationships that should be added to the new information supply chain.
     *
     * @param userId             calling user
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param anchorGUID unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                   or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor boolean flag to day that the element should be classified as its own anchor once its element
     *                    is created in the repository.
     * @param anchorScopeGUID unique identifier of any anchor scope to use for searching
     * @param effectiveFrom the date when this element is active - null for active on creation
     * @param effectiveTo the date when this element becomes inactive - null for active until deleted
     * @param templateGUID the unique identifier of the existing asset to copy (this will copy all the attachments such as nested content, schema
     *                     connection etc)
     * @param replacementProperties properties of the new metadata element.  These override the template values
     * @param placeholderProperties property name-to-property value map to replace any placeholder values in the
     *                              template element - and their anchored elements, which are also copied as part of this operation.
     * @param parentGUID unique identifier of optional parent entity
     * @param parentRelationshipTypeName type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1 which end should the parent GUID go in the relationship
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     *
     * @return unique identifier of the new metadata element
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public String createInformationSupplyChainFromTemplate(String              userId,
                                                           String              externalSourceGUID,
                                                           String              externalSourceName,
                                                           String              anchorGUID,
                                                           boolean             isOwnAnchor,
                                                           String              anchorScopeGUID,
                                                           Date                effectiveFrom,
                                                           Date                effectiveTo,
                                                           String              templateGUID,
                                                           ElementProperties   replacementProperties,
                                                           Map<String, String> placeholderProperties,
                                                           String              parentGUID,
                                                           String              parentRelationshipTypeName,
                                                           ElementProperties   parentRelationshipProperties,
                                                           boolean             parentAtEnd1,
                                                           boolean             forLineage,
                                                           boolean             forDuplicateProcessing,
                                                           Date                effectiveTime) throws InvalidParameterException,
                                                                                                     UserNotAuthorizedException,
                                                                                                     PropertyServerException
    {
        return openMetadataStoreClient.createMetadataElementFromTemplate(userId,
                                                                         externalSourceGUID,
                                                                         externalSourceName,
                                                                         OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName,
                                                                         anchorGUID,
                                                                         isOwnAnchor,
                                                                         anchorScopeGUID,
                                                                         effectiveFrom,
                                                                         effectiveTo,
                                                                         templateGUID,
                                                                         replacementProperties,
                                                                         placeholderProperties,
                                                                         parentGUID,
                                                                         parentRelationshipTypeName,
                                                                         parentRelationshipProperties,
                                                                         parentAtEnd1,
                                                                         forLineage,
                                                                         forDuplicateProcessing,
                                                                         effectiveTime);
    }


    /**
     * Update the properties of an information supply chain.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param informationSupplyChainGUID         unique identifier of the information supply chain (returned from create)
     * @param replaceAllProperties   flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                               the individual properties specified on the request.
     * @param properties             properties for the element.
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void   updateInformationSupplyChain(String                           userId,
                                               String                           externalSourceGUID,
                                               String                           externalSourceName,
                                               String                           informationSupplyChainGUID,
                                               boolean                          replaceAllProperties,
                                               InformationSupplyChainProperties properties,
                                               boolean                          forLineage,
                                               boolean                          forDuplicateProcessing,
                                               Date                             effectiveTime) throws InvalidParameterException,
                                                                                                      PropertyServerException,
                                                                                                      UserNotAuthorizedException
    {
        final String methodName = "updateInformationSupplyChain";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";
        final String guidParameterName = "informationSupplyChainGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(informationSupplyChainGUID, guidParameterName, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);

        if (replaceAllProperties)
        {
            invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);
        }

        openMetadataStoreClient.updateMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             informationSupplyChainGUID,
                                                             replaceAllProperties,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             this.getElementProperties(properties),
                                                             effectiveTime);
    }


    /**
     * Create a new information supply chain segment that is anchored from an information supply chain.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param properties             properties for the new element.
     * @param informationSupplyChainGUID unique identifier of optional parent information supply chain
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @return unique identifier of the newly created element
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String createInformationSupplyChainSegment(String                                  userId,
                                                      String                                  externalSourceGUID,
                                                      String                                  externalSourceName,
                                                      InformationSupplyChainSegmentProperties properties,
                                                      String                                  informationSupplyChainGUID,
                                                      boolean                                 forLineage,
                                                      boolean                                 forDuplicateProcessing,
                                                      Date                                    effectiveTime) throws InvalidParameterException,
                                                                                                                    PropertyServerException,
                                                                                                                    UserNotAuthorizedException
    {
        final String methodName = "createInformationSupplyChain";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);
        invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);

        String elementTypeName = OpenMetadataType.INFORMATION_SUPPLY_CHAIN_SEGMENT.typeName;

        if (properties.getTypeName() != null)
        {
            elementTypeName = properties.getTypeName();
        }

        if (informationSupplyChainGUID != null)
        {
            return openMetadataStoreClient.createMetadataElementInStore(userId,
                                                                        externalSourceGUID,
                                                                        externalSourceName,
                                                                        elementTypeName,
                                                                        ElementStatus.ACTIVE,
                                                                        null,
                                                                        informationSupplyChainGUID,
                                                                        false,
                                                                        null,
                                                                        properties.getEffectiveFrom(),
                                                                        properties.getEffectiveTo(),
                                                                        this.getElementProperties(properties),
                                                                        informationSupplyChainGUID,
                                                                        OpenMetadataType.INFORMATION_SUPPLY_CHAIN_COMPOSITION_RELATIONSHIP.typeName,
                                                                        null,
                                                                        true,
                                                                        forLineage,
                                                                        forDuplicateProcessing,
                                                                        effectiveTime);
        }
        else
        {
            return openMetadataStoreClient.createMetadataElementInStore(userId,
                                                                        externalSourceGUID,
                                                                        externalSourceName,
                                                                        elementTypeName,
                                                                        ElementStatus.ACTIVE,
                                                                        null,
                                                                        null,
                                                                        true,
                                                                        null,
                                                                        properties.getEffectiveFrom(),
                                                                        properties.getEffectiveTo(),
                                                                        this.getElementProperties(properties),
                                                                        null,
                                                                        null,
                                                                        null,
                                                                        true,
                                                                        forLineage,
                                                                        forDuplicateProcessing,
                                                                        effectiveTime);
        }
    }


    /**
     * Update the properties of an information supply chain.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param segmentGUID         unique identifier of the information supply chain segment (returned from create)
     * @param replaceAllProperties   flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                               the individual properties specified on the request.
     * @param properties             properties for the element.
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void   updateInformationSupplyChainSegment(String                                  userId,
                                                      String                                  externalSourceGUID,
                                                      String                                  externalSourceName,
                                                      String                                  segmentGUID,
                                                      boolean                                 replaceAllProperties,
                                                      InformationSupplyChainSegmentProperties properties,
                                                      boolean                                 forLineage,
                                                      boolean                                 forDuplicateProcessing,
                                                      Date                                    effectiveTime) throws InvalidParameterException,
                                                                                                                    PropertyServerException,
                                                                                                                    UserNotAuthorizedException
    {
        final String methodName = "updateInformationSupplyChainSegment";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";
        final String guidParameterName = "segmentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(segmentGUID, guidParameterName, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);

        if (replaceAllProperties)
        {
            invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);
        }

        openMetadataStoreClient.updateMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             segmentGUID,
                                                             replaceAllProperties,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             this.getElementProperties(properties),
                                                             effectiveTime);
    }


    /**
     * Connect two information supply chain segments.
     *
     * @param userId          userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param segment1GUID  unique identifier of the first segment
     * @param segment2GUID      unique identifier of the second segment
     * @param linkProperties   description of the relationship.
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void linkSegments(String                               userId,
                             String                               externalSourceGUID,
                             String                               externalSourceName,
                             String                               segment1GUID,
                             String                               segment2GUID,
                             InformationSupplyChainLinkProperties linkProperties,
                             boolean                              forLineage,
                             boolean                              forDuplicateProcessing,
                             Date                                 effectiveTime) throws InvalidParameterException,
                                                                                        PropertyServerException,
                                                                                        UserNotAuthorizedException
    {
        final String methodName = "linkSegments";
        final String end1GUIDParameterName = "segment1GUID";
        final String end2GUIDParameterName = "segment2GUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(segment1GUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(segment2GUID, end2GUIDParameterName, methodName);

        if (linkProperties != null)
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.INFORMATION_SUPPLY_CHAIN_LINK_RELATIONSHIP.typeName,
                                                                 segment1GUID,
                                                                 segment2GUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 linkProperties.getEffectiveFrom(),
                                                                 linkProperties.getEffectiveTo(),
                                                                 this.getElementProperties(linkProperties),
                                                                 effectiveTime);
        }
        else
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.INFORMATION_SUPPLY_CHAIN_LINK_RELATIONSHIP.typeName,
                                                                 segment1GUID,
                                                                 segment2GUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 effectiveTime);
        }
    }


    /**
     * Detach two information supply chain segments from one another.
     *
     * @param userId          userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param segment1GUID  unique identifier of the first segment.
     * @param segment2GUID      unique identifier of the second segment.
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void detachSegments(String  userId,
                               String  externalSourceGUID,
                               String  externalSourceName,
                               String  segment1GUID,
                               String  segment2GUID,
                               boolean forLineage,
                               boolean forDuplicateProcessing,
                               Date    effectiveTime) throws InvalidParameterException,
                                                             PropertyServerException,
                                                             UserNotAuthorizedException
    {
        final String methodName = "detachSegments";

        final String end1GUIDParameterName = "segment1GUID";
        final String end2GUIDParameterName = "segment2GUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(segment1GUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(segment2GUID, end2GUIDParameterName, methodName);

        OpenMetadataRelationshipList relationshipList = openMetadataStoreClient.getMetadataElementRelationships(userId,
                                                                                                                segment1GUID,
                                                                                                                segment2GUID,
                                                                                                                OpenMetadataType.INFORMATION_SUPPLY_CHAIN_LINK_RELATIONSHIP.typeName,
                                                                                                                null,
                                                                                                                null,
                                                                                                                null,
                                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                forLineage,
                                                                                                                forDuplicateProcessing,
                                                                                                                effectiveTime,
                                                                                                                0,
                                                                                                                0);

        if ((relationshipList != null) && (relationshipList.getElementList() != null))
        {
            for (OpenMetadataRelationship relationship : relationshipList.getElementList())
            {
                if (relationship != null)
                {
                    if ((segment1GUID.equals(relationship.getElementGUIDAtEnd1())) && (segment2GUID.equals(relationship.getElementGUIDAtEnd2())))
                    {
                        openMetadataStoreClient.deleteRelationshipInStore(userId,
                                                                          externalSourceGUID,
                                                                          externalSourceName,
                                                                          relationship.getRelationshipGUID(),
                                                                          forLineage,
                                                                          forDuplicateProcessing,
                                                                          effectiveTime);
                    }
                }
            }
        }
    }


    /**
     * Delete an information supply chain segment.
     *
     * @param userId   userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param segmentGUID  unique identifier of the obsolete element
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void deleteInformationSupplyChainSegment(String  userId,
                                                    String  externalSourceGUID,
                                                    String  externalSourceName,
                                                    String  segmentGUID,
                                                    boolean forLineage,
                                                    boolean forDuplicateProcessing,
                                                    Date    effectiveTime) throws InvalidParameterException,
                                                                                  PropertyServerException,
                                                                                  UserNotAuthorizedException
    {
        final String methodName = "deleteInformationSupplyChainSegment";
        final String guidParameterName = "segmentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(segmentGUID, guidParameterName, methodName);

        openMetadataStoreClient.deleteMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             segmentGUID,
                                                             false,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Delete an information supply chain and all of its segments.
     *
     * @param userId   userId of user making request.
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param informationSupplyChainGUID  unique identifier of the required element.
     * @param cascadedDelete can information supply chains be deleted if segments are attached?
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void deleteInformationSupplyChain(String  userId,
                                             String  externalSourceGUID,
                                             String  externalSourceName,
                                             String  informationSupplyChainGUID,
                                             boolean cascadedDelete,
                                             boolean forLineage,
                                             boolean forDuplicateProcessing,
                                             Date    effectiveTime) throws InvalidParameterException,
                                                                           PropertyServerException,
                                                                           UserNotAuthorizedException
    {
        final String methodName = "deleteInformationSupplyChain";
        final String guidParameterName = "informationSupplyChainGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(informationSupplyChainGUID, guidParameterName, methodName);

        openMetadataStoreClient.deleteMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             informationSupplyChainGUID,
                                                             cascadedDelete,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Returns the list of information supply chains with a particular name.
     *
     * @param userId     userId of user making request
     * @param name       name of the element to return - match is full text match in qualifiedName or name
     * @param templateFilter         should templates be returned?
     * @param addImplementation    should details of the implementation of the information supply chain be extracted too?
     * @param limitResultsByStatus control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime             repository time to use
     * @param sequencingOrder      order to retrieve results
     * @param sequencingProperty   property to use for sequencing order
     * @param startFrom            paging start point
     * @param pageSize             maximum results that can be returned
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     *
     * @return a list of elements
     *
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public List<InformationSupplyChainElement> getInformationSupplyChainsByName(String              userId,
                                                                                String              name,
                                                                                TemplateFilter      templateFilter,
                                                                                boolean             addImplementation,
                                                                                List<ElementStatus> limitResultsByStatus,
                                                                                Date                asOfTime,
                                                                                SequencingOrder     sequencingOrder,
                                                                                String              sequencingProperty,
                                                                                int                 startFrom,
                                                                                int                 pageSize,
                                                                                boolean             forLineage,
                                                                                boolean             forDuplicateProcessing,
                                                                                Date                effectiveTime) throws InvalidParameterException,
                                                                                                                          PropertyServerException,
                                                                                                                          UserNotAuthorizedException
    {
        final String methodName = "getInformationSupplyChainsByName";
        final String nameParameterName = "name";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(name, nameParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<String> propertyNames = Arrays.asList(OpenMetadataProperty.QUALIFIED_NAME.name,
                                                   OpenMetadataProperty.DISPLAY_NAME.name);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElements(userId,
                                                                                                      OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName,
                                                                                                      null,
                                                                                                      propertyHelper.getSearchPropertiesByName(propertyNames, name, PropertyComparisonOperator.EQ, templateFilter),
                                                                                                      limitResultsByStatus,
                                                                                                      asOfTime,
                                                                                                      null,
                                                                                                      sequencingProperty,
                                                                                                      sequencingOrder,
                                                                                                      forLineage,
                                                                                                      forDuplicateProcessing,
                                                                                                      effectiveTime,
                                                                                                      startFrom,
                                                                                                      pageSize);

        return convertInformationSupplyChains(userId,
                                              openMetadataElements,
                                              addImplementation,
                                              asOfTime,
                                              forLineage,
                                              forDuplicateProcessing,
                                              effectiveTime,
                                              methodName);
    }


    /**
     * Return the properties of a specific information supply chain.
     *
     * @param userId            userId of user making request
     * @param informationSupplyChainGUID    unique identifier of the required element
     * @param addImplementation    should details of the implementation of the information supply chain be extracted too?
     * @param asOfTime             repository time to use
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return retrieved properties
     *
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public InformationSupplyChainElement getInformationSupplyChainByGUID(String  userId,
                                                                         String  informationSupplyChainGUID,
                                                                         boolean addImplementation,
                                                                         Date    asOfTime,
                                                                         boolean forLineage,
                                                                         boolean forDuplicateProcessing,
                                                                         Date    effectiveTime) throws InvalidParameterException,
                                                                                                       PropertyServerException,
                                                                                                       UserNotAuthorizedException
    {
        final String methodName = "getInformationSupplyChainByGUID";
        final String guidParameterName = "informationSupplyChainGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(informationSupplyChainGUID, guidParameterName, methodName);

        OpenMetadataElement openMetadataElement = openMetadataStoreClient.getMetadataElementByGUID(userId,
                                                                                                   informationSupplyChainGUID,
                                                                                                   forLineage,
                                                                                                   forDuplicateProcessing,
                                                                                                   asOfTime,
                                                                                                   effectiveTime);

        if ((openMetadataElement != null) && (propertyHelper.isTypeOf(openMetadataElement, OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName)))
        {
            return convertInformationSupplyChain(userId,
                                                 openMetadataElement,
                                                 addImplementation,
                                                 asOfTime,
                                                 forLineage,
                                                 forDuplicateProcessing,
                                                 effectiveTime,
                                                 methodName);
        }

        return null;
    }



    /**
     * Retrieve the list of information supply chains metadata elements that contain the search string.
     * The search string is treated as a regular expression.
     *
     * @param userId               calling user
     * @param searchString         string to find in the properties
     * @param templateFilter         should templates be returned?
     * @param addImplementation    should details of the implementation of the information supply chain be extracted too?
     * @param limitResultsByStatus control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime             repository time to use
     * @param sequencingOrder      order to retrieve results
     * @param sequencingProperty   property to use for sequencing order
     * @param startFrom            paging start point
     * @param pageSize             maximum results that can be returned
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return list of matching metadata elements
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<InformationSupplyChainElement> findInformationSupplyChains(String              userId,
                                                                           String              searchString,
                                                                           TemplateFilter      templateFilter,
                                                                           boolean             addImplementation,
                                                                           List<ElementStatus> limitResultsByStatus,
                                                                           Date                asOfTime,
                                                                           SequencingOrder     sequencingOrder,
                                                                           String              sequencingProperty,
                                                                           int                 startFrom,
                                                                           int                 pageSize,
                                                                           boolean             forLineage,
                                                                           boolean             forDuplicateProcessing,
                                                                           Date                effectiveTime) throws InvalidParameterException,
                                                                                                                     UserNotAuthorizedException,
                                                                                                                     PropertyServerException
    {
        final String methodName = "findInformationSupplyChains";
        final String searchStringParameterName = "searchString";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateSearchString(searchString, searchStringParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElementsWithString(userId,
                                                                                                                searchString,
                                                                                                                templateFilter,
                                                                                                                OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName,
                                                                                                                limitResultsByStatus,
                                                                                                                asOfTime,
                                                                                                                sequencingProperty,
                                                                                                                sequencingOrder,
                                                                                                                forLineage,
                                                                                                                forDuplicateProcessing,
                                                                                                                effectiveTime,
                                                                                                                startFrom,
                                                                                                                pageSize);

        return convertInformationSupplyChains(userId,
                                              openMetadataElements,
                                              addImplementation,
                                              asOfTime,
                                              forLineage,
                                              forDuplicateProcessing,
                                              effectiveTime,
                                              methodName);
    }


    /**
     * Create a new solution blueprint.
     *
     * @param userId                       userId of user making request.
     * @param externalSourceGUID           unique identifier of the software capability that owns this element
     * @param externalSourceName           unique name of the software capability that owns this element
     * @param anchorGUID                   unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                                     or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor                  boolean flag to day that the element should be classified as its own anchor once its element
     *                                     is created in the repository.
     * @param anchorScopeGUID              unique identifier of any anchor scope to use for searching
     * @param properties                   properties for the new element.
     * @param parentGUID                   unique identifier of optional parent entity
     * @param parentRelationshipTypeName   type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1                 which end should the parent GUID go in the relationship
     * @param forLineage                   the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing       the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime                only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return unique identifier of the newly created element
     * @throws InvalidParameterException  one of the parameters is invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String createSolutionBlueprint(String                      userId,
                                          String                      externalSourceGUID,
                                          String                      externalSourceName,
                                          String                      anchorGUID,
                                          boolean                     isOwnAnchor,
                                          String                      anchorScopeGUID,
                                          SolutionBlueprintProperties properties,
                                          String                      parentGUID,
                                          String                      parentRelationshipTypeName,
                                          ElementProperties           parentRelationshipProperties,
                                          boolean                     parentAtEnd1,
                                          boolean                     forLineage,
                                          boolean                     forDuplicateProcessing,
                                          Date                        effectiveTime) throws InvalidParameterException,
                                                                                            PropertyServerException,
                                                                                            UserNotAuthorizedException
    {
        final String methodName = "createSolutionBlueprint";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);
        invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);

        String elementTypeName = OpenMetadataType.SOLUTION_BLUEPRINT.typeName;

        if (properties.getTypeName() != null)
        {
            elementTypeName = properties.getTypeName();
        }


        return openMetadataStoreClient.createMetadataElementInStore(userId,
                                                                    externalSourceGUID,
                                                                    externalSourceName,
                                                                    elementTypeName,
                                                                    ElementStatus.ACTIVE,
                                                                    null,
                                                                    anchorGUID,
                                                                    isOwnAnchor,
                                                                    anchorScopeGUID,
                                                                    properties.getEffectiveFrom(),
                                                                    properties.getEffectiveTo(),
                                                                    this.getElementProperties(properties),
                                                                    parentGUID,
                                                                    parentRelationshipTypeName,
                                                                    parentRelationshipProperties,
                                                                    parentAtEnd1,
                                                                    forLineage,
                                                                    forDuplicateProcessing,
                                                                    effectiveTime);
    }


    /**
     * Create a new metadata element to represent a solution blueprint using an existing element as a template.
     * The template defines additional classifications and relationships that should be added to the new solution blueprint.
     *
     * @param userId                       calling user
     * @param externalSourceGUID           unique identifier of the software capability that owns this element
     * @param externalSourceName           unique name of the software capability that owns this element
     * @param anchorGUID                   unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                                     or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor                  boolean flag to day that the element should be classified as its own anchor once its element
     *                                     is created in the repository.
     * @param anchorScopeGUID              unique identifier of any anchor scope to use for searching
     * @param effectiveFrom                the date when this element is active - null for active on creation
     * @param effectiveTo                  the date when this element becomes inactive - null for active until deleted
     * @param templateGUID                 the unique identifier of the existing asset to copy (this will copy all the attachments such as nested content, schema
     *                                     connection etc)
     * @param replacementProperties        properties of the new metadata element.  These override the template values
     * @param placeholderProperties        property name-to-property value map to replace any placeholder values in the
     *                                     template element - and their anchored elements, which are also copied as part of this operation.
     * @param parentGUID                   unique identifier of optional parent entity
     * @param parentRelationshipTypeName   type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1                 which end should the parent GUID go in the relationship
     * @param forLineage                   the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing       the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime                only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     *
     * @return unique identifier of the new metadata element
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public String createSolutionBlueprintFromTemplate(String              userId,
                                                      String              externalSourceGUID,
                                                      String              externalSourceName,
                                                      String              anchorGUID,
                                                      boolean             isOwnAnchor,
                                                      String              anchorScopeGUID,
                                                      Date                effectiveFrom,
                                                      Date                effectiveTo,
                                                      String              templateGUID,
                                                      ElementProperties   replacementProperties,
                                                      Map<String, String> placeholderProperties,
                                                      String              parentGUID,
                                                      String              parentRelationshipTypeName,
                                                      ElementProperties   parentRelationshipProperties,
                                                      boolean             parentAtEnd1,
                                                      boolean             forLineage,
                                                      boolean             forDuplicateProcessing,
                                                      Date                effectiveTime) throws InvalidParameterException,
                                                                                                UserNotAuthorizedException,
                                                                                                PropertyServerException
    {
        return openMetadataStoreClient.createMetadataElementFromTemplate(userId,
                                                                         externalSourceGUID,
                                                                         externalSourceName,
                                                                         OpenMetadataType.SOLUTION_BLUEPRINT.typeName,
                                                                         anchorGUID,
                                                                         isOwnAnchor,
                                                                         anchorScopeGUID,
                                                                         effectiveFrom,
                                                                         effectiveTo,
                                                                         templateGUID,
                                                                         replacementProperties,
                                                                         placeholderProperties,
                                                                         parentGUID,
                                                                         parentRelationshipTypeName,
                                                                         parentRelationshipProperties,
                                                                         parentAtEnd1,
                                                                         forLineage,
                                                                         forDuplicateProcessing,
                                                                         effectiveTime);
    }


    /**
     * Update the properties of a solution blueprint.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionBlueprintGUID      unique identifier of the solution blueprint (returned from create)
     * @param replaceAllProperties   flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                               the individual properties specified on the request.
     * @param properties             properties for the element.
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void updateSolutionBlueprint(String                      userId,
                                        String                      externalSourceGUID,
                                        String                      externalSourceName,
                                        String                      solutionBlueprintGUID,
                                        boolean                     replaceAllProperties,
                                        SolutionBlueprintProperties properties,
                                        boolean                     forLineage,
                                        boolean                     forDuplicateProcessing,
                                        Date                        effectiveTime) throws InvalidParameterException,
                                                                                          PropertyServerException,
                                                                                          UserNotAuthorizedException
    {
        final String methodName = "updateSolutionBlueprint";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";
        final String guidParameterName = "solutionBlueprintGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionBlueprintGUID, guidParameterName, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);

        if (replaceAllProperties)
        {
            invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);
        }

        openMetadataStoreClient.updateMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             solutionBlueprintGUID,
                                                             replaceAllProperties,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             this.getElementProperties(properties),
                                                             effectiveTime);
    }


    /**
     * Attach a solution component to a solution blueprint.
     *
     * @param userId                  userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param parentSolutionBlueprintGUID unique identifier of the parent
     * @param solutionComponentGUID     unique identifier of the solution component
     * @param relationshipProperties  description of the relationship.
     * @param forLineage              the query is to support lineage retrieval
     * @param forDuplicateProcessing  the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime           the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void linkSolutionComponentToBlueprint(String                                 userId,
                                                 String                                 externalSourceGUID,
                                                 String                                 externalSourceName,
                                                 String                                 parentSolutionBlueprintGUID,
                                                 String                                 solutionComponentGUID,
                                                 SolutionBlueprintCompositionProperties relationshipProperties,
                                                 boolean                                forLineage,
                                                 boolean                                forDuplicateProcessing,
                                                 Date                                   effectiveTime) throws InvalidParameterException,
                                                                                                              PropertyServerException,
                                                                                                              UserNotAuthorizedException
    {
        final String methodName = "linkSolutionComponentToBlueprint";
        final String end1GUIDParameterName = "parentSolutionBlueprintGUID";
        final String end2GUIDParameterName = "solutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(parentSolutionBlueprintGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, end2GUIDParameterName, methodName);

        if (relationshipProperties != null)
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_BLUEPRINT_COMPOSITION_RELATIONSHIP.typeName,
                                                                 parentSolutionBlueprintGUID,
                                                                 solutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 relationshipProperties.getEffectiveFrom(),
                                                                 relationshipProperties.getEffectiveTo(),
                                                                 this.getElementProperties(relationshipProperties),
                                                                 effectiveTime);
        }
        else
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_BLUEPRINT_COMPOSITION_RELATIONSHIP.typeName,
                                                                 parentSolutionBlueprintGUID,
                                                                 solutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 effectiveTime);
        }
    }


    /**
     * Detach a solution component from a solution blueprint.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionBlueprintGUID    unique identifier of the solution blueprint.
     * @param solutionComponentGUID    unique identifier of the solution component.
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void detachSolutionComponentFromBlueprint(String  userId,
                                                     String  externalSourceGUID,
                                                     String  externalSourceName,
                                                     String  solutionBlueprintGUID,
                                                     String  solutionComponentGUID,
                                                     boolean forLineage,
                                                     boolean forDuplicateProcessing,
                                                     Date    effectiveTime) throws InvalidParameterException,
                                                                                   PropertyServerException,
                                                                                   UserNotAuthorizedException
    {
        final String methodName = "detachSolutionComponentFromBlueprint";

        final String end1GUIDParameterName = "parentSolutionBlueprintGUID";
        final String end2GUIDParameterName = "nestedSolutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionBlueprintGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, end2GUIDParameterName, methodName);

        openMetadataStoreClient.detachRelatedElementsInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             OpenMetadataType.SOLUTION_BLUEPRINT_COMPOSITION_RELATIONSHIP.typeName,
                                                             solutionBlueprintGUID,
                                                             solutionComponentGUID,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Delete a solution blueprint.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionBlueprintGUID      unique identifier of the element
     * @param cascadedDelete         can the solution blueprint be deleted if it has solution components linked to it?
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void deleteSolutionBlueprint(String  userId,
                                        String  externalSourceGUID,
                                        String  externalSourceName,
                                        String  solutionBlueprintGUID,
                                        boolean cascadedDelete,
                                        boolean forLineage,
                                        boolean forDuplicateProcessing,
                                        Date    effectiveTime) throws InvalidParameterException,
                                                                      PropertyServerException,
                                                                      UserNotAuthorizedException
    {
        final String methodName = "deleteSolutionBlueprint";
        final String guidParameterName = "solutionBlueprintGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionBlueprintGUID, guidParameterName, methodName);

        openMetadataStoreClient.deleteMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             solutionBlueprintGUID,
                                                             cascadedDelete,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Returns the list of solution blueprints with a particular name.
     *
     * @param userId                 userId of user making request
     * @param name                   name of the element to return - match is full text match in qualifiedName or name
     * @param templateFilter         should templates be returned?
     * @param limitResultsByStatus   control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime               repository time to use
     * @param sequencingOrder        order to retrieve results
     * @param sequencingProperty     property to use for sequencing order
     * @param startFrom              paging start point
     * @param pageSize               maximum results that can be returned
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @return a list of elements
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public List<SolutionBlueprintElement> getSolutionBlueprintsByName(String              userId,
                                                                      String              name,
                                                                      TemplateFilter      templateFilter,
                                                                      List<ElementStatus> limitResultsByStatus,
                                                                      Date                asOfTime,
                                                                      SequencingOrder     sequencingOrder,
                                                                      String              sequencingProperty,
                                                                      int                 startFrom,
                                                                      int                 pageSize,
                                                                      boolean             forLineage,
                                                                      boolean             forDuplicateProcessing,
                                                                      Date                effectiveTime) throws InvalidParameterException,
                                                                                                                PropertyServerException,
                                                                                                                UserNotAuthorizedException
    {
        final String methodName = "getSolutionBlueprintsByName";
        final String nameParameterName = "name";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(name, nameParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<String> propertyNames = Arrays.asList(OpenMetadataProperty.QUALIFIED_NAME.name,
                                                   OpenMetadataProperty.DISPLAY_NAME.name);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElements(userId,
                                                                                                      OpenMetadataType.SOLUTION_BLUEPRINT.typeName,
                                                                                                      null,
                                                                                                      propertyHelper.getSearchPropertiesByName(propertyNames, name, PropertyComparisonOperator.EQ, templateFilter),
                                                                                                      limitResultsByStatus,
                                                                                                      asOfTime,
                                                                                                      null,
                                                                                                      sequencingProperty,
                                                                                                      sequencingOrder,
                                                                                                      forLineage,
                                                                                                      forDuplicateProcessing,
                                                                                                      effectiveTime,
                                                                                                      startFrom,
                                                                                                      pageSize);

        return convertSolutionBlueprints(userId,
                                         openMetadataElements,
                                         asOfTime,
                                         forLineage,
                                         forDuplicateProcessing,
                                         effectiveTime,
                                         methodName);
    }


    /**
     * Return the properties of a specific solution blueprint.
     *
     * @param userId                 userId of user making request
     * @param solutionBlueprintGUID      unique identifier of the required element
     * @param asOfTime               repository time to use
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @return retrieved properties
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public SolutionBlueprintElement getSolutionBlueprintByGUID(String  userId,
                                                               String  solutionBlueprintGUID,
                                                               Date    asOfTime,
                                                               boolean forLineage,
                                                               boolean forDuplicateProcessing,
                                                               Date    effectiveTime) throws InvalidParameterException,
                                                                                             PropertyServerException,
                                                                                             UserNotAuthorizedException
    {
        final String methodName = "getSolutionBlueprintByGUID";
        final String guidParameterName = "solutionBlueprintGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionBlueprintGUID, guidParameterName, methodName);

        OpenMetadataElement openMetadataElement = openMetadataStoreClient.getMetadataElementByGUID(userId,
                                                                                                   solutionBlueprintGUID,
                                                                                                   forLineage,
                                                                                                   forDuplicateProcessing,
                                                                                                   asOfTime,
                                                                                                   effectiveTime);

        if ((openMetadataElement != null) && (propertyHelper.isTypeOf(openMetadataElement, OpenMetadataType.SOLUTION_BLUEPRINT.typeName)))
        {
            return convertSolutionBlueprint(userId,
                                            openMetadataElement,
                                            asOfTime,
                                            forLineage,
                                            forDuplicateProcessing,
                                            effectiveTime,
                                            methodName);
        }

        return null;
    }


    /**
     * Retrieve the list of solution blueprint metadata elements that contain the search string.
     * The returned blueprints include a list of the components that are associated with it.
     * The search string is treated as a regular expression.
     *
     * @param userId               calling user
     * @param searchString         string to find in the properties
     * @param templateFilter         should templates be returned?
     * @param limitResultsByStatus control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime             repository time to use
     * @param sequencingOrder      order to retrieve results
     * @param sequencingProperty   property to use for sequencing order
     * @param startFrom            paging start point
     * @param pageSize             maximum results that can be returned
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return list of matching metadata elements
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<SolutionBlueprintElement> findSolutionBlueprints(String              userId,
                                                                 String              searchString,
                                                                 TemplateFilter      templateFilter,
                                                                 List<ElementStatus> limitResultsByStatus,
                                                                 Date                asOfTime,
                                                                 SequencingOrder     sequencingOrder,
                                                                 String              sequencingProperty,
                                                                 int                 startFrom,
                                                                 int                 pageSize,
                                                                 boolean             forLineage,
                                                                 boolean             forDuplicateProcessing,
                                                                 Date                effectiveTime) throws InvalidParameterException,
                                                                                                           UserNotAuthorizedException,
                                                                                                           PropertyServerException
    {
        final String methodName = "findSolutionBlueprints";
        final String searchStringParameterName = "searchString";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateSearchString(searchString, searchStringParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElementsWithString(userId,
                                                                                                                searchString,
                                                                                                                templateFilter,
                                                                                                                OpenMetadataType.SOLUTION_BLUEPRINT.typeName,
                                                                                                                limitResultsByStatus,
                                                                                                                asOfTime,
                                                                                                                sequencingProperty,
                                                                                                                sequencingOrder,
                                                                                                                forLineage,
                                                                                                                forDuplicateProcessing,
                                                                                                                effectiveTime,
                                                                                                                startFrom,
                                                                                                                pageSize);

        return convertSolutionBlueprints(userId,
                                         openMetadataElements,
                                         asOfTime,
                                         forLineage,
                                         forDuplicateProcessing,
                                         effectiveTime,
                                         methodName);
    }


    /**
     * Create a new solution component.
     *
     * @param userId                       userId of user making request.
     * @param externalSourceGUID           unique identifier of the software capability that owns this element
     * @param externalSourceName           unique name of the software capability that owns this element
     * @param anchorGUID                   unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                                     or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor                  boolean flag to day that the element should be classified as its own anchor once its element
     *                                     is created in the repository.
     * @param anchorScopeGUID              unique identifier of any anchor scope to use for searching
     * @param properties                   properties for the new element.
     * @param parentGUID                   unique identifier of optional parent entity
     * @param parentRelationshipTypeName   type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1                 which end should the parent GUID go in the relationship
     * @param forLineage                   the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing       the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime                only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return unique identifier of the newly created element
     * @throws InvalidParameterException  one of the parameters is invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String createSolutionComponent(String                      userId,
                                          String                      externalSourceGUID,
                                          String                      externalSourceName,
                                          String                      anchorGUID,
                                          boolean                     isOwnAnchor,
                                          String                      anchorScopeGUID,
                                          SolutionComponentProperties properties,
                                          String                      parentGUID,
                                          String                      parentRelationshipTypeName,
                                          ElementProperties           parentRelationshipProperties,
                                          boolean                     parentAtEnd1,
                                          boolean                     forLineage,
                                          boolean                     forDuplicateProcessing,
                                          Date                        effectiveTime) throws InvalidParameterException,
                                                                                            PropertyServerException,
                                                                                            UserNotAuthorizedException
    {
        final String methodName = "createSolutionComponent";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);
        invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);

        String elementTypeName = OpenMetadataType.SOLUTION_COMPONENT.typeName;

        if (properties.getTypeName() != null)
        {
            elementTypeName = properties.getTypeName();
        }

        return openMetadataStoreClient.createMetadataElementInStore(userId,
                                                                    externalSourceGUID,
                                                                    externalSourceName,
                                                                    elementTypeName,
                                                                    ElementStatus.ACTIVE,
                                                                    null,
                                                                    anchorGUID,
                                                                    isOwnAnchor,
                                                                    anchorScopeGUID,
                                                                    properties.getEffectiveFrom(),
                                                                    properties.getEffectiveTo(),
                                                                    this.getElementProperties(properties),
                                                                    parentGUID,
                                                                    parentRelationshipTypeName,
                                                                    parentRelationshipProperties,
                                                                    parentAtEnd1,
                                                                    forLineage,
                                                                    forDuplicateProcessing,
                                                                    effectiveTime);
    }


    /**
     * Create a new metadata element to represent a solution component using an existing element as a template.
     * The template defines additional classifications and relationships that should be added to the new solution component.
     *
     * @param userId                       calling user
     * @param externalSourceGUID           unique identifier of the software capability that owns this element
     * @param externalSourceName           unique name of the software capability that owns this element
     * @param anchorGUID                   unique identifier of the element that should be the anchor for the new element. Set to null if no anchor,
     *                                     or the Anchors classification is included in the initial classifications.
     * @param isOwnAnchor                  boolean flag to day that the element should be classified as its own anchor once its element
     *                                     is created in the repository.
     * @param anchorScopeGUID              unique identifier of any anchor scope to use for searching
     * @param effectiveFrom                the date when this element is active - null for active on creation
     * @param effectiveTo                  the date when this element becomes inactive - null for active until deleted
     * @param templateGUID                 the unique identifier of the existing asset to copy (this will copy all the attachments such as nested content, schema
     *                                     connection etc)
     * @param replacementProperties        properties of the new metadata element.  These override the template values
     * @param placeholderProperties        property name-to-property value map to replace any placeholder values in the
     *                                     template element - and their anchored elements, which are also copied as part of this operation.
     * @param parentGUID                   unique identifier of optional parent entity
     * @param parentRelationshipTypeName   type of relationship to connect the new element to the parent
     * @param parentRelationshipProperties properties to include in parent relationship
     * @param parentAtEnd1                 which end should the parent GUID go in the relationship
     * @param forLineage                   the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing       the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime                only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     *
     * @return unique identifier of the new metadata element
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public String createSolutionComponentFromTemplate(String              userId,
                                                      String              externalSourceGUID,
                                                      String              externalSourceName,
                                                      String              anchorGUID,
                                                      boolean             isOwnAnchor,
                                                      String              anchorScopeGUID,
                                                      Date                effectiveFrom,
                                                      Date                effectiveTo,
                                                      String              templateGUID,
                                                      ElementProperties   replacementProperties,
                                                      Map<String, String> placeholderProperties,
                                                      String              parentGUID,
                                                      String              parentRelationshipTypeName,
                                                      ElementProperties   parentRelationshipProperties,
                                                      boolean             parentAtEnd1,
                                                      boolean             forLineage,
                                                      boolean             forDuplicateProcessing,
                                                      Date                effectiveTime) throws InvalidParameterException,
                                                                                                UserNotAuthorizedException,
                                                                                                PropertyServerException
    {
        return openMetadataStoreClient.createMetadataElementFromTemplate(userId,
                                                                         externalSourceGUID,
                                                                         externalSourceName,
                                                                         OpenMetadataType.SOLUTION_COMPONENT.typeName,
                                                                         anchorGUID,
                                                                         isOwnAnchor,
                                                                         anchorScopeGUID,
                                                                         effectiveFrom,
                                                                         effectiveTo,
                                                                         templateGUID,
                                                                         replacementProperties,
                                                                         placeholderProperties,
                                                                         parentGUID,
                                                                         parentRelationshipTypeName,
                                                                         parentRelationshipProperties,
                                                                         parentAtEnd1,
                                                                         forLineage,
                                                                         forDuplicateProcessing,
                                                                         effectiveTime);
    }


    /**
     * Update the properties of a solution component.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionComponentGUID      unique identifier of the solution component (returned from create)
     * @param replaceAllProperties   flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                               the individual properties specified on the request.
     * @param properties             properties for the element.
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void updateSolutionComponent(String                      userId,
                                        String                      externalSourceGUID,
                                        String                      externalSourceName,
                                        String                      solutionComponentGUID,
                                        boolean                     replaceAllProperties,
                                        SolutionComponentProperties properties,
                                        boolean                     forLineage,
                                        boolean                     forDuplicateProcessing,
                                        Date                        effectiveTime) throws InvalidParameterException,
                                                                                          PropertyServerException,
                                                                                          UserNotAuthorizedException
    {
        final String methodName = "updateSolutionComponent";
        final String propertiesName = "properties";
        final String qualifiedNameParameterName = "properties.qualifiedName";
        final String guidParameterName = "solutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, guidParameterName, methodName);
        invalidParameterHandler.validateObject(properties, propertiesName, methodName);

        if (replaceAllProperties)
        {
            invalidParameterHandler.validateName(properties.getQualifiedName(), qualifiedNameParameterName, methodName);
        }

        openMetadataStoreClient.updateMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             solutionComponentGUID,
                                                             replaceAllProperties,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             this.getElementProperties(properties),
                                                             effectiveTime);
    }


    /**
     * Attach a solution component to a solution component.
     *
     * @param userId                  userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param parentSolutionComponentGUID unique identifier of the parent solution component
     * @param childSolutionComponentGUID     unique identifier of the child solution component
     * @param relationshipProperties  description of the relationship.
     * @param forLineage              the query is to support lineage retrieval
     * @param forDuplicateProcessing  the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime           the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void linkSubcomponent(String                    userId,
                                 String                    externalSourceGUID,
                                 String                    externalSourceName,
                                 String                    parentSolutionComponentGUID,
                                 String                    childSolutionComponentGUID,
                                 RelationshipProperties    relationshipProperties,
                                 boolean                   forLineage,
                                 boolean                   forDuplicateProcessing,
                                 Date                      effectiveTime) throws InvalidParameterException,
                                                                                 PropertyServerException,
                                                                                 UserNotAuthorizedException
    {
        final String methodName = "linkSubcomponent";
        final String end1GUIDParameterName = "parentSolutionComponentGUID";
        final String end2GUIDParameterName = "childSolutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(parentSolutionComponentGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(childSolutionComponentGUID, end2GUIDParameterName, methodName);

        if (relationshipProperties != null)
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_COMPOSITION_RELATIONSHIP.typeName,
                                                                 parentSolutionComponentGUID,
                                                                 childSolutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 relationshipProperties.getEffectiveFrom(),
                                                                 relationshipProperties.getEffectiveTo(),
                                                                 null,
                                                                 effectiveTime);
        }
        else
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_COMPOSITION_RELATIONSHIP.typeName,
                                                                 parentSolutionComponentGUID,
                                                                 childSolutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 effectiveTime);
        }
    }


    /**
     * Detach a solution component from a solution component.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param parentSolutionComponentGUID    unique identifier of the parent solution component.
     * @param subcomponentGUID    unique identifier of the nested solution component.
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void detachSubcomponent(String  userId,
                                   String  externalSourceGUID,
                                   String  externalSourceName,
                                   String  parentSolutionComponentGUID,
                                   String  subcomponentGUID,
                                   boolean forLineage,
                                   boolean forDuplicateProcessing,
                                   Date    effectiveTime) throws InvalidParameterException,
                                                                 PropertyServerException,
                                                                 UserNotAuthorizedException
    {
        final String methodName = "detachSubcomponent";

        final String end1GUIDParameterName = "parentSolutionComponentGUID";
        final String end2GUIDParameterName = "nestedDataFieldGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(parentSolutionComponentGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(subcomponentGUID, end2GUIDParameterName, methodName);

        openMetadataStoreClient.detachRelatedElementsInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             OpenMetadataType.SOLUTION_COMPOSITION_RELATIONSHIP.typeName,
                                                             parentSolutionComponentGUID,
                                                             subcomponentGUID,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Attach a design object such as a solution component or governance definition to its implementation via the ImplementedBy relationship.
     *
     * @param userId                  userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param solutionComponentOneGUID unique identifier of the solution component at end 1
     * @param solutionComponentTwoGUID unique identifier of the solution component at end 2
     * @param relationshipProperties  additional properties for the relationship.
     * @param forLineage              the query is to support lineage retrieval
     * @param forDuplicateProcessing  the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime           the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void linkSolutionLinkingWire(String                        userId,
                                        String                        externalSourceGUID,
                                        String                        externalSourceName,
                                        String                        solutionComponentOneGUID,
                                        String                        solutionComponentTwoGUID,
                                        SolutionLinkingWireProperties relationshipProperties,
                                        boolean                       forLineage,
                                        boolean                       forDuplicateProcessing,
                                        Date                          effectiveTime) throws InvalidParameterException,
                                                                                            PropertyServerException,
                                                                                            UserNotAuthorizedException
    {
        final String methodName = "linkSolutionLinkingWire";
        final String end1GUIDParameterName = "solutionComponentOneGUID";
        final String end2GUIDParameterName = "solutionComponentTwoGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentOneGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentTwoGUID, end2GUIDParameterName, methodName);

        if (relationshipProperties != null)
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_LINKING_WIRE_RELATIONSHIP.typeName,
                                                                 solutionComponentOneGUID,
                                                                 solutionComponentTwoGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 relationshipProperties.getEffectiveFrom(),
                                                                 relationshipProperties.getEffectiveTo(),
                                                                 this.getElementProperties(relationshipProperties),
                                                                 effectiveTime);
        }
        else
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_LINKING_WIRE_RELATIONSHIP.typeName,
                                                                 solutionComponentOneGUID,
                                                                 solutionComponentTwoGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 effectiveTime);
        }
    }


    /**
     * Detach a design object such as a solution component or governance definition from its implementation.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionComponentOneGUID unique identifier of the solution component at end 1
     * @param solutionComponentTwoGUID unique identifier of the solution component at end 2
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void detachSolutionLinkingWire(String  userId,
                                          String  externalSourceGUID,
                                          String  externalSourceName,
                                          String  solutionComponentOneGUID,
                                          String  solutionComponentTwoGUID,
                                          boolean forLineage,
                                          boolean forDuplicateProcessing,
                                          Date    effectiveTime) throws InvalidParameterException,
                                                                        PropertyServerException,
                                                                        UserNotAuthorizedException
    {
        final String methodName = "detachSolutionLinkingWire";

        final String end1GUIDParameterName = "solutionComponentOneGUID";
        final String end2GUIDParameterName = "solutionComponentTwoGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentOneGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentTwoGUID, end2GUIDParameterName, methodName);

        openMetadataStoreClient.detachRelatedElementsInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             OpenMetadataType.SOLUTION_LINKING_WIRE_RELATIONSHIP.typeName,
                                                             solutionComponentOneGUID,
                                                             solutionComponentTwoGUID,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }



    /**
     * Attach a solution component to an actor role.
     *
     * @param userId                  userId of user making request
     * @param externalSourceGUID      unique identifier of the software capability that owns this element
     * @param externalSourceName      unique name of the software capability that owns this element
     * @param solutionRoleGUID unique identifier of the parent
     * @param solutionComponentGUID     unique identifier of the solution component
     * @param relationshipProperties  description of the relationship.
     * @param forLineage              the query is to support lineage retrieval
     * @param forDuplicateProcessing  the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime           the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void linkSolutionComponentActor(String                           userId,
                                           String                           externalSourceGUID,
                                           String                           externalSourceName,
                                           String                           solutionRoleGUID,
                                           String                           solutionComponentGUID,
                                           SolutionComponentActorProperties relationshipProperties,
                                           boolean                          forLineage,
                                           boolean                          forDuplicateProcessing,
                                           Date                             effectiveTime) throws InvalidParameterException,
                                                                                                  PropertyServerException,
                                                                                                  UserNotAuthorizedException
    {
        final String methodName = "linkSolutionComponentActor";
        final String end1GUIDParameterName = "parentSolutionRoleGUID";
        final String end2GUIDParameterName = "solutionComponentActorGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionRoleGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, end2GUIDParameterName, methodName);

        if (relationshipProperties != null)
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_COMPONENT_ACTOR_RELATIONSHIP.typeName,
                                                                 solutionRoleGUID,
                                                                 solutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 relationshipProperties.getEffectiveFrom(),
                                                                 relationshipProperties.getEffectiveTo(),
                                                                 this.getElementProperties(relationshipProperties),
                                                                 effectiveTime);
        }
        else
        {
            openMetadataStoreClient.createRelatedElementsInStore(userId,
                                                                 externalSourceGUID,
                                                                 externalSourceName,
                                                                 OpenMetadataType.SOLUTION_COMPONENT_ACTOR_RELATIONSHIP.typeName,
                                                                 solutionRoleGUID,
                                                                 solutionComponentGUID,
                                                                 forLineage,
                                                                 forDuplicateProcessing,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 effectiveTime);
        }
    }


    /**
     * Detach a solution component from an actor role.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionRoleGUID    unique identifier of the parent solution component.
     * @param solutionComponentGUID    unique identifier of the nested solution component.
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void detachSolutionComponentActor(String  userId,
                                             String  externalSourceGUID,
                                             String  externalSourceName,
                                             String  solutionRoleGUID,
                                             String  solutionComponentGUID,
                                             boolean forLineage,
                                             boolean forDuplicateProcessing,
                                             Date    effectiveTime) throws InvalidParameterException,
                                                                           PropertyServerException,
                                                                           UserNotAuthorizedException
    {
        final String methodName = "detachSolutionComponentActor";

        final String end1GUIDParameterName = "parentSolutionRoleGUID";
        final String end2GUIDParameterName = "nestedDataFieldGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionRoleGUID, end1GUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, end2GUIDParameterName, methodName);

        openMetadataStoreClient.detachRelatedElementsInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             OpenMetadataType.SOLUTION_COMPONENT_ACTOR_RELATIONSHIP.typeName,
                                                             solutionRoleGUID,
                                                             solutionComponentGUID,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Delete a solution component.
     *
     * @param userId                 userId of user making request.
     * @param externalSourceGUID     unique identifier of the software capability that owns this element
     * @param externalSourceName     unique name of the software capability that owns this element
     * @param solutionComponentGUID      unique identifier of the element
     * @param cascadedDelete         can the solution component be deleted if it has solution components linked to it?
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void deleteSolutionComponent(String  userId,
                                        String  externalSourceGUID,
                                        String  externalSourceName,
                                        String  solutionComponentGUID,
                                        boolean cascadedDelete,
                                        boolean forLineage,
                                        boolean forDuplicateProcessing,
                                        Date    effectiveTime) throws InvalidParameterException,
                                                                      PropertyServerException,
                                                                      UserNotAuthorizedException
    {
        final String methodName = "deleteSolutionComponent";
        final String guidParameterName = "solutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, guidParameterName, methodName);

        openMetadataStoreClient.deleteMetadataElementInStore(userId,
                                                             externalSourceGUID,
                                                             externalSourceName,
                                                             solutionComponentGUID,
                                                             cascadedDelete,
                                                             forLineage,
                                                             forDuplicateProcessing,
                                                             effectiveTime);
    }


    /**
     * Returns the list of solution components with a particular name.
     *
     * @param userId                 userId of user making request
     * @param name                   name of the element to return - match is full text match in qualifiedName or name
     * @param templateFilter         should templates be returned?
     * @param limitResultsByStatus   control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime               repository time to use
     * @param sequencingOrder        order to retrieve results
     * @param sequencingProperty     property to use for sequencing order
     * @param startFrom              paging start point
     * @param pageSize               maximum results that can be returned
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @return a list of elements
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public List<SolutionComponentElement> getSolutionComponentsByName(String              userId,
                                                                      String              name,
                                                                      TemplateFilter      templateFilter,
                                                                      List<ElementStatus> limitResultsByStatus,
                                                                      Date                asOfTime,
                                                                      SequencingOrder     sequencingOrder,
                                                                      String              sequencingProperty,
                                                                      int                 startFrom,
                                                                      int                 pageSize,
                                                                      boolean             forLineage,
                                                                      boolean             forDuplicateProcessing,
                                                                      Date                effectiveTime) throws InvalidParameterException,
                                                                                                                PropertyServerException,
                                                                                                                UserNotAuthorizedException
    {
        final String methodName = "getSolutionComponentsByName";
        final String nameParameterName = "name";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(name, nameParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<String> propertyNames = Arrays.asList(OpenMetadataProperty.QUALIFIED_NAME.name,
                                                   OpenMetadataProperty.DISPLAY_NAME.name);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElements(userId,
                                                                                                      OpenMetadataType.SOLUTION_COMPONENT.typeName,
                                                                                                      null,
                                                                                                      propertyHelper.getSearchPropertiesByName(propertyNames, name, PropertyComparisonOperator.EQ, templateFilter),
                                                                                                      limitResultsByStatus,
                                                                                                      asOfTime,
                                                                                                      null,
                                                                                                      sequencingProperty,
                                                                                                      sequencingOrder,
                                                                                                      forLineage,
                                                                                                      forDuplicateProcessing,
                                                                                                      effectiveTime,
                                                                                                      startFrom,
                                                                                                      pageSize);

        return convertSolutionComponents(userId,
                                         openMetadataElements,
                                         true,
                                         asOfTime,
                                         forLineage,
                                         forDuplicateProcessing,
                                         effectiveTime,
                                         true,
                                         methodName);
    }


    /**
     * Return the properties of a specific solution component.
     *
     * @param userId                 userId of user making request
     * @param solutionComponentGUID      unique identifier of the required element
     * @param asOfTime               repository time to use
     * @param forLineage             the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime          the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @return retrieved properties
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws PropertyServerException    there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public SolutionComponentElement getSolutionComponentByGUID(String  userId,
                                                               String  solutionComponentGUID,
                                                               Date    asOfTime,
                                                               boolean forLineage,
                                                               boolean forDuplicateProcessing,
                                                               Date    effectiveTime) throws InvalidParameterException,
                                                                                             PropertyServerException,
                                                                                             UserNotAuthorizedException
    {
        final String methodName = "getSolutionComponentByGUID";
        final String guidParameterName = "solutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, guidParameterName, methodName);

        OpenMetadataElement openMetadataElement = openMetadataStoreClient.getMetadataElementByGUID(userId,
                                                                                                   solutionComponentGUID,
                                                                                                   forLineage,
                                                                                                   forDuplicateProcessing,
                                                                                                   asOfTime,
                                                                                                   effectiveTime);

        if ((openMetadataElement != null) && (propertyHelper.isTypeOf(openMetadataElement, OpenMetadataType.SOLUTION_COMPONENT.typeName)))
        {
            return convertSolutionComponent(userId,
                                            openMetadataElement,
                                            true,
                                            asOfTime,
                                            forLineage,
                                            forDuplicateProcessing,
                                            effectiveTime,
                                            true,
                                            methodName);
        }

        return null;
    }



    /**
     * Retrieve the list of solution component metadata elements that contain the search string.  The solutions components returned include information about the consumers, actors and other solution components that are associated with them.
     * The returned solution components include a list of the subcomponents, peer components and solution roles that are associated with it.
     * The search string is treated as a regular expression.
     *
     * @param userId               calling user
     * @param searchString         string to find in the properties
     * @param templateFilter         should templates be returned?
     * @param limitResultsByStatus control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime             repository time to use
     * @param sequencingOrder      order to retrieve results
     * @param sequencingProperty   property to use for sequencing order
     * @param startFrom            paging start point
     * @param pageSize             maximum results that can be returned
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return list of matching metadata elements
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<SolutionComponentElement> findSolutionComponents(String              userId,
                                                                 String              searchString,
                                                                 TemplateFilter      templateFilter,
                                                                 List<ElementStatus> limitResultsByStatus,
                                                                 Date                asOfTime,
                                                                 SequencingOrder     sequencingOrder,
                                                                 String              sequencingProperty,
                                                                 int                 startFrom,
                                                                 int                 pageSize,
                                                                 boolean             forLineage,
                                                                 boolean             forDuplicateProcessing,
                                                                 Date                effectiveTime) throws InvalidParameterException,
                                                                                                           UserNotAuthorizedException,
                                                                                                           PropertyServerException
    {
        final String methodName = "findSolutionComponents";
        final String searchStringParameterName = "searchString";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateSearchString(searchString, searchStringParameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        List<OpenMetadataElement> openMetadataElements = openMetadataStoreClient.findMetadataElementsWithString(userId,
                                                                                                                searchString,
                                                                                                                templateFilter,
                                                                                                                OpenMetadataType.SOLUTION_COMPONENT.typeName,
                                                                                                                limitResultsByStatus,
                                                                                                                asOfTime,
                                                                                                                sequencingProperty,
                                                                                                                sequencingOrder,
                                                                                                                forLineage,
                                                                                                                forDuplicateProcessing,
                                                                                                                effectiveTime,
                                                                                                                startFrom,
                                                                                                                pageSize);

        return convertSolutionComponents(userId,
                                         openMetadataElements,
                                         true,
                                         asOfTime,
                                         forLineage,
                                         forDuplicateProcessing,
                                         effectiveTime,
                                         true,
                                         methodName);
    }


    /**
     * Retrieve the list of  metadata elements that are associated with the solution component via the ImplementedBy relationship.
     *
     * @param userId                calling user
     * @param solutionComponentGUID unique identifier of the solution component to query
     * @param limitResultsByStatus  control the status of the elements to retrieve - default is everything but Deleted
     * @param asOfTime              repository time to use
     * @param sequencingOrder       order to retrieve results
     * @param sequencingProperty    property to use for sequencing order
     * @param startFrom             paging start point
     * @param pageSize              maximum results that can be returned
     * @param forLineage             the retrieved elements are for lineage processing so include archived elements
     * @param forDuplicateProcessing the retrieved element is for duplicate processing so do not combine results from known duplicates.
     * @param effectiveTime          only return an element if it is effective at this time. Null means anytime. Use "new Date()" for now.
     * @return list of matching metadata elements
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<RelatedMetadataElementSummary> getSolutionComponentImplementations(String              userId,
                                                                                   String              solutionComponentGUID,
                                                                                   List<ElementStatus> limitResultsByStatus,
                                                                                   Date                asOfTime,
                                                                                   SequencingOrder     sequencingOrder,
                                                                                   String              sequencingProperty,
                                                                                   int                 startFrom,
                                                                                   int                 pageSize,
                                                                                   boolean             forLineage,
                                                                                   boolean             forDuplicateProcessing,
                                                                                   Date                effectiveTime) throws InvalidParameterException,
                                                                                                                             UserNotAuthorizedException,
                                                                                                                             PropertyServerException
    {
        final String methodName = "getSolutionComponentImplementations";
        final String parameterName = "solutionComponentGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(solutionComponentGUID, parameterName, methodName);
        invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        RelatedMetadataElementList relatedMetadataElements = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                solutionComponentGUID,
                                                                                                                1,
                                                                                                                OpenMetadataType.IMPLEMENTED_BY_RELATIONSHIP.typeName,
                                                                                                                limitResultsByStatus,
                                                                                                                asOfTime,
                                                                                                                sequencingProperty,
                                                                                                                sequencingOrder,
                                                                                                                forLineage,
                                                                                                                forDuplicateProcessing,
                                                                                                                effectiveTime,
                                                                                                                startFrom,
                                                                                                                pageSize);

        return convertSolutionComponentImplementations(relatedMetadataElements, methodName);
    }


    /**
     * Convert the open metadata elements retrieve into information supply chain elements.
     *
     * @param userId calling user
     * @param openMetadataElements elements extracted from the repository
     * @param addImplementation should details of the implementation of the information supply chain be extracted too?
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return list of information supply chains (or null)
     */
    private List<InformationSupplyChainElement> convertInformationSupplyChains(String                    userId,
                                                                               List<OpenMetadataElement> openMetadataElements,
                                                                               boolean                   addImplementation,
                                                                               Date                      asOfTime,
                                                                               boolean                   forLineage,
                                                                               boolean                   forDuplicateProcessing,
                                                                               Date                      effectiveTime,
                                                                               String                    methodName)
    {
        if (openMetadataElements != null)
        {
            List<InformationSupplyChainElement> informationSupplyChainElements = new ArrayList<>();

            for (OpenMetadataElement openMetadataElement : openMetadataElements)
            {
                if (openMetadataElement != null)
                {
                    informationSupplyChainElements.add(convertInformationSupplyChain(userId,
                                                                                     openMetadataElement,
                                                                                     addImplementation,
                                                                                     asOfTime,
                                                                                     forLineage,
                                                                                     forDuplicateProcessing,
                                                                                     effectiveTime,
                                                                                     methodName));
                }
            }

            return informationSupplyChainElements;
        }

        return null;
    }


    /**
     * Convert the open metadata elements retrieve into solution blueprint elements.
     *
     * @param userId calling user
     * @param openMetadataElements elements extracted from the repository
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return list of solution blueprints (or null)
     */
    private List<SolutionBlueprintElement> convertSolutionBlueprints(String                    userId,
                                                                     List<OpenMetadataElement> openMetadataElements,
                                                                     Date                      asOfTime,
                                                                     boolean                   forLineage,
                                                                     boolean                   forDuplicateProcessing,
                                                                     Date                      effectiveTime,
                                                                     String                    methodName)
    {
        if (openMetadataElements != null)
        {
            List<SolutionBlueprintElement> solutionBlueprintElements = new ArrayList<>();

            for (OpenMetadataElement openMetadataElement : openMetadataElements)
            {
                if (openMetadataElement != null)
                {
                    solutionBlueprintElements.add(convertSolutionBlueprint(userId,
                                                                           openMetadataElement,
                                                                           asOfTime,
                                                                           forLineage,
                                                                           forDuplicateProcessing,
                                                                           effectiveTime,
                                                                           methodName));
                }
            }

            return solutionBlueprintElements;
        }

        return null;
    }


    /**
     * Convert the open metadata elements retrieve into solution component elements.
     *
     * @param userId calling user
     * @param openMetadataElements elements extracted from the repository
     * @param addParentContext should parent information supply chains, segments and solution components be added?
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param fullDisplay print all elements
     * @param methodName calling method
     * @return list of solution components (or null)
     */
    private List<SolutionComponentElement> convertSolutionComponents(String                    userId,
                                                                     List<OpenMetadataElement> openMetadataElements,
                                                                     boolean                   addParentContext,
                                                                     Date                      asOfTime,
                                                                     boolean                   forLineage,
                                                                     boolean                   forDuplicateProcessing,
                                                                     Date                      effectiveTime,
                                                                     boolean                   fullDisplay,
                                                                     String                    methodName)
    {
        if (openMetadataElements != null)
        {
            List<SolutionComponentElement> solutionComponentElements = new ArrayList<>();

            for (OpenMetadataElement openMetadataElement : openMetadataElements)
            {
                if (openMetadataElement != null)
                {
                    solutionComponentElements.add(convertSolutionComponent(userId,
                                                                           openMetadataElement,
                                                                           addParentContext,
                                                                           asOfTime,
                                                                           forLineage,
                                                                           forDuplicateProcessing,
                                                                           effectiveTime,
                                                                           fullDisplay,
                                                                           methodName));
                }
            }

            return solutionComponentElements;
        }

        return null;
    }


    /**
     * Convert the open metadata elements retrieve into summary elements.
     *
     * @param relatedMetadataElementList elements retrieved from the repository
     * @param methodName calling method
     * @return list of summary elements (or null)
     */
    private List<RelatedMetadataElementSummary> convertSolutionComponentImplementations(RelatedMetadataElementList relatedMetadataElementList,
                                                                                        String                     methodName)
    {
        if ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
        {
            List<RelatedMetadataElementSummary> solutionComponentImplementationElements = new ArrayList<>();

            for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
            {
                if (relatedMetadataElement != null)
                {
                    solutionComponentImplementationElements.add(convertSolutionComponentImplementation(relatedMetadataElement, methodName));
                }
            }

            return solutionComponentImplementationElements;
        }

        return null;
    }


    /**
     * Return the information supply chain extracted from the open metadata element.
     *
     * @param userId calling user
     * @param openMetadataElement element extracted from the repository
     * @param addImplementation should details of the implementation of the information supply chain be extracted too?
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return bean or null
     */
    private InformationSupplyChainElement convertInformationSupplyChain(String              userId,
                                                                        OpenMetadataElement openMetadataElement,
                                                                        boolean             addImplementation,
                                                                        Date                asOfTime,
                                                                        boolean             forLineage,
                                                                        boolean             forDuplicateProcessing,
                                                                        Date                effectiveTime,
                                                                        String              methodName)
    {
        try
        {
            List<InformationSupplyChainSegmentElement> relatedSegments = new ArrayList<>();

            int startFrom = 0;
            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       openMetadataElement.getElementGUID(),
                                                                                                                       1,
                                                                                                                       OpenMetadataType.INFORMATION_SUPPLY_CHAIN_COMPOSITION_RELATIONSHIP.typeName,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        relatedSegments.add(this.convertInformationSupplyChainSegment(userId,
                                                                                      relatedMetadataElement.getElement(),
                                                                                      asOfTime,
                                                                                      forLineage,
                                                                                      forDuplicateProcessing,
                                                                                      effectiveTime,
                                                                                      methodName));
                    }
                }

                startFrom = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                openMetadataElement.getElementGUID(),
                                                                                                1,
                                                                                                OpenMetadataType.INFORMATION_SUPPLY_CHAIN_COMPOSITION_RELATIONSHIP.typeName,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            List<OpenMetadataRelationship> lineageRelationships = null;

            if (addImplementation)
            {
                lineageRelationships = new ArrayList<>();
                startFrom = 0;

                SearchProperties        searchProperties   = new SearchProperties();
                List<PropertyCondition> propertyConditions = new ArrayList<>();
                PropertyCondition       propertyCondition  = new PropertyCondition();

                propertyCondition.setProperty(OpenMetadataProperty.ISC_QUALIFIED_NAME.name);

                propertyCondition.setOperator(PropertyComparisonOperator.EQ);
                propertyCondition.setValue(openMetadataElement.getElementProperties().getPropertyValue(OpenMetadataProperty.QUALIFIED_NAME.name));

                propertyConditions.add(propertyCondition);

                searchProperties.setMatchCriteria(MatchCriteria.ANY);
                searchProperties.setConditions(propertyConditions);

                OpenMetadataRelationshipList relationshipList = openMetadataStoreClient.findRelationshipsBetweenMetadataElements(userId,
                                                                                                                                 null,
                                                                                                                                 searchProperties,
                                                                                                                                 null,
                                                                                                                                 asOfTime,
                                                                                                                                 null,
                                                                                                                                 SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                                 forLineage,
                                                                                                                                 forDuplicateProcessing,
                                                                                                                                 effectiveTime,
                                                                                                                                 startFrom,
                                                                                                                                 invalidParameterHandler.getMaxPagingSize());

                while ((relationshipList != null) && (relationshipList.getElementList() != null))
                {
                    lineageRelationships.addAll(relationshipList.getElementList());

                    startFrom = startFrom + invalidParameterHandler.getMaxPagingSize();

                    relationshipList = openMetadataStoreClient.findRelationshipsBetweenMetadataElements(userId,
                                                                                                        null,
                                                                                                        searchProperties,
                                                                                                        null,
                                                                                                        asOfTime,
                                                                                                        null,
                                                                                                        SequencingOrder.CREATION_DATE_RECENT,
                                                                                                        forLineage,
                                                                                                        forDuplicateProcessing,
                                                                                                        effectiveTime,
                                                                                                        startFrom,
                                                                                                        invalidParameterHandler.getMaxPagingSize());
                }
            }


            InformationSupplyChainConverter<InformationSupplyChainElement> converter = new InformationSupplyChainConverter<>(propertyHelper, serviceName, serverName, relatedSegments, lineageRelationships);
            InformationSupplyChainElement informationSupplyChainElement = converter.getNewBean(InformationSupplyChainElement.class,
                                                                                               openMetadataElement,
                                                                                               methodName);
            if (informationSupplyChainElement != null)
            {
                InformationSupplyChainMermaidGraphBuilder graphBuilder = new InformationSupplyChainMermaidGraphBuilder(informationSupplyChainElement);

                informationSupplyChainElement.setMermaidGraph(graphBuilder.getMermaidGraph(false));
            }

            return informationSupplyChainElement;
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }

            return null;
        }
    }


    /**
     * Return the information supply chain extracted from the open metadata element.
     *
     * @param userId calling user
     * @param openMetadataElement element extracted from the repository
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return bean or null
     */
    private InformationSupplyChainSegmentElement convertInformationSupplyChainSegment(String              userId,
                                                                                      OpenMetadataElement openMetadataElement,
                                                                                      Date                asOfTime,
                                                                                      boolean             forLineage,
                                                                                      boolean             forDuplicateProcessing,
                                                                                      Date                effectiveTime,
                                                                                      String              methodName)
    {
        try
        {
            List<RelatedMetadataElement>        relatedMetadataElements      = new ArrayList<>();
            List<RelatedMetadataElementSummary> relatedPortElements          = new ArrayList<>();
            List<SolutionLinkingWireRelationship> solutionLinkingWires       = new ArrayList<>();

            int startFrom = 0;
            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       openMetadataElement.getElementGUID(),
                                                                                                                       1,
                                                                                                                       null,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        if (propertyHelper.isTypeOf(relatedMetadataElement.getElement(), OpenMetadataType.SOLUTION_COMPONENT.typeName))
                        {
                            List<RelatedMetadataElementSummary> componentPorts = this.convertPortSummaries(userId,
                                                                                                           relatedMetadataElement.getElement().getElementGUID(),
                                                                                                           asOfTime,
                                                                                                           forLineage,
                                                                                                           forDuplicateProcessing,
                                                                                                           effectiveTime,
                                                                                                           methodName);

                            if (componentPorts != null)
                            {
                                relatedPortElements.addAll(componentPorts);
                            }

                            List<SolutionLinkingWireRelationship> solutionLinkingWireRelationships = this.convertSolutionLinkingWires(userId,
                                                                                                                                      relatedMetadataElement.getElement(),
                                                                                                                                      asOfTime,
                                                                                                                                      forLineage,
                                                                                                                                      forDuplicateProcessing,
                                                                                                                                      effectiveTime,
                                                                                                                                      methodName);

                            if (solutionLinkingWireRelationships != null)
                            {
                                solutionLinkingWires.addAll(solutionLinkingWireRelationships);
                            }
                        }

                        relatedMetadataElements.add(relatedMetadataElement);
                    }
                }

                startFrom = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                openMetadataElement.getElementGUID(),
                                                                                                1,
                                                                                                null,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            InformationSupplyChainSegmentConverter<InformationSupplyChainSegmentElement> converter = new InformationSupplyChainSegmentConverter<>(propertyHelper, serviceName, serverName, solutionLinkingWires, relatedPortElements);
            return converter.getNewComplexBean(InformationSupplyChainSegmentElement.class,
                                               openMetadataElement,
                                               relatedMetadataElements,
                                               methodName);
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }

            return null;
        }
    }


    /**
     * Return the port summaries for a solution component.
     *
     * @param userId calling user
     * @param solutionComponent starting entity
     * @param asOfTime repository time to use
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return list
     */
    private List<SolutionLinkingWireRelationship> convertSolutionLinkingWires(String              userId,
                                                                              OpenMetadataElement solutionComponent,
                                                                              Date                asOfTime,
                                                                              boolean             forLineage,
                                                                              boolean             forDuplicateProcessing,
                                                                              Date                effectiveTime,
                                                                              String              methodName)
    {
        try
        {
            OpenMetadataConverterBase<ElementStub> wireConverter                    = new OpenMetadataConverterBase<>(propertyHelper, serviceName, serverName);
            List<SolutionLinkingWireRelationship>  solutionLinkingWireRelationships = new ArrayList<>();

            ElementStub solutionComponentStub = wireConverter.getElementStub(ElementStub.class, solutionComponent, methodName);

            int startFrom = 0;

            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       solutionComponent.getElementGUID(),
                                                                                                                       0,
                                                                                                                       OpenMetadataType.SOLUTION_LINKING_WIRE_RELATIONSHIP.typeName,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        SolutionLinkingWireRelationship wire = new SolutionLinkingWireRelationship();

                        wire.setElementHeader(wireConverter.getMetadataElementHeader(ElementStub.class,
                                                                                     relatedMetadataElement,
                                                                                     relatedMetadataElement.getRelationshipGUID(),
                                                                                     null,
                                                                                     methodName));

                        if (relatedMetadataElement.getRelationshipProperties() != null)
                        {
                            SolutionLinkingWireProperties wireProperties = new SolutionLinkingWireProperties();

                            ElementProperties elementProperties = new ElementProperties(relatedMetadataElement.getRelationshipProperties());

                            wireProperties.setLabel(wireConverter.removeLabel(elementProperties));
                            wireProperties.setDescription(wireConverter.removeDescription(elementProperties));
                            wireProperties.setInformationSupplyChainSegmentGUIDs(wireConverter.removeInformationSupplyChainSegmentGUIDs(elementProperties));
                            wireProperties.setExtendedProperties(wireConverter.getRemainingExtendedProperties(elementProperties));
                            wireProperties.setEffectiveFrom(relatedMetadataElement.getEffectiveFromTime());
                            wireProperties.setEffectiveTo(relatedMetadataElement.getEffectiveToTime());

                            wire.setProperties(wireProperties);
                        }
                        ElementStub otherEndStub = wireConverter.getElementStub(ElementStub.class, relatedMetadataElement.getElement(), methodName);

                        if (relatedMetadataElement.getElementAtEnd1())
                        {
                            wire.setEnd1Element(otherEndStub);
                            wire.setEnd2Element(solutionComponentStub);
                        }
                        else
                        {
                            wire.setEnd1Element(solutionComponentStub);
                            wire.setEnd2Element(otherEndStub);
                        }

                        solutionLinkingWireRelationships.add(wire);
                    }
                }

                startFrom                  = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                solutionComponent.getElementGUID(),
                                                                                                0,
                                                                                                OpenMetadataType.SOLUTION_LINKING_WIRE_RELATIONSHIP.typeName,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            if (! solutionLinkingWireRelationships.isEmpty())
            {
                return solutionLinkingWireRelationships;
            }
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }
        }

        return null;
    }


    /**
     * Return the port summaries for a solution component.
     *
     * @param userId calling user
     * @param solutionComponentGUID starting guid
     * @param asOfTime repository time to use
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return list
     */
    private List<RelatedMetadataElementSummary> convertPortSummaries(String  userId,
                                                                     String  solutionComponentGUID,
                                                                     Date    asOfTime,
                                                                     boolean forLineage,
                                                                     boolean forDuplicateProcessing,
                                                                     Date    effectiveTime,
                                                                     String  methodName)
    {
        try
        {
            OpenMetadataConverterBase<RelatedMetadataElementSummary> portConverter = new OpenMetadataConverterBase<>(propertyHelper, serviceName, serverName);
            List<RelatedMetadataElementSummary>                      portSummaries = new ArrayList<>();

            int startFrom = 0;

            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       solutionComponentGUID,
                                                                                                                       1,
                                                                                                                       OpenMetadataType.SOLUTION_COMPONENT_PORT_RELATIONSHIP.typeName,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (propertyHelper.isTypeOf(relatedMetadataElement.getElement(), OpenMetadataType.SOLUTION_COMPONENT.typeName))
                    {
                        portSummaries.add(portConverter.getRelatedElementSummary(RelatedMetadataElementSummary.class, relatedMetadataElement, methodName));
                    }
                }

                startFrom                  = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                solutionComponentGUID,
                                                                                                1,
                                                                                                OpenMetadataType.SOLUTION_COMPONENT_PORT_RELATIONSHIP.typeName,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            if (! portSummaries.isEmpty())
            {
                return portSummaries;
            }
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }
        }

        return null;
    }


    /**
     * Return the solution blueprint extracted from the open metadata element.
     *
     * @param userId calling user
     * @param openMetadataElement element extracted from the repository
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return bean or null
     */
    private SolutionBlueprintElement convertSolutionBlueprint(String              userId,
                                                              OpenMetadataElement openMetadataElement,
                                                              Date                asOfTime,
                                                              boolean             forLineage,
                                                              boolean             forDuplicateProcessing,
                                                              Date                effectiveTime,
                                                              String              methodName)
    {
        try
        {
            List<SolutionBlueprintComponent> solutionBlueprintComponents = new ArrayList<>();
            OpenMetadataConverterBase<SolutionBlueprintComponent> solutionBlueprintConverter = new OpenMetadataConverterBase<>(propertyHelper, serviceName, serverName);

            int startFrom = 0;
            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       openMetadataElement.getElementGUID(),
                                                                                                                       1,
                                                                                                                       OpenMetadataType.SOLUTION_BLUEPRINT_COMPOSITION_RELATIONSHIP.typeName,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        SolutionBlueprintComponent solutionBlueprintComponent = new SolutionBlueprintComponent();

                        solutionBlueprintComponent.setElementHeader(solutionBlueprintConverter.getMetadataElementHeader(SolutionBlueprintComponent.class,
                                                                                                                        relatedMetadataElement,
                                                                                                                        relatedMetadataElement.getRelationshipGUID(),
                                                                                                                        null,
                                                                                                                        methodName));
                        SolutionBlueprintCompositionProperties relationshipProperties = new SolutionBlueprintCompositionProperties();
                        ElementProperties                      elementProperties      = new ElementProperties(relatedMetadataElement.getRelationshipProperties());

                        relationshipProperties.setRole(solutionBlueprintConverter.removeRole(elementProperties));
                        relationshipProperties.setDescription(solutionBlueprintConverter.removeDescription(elementProperties));
                        relationshipProperties.setEffectiveFrom(relatedMetadataElement.getEffectiveFromTime());
                        relationshipProperties.setEffectiveTo(relatedMetadataElement.getEffectiveToTime());
                        relationshipProperties.setExtendedProperties(solutionBlueprintConverter.getRemainingExtendedProperties(elementProperties));

                        solutionBlueprintComponent.setProperties(relationshipProperties);
                        solutionBlueprintComponent.setSolutionComponent(this.convertSolutionComponent(userId,
                                                                                                      relatedMetadataElement.getElement(),
                                                                                                      false,
                                                                                                      asOfTime,
                                                                                                      forLineage,
                                                                                                      forDuplicateProcessing,
                                                                                                      effectiveTime,
                                                                                                      false,
                                                                                                      methodName));
                        solutionBlueprintComponents.add(solutionBlueprintComponent);
                    }
                }

                startFrom = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                openMetadataElement.getElementGUID(),
                                                                                                1,
                                                                                                OpenMetadataType.SOLUTION_BLUEPRINT_COMPOSITION_RELATIONSHIP.typeName,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            SolutionBlueprintConverter<SolutionBlueprintElement> converter                = new SolutionBlueprintConverter<>(propertyHelper, serviceName, serverName, solutionBlueprintComponents);
            SolutionBlueprintElement                             solutionBlueprintElement =  converter.getNewBean(SolutionBlueprintElement.class, openMetadataElement, methodName);

            if (solutionBlueprintElement != null)
            {
                SolutionBlueprintMermaidGraphBuilder graphBuilder = new SolutionBlueprintMermaidGraphBuilder(solutionBlueprintElement);

                solutionBlueprintElement.setMermaidGraph(graphBuilder.getMermaidGraph());
            }

            return solutionBlueprintElement;
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }

            return null;
        }
    }


    /**
     * Return the solution component extracted from the open metadata element.
     *
     * @param userId calling user
     * @param openMetadataElement element extracted from the repository
     * @param addParentContext should parent information supply chains, segments and solution components be added?
     * @param asOfTime repository time to use
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity dating for elements
     * @param fullDisplay print all elements
     * @param methodName calling method
     * @return bean or null
     */
    private SolutionComponentElement convertSolutionComponent(String              userId,
                                                              OpenMetadataElement openMetadataElement,
                                                              boolean             addParentContext,
                                                              Date                asOfTime,
                                                              boolean             forLineage,
                                                              boolean             forDuplicateProcessing,
                                                              Date                effectiveTime,
                                                              boolean             fullDisplay,
                                                              String              methodName)
    {
        try
        {
            List<RelatedMetadataElement>   relatedMetadataElements        = new ArrayList<>();
            List<RelatedMetadataElement>   relatedParentElements          = new ArrayList<>();
            List<SolutionPortElement>      relatedPortElements            = new ArrayList<>();
            List<SolutionComponentElement> relatedSubComponentsElements   = new ArrayList<>();

            int startFrom = 0;
            RelatedMetadataElementList relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                                       openMetadataElement.getElementGUID(),
                                                                                                                       0,
                                                                                                                       null,
                                                                                                                       null,
                                                                                                                       asOfTime,
                                                                                                                       null,
                                                                                                                       SequencingOrder.CREATION_DATE_RECENT,
                                                                                                                       forLineage,
                                                                                                                       forDuplicateProcessing,
                                                                                                                       effectiveTime,
                                                                                                                       startFrom,
                                                                                                                       invalidParameterHandler.getMaxPagingSize());
            while ((relatedMetadataElementList != null) && (relatedMetadataElementList.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.SOLUTION_COMPOSITION_RELATIONSHIP.typeName))
                        {
                            if (relatedMetadataElement.getElementAtEnd1())
                            {
                                relatedParentElements.add(relatedMetadataElement);
                            }
                            else
                            {
                                relatedSubComponentsElements.add(this.convertSolutionComponent(userId,
                                                                                               relatedMetadataElement.getElement(),
                                                                                               addParentContext,
                                                                                               asOfTime,
                                                                                               forLineage,
                                                                                               forDuplicateProcessing,
                                                                                               effectiveTime,
                                                                                               fullDisplay,
                                                                                               methodName));
                            }
                        }
                        else if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.IMPLEMENTED_BY_RELATIONSHIP.typeName))
                        {
                            if (relatedMetadataElement.getElementAtEnd1())
                            {
                                relatedParentElements.add(relatedMetadataElement);
                            }
                            else
                            {
                                relatedMetadataElements.add(relatedMetadataElement);
                            }
                        }
                        else if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.SOLUTION_COMPONENT_PORT_RELATIONSHIP.typeName))
                        {
                            relatedPortElements.add(this.convertSolutionPort(userId, relatedMetadataElement, asOfTime, effectiveTime, methodName));
                        }
                        else
                        {
                            relatedMetadataElements.add(relatedMetadataElement);
                        }
                    }
                }

                startFrom = startFrom + invalidParameterHandler.getMaxPagingSize();
                relatedMetadataElementList = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                openMetadataElement.getElementGUID(),
                                                                                                0,
                                                                                                null,
                                                                                                null,
                                                                                                asOfTime,
                                                                                                null,
                                                                                                SequencingOrder.CREATION_DATE_RECENT,
                                                                                                forLineage,
                                                                                                forDuplicateProcessing,
                                                                                                effectiveTime,
                                                                                                startFrom,
                                                                                                invalidParameterHandler.getMaxPagingSize());
            }

            SolutionComponentConverter<SolutionComponentElement> converter = new SolutionComponentConverter<>(propertyHelper, serviceName, serverName, relatedSubComponentsElements, relatedPortElements);
            SolutionComponentElement solutionComponentElement = converter.getNewComplexBean(SolutionComponentElement.class,
                                                                                            openMetadataElement,
                                                                                            relatedMetadataElements,
                                                                                            methodName);

            if (solutionComponentElement != null)
            {
                if (addParentContext)
                {
                    solutionComponentElement.setContext(this.getInformationSupplyChainContext(userId,
                                                                                              relatedParentElements,
                                                                                              asOfTime,
                                                                                              forLineage,
                                                                                              forDuplicateProcessing,
                                                                                              effectiveTime,
                                                                                              methodName));
                }

                SolutionComponentMermaidGraphBuilder graphBuilder = new SolutionComponentMermaidGraphBuilder(solutionComponentElement,
                                                                                                             fullDisplay);

                solutionComponentElement.setMermaidGraph(graphBuilder.getMermaidGraph());
            }

            return solutionComponentElement;
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }

            return null;
        }
    }


    /**
     * Navigate through the ImplementedBy and ImplementationSupplyChainComposition relationships to locate
     * the Information Supply Chains associated with the originally requested element
     * @param userId calling userId
     * @param relatedParentElements list of parents for requested element
     * @param asOfTime repository time
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effective time for subsequent queries
     * @param methodName calling method
     * @return supply chain context
     * @throws PropertyServerException problem in converter
     */
    private List<InformationSupplyChainContext> getInformationSupplyChainContext(String                       userId,
                                                                                 List<RelatedMetadataElement> relatedParentElements,
                                                                                 Date                         asOfTime,
                                                                                 boolean                      forLineage,
                                                                                 boolean                      forDuplicateProcessing,
                                                                                 Date                         effectiveTime,
                                                                                 String                       methodName) throws PropertyServerException
    {
        if (relatedParentElements != null)
        {
            RelatedMetadataElementSummaryConverter<RelatedMetadataElementSummary> converter = new RelatedMetadataElementSummaryConverter<>(propertyHelper,
                                                                                                                                           serviceName,
                                                                                                                                           serverName);
            List<InformationSupplyChainContext> contexts = new ArrayList<>();

            for (RelatedMetadataElement parentElement : relatedParentElements)
            {
                if (parentElement != null)
                {
                    if (propertyHelper.isTypeOf(parentElement.getElement(), OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName))
                    {
                        InformationSupplyChainContext context = new InformationSupplyChainContext(null,
                                                                                                  null,
                                                                                                  converter.getNewBean(RelatedMetadataElementSummary.class,
                                                                                                                       parentElement,
                                                                                                                       methodName));
                        contexts.add(context);
                    }
                    else
                    {
                        List<RelatedMetadataElement> fullParentContext = this.getFullParentContext(userId, parentElement, asOfTime, forLineage, forDuplicateProcessing, effectiveTime, methodName);
                        RelatedMetadataElementSummary informationSupplyChain = null;
                        RelatedMetadataElementSummary linkedSegment = null;
                        List<RelatedMetadataElementSummary> parentComponents = new ArrayList<>();

                        for (RelatedMetadataElement relatedMetadataElement : fullParentContext)
                        {
                            if (relatedMetadataElement != null)
                            {
                                RelatedMetadataElementSummary bean = converter.getNewBean(RelatedMetadataElementSummary.class,
                                                                                          relatedMetadataElement,
                                                                                          methodName);


                                if (propertyHelper.isTypeOf(relatedMetadataElement.getElement(), OpenMetadataType.INFORMATION_SUPPLY_CHAIN_SEGMENT.typeName))
                                {
                                    linkedSegment = bean;
                                }
                                else if (propertyHelper.isTypeOf(relatedMetadataElement.getElement(), OpenMetadataType.INFORMATION_SUPPLY_CHAIN.typeName))
                                {
                                    informationSupplyChain = bean;
                                }
                                else
                                {
                                    parentComponents.add(bean);
                                }
                            }
                        }

                        if (parentComponents.isEmpty())
                        {
                            parentComponents = null;
                        }

                        InformationSupplyChainContext context = new InformationSupplyChainContext(parentComponents,
                                                                                                  linkedSegment,
                                                                                                  informationSupplyChain);
                        contexts.add(context);
                    }
                }
            }

            return contexts;
        }

        return null;
    }


    /**
     * Retrieve the context in which this component is used.
     *
     * @param userId caller
     * @param initialParentElement first parent element
     * @param asOfTime repository retrieval time
     * @param forLineage the query is to support lineage retrieval
     * @param forDuplicateProcessing the query is for duplicate processing and so must not deduplicate
     * @param effectiveTime effectivity data
     * @param methodName calling method
     * @return list of parent elements (including the starting one)
     */
    private List<RelatedMetadataElement> getFullParentContext(String                 userId,
                                                              RelatedMetadataElement initialParentElement,
                                                              Date                   asOfTime,
                                                              boolean                forLineage,
                                                              boolean                forDuplicateProcessing,
                                                              Date                   effectiveTime,
                                                              String                 methodName)
    {
        List<RelatedMetadataElement> fullParentContext = new ArrayList<>();

        fullParentContext.add(initialParentElement);

        try
        {
            RelatedMetadataElementList additionalParents = openMetadataStoreClient.getRelatedMetadataElements(userId,
                                                                                                              initialParentElement.getElement().getElementGUID(),
                                                                                                              2,
                                                                                                              null,
                                                                                                              null,
                                                                                                              asOfTime,
                                                                                                              null,
                                                                                                              SequencingOrder.CREATION_DATE_RECENT,
                                                                                                              forLineage,
                                                                                                              forDuplicateProcessing,
                                                                                                              effectiveTime,
                                                                                                              0,
                                                                                                              0);

            if ((additionalParents != null) && (additionalParents.getElementList() != null))
            {
                for (RelatedMetadataElement relatedMetadataElement : additionalParents.getElementList())
                {
                    if (relatedMetadataElement != null)
                    {
                        if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.INFORMATION_SUPPLY_CHAIN_COMPOSITION_RELATIONSHIP.typeName))
                        {
                            fullParentContext.add(relatedMetadataElement);
                        }
                        else
                        {
                            if (propertyHelper.isTypeOf(relatedMetadataElement, OpenMetadataType.IMPLEMENTED_BY_RELATIONSHIP.typeName))
                            {
                                List<RelatedMetadataElement> higherParents = this.getFullParentContext(userId, relatedMetadataElement, asOfTime, forLineage, forDuplicateProcessing, effectiveTime, methodName);

                                fullParentContext.addAll(higherParents);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception error)
        {
            auditLog.logMessage(methodName, OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                           methodName,
                                                                                                                           error.getMessage()));
        }

        return fullParentContext;
    }


    /**
     * Return the solution port extracted from the open metadata element.
     *
     * @param userId calling user
     * @param openMetadataElement element extracted from the repository
     * @param asOfTime repository time to use
     * @param effectiveTime effectivity dating for elements
     * @param methodName calling method
     * @return bean or null
     */
    private SolutionPortElement convertSolutionPort(String                 userId,
                                                    RelatedMetadataElement openMetadataElement,
                                                    Date                   asOfTime,
                                                    Date                   effectiveTime,
                                                    String                 methodName)
    {
        // todo
        return null;
    }


    /**
     * Return the solution component implementation extracted from the open metadata element.
     *
     * @param relatedMetadataElement element extracted from the repository
     * @param methodName calling method
     * @return bean or null
     */
    private RelatedMetadataElementSummary convertSolutionComponentImplementation(RelatedMetadataElement relatedMetadataElement,
                                                                                 String                 methodName)
    {
        RelatedMetadataElementSummaryConverter<RelatedMetadataElementSummary> converter = new RelatedMetadataElementSummaryConverter<>(propertyHelper,
                                                                                                                                       serviceName,
                                                                                                                                       serverName);
        try
        {
            return converter.getRelatedElementSummary(RelatedMetadataElementSummary.class,
                                                      relatedMetadataElement,
                                                      methodName);
        }
        catch (PropertyServerException error)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    OpenMetadataStoreAuditCode.UNEXPECTED_CONVERTER_EXCEPTION.getMessageDefinition(error.getClass().getName(),
                                                                                                                   methodName,
                                                                                                                   error.getMessage()));
            }

            return null;
        }
    }


    /*
     * Mapping functions
     */


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(InformationSupplyChainProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                                   properties.getQualifiedName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DISPLAY_NAME.name,
                                                                 properties.getDisplayName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.SCOPE.name,
                                                                 properties.getScope());

            elementProperties = propertyHelper.addStringArrayProperty(elementProperties,
                                                                      OpenMetadataProperty.PURPOSES.name,
                                                                      properties.getPurposes());

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                    properties.getAdditionalProperties());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(InformationSupplyChainSegmentProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                                   properties.getQualifiedName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DISPLAY_NAME.name,
                                                                 properties.getDisplayName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.SCOPE.name,
                                                                 properties.getScope());

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ESTIMATED_VOLUMETRICS.name,
                                                                    properties.getEstimatedVolumetrics());

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                    properties.getAdditionalProperties());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(InformationSupplyChainLinkProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.LABEL.name,
                                                                                   properties.getLabel());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionBlueprintProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                                   properties.getQualifiedName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DISPLAY_NAME.name,
                                                                 properties.getDisplayName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.VERSION.name,
                                                                 properties.getVersion());

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                    properties.getAdditionalProperties());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionBlueprintCompositionProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.ROLE.name,
                                                                                   properties.getRole());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionComponentProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                                   properties.getQualifiedName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DISPLAY_NAME.name,
                                                                 properties.getDisplayName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.VERSION.name,
                                                                 properties.getVersion());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.SOLUTION_COMPONENT_TYPE.name,
                                                                 properties.getSolutionComponentType());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.PLANNED_DEPLOYED_IMPLEMENTATION_TYPE.name,
                                                                 properties.getPlannedDeployedImplementationType());

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                    properties.getAdditionalProperties());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionLinkingWireProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.LABEL.name,
                                                                                   properties.getLabel());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringArrayProperty(elementProperties,
                                                                      OpenMetadataProperty.INFORMATION_SUPPLY_CHAIN_SEGMENTS_GUIDS.name,
                                                                      properties.getInformationSupplyChainSegmentGUIDs());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionPortProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                                   properties.getQualifiedName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DISPLAY_NAME.name,
                                                                 properties.getDisplayName());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.VERSION.name,
                                                                 properties.getVersion());

            if (properties.getSolutionPortDirection() != null)
            {
                elementProperties = propertyHelper.addEnumProperty(elementProperties,
                                                                   OpenMetadataProperty.DIRECTION.name,
                                                                   SolutionPortDirection.getOpenTypeName(),
                                                                   properties.getSolutionPortDirection().getName());
            }

            elementProperties = propertyHelper.addStringMapProperty(elementProperties,
                                                                    OpenMetadataProperty.ADDITIONAL_PROPERTIES.name,
                                                                    properties.getAdditionalProperties());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }


    /**
     * Convert the specific properties into a set of element properties for the open metadata client.
     *
     * @param properties supplied properties
     * @return element properties
     */
    private ElementProperties getElementProperties(SolutionComponentActorProperties properties)
    {
        if (properties != null)
        {
            ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                                   OpenMetadataProperty.ROLE.name,
                                                                                   properties.getRole());

            elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                                 OpenMetadataProperty.DESCRIPTION.name,
                                                                 properties.getDescription());

            elementProperties = propertyHelper.addPropertyMap(elementProperties,
                                                              properties.getExtendedProperties());

            return elementProperties;
        }

        return null;
    }
}
