/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.governanceprogram.server.spring;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.odpi.openmetadata.accessservices.governanceprogram.server.GovernanceProgramRESTServices;
import org.odpi.openmetadata.accessservices.governanceprogram.server.RelatedElementRESTServices;
import org.odpi.openmetadata.commonservices.ffdc.rest.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * The GovernanceProgramResource provides a Spring based server-side REST API
 * that supports the RelatedElementsManagementInterface.   It delegates each request to the
 * RelatedElementRESTServices.  This provides the server-side implementation of the Community Profile Open Metadata
 * Assess Service (OMAS) which is used to manage information about people, roles and organizations.
 */
@RestController
@RequestMapping("/servers/{serverName}/open-metadata/access-services/governance-program/users/{userId}")

@Tag(name="Metadata Access Server: Governance Program OMAS",
     description="The Governance Program OMAS provides APIs and events for tools and applications focused on defining a data strategy, planning support for a regulation and/or developing a governance program for the data landscape.",
     externalDocs=@ExternalDocumentation(description="Further Information",
                                         url="https://egeria-project.org/services/omas/governance-program/overview/"))

public class GovernanceProgramResource
{
    private final RelatedElementRESTServices relatedElementsRESTAPI = new RelatedElementRESTServices();
    private final GovernanceProgramRESTServices restAPI = new GovernanceProgramRESTServices();

    /**
     * Default constructor
     */
    public GovernanceProgramResource()
    {
    }



    /**
     * Return the description of this service.
     *
     * @param serverName name of the server to route the request to
     * @param userId identifier of calling user
     *
     * @return service description or
     * InvalidParameterException one of the parameters is null or invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException problem retrieving the discovery engine definition.
     */
    @GetMapping(path = "/description")

    public RegisteredOMAGServiceResponse getServiceDescription(@PathVariable String serverName,
                                                               @PathVariable String userId)
    {
        return restAPI.getServiceDescription(serverName, userId);
    }


