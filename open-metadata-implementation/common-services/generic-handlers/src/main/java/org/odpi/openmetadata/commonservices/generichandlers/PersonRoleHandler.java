/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.generichandlers;

import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.generichandlers.ffdc.GenericHandlersErrorCode;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.metadatasecurity.server.OpenMetadataServerSecurityVerifier;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.SequencingOrder;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * PersonRoleHandler provides the exchange of metadata about roles between the repository and the OMAS.
 * The PersonRole entity does not support effectivity dates - but appointments - ie links between person roles and actors
 * do need effectivity dates
 *
 * @param <B> class that represents the role
 */
public class PersonRoleHandler<B> extends ReferenceableHandler<B>
{
    /**
     * Construct the handler with information needed to work with B objects.
     *
     * @param converter specific converter for this bean class
     * @param beanClass name of bean class that is represented by the generic class B
     * @param serviceName name of this service
     * @param serverName name of the local server
     * @param invalidParameterHandler handler for managing parameter errors
     * @param repositoryHandler manages calls to the repository services
     * @param repositoryHelper provides utilities for manipulating the repository services objects
     * @param localServerUserId userId for this server
     * @param securityVerifier open metadata security services verifier
     * @param supportedZones list of zones that the access service is allowed to serve B instances from
     * @param defaultZones list of zones that the access service should set in all new B instances
     * @param publishZones list of zones that the access service sets up in published B instances
     * @param auditLog destination for audit log events
     */
    public PersonRoleHandler(OpenMetadataAPIGenericConverter<B> converter,
                             Class<B>                           beanClass,
                             String                             serviceName,
                             String                             serverName,
                             InvalidParameterHandler            invalidParameterHandler,
                             RepositoryHandler                  repositoryHandler,
                             OMRSRepositoryHelper               repositoryHelper,
                             String                             localServerUserId,
                             OpenMetadataServerSecurityVerifier securityVerifier,
                             List<String>                       supportedZones,
                             List<String>                       defaultZones,
                             List<String>                       publishZones,
                             AuditLog                           auditLog)
    {
        super(converter,
              beanClass,
              serviceName,
              serverName,
              invalidParameterHandler,
              repositoryHandler,
              repositoryHelper,
              localServerUserId,
              securityVerifier,
              supportedZones,
              defaultZones,
              publishZones,
              auditLog);
    }


    /**
     * Create the description of a role.  This is typically a subtype of PersonRole.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param qualifiedName unique name for the role - used in other configuration
     * @param identifier unique identifier for the role - typically from external system
     * @param name short display name for the role
     * @param description description of the role
     * @param scope the scope of the role
     * @param headCount number of individuals that can be appointed to this role
     * @param headCountLimitSet should the headcount be added to the entity?
     * @param domainIdentifier governance domain identifier
     * @param additionalProperties additional properties for a role
     * @param suppliedTypeName type name from the caller (enables creation of subtypes)
     * @param extendedProperties  properties for a governance role subtype
     * @param effectiveFrom starting time for this relationship (null for all time)
     * @param effectiveTo ending time for this relationship (null for all time)
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return unique identifier of the new role object
     * @throws InvalidParameterException qualifiedName or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public String createPersonRole(String              userId,
                                   String              externalSourceGUID,
                                   String              externalSourceName,
                                   String              qualifiedName,
                                   String              identifier,
                                   String              name,
                                   String              description,
                                   String              scope,
                                   int                 headCount,
                                   boolean             headCountLimitSet,
                                   int                 domainIdentifier,
                                   Map<String, String> additionalProperties,
                                   String              suppliedTypeName,
                                   Map<String, Object> extendedProperties,
                                   Date                effectiveFrom,
                                   Date                effectiveTo,
                                   Date                effectiveTime,
                                   String              methodName) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        final String qualifiedNameParameterName = "qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameterName, methodName);

        String typeName = OpenMetadataType.PERSON_ROLE.typeName;

        if (suppliedTypeName != null)
        {
            typeName = suppliedTypeName;
        }

        String typeGUID = invalidParameterHandler.validateTypeName(typeName,
                                                                   OpenMetadataType.PERSON_ROLE.typeName,
                                                                   serviceName,
                                                                   methodName,
                                                                   repositoryHelper);

        /*
         * GovernanceRole inherits from PersonRole and introduces the domainIdentifier property.  If the requested role
         * is a type of governance role then domain identifier is set.  If the role comes from an API that does not explicitly support
         * the domain identifier then it will be set to zero (all domains) unless overridden in the extended properties.
         */
        boolean domainIdentifierSet = repositoryHelper.isTypeOf(serviceName, typeName, OpenMetadataType.GOVERNANCE_ROLE.typeName);

