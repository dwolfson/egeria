/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.viewservices.connectionmaker.server.spring;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.odpi.openmetadata.commonservices.ffdc.rest.*;
import org.odpi.openmetadata.frameworkservices.omf.rest.AnyTimeRequestBody;
import org.odpi.openmetadata.viewservices.connectionmaker.server.ConnectionMakerRESTServices;
import org.springframework.web.bind.annotation.*;


/**
 * The ConnectionMakerResource provides part of the server-side implementation of the Connection Maker OMVS.
 = */
@RestController
@RequestMapping("/servers/{serverName}/api/open-metadata/{urlMarker}")

@Tag(name="API: Connection Maker OMVS", description="The Connection Maker OMVS provides APIs for supporting the creation and editing of connections, connectorTypes and endpoints.",
        externalDocs=@ExternalDocumentation(description="Further Information",
                url="https://egeria-project.org/services/omvs/connection-maker/overview/"))

public class ConnectionMakerResource
{
    private final ConnectionMakerRESTServices restAPI = new ConnectionMakerRESTServices();

    /**
     * Default constructor
     */
    public ConnectionMakerResource()
    {
    }


    /**
     * Create a connection.
     *
     * @param serverName                 name of called server.
     * @param urlMarker  view service URL marker
     * @param requestBody             properties for the connection.
     *
     * @return unique identifier of the newly created element
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connections")

    @Operation(summary="createConnection",
            description="Create a connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public GUIDResponse createConnection(@PathVariable String                               serverName,
                                           @PathVariable String             urlMarker,
                                           @RequestBody (required = false)
                                           NewElementRequestBody requestBody)
    {
        return restAPI.createConnection(serverName, urlMarker, requestBody);
    }


    /**
     * Create a new metadata element to represent a connection using an existing metadata element as a template.
     * The template defines additional classifications and relationships that should be added to the new element.
     *
     * @param serverName             calling user
     * @param urlMarker  view service URL marker
     * @param requestBody properties that override the template
     *
     * @return unique identifier of the new metadata element
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connections/from-template")
    @Operation(summary="createConnectionFromTemplate",
            description="Create a new metadata element to represent a connection using an existing metadata element as a template.  The template defines additional classifications and relationships that should be added to the new element.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public GUIDResponse createConnectionFromTemplate(@PathVariable
                                                       String              serverName,
                                                       @PathVariable String             urlMarker,
                                                       @RequestBody (required = false)
                                                       TemplateRequestBody requestBody)
    {
        return restAPI.createConnectionFromTemplate(serverName, urlMarker, requestBody);
    }


    /**
     * Update the properties of a connection.
     *
     * @param serverName         name of called server.
     * @param urlMarker  view service URL marker
     * @param connectionGUID unique identifier of the connection (returned from create)
     * @param replaceAllProperties flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                          the individual properties specified on the request.
     * @param requestBody     properties for the new element.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connections/{connectionGUID}/update")
    @Operation(summary="updateConnection",
            description="Update the properties of a connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public VoidResponse updateConnection(@PathVariable
                                           String                                  serverName,
                                           @PathVariable String             urlMarker,
                                           @PathVariable
                                           String                                  connectionGUID,
                                           @RequestParam (required = false, defaultValue = "false")
                                           boolean                                 replaceAllProperties,
                                           @RequestBody (required = false)
                                           UpdateElementRequestBody requestBody)
    {
        return restAPI.updateConnection(serverName, urlMarker, connectionGUID, replaceAllProperties, requestBody);
    }


    /**
     * Attach an asset to a connection.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param assetGUID       unique identifier of the asset
     * @param connectionGUID            unique identifier of the connection
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/assets/{assetGUID}/connections/{connectionGUID}/attach")
    @Operation(summary="linkAssetToConnection",
            description="Attach an asset to a connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public VoidResponse linkAssetToConnection(@PathVariable
                                           String                     serverName,
                                           @PathVariable String             urlMarker,
                                           @PathVariable
                                           String assetGUID,
                                           @PathVariable
                                           String connectionGUID,
                                           @RequestBody (required = false)
                                           RelationshipRequestBody requestBody)
    {
        return restAPI.linkAssetToConnection(serverName, urlMarker, assetGUID, connectionGUID, requestBody);
    }


    /**
     * Detach an asset from a connection.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param assetGUID       unique identifier of the asset
     * @param connectionGUID            unique identifier of the IT profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/assets/{assetGUID}/connections/{connectionGUID}/detach")
    @Operation(summary="detachAssetFromConnection",
            description="Detach an asset from a connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public VoidResponse detachAssetFromConnection(@PathVariable
                                               String                    serverName,
                                               @PathVariable String             urlMarker,
                                               @PathVariable
                                               String assetGUID,
                                               @PathVariable
                                               String connectionGUID,
                                               @RequestBody (required = false)
                                               MetadataSourceRequestBody requestBody)
    {
        return restAPI.detachAssetFromConnection(serverName, urlMarker, assetGUID, connectionGUID, requestBody);
    }


    /**
     * Delete a connection.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param connectionGUID  unique identifier of the element to delete
     * @param cascadedDelete ca connections be deleted if data fields are attached?
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connections/{connectionGUID}/delete")
    @Operation(summary="deleteConnection",
            description="Delete a connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public VoidResponse deleteConnection(@PathVariable
                                           String                    serverName,
                                           @PathVariable String             urlMarker,
                                           @PathVariable
                                           String                    connectionGUID,
                                           @RequestParam(required = false, defaultValue = "false")
                                           boolean                   cascadedDelete,
                                           @RequestBody (required = false)
                                           MetadataSourceRequestBody requestBody)
    {
        return restAPI.deleteConnection(serverName, urlMarker, connectionGUID, cascadedDelete, requestBody);
    }


    /**
     * Returns the list of connections with a particular name.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connections/by-name")
    @Operation(summary="getConnectionsByName",
            description="Returns the list of connections with a particular name.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public ConnectionsResponse getConnectionsByName(@PathVariable
                                                        String            serverName,
                                                        @PathVariable String             urlMarker,
                                                        @RequestParam (required = false, defaultValue = "0")
                                                        int                     startFrom,
                                                        @RequestParam (required = false, defaultValue = "0")
                                                        int                     pageSize,
                                                        @RequestBody (required = false)
                                                        FilterRequestBody requestBody)
    {
        return restAPI.getConnectionsByName(serverName, urlMarker, startFrom, pageSize, requestBody);
    }


    /**
     * Retrieve the list of connection metadata elements that contain the search string.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startsWith does the value start with the supplied string?
     * @param endsWith does the value end with the supplied string?
     * @param ignoreCase should the search ignore case?
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connections/by-search-string")
    @Operation(summary="findConnections",
            description="Retrieve the list of connection metadata elements that contain the search string.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public ConnectionsResponse findConnections(@PathVariable
                                                   String                  serverName,
                                                   @PathVariable String             urlMarker,
                                                   @RequestParam (required = false, defaultValue = "0")
                                                   int                     startFrom,
                                                   @RequestParam (required = false, defaultValue = "0")
                                                   int                     pageSize,
                                                   @RequestParam (required = false, defaultValue = "false")
                                                   boolean                 startsWith,
                                                   @RequestParam (required = false, defaultValue = "false")
                                                   boolean                 endsWith,
                                                   @RequestParam (required = false, defaultValue = "false")
                                                   boolean                 ignoreCase,
                                                   @RequestBody (required = false)
                                                   FilterRequestBody requestBody)
    {
        return restAPI.findConnections(serverName, urlMarker, startsWith, endsWith, ignoreCase, startFrom, pageSize, requestBody);
    }


    /**
     * Return the properties of a specific connection.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param connectionGUID    unique identifier of the required element
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connections/{connectionGUID}/retrieve")
    @Operation(summary="getConnectionByGUID",
            description="Return the properties of a specific connection.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connection"))

    public ConnectionResponse getConnectionByGUID(@PathVariable
                                                      String             serverName,
                                                      @PathVariable String             urlMarker,
                                                      @PathVariable
                                                      String             connectionGUID,
                                                      @RequestBody (required = false)
                                                      AnyTimeRequestBody requestBody)
    {
        return restAPI.getConnectionByGUID(serverName, urlMarker, connectionGUID, requestBody);
    }


    /**
     * Create a connectorType.
     *
     * @param serverName                 name of called server.
     * @param urlMarker  view service URL marker
     * @param requestBody             properties for the connectorType.
     *
     * @return unique identifier of the newly created element
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types")

    @Operation(summary="createConnectorType",
            description="Create a connectorType.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public GUIDResponse createConnectorType(@PathVariable String                               serverName,
                                        @PathVariable String             urlMarker,
                                        @RequestBody (required = false)
                                        NewElementRequestBody requestBody)
    {
        return restAPI.createConnectorType(serverName, urlMarker, requestBody);
    }


    /**
     * Create a new metadata element to represent a connectorType using an existing metadata element as a template.
     * The template defines additional classifications and relationships that should be added to the new element.
     *
     * @param serverName             calling user
     * @param urlMarker  view service URL marker
     * @param requestBody properties that override the template
     *
     * @return unique identifier of the new metadata element
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connector-types/from-template")
    @Operation(summary="createConnectorTypeFromTemplate",
            description="Create a new metadata element to represent a connectorType using an existing metadata element as a template.  The template defines additional classifications and relationships that should be added to the new element.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public GUIDResponse createConnectorTypeFromTemplate(@PathVariable
                                                    String              serverName,
                                                    @PathVariable String             urlMarker,
                                                    @RequestBody (required = false)
                                                    TemplateRequestBody requestBody)
    {
        return restAPI.createConnectorTypeFromTemplate(serverName, urlMarker, requestBody);
    }


    /**
     * Update the properties of a connectorType.
     *
     * @param serverName         name of called server.
     * @param urlMarker  view service URL marker
     * @param connectorTypeGUID unique identifier of the connectorType (returned from create)
     * @param replaceAllProperties flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                          the individual properties specified on the request.
     * @param requestBody     properties for the new element.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{connectorTypeGUID}/update")
    @Operation(summary="updateConnectorType",
            description="Update the properties of a connectorType.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse updateConnectorType(@PathVariable
                                        String                                  serverName,
                                        @PathVariable String             urlMarker,
                                        @PathVariable
                                        String                                  connectorTypeGUID,
                                        @RequestParam (required = false, defaultValue = "false")
                                        boolean                                 replaceAllProperties,
                                        @RequestBody (required = false)
                                        UpdateElementRequestBody requestBody)
    {
        return restAPI.updateConnectorType(serverName, urlMarker, connectorTypeGUID, replaceAllProperties, requestBody);
    }


    /**
     * Attach a person role to a person profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param personRoleGUID       unique identifier of the person role
     * @param personProfileGUID            unique identifier of the person profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{personRoleGUID}/person-role-appointments/{personProfileGUID}/attach")
    @Operation(summary="linkPersonRoleToProfile",
            description="Attach a person role to a person profile.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse linkPersonRoleToProfile(@PathVariable
                                            String                     serverName,
                                            @PathVariable String             urlMarker,
                                            @PathVariable
                                            String personRoleGUID,
                                            @PathVariable
                                            String personProfileGUID,
                                            @RequestBody (required = false)
                                            RelationshipRequestBody requestBody)
    {
        return restAPI.linkPersonRoleToProfile(serverName, urlMarker, personRoleGUID, personProfileGUID, requestBody);
    }


    /**
     * Detach a person role from a profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param personRoleGUID       unique identifier of the person role
     * @param personProfileGUID            unique identifier of the person profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{personRoleGUID}/person-role-appointments/{personProfileGUID}/detach")
    @Operation(summary="detachPersonRoleFromProfile",
            description="Detach a person role from a profile.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse detachPersonRoleFromProfile(@PathVariable
                                              String                    serverName,
                                              @PathVariable String             urlMarker,
                                              @PathVariable
                                              String personRoleGUID,
                                              @PathVariable
                                              String personProfileGUID,
                                              @RequestBody (required = false)
                                              MetadataSourceRequestBody requestBody)
    {
        return restAPI.detachPersonRoleFromProfile(serverName, urlMarker, personRoleGUID, personProfileGUID, requestBody);
    }



    /**
     * Attach a team role to a team profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param teamRoleGUID           unique identifier of the team role
     * @param teamProfileGUID        unique identifier of the team profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{teamRoleGUID}/team-role-appointments/{teamProfileGUID}/attach")
    @Operation(summary="linkTeamRoleToProfile",
            description="Attach a team role to a team profile.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse linkTeamRoleToProfile(@PathVariable
                                              String                     serverName,
                                              @PathVariable String             urlMarker,
                                              @PathVariable
                                              String teamRoleGUID,
                                              @PathVariable
                                              String teamProfileGUID,
                                              @RequestBody (required = false)
                                              RelationshipRequestBody requestBody)
    {
        return restAPI.linkTeamRoleToProfile(serverName, urlMarker, teamRoleGUID, teamProfileGUID, requestBody);
    }


    /**
     * Detach a team role from a team profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param teamRoleGUID           unique identifier of the team role
     * @param teamProfileGUID        unique identifier of the team profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{teamRoleGUID}/team-role-appointments/{teamProfileGUID}/detach")
    @Operation(summary="Detach a team role from a team profile.",
            description="Detach a connectorType from one of its peers.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse detachTeamRoleFromProfile(@PathVariable
                                                  String                    serverName,
                                                  @PathVariable String             urlMarker,
                                                  @PathVariable
                                                  String teamRoleGUID,
                                                  @PathVariable
                                                  String teamProfileGUID,
                                                  @RequestBody (required = false)
                                                  MetadataSourceRequestBody requestBody)
    {
        return restAPI.detachTeamRoleFromProfile(serverName, urlMarker, teamRoleGUID, teamProfileGUID, requestBody);
    }


    /**
     * Attach an IT profile role to an IT profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param itProfileRoleGUID      unique identifier of the IT profile role
     * @param itProfileGUID          unique identifier of the IT profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{itProfileRoleGUID}/it-profile-role-appointments/{itProfileGUID}/attach")
    @Operation(summary="linkITProfileRoleToProfile",
            description="Attach an IT profile role to an IT profile.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse linkITProfileRoleToProfile(@PathVariable
                                                   String                     serverName,
                                                   @PathVariable String             urlMarker,
                                                   @PathVariable
                                                   String itProfileRoleGUID,
                                                   @PathVariable
                                                   String itProfileGUID,
                                                   @RequestBody (required = false)
                                                   RelationshipRequestBody requestBody)
    {
        return restAPI.linkITProfileRoleToProfile(serverName, urlMarker, itProfileRoleGUID, itProfileGUID, requestBody);
    }


    /**
     * Detach an IT profile role from an IT profile.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param itProfileRoleGUID      unique identifier of the IT profile role
     * @param itProfileGUID          unique identifier of the IT profile
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{itProfileRoleGUID}/it-profile-role-appointments/{itProfileGUID}/detach")
    @Operation(summary="detachITProfileRoleFromProfile",
            description="Detach an IT profile role from an IT profile.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse detachITProfileRoleFromProfile(@PathVariable
                                                   String                    serverName,
                                                   @PathVariable String             urlMarker,
                                                   @PathVariable
                                                   String itProfileRoleGUID,
                                                   @PathVariable
                                                   String itProfileGUID,
                                                   @RequestBody (required = false)
                                                   MetadataSourceRequestBody requestBody)
    {
        return restAPI.detachITProfileRoleFromProfile(serverName, urlMarker, itProfileRoleGUID, itProfileGUID, requestBody);
    }


    /**
     * Delete a connectorType.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param connectorTypeGUID  unique identifier of the element to delete
     * @param cascadedDelete ca connectorTypes be deleted if data fields are attached?
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/connector-types/{connectorTypeGUID}/delete")
    @Operation(summary="deleteConnectorType",
            description="Delete a connectorType.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public VoidResponse deleteConnectorType(@PathVariable
                                        String                    serverName,
                                        @PathVariable String             urlMarker,
                                        @PathVariable
                                        String                    connectorTypeGUID,
                                        @RequestParam(required = false, defaultValue = "false")
                                        boolean                   cascadedDelete,
                                        @RequestBody (required = false)
                                        MetadataSourceRequestBody requestBody)
    {
        return restAPI.deleteConnectorType(serverName, urlMarker, connectorTypeGUID, cascadedDelete, requestBody);
    }


    /**
     * Returns the list of connectorTypes with a particular name.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connector-types/by-name")
    @Operation(summary="getConnectorTypesByName",
            description="Returns the list of connectorTypes with a particular name.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public ConnectorTypesResponse getConnectorTypesByName(@PathVariable
                                                  String            serverName,
                                                  @PathVariable String             urlMarker,
                                                  @RequestParam (required = false, defaultValue = "0")
                                                  int                     startFrom,
                                                  @RequestParam (required = false, defaultValue = "0")
                                                  int                     pageSize,
                                                  @RequestBody (required = false)
                                                  FilterRequestBody requestBody)
    {
        return restAPI.getConnectorTypesByName(serverName, urlMarker, startFrom, pageSize, requestBody);
    }


    /**
     * Retrieve the list of connectorType metadata elements that contain the search string.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startsWith does the value start with the supplied string?
     * @param endsWith does the value end with the supplied string?
     * @param ignoreCase should the search ignore case?
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connector-types/by-search-string")
    @Operation(summary="findConnectorTypes",
            description="Retrieve the list of connectorType metadata elements that contain the search string.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public ConnectorTypesResponse findConnectorTypes(@PathVariable
                                             String                  serverName,
                                             @PathVariable String             urlMarker,
                                             @RequestParam (required = false, defaultValue = "0")
                                             int                     startFrom,
                                             @RequestParam (required = false, defaultValue = "0")
                                             int                     pageSize,
                                             @RequestParam (required = false, defaultValue = "false")
                                             boolean                 startsWith,
                                             @RequestParam (required = false, defaultValue = "false")
                                             boolean                 endsWith,
                                             @RequestParam (required = false, defaultValue = "false")
                                             boolean                 ignoreCase,
                                             @RequestBody (required = false)
                                             FilterRequestBody requestBody)
    {
        return restAPI.findConnectorTypes(serverName, urlMarker, startsWith, endsWith, ignoreCase, startFrom, pageSize, requestBody);
    }


    /**
     * Return the properties of a specific connectorType.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param connectorTypeGUID    unique identifier of the required element
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/connector-types/{connectorTypeGUID}/retrieve")
    @Operation(summary="getConnectorTypeByGUID",
            description="Return the properties of a specific connectorType.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/connector-type"))

    public ConnectorTypeResponse getConnectorTypeByGUID(@PathVariable
                                                String             serverName,
                                                @PathVariable String             urlMarker,
                                                @PathVariable
                                                String             connectorTypeGUID,
                                                @RequestBody (required = false)
                                                AnyTimeRequestBody requestBody)
    {
        return restAPI.getConnectorTypeByGUID(serverName, urlMarker, connectorTypeGUID, requestBody);
    }



    /**
     * Create a endpoint.
     *
     * @param serverName                 name of called server.
     * @param urlMarker  view service URL marker
     * @param requestBody             properties for the endpoint.
     *
     * @return unique identifier of the newly created element
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/endpoints")

    @Operation(summary="createEndpoint",
            description="Create a endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public GUIDResponse createEndpoint(@PathVariable String                               serverName,
                                           @PathVariable String             urlMarker,
                                           @RequestBody (required = false)
                                           NewElementRequestBody requestBody)
    {
        return restAPI.createEndpoint(serverName, urlMarker, requestBody);
    }


    /**
     * Create a new metadata element to represent a endpoint using an existing metadata element as a template.
     * The template defines additional classifications and relationships that should be added to the new element.
     *
     * @param serverName             calling user
     * @param urlMarker  view service URL marker
     * @param requestBody properties that override the template
     *
     * @return unique identifier of the new metadata element
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/endpoints/from-template")
    @Operation(summary="createEndpointFromTemplate",
            description="Create a new metadata element to represent a endpoint using an existing metadata element as a template.  The template defines additional classifications and relationships that should be added to the new element.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public GUIDResponse createEndpointFromTemplate(@PathVariable
                                                       String              serverName,
                                                       @PathVariable String             urlMarker,
                                                       @RequestBody (required = false)
                                                       TemplateRequestBody requestBody)
    {
        return restAPI.createEndpointFromTemplate(serverName, urlMarker, requestBody);
    }


    /**
     * Update the properties of a endpoint.
     *
     * @param serverName         name of called server.
     * @param urlMarker  view service URL marker
     * @param endpointGUID unique identifier of the endpoint (returned from create)
     * @param replaceAllProperties flag to indicate whether to completely replace the existing properties with the new properties, or just update
     *                          the individual properties specified on the request.
     * @param requestBody     properties for the new element.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/endpoints/{endpointGUID}/update")
    @Operation(summary="updateEndpoint",
            description="Update the properties of a endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public VoidResponse updateEndpoint(@PathVariable
                                           String                                  serverName,
                                           @PathVariable String             urlMarker,
                                           @PathVariable
                                           String                                  endpointGUID,
                                           @RequestParam (required = false, defaultValue = "false")
                                           boolean                                 replaceAllProperties,
                                           @RequestBody (required = false)
                                           UpdateElementRequestBody requestBody)
    {
        return restAPI.updateEndpoint(serverName, urlMarker, endpointGUID, replaceAllProperties, requestBody);
    }


    /**
     * Attach a profile to a endpoint.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param endpointGUID unique identifier of the endpoint
     * @param profileGUID unique identifier of the connection
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/endpoints/{endpointGUID}/profile-identity/{profileGUID}/attach")
    @Operation(summary="linkIdentityToProfile",
            description="Attach a profile to a endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public VoidResponse linkIdentityToProfile(@PathVariable
                                              String                     serverName,
                                              @PathVariable String             urlMarker,
                                              @PathVariable
                                              String                     endpointGUID,
                                              @PathVariable
                                              String profileGUID,
                                              @RequestBody (required = false)
                                              RelationshipRequestBody requestBody)
    {
        return restAPI.linkIdentityToProfile(serverName, urlMarker, endpointGUID, profileGUID, requestBody);
    }


    /**
     * Detach a connection from a endpoint.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param endpointGUID unique identifier of the endpoint
     * @param profileGUID unique identifier of the connection
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/endpoints/{endpointGUID}/profile-identity/{profileGUID}/detach")
    @Operation(summary="detachProfileIdentity",
            description="Detach a connection from a endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public VoidResponse detachProfileIdentity(@PathVariable
                                              String                    serverName,
                                              @PathVariable String             urlMarker,
                                              @PathVariable
                                              String                     endpointGUID,
                                              @PathVariable
                                              String                     profileGUID,
                                              @RequestBody (required = false)
                                              MetadataSourceRequestBody requestBody)
    {
        return restAPI.detachProfileIdentity(serverName, urlMarker, endpointGUID, profileGUID, requestBody);
    }


    /**
     * Delete a endpoint.
     *
     * @param serverName         name of called server
     * @param urlMarker  view service URL marker
     * @param endpointGUID  unique identifier of the element to delete
     * @param cascadedDelete can endpoints be deleted if data fields are attached?
     * @param requestBody  description of the relationship.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is null or invalid.
     *  PropertyServerException    there is a problem retrieving information from the property server(s).
     *  UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    @PostMapping(path = "/endpoints/{endpointGUID}/delete")
    @Operation(summary="deleteEndpoint",
            description="Delete a endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public VoidResponse deleteEndpoint(@PathVariable
                                           String                    serverName,
                                           @PathVariable String             urlMarker,
                                           @PathVariable
                                           String                    endpointGUID,
                                           @RequestParam(required = false, defaultValue = "false")
                                           boolean                   cascadedDelete,
                                           @RequestBody (required = false)
                                           MetadataSourceRequestBody requestBody)
    {
        return restAPI.deleteEndpoint(serverName, urlMarker, endpointGUID, cascadedDelete, requestBody);
    }


    /**
     * Returns the list of endpoints with a particular name.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/endpoints/by-name")
    @Operation(summary="getEndpointsByName",
            description="Returns the list of endpoints with a particular name.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public EndpointsResponse getEndpointsByName(@PathVariable
                                                          String            serverName,
                                                          @PathVariable String             urlMarker,
                                                          @RequestParam (required = false, defaultValue = "0")
                                                          int                     startFrom,
                                                          @RequestParam (required = false, defaultValue = "0")
                                                          int                     pageSize,
                                                          @RequestBody (required = false)
                                                          FilterRequestBody requestBody)
    {
        return restAPI.getEndpointsByName(serverName, urlMarker, startFrom, pageSize, requestBody);
    }


    /**
     * Retrieve the list of endpoint metadata elements that contain the search string.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param startsWith does the value start with the supplied string?
     * @param endsWith does the value end with the supplied string?
     * @param ignoreCase should the search ignore case?
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/endpoints/by-search-string")
    @Operation(summary="findEndpoints",
            description="Retrieve the list of endpoint metadata elements that contain the search string.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public EndpointsResponse findEndpoints(@PathVariable
                                                     String                  serverName,
                                                     @PathVariable String             urlMarker,
                                                     @RequestParam (required = false, defaultValue = "0")
                                                     int                     startFrom,
                                                     @RequestParam (required = false, defaultValue = "0")
                                                     int                     pageSize,
                                                     @RequestParam (required = false, defaultValue = "false")
                                                     boolean                 startsWith,
                                                     @RequestParam (required = false, defaultValue = "false")
                                                     boolean                 endsWith,
                                                     @RequestParam (required = false, defaultValue = "false")
                                                     boolean                 ignoreCase,
                                                     @RequestBody (required = false)
                                                     FilterRequestBody requestBody)
    {
        return restAPI.findEndpoints(serverName, urlMarker, startsWith, endsWith, ignoreCase, startFrom, pageSize, requestBody);
    }


    /**
     * Return the properties of a specific endpoint.
     *
     * @param serverName name of the service to route the request to
     * @param urlMarker  view service URL marker
     * @param endpointGUID    unique identifier of the required element
     * @param requestBody string to find in the properties
     *
     * @return list of matching metadata elements or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/endpoints/{endpointGUID}/retrieve")
    @Operation(summary="getEndpointByGUID",
            description="Return the properties of a specific endpoint.",
            externalDocs=@ExternalDocumentation(description="Further Information",
                    url="https://egeria-project.org/concepts/endpoint"))

    public EndpointResponse getEndpointByGUID(@PathVariable
                                                      String             serverName,
                                                      @PathVariable String             urlMarker,
                                                      @PathVariable
                                                      String             endpointGUID,
                                                      @RequestBody (required = false)
                                                      AnyTimeRequestBody requestBody)
    {
        return restAPI.getEndpointByGUID(serverName, urlMarker, endpointGUID, requestBody);
    }
}