    /**
     * Create a "MoreInformation" relationship between an element that is descriptive and one that is providing the detail.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param detailGUID         unique identifier of the element that provides the detail
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/more-information/{detailGUID}")

    public VoidResponse setupMoreInformation(@PathVariable String                  serverName,
                                             @PathVariable String                  userId,
                                             @PathVariable String                  elementGUID,
                                             @PathVariable String                  detailGUID,
                                             @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupMoreInformation(serverName, userId, elementGUID, detailGUID, requestBody);
    }


    /**
     * Remove a "MoreInformation" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param detailGUID         unique identifier of the element that provides the detail
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/more-information/{detailGUID}/delete")

    public VoidResponse clearMoreInformation(@PathVariable String                    serverName,
                                             @PathVariable String                    userId,
                                             @PathVariable String                    elementGUID,
                                             @PathVariable String                    detailGUID,
                                             @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearMoreInformation(serverName, userId, elementGUID, detailGUID, requestBody);
    }


    /**
     * Retrieve the detail elements linked via a "MoreInformation" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/more-information/by-descriptive-element/{elementGUID}")

    public RelatedElementsResponse getMoreInformation(@PathVariable String serverName,
                                                      @PathVariable String userId,
                                                      @PathVariable String elementGUID,
                                                      @RequestParam int    startFrom,
                                                      @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getMoreInformation(serverName, userId, elementGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the descriptive elements linked via a "MoreInformation" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param detailGUID         unique identifier of the element that provides the detail
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/more-information/by-detail-element/{detailGUID}")

    public RelatedElementsResponse getDescriptiveElements(@PathVariable String serverName,
                                                          @PathVariable String userId,
                                                          @PathVariable String detailGUID,
                                                          @RequestParam int    startFrom,
                                                          @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getDescriptiveElements(serverName, userId, detailGUID, startFrom, pageSize);
    }


    /**
     * Create a "GovernedBy" relationship between an element and a governance definition.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param governanceDefinitionGUID unique identifier of the element that provides the detail
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/governed-by/{governanceDefinitionGUID}")

    public VoidResponse setupGovernedBy(@PathVariable String                  serverName,
                                        @PathVariable String                  userId,
                                        @PathVariable String                  elementGUID,
                                        @PathVariable String                  governanceDefinitionGUID,
                                        @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupGovernedBy(serverName, userId, elementGUID, governanceDefinitionGUID, requestBody);
    }


    /**
     * Remove a "GovernedBy" relationship between two elements.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param governanceDefinitionGUID         unique identifier of the element that provides the detail
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/governed-by/{governanceDefinitionGUID}/delete")

    public VoidResponse clearGovernedBy(@PathVariable String                    serverName,
                                        @PathVariable String                    userId,
                                        @PathVariable String                    elementGUID,
                                        @PathVariable String                    governanceDefinitionGUID,
                                        @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearGovernedBy(serverName, userId, elementGUID, governanceDefinitionGUID, requestBody);
    }


    /**
     * Retrieve the governance definitions linked via a "GovernedBy" relationship to an element.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element that is descriptive
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governed-by/by-element/{elementGUID}")

    public GovernanceDefinitionsResponse getGovernanceDefinitionsForElement(@PathVariable String serverName,
                                                                            @PathVariable String userId,
                                                                            @PathVariable String elementGUID,
                                                                            @RequestParam int    startFrom,
                                                                            @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getGovernanceDefinitionsForElement(serverName, userId, elementGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the governed elements linked via a "GovernedBy" relationship to a governance definition.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceDefinitionGUID         unique identifier of the element that provides the detail
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governed-by/by-governance-definition/{governanceDefinitionGUID}")

    public RelatedElementsResponse getGovernedElements(@PathVariable String serverName,
                                                       @PathVariable String userId,
                                                       @PathVariable String governanceDefinitionGUID,
                                                       @RequestParam int    startFrom,
                                                       @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getGovernedElements(serverName, userId, governanceDefinitionGUID, startFrom, pageSize);
    }



    /**
     * Create an "GovernanceDefinitionScope" relationship between a governance definition and its scope element.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceDefinitionGUID unique identifier of the governance definition
     * @param scopeGUID unique identifier of the scope
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{governanceDefinitionGUID}/governance-definition-scopes/{scopeGUID}")

    public VoidResponse setupGovernanceDefinitionScope(@PathVariable String                  serverName,
                                                       @PathVariable String                  userId,
                                                       @PathVariable String                  governanceDefinitionGUID,
                                                       @PathVariable String                  scopeGUID,
                                                       @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupGovernanceDefinitionScope(serverName, userId, governanceDefinitionGUID, scopeGUID, requestBody);
    }


    /**
     * Remove an "GovernanceDefinitionScope" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceDefinitionGUID unique identifier of the governance definition
     * @param scopeGUID unique identifier of the scope
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{governanceDefinitionGUID}/governance-definition-scopes/{scopeGUID}/delete")

    public VoidResponse clearGovernanceDefinitionScope(@PathVariable String                    serverName,
                                                       @PathVariable String                    userId,
                                                       @PathVariable String                  governanceDefinitionGUID,
                                                       @PathVariable String                  scopeGUID,
                                                       @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearGovernanceDefinitionScope(serverName, userId, governanceDefinitionGUID, scopeGUID, requestBody);
    }


    /**
     * Retrieve the assigned scopes linked by the "GovernanceDefinitionScope" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceDefinitionGUID unique identifier of the governance definition
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governance-definition-scopes/by-governance-definition/{governanceDefinitionGUID}")

    public RelatedElementsResponse getGovernanceDefinitionScopes(@PathVariable String serverName,
                                                                 @PathVariable String userId,
                                                                 @PathVariable String governanceDefinitionGUID,
                                                                 @RequestParam int    startFrom,
                                                                 @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getGovernanceDefinitionScopes(serverName, userId, governanceDefinitionGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the governance definitions linked by the "GovernanceDefinitionScope" relationship to a scope element.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param scopeGUID unique identifier of the scope
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governance-definition-scopes/by-scope/{scopeGUID}")

    public GovernanceDefinitionsResponse getScopedGovernanceDefinitions(@PathVariable String serverName,
                                                                        @PathVariable String userId,
                                                                        @PathVariable String scopeGUID,
                                                                        @RequestParam int    startFrom,
                                                                        @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getScopedGovernanceDefinitions(serverName, userId, scopeGUID, startFrom, pageSize);
    }


    /**
     * Create a "GovernanceResponsibilityAssignment" relationship between a governance responsibility and a person role.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceResponsibilityGUID unique identifier of the governance responsibility (type of governance definition)
     * @param personRoleGUID unique identifier of the person role element
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{governanceResponsibilityGUID}/governance-responsibility-assignments/{personRoleGUID}")

    public VoidResponse setupGovernanceResponsibilityAssignment(@PathVariable String                  serverName,
                                                                @PathVariable String                  userId,
                                                                @PathVariable String                  governanceResponsibilityGUID,
                                                                @PathVariable String                  personRoleGUID,
                                                                @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupGovernanceResponsibilityAssignment(serverName, userId, governanceResponsibilityGUID, personRoleGUID, requestBody);
    }


    /**
     * Remove a "GovernanceResponsibilityAssignment" relationship between a governance responsibility and a person role.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceResponsibilityGUID unique identifier of the governance responsibility (type of governance definition)
     * @param personRoleGUID unique identifier of the person role element
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{governanceResponsibilityGUID}/governance-responsibility-assignments/{personRoleGUID}/delete")

    public VoidResponse clearGovernanceResponsibilityAssignment(@PathVariable String                    serverName,
                                                                @PathVariable String                    userId,
                                                                @PathVariable String                    governanceResponsibilityGUID,
                                                                @PathVariable String                    personRoleGUID,
                                                                @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearGovernanceResponsibilityAssignment(serverName, userId, governanceResponsibilityGUID, personRoleGUID, requestBody);
    }


    /**
     * Retrieve the person roles linked via a "GovernanceResponsibilityAssignment" relationship to a governance responsibility.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param governanceResponsibilityGUID unique identifier of the governance responsibility (type of governance definition)
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governance-responsibility-assignments/by-responsibility/{governanceResponsibilityGUID}")

    public GovernanceRolesResponse getResponsibleRoles(@PathVariable String serverName,
                                                       @PathVariable String userId,
                                                       @PathVariable String governanceResponsibilityGUID,
                                                       @RequestParam int    startFrom,
                                                       @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getResponsibleRoles(serverName, userId, governanceResponsibilityGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the governance responsibilities linked via a "GovernanceResponsibilityAssignment" relationship to a person role.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param personRoleGUID unique identifier of the person role element
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/governance-responsibility-assignments/by-role/{personRoleGUID}")

    public GovernanceDefinitionsResponse getRoleResponsibilities(@PathVariable String serverName,
                                                                 @PathVariable String userId,
                                                                 @PathVariable String personRoleGUID,
                                                                 @RequestParam int    startFrom,
                                                                 @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getRoleResponsibilities(serverName, userId, personRoleGUID, startFrom, pageSize);
    }


    /**
     * Create a "Stakeholder" relationship between an element and its stakeholder.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element
     * @param stakeholderGUID    unique identifier of the stakeholder
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/stakeholders/{stakeholderGUID}")

    public VoidResponse setupStakeholder(@PathVariable String                  serverName,
                                         @PathVariable String                  userId,
                                         @PathVariable String                  elementGUID,
                                         @PathVariable String                  stakeholderGUID,
                                         @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupStakeholder(serverName, userId, elementGUID, stakeholderGUID, requestBody);
    }


    /**
     * Remove a "Stakeholder" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element
     * @param stakeholderGUID    unique identifier of the stakeholder
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/stakeholders/{stakeholderGUID}/delete")

    public VoidResponse clearStakeholder(@PathVariable String                    serverName,
                                         @PathVariable String                    userId,
                                         @PathVariable String                    elementGUID,
                                         @PathVariable String                    stakeholderGUID,
                                         @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearStakeholder(serverName, userId, elementGUID, stakeholderGUID, requestBody);
    }


    /**
     * Retrieve the stakeholder elements linked via the "Stakeholder"  relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId             calling user
     * @param elementGUID        unique identifier of the element
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/stakeholders/by-commissioned-element/{elementGUID}")

    public RelatedElementsResponse getStakeholders(@PathVariable String serverName,
                                                   @PathVariable String userId,
                                                   @PathVariable String elementGUID,
                                                   @RequestParam int   startFrom,
                                                   @RequestParam int   pageSize)
    {
        return relatedElementsRESTAPI.getStakeholders(serverName, userId, elementGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the elements commissioned by a stakeholder, linked via the "Stakeholder"  relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param stakeholderGUID unique identifier of the stakeholder
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/stakeholders/by-stakeholder/{stakeholderGUID}")

    public RelatedElementsResponse getStakeholderCommissionedElements(@PathVariable String serverName,
                                                                      @PathVariable String userId,
                                                                      @PathVariable String stakeholderGUID,
                                                                      @RequestParam int   startFrom,
                                                                      @RequestParam int   pageSize)
    {
        return relatedElementsRESTAPI.getStakeholderCommissionedElements(serverName, userId, stakeholderGUID, startFrom, pageSize);
    }


    /**
     * Create an "AssignmentScope" relationship between an element and its scope.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param scopeGUID unique identifier of the scope
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/assignment-scopes/{scopeGUID}")

    public VoidResponse setupAssignmentScope(@PathVariable String                  serverName,
                                             @PathVariable String                  userId,
                                             @PathVariable String                  elementGUID,
                                             @PathVariable String                  scopeGUID,
                                             @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupAssignmentScope(serverName, userId, elementGUID, scopeGUID, requestBody);
    }


    /**
     * Remove an "AssignmentScope" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param scopeGUID unique identifier of the scope
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/assignment-scopes/{scopeGUID}/delete")

    public VoidResponse clearAssignmentScope(@PathVariable String                    serverName,
                                             @PathVariable String                    userId,
                                             @PathVariable String                    elementGUID,
                                             @PathVariable String                    scopeGUID,
                                             @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearAssignmentScope(serverName, userId, elementGUID, scopeGUID, requestBody);
    }


    /**
     * Retrieve the assigned scopes linked by the "AssignmentScope" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/assignment-scopes/by-assigned-actor/{elementGUID}")

    public RelatedElementsResponse getAssignedScopes(@PathVariable String serverName,
                                                     @PathVariable String userId,
                                                     @PathVariable String elementGUID,
                                                     @RequestParam int   startFrom,
                                                     @RequestParam int   pageSize)
    {
        return relatedElementsRESTAPI.getAssignedScopes(serverName, userId, elementGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the assigned actors linked by the "AssignmentScope" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param scopeGUID unique identifier of the scope
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/assignment-scopes/by-assigned-scope/{scopeGUID}")

    public RelatedElementsResponse getAssignedActors(@PathVariable String serverName,
                                                     @PathVariable String userId,
                                                     @PathVariable String scopeGUID,
                                                     @RequestParam int   startFrom,
                                                     @RequestParam int   pageSize)
    {
        return relatedElementsRESTAPI.getAssignedActors(serverName, userId, scopeGUID, startFrom, pageSize);
    }


    /**
     * Create a "ResourceList" relationship between a consuming element and an element that represents resources.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param resourceGUID unique identifier of the resource
     * @param requestBody relationship properties
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/resource-list/{resourceGUID}")

    public VoidResponse setupResource(@PathVariable String                  serverName,
                                      @PathVariable String                  userId,
                                      @PathVariable String                  elementGUID,
                                      @PathVariable String                  resourceGUID,
                                      @RequestBody  RelationshipRequestBody requestBody)
    {
        return relatedElementsRESTAPI.setupResource(serverName, userId, elementGUID, resourceGUID, requestBody);
    }


    /**
     * Remove a "ResourceList" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param resourceGUID unique identifier of the resource
     * @param requestBody external source identifiers
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @PostMapping(path = "/related-elements/{elementGUID}/resource-list/{resourceGUID}/delete")

    public VoidResponse clearResource(@PathVariable String                    serverName,
                                      @PathVariable String                    userId,
                                      @PathVariable String                    elementGUID,
                                      @PathVariable String                    resourceGUID,
                                      @RequestBody  ExternalSourceRequestBody requestBody)
    {
        return relatedElementsRESTAPI.clearResource(serverName, userId, elementGUID, resourceGUID, requestBody);
    }


    /**
     * Retrieve the list of resources assigned to an element via the "ResourceList" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param elementGUID unique identifier of the element
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/resource-list/by-assignee/{elementGUID}")

    public RelatedElementsResponse getResourceList(@PathVariable String serverName,
                                                   @PathVariable String userId,
                                                   @PathVariable String elementGUID,
                                                   @RequestParam int    startFrom,
                                                   @RequestParam int    pageSize)
    {
        return relatedElementsRESTAPI.getResourceList(serverName, userId, elementGUID, startFrom, pageSize);
    }


    /**
     * Retrieve the list of elements assigned to a resource via the "ResourceList" relationship between two referenceables.
     *
     * @param serverName name of the service to route the request to.
     * @param userId calling user
     * @param resourceGUID unique identifier of the resource
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return void or
     *  InvalidParameterException  one of the parameters is invalid
     *  UserNotAuthorizedException the user is not authorized to issue this request
     *  PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    @GetMapping(path = "/related-elements/resource-list/by-resource/{resourceGUID}")

    public RelatedElementsResponse getSupportedByResource(@PathVariable String serverName,
                                                          @PathVariable String userId,
                                                          @PathVariable String resourceGUID,
                                                          @RequestParam int   startFrom,
                                                          @RequestParam int   pageSize)
    {
        return relatedElementsRESTAPI.getSupportedByResource(serverName, userId, resourceGUID, startFrom, pageSize);
    }
}