        PersonRoleBuilder roleBuilder = new PersonRoleBuilder(qualifiedName,
                                                              identifier,
                                                              name,
                                                              description,
                                                              scope,
                                                              headCount,
                                                              headCountLimitSet,
                                                              domainIdentifier,
                                                              domainIdentifierSet,
                                                              additionalProperties,
                                                              typeGUID,
                                                              typeName,
                                                              extendedProperties,
                                                              repositoryHelper,
                                                              serviceName,
                                                              serverName);

        roleBuilder.setEffectivityDates(effectiveFrom, effectiveTo);

        return this.createBeanInRepository(userId,
                                           externalSourceGUID,
                                           externalSourceName,
                                           typeGUID,
                                           typeName,
                                           roleBuilder,
                                           effectiveTime,
                                           methodName);
    }


    /**
     * Link a person role with a Person profile to show that the person has been appointed to role.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param profileGUID unique identifier of actor profile
     * @param profileGUIDParameterName parameter name supplying profileGUID
     * @param roleGUID  unique identifier of the person role
     * @param roleGUIDParameterName parameter name supplying roleGUID
     * @param isPublic is this appointment visible to others
     * @param effectiveFrom starting time for this relationship (null for all time)
     * @param effectiveTo ending time for this relationship (null for all time)
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return unique identifier of the appointment relationship
     *
     * @throws InvalidParameterException entity not known, null userId or guid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public String appointPersonToRole(String  userId,
                                      String  externalSourceGUID,
                                      String  externalSourceName,
                                      String  profileGUID,
                                      String  profileGUIDParameterName,
                                      String  roleGUID,
                                      String  roleGUIDParameterName,
                                      boolean isPublic,
                                      Date    effectiveFrom,
                                      Date    effectiveTo,
                                      boolean forLineage,
                                      boolean forDuplicateProcessing,
                                      Date    effectiveTime,
                                      String  methodName) throws InvalidParameterException,
                                                                 UserNotAuthorizedException,
                                                                 PropertyServerException
    {
        PersonRoleBuilder builder = new PersonRoleBuilder(repositoryHelper, serviceName, serverName);

        return this.linkElementToElement(userId,
                                         externalSourceGUID,
                                         externalSourceName,
                                         roleGUID,
                                         roleGUIDParameterName,
                                         OpenMetadataType.PERSON_ROLE.typeName,
                                         profileGUID,
                                         profileGUIDParameterName,
                                         OpenMetadataType.PERSON.typeName,
                                         forLineage,
                                         forDuplicateProcessing,
                                         supportedZones,
                                         OpenMetadataType.PERSON_ROLE_APPOINTMENT_RELATIONSHIP.typeGUID,
                                         OpenMetadataType.PERSON_ROLE_APPOINTMENT_RELATIONSHIP.typeName,
                                         builder.getAppointmentProperties(isPublic, effectiveFrom, effectiveTo, methodName),
                                         effectiveFrom,
                                         effectiveTo,
                                         effectiveTime,
                                         methodName);
    }


    /**
     * Set an end date on a specific appointment.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param profileGUID unique identifier of person profile
     * @param profileGUIDParameterName parameter name supplying profileGUID
     * @param roleGUID  unique identifier of the person role
     * @param roleGUIDParameterName parameter name supplying roleGUID
     * @param appointmentGUID unique identifier (guid) of the appointment relationship
     * @param appointmentGUIDParameterName parameter name supplying appointmentGUID
     * @param endDate the official end of the appointment - null means effective immediately
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @throws InvalidParameterException entity not known, null userId or guid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void relievePersonFromRole(String userId,
                                      String externalSourceGUID,
                                      String externalSourceName,
                                      String profileGUID,
                                      String profileGUIDParameterName,
                                      String roleGUID,
                                      String roleGUIDParameterName,
                                      String appointmentGUID,
                                      String appointmentGUIDParameterName,
                                      Date   endDate,
                                      Date   effectiveTime,
                                      String methodName) throws InvalidParameterException,
                                                                UserNotAuthorizedException,
                                                                PropertyServerException
    {
        invalidParameterHandler.validateGUID(appointmentGUID, appointmentGUIDParameterName, methodName);

        Relationship relationship = repositoryHandler.getRelationshipByGUID(userId,
                                                                            appointmentGUID,
                                                                            appointmentGUIDParameterName,
                                                                            OpenMetadataType.PERSON_ROLE_APPOINTMENT_RELATIONSHIP.typeName,
                                                                            effectiveTime,
                                                                            methodName);


        if ((relationship != null) && (relationship.getEntityOneProxy() != null) && (relationship.getEntityTwoProxy() != null))
        {
            if (roleGUID != null)
            {
                if (! roleGUID.equals(relationship.getEntityOneProxy().getGUID()))
                {
                    String relationshipTypeName = "<Unknown>";
                    String proxyTypeName        = "<Unknown>";

                    if (relationship.getType() != null)
                    {
                        relationshipTypeName = relationship.getType().getTypeDefName();
                    }
                    if (relationship.getEntityTwoProxy().getType() != null)
                    {
                        proxyTypeName = relationship.getEntityTwoProxy().getType().getTypeDefName();
                    }

                    throw new InvalidParameterException(GenericHandlersErrorCode.WRONG_END_GUID.getMessageDefinition(roleGUIDParameterName,
                                                                                                                     roleGUID,
                                                                                                                     proxyTypeName,
                                                                                                                     relationship.getEntityTwoProxy().getGUID(),
                                                                                                                     Integer.toString(2),
                                                                                                                     relationshipTypeName,
                                                                                                                     appointmentGUIDParameterName,
                                                                                                                     appointmentGUID),
                                                        this.getClass().getName(),
                                                        methodName,
                                                        roleGUIDParameterName);
                }
            }

            if (profileGUID != null)
            {
                if (! profileGUID.equals(relationship.getEntityTwoProxy().getGUID()))
                {
                    String relationshipTypeName = "<Unknown>";
                    String proxyTypeName        = "<Unknown>";

                    if (relationship.getType() != null)
                    {
                        relationshipTypeName = relationship.getType().getTypeDefName();
                    }
                    if (relationship.getEntityOneProxy().getType() != null)
                    {
                        proxyTypeName = relationship.getEntityOneProxy().getType().getTypeDefName();
                    }

                    throw new InvalidParameterException(GenericHandlersErrorCode.WRONG_END_GUID.getMessageDefinition(profileGUIDParameterName,
                                                                                                                     profileGUID,
                                                                                                                     proxyTypeName,
                                                                                                                     relationship.getEntityOneProxy().getGUID(),
                                                                                                                     Integer.toString(1),
                                                                                                                     relationshipTypeName,
                                                                                                                     appointmentGUIDParameterName,
                                                                                                                     appointmentGUID),
                                                        this.getClass().getName(),
                                                        methodName,
                                                        roleGUIDParameterName);
                }
            }

            InstanceProperties properties = new InstanceProperties(relationship.getProperties());

            if (endDate == null)
            {
                properties.setEffectiveToTime(new Date());
            }
            else
            {
                properties.setEffectiveToTime(endDate);
            }

            repositoryHandler.updateRelationshipProperties(userId,
                                                           externalSourceGUID,
                                                           externalSourceName,
                                                           relationship,
                                                           properties,
                                                           methodName);

        }
    }


    /**
     * Link a role to a governance responsibility.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param personRoleGUID unique identifier of TeamMember role
     * @param personRoleGUIDParameterName parameter name supplying personRoleGUID
     * @param governanceResponsibilityGUID  unique identifier of the user identity
     * @param governanceResponsibilityGUIDParameterName parameter name supplying governanceResponsibilityGUID
     * @param effectiveFrom starting time for this relationship (null for all time)
     * @param effectiveTo ending time for this relationship (null for all time)
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @throws InvalidParameterException entity not known, null userId or guid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void  addGovernanceResponsibility(String  userId,
                               String  externalSourceGUID,
                               String  externalSourceName,
                               String  personRoleGUID,
                               String  personRoleGUIDParameterName,
                               String  governanceResponsibilityGUID,
                               String  governanceResponsibilityGUIDParameterName,
                               Date    effectiveFrom,
                               Date    effectiveTo,
                               boolean forLineage,
                               boolean forDuplicateProcessing,
                               Date    effectiveTime,
                               String  methodName) throws InvalidParameterException,
                                                          UserNotAuthorizedException,
                                                          PropertyServerException
    {
        this.linkElementToElement(userId,
                                  externalSourceGUID,
                                  externalSourceName,
                                  governanceResponsibilityGUID,
                                  governanceResponsibilityGUIDParameterName,
                                  OpenMetadataType.GOVERNANCE_RESPONSIBILITY.typeName,
                                  personRoleGUID,
                                  personRoleGUIDParameterName,
                                  OpenMetadataType.PERSON_ROLE.typeName,
                                  forLineage,
                                  forDuplicateProcessing,
                                  supportedZones,
                                  OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeGUID,
                                  OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeName,
                                  this.setUpEffectiveDates(null, effectiveFrom, effectiveTo),
                                  effectiveFrom,
                                  effectiveTo,
                                  effectiveTime,
                                  methodName);
    }


    /**
     * Unlink a role from a governance responsibility.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param personRoleGUID unique identifier of TeamMember role
     * @param personRoleGUIDParameterName parameter name supplying personRoleGUID
     * @param governanceResponsibilityGUID  unique identifier of the user identity
     * @param governanceResponsibilityGUIDParameterName parameter name supplying governanceResponsibilityGUID
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @throws InvalidParameterException entity not known, null userId or guid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void removeGovernanceResponsibility(String  userId,
                                               String  externalSourceGUID,
                                               String  externalSourceName,
                                               String  personRoleGUID,
                                               String  personRoleGUIDParameterName,
                                               String  governanceResponsibilityGUID,
                                               String  governanceResponsibilityGUIDParameterName,
                                               boolean forLineage,
                                               boolean forDuplicateProcessing,
                                               Date    effectiveTime,
                                               String  methodName) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        this.unlinkElementFromElement(userId,
                                      false,
                                      externalSourceGUID,
                                      externalSourceName,
                                      governanceResponsibilityGUID,
                                      governanceResponsibilityGUIDParameterName,
                                      OpenMetadataType.GOVERNANCE_RESPONSIBILITY.typeName,
                                      personRoleGUID,
                                      personRoleGUIDParameterName,
                                      OpenMetadataType.PERSON_ROLE.typeGUID,
                                      OpenMetadataType.PERSON_ROLE.typeName,
                                      forLineage,
                                      forDuplicateProcessing,
                                      supportedZones,
                                      OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeGUID,
                                      OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeName,
                                      effectiveTime,
                                      methodName);
    }



    /**
     * Returns the list of roles that are responsible for the supplied governance definition.
     *
     * @param userId       String   userId of user making request.
     * @param governanceResponsibilityGUID    String   unique id for element.
     * @param governanceResponsibilityGUIDParameterName name of parameter supplying the GUID
     * @param startFrom int      starting position for fist returned element.
     * @param pageSize  int      maximum number of elements to return on the call.
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for
     * @param methodName String calling method
     *
     * @return a list of assets or
     * @throws InvalidParameterException - the GUID is not recognized or the paging values are invalid or
     * @throws PropertyServerException - there is a problem retrieving the asset properties from the property server or
     * @throws UserNotAuthorizedException - the requesting user is not authorized to issue this request.
     */
    public List<B> getRolesWithGovernanceResponsibility(String       userId,
                                                        String       governanceResponsibilityGUID,
                                                        String       governanceResponsibilityGUIDParameterName,
                                                        int          startFrom,
                                                        int          pageSize,
                                                        boolean      forLineage,
                                                        boolean      forDuplicateProcessing,
                                                        Date         effectiveTime,
                                                        String       methodName) throws InvalidParameterException,
                                                                                        PropertyServerException,
                                                                                        UserNotAuthorizedException
    {
        return this.getAttachedElements(userId,
                                        null,
                                        null,
                                        governanceResponsibilityGUID,
                                        governanceResponsibilityGUIDParameterName,
                                        OpenMetadataType.GOVERNANCE_RESPONSIBILITY.typeName,
                                        OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeGUID,
                                        OpenMetadataType.GOVERNANCE_RESPONSIBILITY_ASSIGNMENT.typeName,
                                        OpenMetadataType.PERSON_ROLE.typeName,
                                        (String)null,
                                        null,
                                        2,
                                        null,
                                        null,
                                        SequencingOrder.CREATION_DATE_RECENT,
                                        null,
                                        forLineage,
                                        forDuplicateProcessing,
                                        supportedZones,
                                        startFrom,
                                        pageSize,
                                        effectiveTime,
                                        methodName);
    }


    /**
     * Update the person role object.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param roleGUID unique identifier of the role to update
     * @param roleGUIDParameterName parameter passing the roleGUID
     * @param qualifiedName unique name for the role - used in other configuration
     * @param qualifiedNameParameterName  parameter providing qualified name
     * @param identifier unique identifier for the role - typically from external system
     * @param name short display name for the role
     * @param nameParameterName  parameter providing name
     * @param description description of the role
     * @param scope the scope of the role
     * @param headCountLimitSet should the head count be set in the entity?
     * @param headCount number of individuals that can be appointed to this role
     * @param domainIdentifier governance domain identifier
     * @param additionalProperties additional properties for a governance role
     * @param typeName type of role
     * @param extendedProperties  properties for a governance role subtype
     * @param isMergeUpdate should the supplied properties be merged with existing properties (true) only replacing the properties with
     *                      matching names, or should the entire properties of the instance be replaced?
     * @param effectiveFrom starting time for this element (null for all time)
     * @param effectiveTo ending time for this element (null for all time)
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @throws InvalidParameterException qualifiedName or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void updatePersonRole(String              userId,
                                 String              externalSourceGUID,
                                 String              externalSourceName,
                                 String              roleGUID,
                                 String              roleGUIDParameterName,
                                 String              qualifiedName,
                                 String              qualifiedNameParameterName,
                                 String              identifier,
                                 String              name,
                                 String              nameParameterName,
                                 String              description,
                                 String              scope,
                                 int                 headCount,
                                 boolean             headCountLimitSet,
                                 int                 domainIdentifier,
                                 Map<String, String> additionalProperties,
                                 String              typeName,
                                 Map<String, Object> extendedProperties,
                                 boolean             isMergeUpdate,
                                 Date                effectiveFrom,
                                 Date                effectiveTo,
                                 boolean             forLineage,
                                 boolean             forDuplicateProcessing,
                                 Date                effectiveTime,
                                 String              methodName) throws InvalidParameterException,
                                                                        UserNotAuthorizedException,
                                                                        PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(roleGUID, roleGUIDParameterName, methodName);

        if (! isMergeUpdate)
        {
            invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameterName, methodName);
            invalidParameterHandler.validateName(name, nameParameterName, methodName);
        }


        String typeGUID = invalidParameterHandler.validateTypeName(typeName,
                                                                   OpenMetadataType.PERSON_ROLE.typeName,
                                                                   serviceName,
                                                                   methodName,
                                                                   repositoryHelper);

        /*
         * GovernanceRole inherits from PersonRole and introduces the domainIdentifier property.  If the requested role
         * is a type of governance role then domain identifier is set.  If the role comes from an API that does not explicitly support
         * the domain identifier then it will be set to zero (all domains) unless overridden in the extended properties.
         */
        boolean domainIdentifierSet = (typeName != null) &&
                                      (!(isMergeUpdate) && (domainIdentifier == 0)) &&
                                      (repositoryHelper.isTypeOf(serviceName, typeName, OpenMetadataType.GOVERNANCE_ROLE.typeName));

        PersonRoleBuilder roleBuilder = new PersonRoleBuilder(qualifiedName,
                                                              identifier,
                                                              name,
                                                              description,
                                                              scope,
                                                              headCount,
                                                              headCountLimitSet,
                                                              domainIdentifier,
                                                              domainIdentifierSet,
                                                              additionalProperties,
                                                              typeGUID,
                                                              typeName,
                                                              extendedProperties,
                                                              repositoryHelper,
                                                              serviceName,
                                                              serverName);

        roleBuilder.setEffectivityDates(effectiveFrom, effectiveTo);

        this.updateBeanInRepository(userId,
                                    externalSourceGUID,
                                    externalSourceName,
                                    roleGUID,
                                    roleGUIDParameterName,
                                    typeGUID,
                                    typeName,
                                    forLineage,
                                    forDuplicateProcessing,
                                    supportedZones,
                                    roleBuilder.getInstanceProperties(methodName),
                                    isMergeUpdate,
                                    effectiveTime,
                                    methodName);
    }


    /**
     * Remove the metadata element representing a role.  This will delete the role and all categories and terms because
     * the Anchors classifications are set up in these elements.
     *
     * @param userId calling user
     * @param externalSourceGUID     unique identifier of software capability representing the caller
     * @param externalSourceName     unique name of software capability representing the caller
     * @param roleGUID unique identifier of the metadata element to remove
     * @param roleGUIDParameterName parameter supplying the roleGUID
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void removePersonRole(String  userId,
                                 String  externalSourceGUID,
                                 String  externalSourceName,
                                 String  roleGUID,
                                 String  roleGUIDParameterName,
                                 boolean forLineage,
                                 boolean forDuplicateProcessing,
                                 Date    effectiveTime,
                                 String  methodName) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        this.deleteBeanInRepository(userId,
                                    externalSourceGUID,
                                    externalSourceName,
                                    roleGUID,
                                    roleGUIDParameterName,
                                    OpenMetadataType.PERSON_ROLE.typeGUID,
                                    OpenMetadataType.PERSON_ROLE.typeName,
                                    false,
                                    null,
                                    null,
                                    forLineage,
                                    forDuplicateProcessing,
                                    effectiveTime,
                                    methodName);
    }


    /**
     * Retrieve the list of role metadata elements that contain the search string.
     * The search string is treated as a regular expression.
     *
     * @param userId calling user
     * @param searchString string to find in the properties
     * @param searchStringParameterName name of parameter supplying the search string
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return list of matching metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B> findPersonRoles(String  userId,
                                   String  searchString,
                                   String  searchStringParameterName,
                                   int     startFrom,
                                   int     pageSize,
                                   boolean forLineage,
                                   boolean forDuplicateProcessing,
                                   Date    effectiveTime,
                                   String  methodName) throws InvalidParameterException,
                                                              UserNotAuthorizedException,
                                                              PropertyServerException
    {
        return this.findBeans(userId,
                              searchString,
                              searchStringParameterName,
                              OpenMetadataType.PERSON_ROLE.typeGUID,
                              OpenMetadataType.PERSON_ROLE.typeName,
                              startFrom,
                              pageSize,
                              null,
                              null,
                              SequencingOrder.CREATION_DATE_RECENT,
                              null,
                              forLineage,
                              forDuplicateProcessing,
                              effectiveTime,
                              methodName);
    }


    /**
     * Retrieve the list of role metadata elements with a matching qualified or display name.
     * There are no wildcards supported on this request.
     *
     * @param userId calling user
     * @param name name to search for
     * @param nameParameterName parameter supplying name
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return list of matching metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B>  getPersonRolesByName(String  userId,
                                         String  name,
                                         String  nameParameterName,
                                         int     startFrom,
                                         int     pageSize,
                                         boolean forLineage,
                                         boolean forDuplicateProcessing,
                                         Date    effectiveTime,
                                         String  methodName) throws InvalidParameterException,
                                                                    UserNotAuthorizedException,
                                                                    PropertyServerException
    {
        List<String> specificMatchPropertyNames = new ArrayList<>();
        specificMatchPropertyNames.add(OpenMetadataProperty.QUALIFIED_NAME.name);
        specificMatchPropertyNames.add(OpenMetadataProperty.IDENTIFIER.name);
        specificMatchPropertyNames.add(OpenMetadataProperty.NAME.name);

        return this.getBeansByValue(userId,
                                    name,
                                    nameParameterName,
                                    OpenMetadataType.PERSON_ROLE.typeGUID,
                                    OpenMetadataType.PERSON_ROLE.typeName,
                                    specificMatchPropertyNames,
                                    true,
                                    null,
                                    null,
                                    null,
                                    null,
                                    SequencingOrder.CREATION_DATE_RECENT,
                                    null,
                                    forLineage,
                                    forDuplicateProcessing,
                                    supportedZones,
                                    startFrom,
                                    pageSize,
                                    effectiveTime,
                                    methodName);
    }


    /**
     * Return the person roles attached to a supplied project via the project management relationship.
     *
     * @param userId     calling user
     * @param projectGUID identifier for the entity that the contact details are attached to
     * @param projectGUIDParameterName name of parameter supplying the GUID
     * @param startingFrom where to start from in the list
     * @param pageSize maximum number of results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     * @return list of objects or null if none found
     * @throws InvalidParameterException  the input properties are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    public List<B>  getProjectManagerRoles(String              userId,
                                           String              projectGUID,
                                           String              projectGUIDParameterName,
                                           int                 startingFrom,
                                           int                 pageSize,
                                           boolean             forLineage,
                                           boolean             forDuplicateProcessing,
                                           Date                effectiveTime,
                                           String              methodName) throws InvalidParameterException,
                                                                                  PropertyServerException,
                                                                                  UserNotAuthorizedException
    {
        return this.getAttachedElements(userId,
                                        null,
                                        null,
                                        projectGUID,
                                        projectGUIDParameterName,
                                        OpenMetadataType.PROJECT.typeName,
                                        OpenMetadataType.PROJECT_MANAGEMENT_RELATIONSHIP.typeGUID,
                                        OpenMetadataType.PROJECT_MANAGEMENT_RELATIONSHIP.typeName,
                                        OpenMetadataType.PERSON_ROLE.typeName,
                                        (String)null,
                                        null,
                                        2,
                                        null,
                                        null,
                                        SequencingOrder.CREATION_DATE_RECENT,
                                        null,
                                        forLineage,
                                        forDuplicateProcessing,
                                        supportedZones,
                                        startingFrom,
                                        pageSize,
                                        effectiveTime,
                                        methodName);
    }


    /**
     * Return the person roles attached to a supplied community via the community membership relationship.
     *
     * @param userId     calling user
     * @param communityGUID identifier for the entity that the contact details are attached to
     * @param communityGUIDParameterName name of parameter supplying the GUID
     * @param startingFrom where to start from in the list
     * @param pageSize maximum number of results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     * @return list of objects or null if none found
     * @throws InvalidParameterException  the input properties are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    public List<B>  getCommunityRoles(String              userId,
                                      String              communityGUID,
                                      String              communityGUIDParameterName,
                                      int                 startingFrom,
                                      int                 pageSize,
                                      boolean             forLineage,
                                      boolean             forDuplicateProcessing,
                                      Date                effectiveTime,
                                      String              methodName) throws InvalidParameterException,
                                                                             PropertyServerException,
                                                                             UserNotAuthorizedException
    {
        return this.getAttachedElements(userId,
                                        null,
                                        null,
                                        communityGUID,
                                        communityGUIDParameterName,
                                        OpenMetadataType.COMMUNITY.typeName,
                                        OpenMetadataType.COMMUNITY_MEMBERSHIP_RELATIONSHIP.typeGUID,
                                        OpenMetadataType.COMMUNITY_MEMBERSHIP_RELATIONSHIP.typeName,
                                        OpenMetadataType.PERSON_ROLE.typeName,
                                        (String)null,
                                        null,
                                        2,
                                        null,
                                        null,
                                        SequencingOrder.CREATION_DATE_RECENT,
                                        null,
                                        forLineage,
                                        forDuplicateProcessing,
                                        supportedZones,
                                        startingFrom,
                                        pageSize,
                                        effectiveTime,
                                        methodName);
    }



    /**
     * Return the bean that represents a person role.
     *
     * @param userId calling user
     * @param personRoleGUID unique identifier of the role
     * @param personRoleGUIDParameterName name of the parameter that supplied the GUID
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return bean
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public B getPersonRoleByGUID(String  userId,
                                 String  personRoleGUID,
                                 String  personRoleGUIDParameterName,
                                 boolean forLineage,
                                 boolean forDuplicateProcessing,
                                 Date    effectiveTime,
                                 String  methodName) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        return getBeanFromRepository(userId,
                                     personRoleGUID,
                                     personRoleGUIDParameterName,
                                     OpenMetadataType.PERSON_ROLE.typeName,
                                     forLineage,
                                     forDuplicateProcessing,
                                     effectiveTime,
                                     methodName);
    }


    /**
     * Retrieve the list of role metadata elements with a matching qualified or display name.
     * There are no wildcards supported on this request.
     *
     * @param userId calling user
     * @param name name to search for
     * @param nameParameterName parameter supplying name
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return list of matching metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B> getPersonRolesForTitle(String  userId,
                                          String  name,
                                          String  nameParameterName,
                                          int     startFrom,
                                          int     pageSize,
                                          boolean forLineage,
                                          boolean forDuplicateProcessing,
                                          Date    effectiveTime,
                                          String  methodName) throws InvalidParameterException,
                                                                     UserNotAuthorizedException,
                                                                     PropertyServerException
    {
        List<String> specificMatchPropertyNames = new ArrayList<>();
        specificMatchPropertyNames.add(OpenMetadataProperty.NAME.name);

        return this.getBeansByValue(userId,
                                    name,
                                    nameParameterName,
                                    OpenMetadataType.PERSON_ROLE.typeGUID,
                                    OpenMetadataType.PERSON_ROLE.typeName,
                                    specificMatchPropertyNames,
                                    true,
                                    null,
                                    null,
                                    null,
                                    null,
                                    SequencingOrder.CREATION_DATE_RECENT,
                                    null,
                                    forLineage,
                                    forDuplicateProcessing,
                                    supportedZones,
                                    startFrom,
                                    pageSize,
                                    effectiveTime,
                                    methodName);
    }


    /**
     * Retrieve the list of role metadata elements with a matching domain identifier.  If the domain identifier is 0 then all roles are returned.
     *
     * @param userId calling user
     * @param domainIdentifier domain of interest - 0 means all domains
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param forLineage the request is to support lineage retrieval this means entities with the Memento classification can be returned
     * @param forDuplicateProcessing the request is for duplicate processing and so must not deduplicate
     * @param effectiveTime the time that the retrieved elements must be effective for (null for any time, new Date() for now)
     * @param methodName calling method
     *
     * @return list of matching metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B> getPersonRolesForDomainId(String  userId,
                                             int     domainIdentifier,
                                             int     startFrom,
                                             int     pageSize,
                                             boolean forLineage,
                                             boolean forDuplicateProcessing,
                                             Date    effectiveTime,
                                             String  methodName) throws InvalidParameterException,
                                                                        UserNotAuthorizedException,
                                                                        PropertyServerException
    {
        /*
         * A governance domain identifier of 0 is ignored in the retrieval because it means return for all domains.
         */
        if (domainIdentifier == 0)
        {
            return this.getBeansByType(userId,
                                       OpenMetadataType.PERSON_ROLE.typeGUID,
                                       OpenMetadataType.PERSON_ROLE.typeName,
                                       null,
                                       null,
                                       SequencingOrder.CREATION_DATE_RECENT,
                                       null,
                                       forLineage,
                                       forDuplicateProcessing,
                                       supportedZones,
                                       startFrom,
                                       pageSize,
                                       effectiveTime,
                                       methodName);
        }

        return this.getBeansByIntValue(userId,
                                       domainIdentifier,
                                       OpenMetadataType.PERSON_ROLE.typeGUID,
                                       OpenMetadataType.PERSON_ROLE.typeName,
                                       OpenMetadataProperty.DOMAIN_IDENTIFIER.name,
                                       null,
                                       null,
                                       null,
                                       null,
                                       SequencingOrder.CREATION_DATE_RECENT,
                                       null,
                                       forLineage,
                                       forDuplicateProcessing,
                                       supportedZones,
                                       startFrom,
                                       pageSize,
                                       effectiveTime,
                                       methodName);
    }
}
