/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.integrationservices.security.contextmanager;

import org.odpi.openmetadata.accessservices.securitymanager.client.*;
import org.odpi.openmetadata.accessservices.securitymanager.client.rest.SecurityManagerRESTClient;
import org.odpi.openmetadata.frameworks.openmetadata.properties.softwarecapabilities.SecurityManagerProperties;
import org.odpi.openmetadata.frameworks.integration.context.IntegrationContext;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.openmetadata.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.integration.connectors.IntegrationConnector;
import org.odpi.openmetadata.frameworks.integration.contextmanager.IntegrationContextManager;
import org.odpi.openmetadata.adminservices.configuration.registration.IntegrationServiceDescription;
import org.odpi.openmetadata.frameworks.openmetadata.enums.PermittedSynchronization;
import org.odpi.openmetadata.integrationservices.security.connector.SecurityIntegratorConnector;
import org.odpi.openmetadata.integrationservices.security.connector.SecurityIntegratorContext;
import org.odpi.openmetadata.integrationservices.security.ffdc.SecurityIntegratorAuditCode;
import org.odpi.openmetadata.integrationservices.security.ffdc.SecurityIntegratorErrorCode;

import java.util.Map;


/**
 * SecurityIntegratorContextManager provides the bridge between the integration daemon services and
 * the specific implementation of an integration service
 */
public class SecurityIntegratorContextManager extends IntegrationContextManager
{
    private MetadataSourceClient  metadataSourceClient;
    private SecurityManagerClient  securityManagerClient;
    private UserIdentityManagement userIdentityClient = null;

    /**
     * Default constructor
     */
    public SecurityIntegratorContextManager()
    {
    }


    /**
     * Initialize server properties for the context manager.
     *
     * @param localServerName name of this integration daemon
     * @param partnerOMASServerName name of the server to connect to
     * @param partnerOMASPlatformRootURL the network address of the server running the OMAS REST services
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     * @param serviceOptions options from the integration service's configuration
     * @param maxPageSize maximum number of results that can be returned on a single REST call
     * @param auditLog logging destination
     */
    public void initializeContextManager(String              localServerName,
                                         String              partnerOMASServerName,
                                         String              partnerOMASPlatformRootURL,
                                         String              userId,
                                         String              password,
                                         Map<String, Object> serviceOptions,
                                         int                 maxPageSize,
                                         AuditLog            auditLog)
    {
        super.initializeContextManager(localServerName, partnerOMASServerName, partnerOMASPlatformRootURL, userId, password, serviceOptions, maxPageSize, auditLog);

        final String methodName = "initializeContextManager";

        auditLog.logMessage(methodName,
                            SecurityIntegratorAuditCode.CONTEXT_INITIALIZING.getMessageDefinition(partnerOMASServerName, partnerOMASPlatformRootURL));
    }


    /**
     * Suggestion for subclass to create client(s) to partner OMAS.
     *
     * @param maxPageSize maximum value allowed for page size
     * @throws InvalidParameterException the subclass is not able to create one of its clients
     */
    @Override
    public  void createClients(int maxPageSize) throws InvalidParameterException
    {
        super.openIntegrationClient = new OpenIntegrationServiceClient(partnerOMASServerName, partnerOMASPlatformRootURL, maxPageSize);
        super.openMetadataStoreClient = new OpenMetadataStoreClient(partnerOMASServerName, partnerOMASPlatformRootURL, maxPageSize);
        super.actionControlInterface = new OpenGovernanceClient(partnerOMASServerName, partnerOMASPlatformRootURL, maxPageSize);
        super.governanceConfiguration = new GovernanceConfigurationClient(partnerOMASServerName, partnerOMASPlatformRootURL, maxPageSize);

        SecurityManagerRESTClient restClient;

        if (localServerPassword == null)
        {
            restClient = new SecurityManagerRESTClient(partnerOMASServerName,
                                                       partnerOMASPlatformRootURL,
                                                       auditLog);
        }
        else
        {
            restClient = new SecurityManagerRESTClient(partnerOMASServerName,
                                                       partnerOMASPlatformRootURL,
                                                       localServerUserId,
                                                       localServerPassword,
                                                       auditLog);
        }

        metadataSourceClient = new MetadataSourceClient(partnerOMASServerName,
                                                        partnerOMASPlatformRootURL,
                                                        restClient,
                                                        maxPageSize);

        securityManagerClient = new SecurityManagerClient(partnerOMASServerName,
                                                          partnerOMASPlatformRootURL,
                                                          restClient,
                                                          maxPageSize);

        if (localServerPassword == null)
        {
            userIdentityClient = new UserIdentityManagement(localServerName,
                                                            partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            auditLog,
                                                            maxPageSize);
        }
        else
        {
            userIdentityClient = new UserIdentityManagement(localServerName,
                                                            partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            localServerUserId,
                                                            localServerPassword,
                                                            auditLog,
                                                            maxPageSize);
        }
    }


    /**
     * Retrieve the metadata source's unique identifier (GUID) or if it is not defined, create the software server capability
     * for this integrator.
     *
     * @param metadataSourceQualifiedName unique name of the software server capability that represents this integration service
     *
     * @return unique identifier of the metadata source
     *
     * @throws InvalidParameterException one of the parameters passed (probably on initialize) is invalid
     * @throws UserNotAuthorizedException the integration daemon's userId does not have access to the partner OMAS
     * @throws PropertyServerException there is a problem in the remote server running the partner OMAS
     */
    private String setUpMetadataSource(String   metadataSourceQualifiedName) throws InvalidParameterException,
                                                                                    UserNotAuthorizedException,
                                                                                    PropertyServerException
    {
        if (metadataSourceQualifiedName != null)
        {
            String metadataSourceGUID = metadataSourceClient.getExternalSecurityManagerGUID(localServerUserId, metadataSourceQualifiedName);

            if (metadataSourceGUID == null)
            {
                SecurityManagerProperties properties = new SecurityManagerProperties();

                properties.setQualifiedName(metadataSourceQualifiedName);

                metadataSourceGUID = metadataSourceClient.createExternalSecurityManager(localServerUserId, null, null, null, properties);
            }

            return metadataSourceGUID;
        }

        return null;
    }


    /**
     * Set up the context in the supplied connector. This is called between initialize() and start() on the connector.
     *
     * @param connectorId unique identifier of the connector (used to configure the event listener)
     * @param connectorName name of connector from config
     * @param connectorUserId userId for the connector
     * @param integrationConnector connector created from connection integration service configuration
     * @param integrationConnectorGUID unique identifier of the integration connector entity (only set if working with integration groups)
     * @param permittedSynchronization controls the direction(s) that metadata is allowed to flow
     * @param generateIntegrationReport should the connector generate an integration reports?
     * @param metadataSourceQualifiedName unique name of the software server capability that represents the metadata source.
     *
     * @return the new integration context
     * @throws InvalidParameterException the connector is not of the correct type
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException problem accessing the property server
     */
    @Override
    public IntegrationContext setContext(String                   connectorId,
                                         String                   connectorName,
                                         String                   connectorUserId,
                                         IntegrationConnector     integrationConnector,
                                         String                   integrationConnectorGUID,
                                         PermittedSynchronization permittedSynchronization,
                                         boolean                  generateIntegrationReport,
                                         String                   metadataSourceQualifiedName) throws InvalidParameterException,
                                                                                                      UserNotAuthorizedException,
                                                                                                      PropertyServerException
    {
        final String  methodName = "setContext";

        String permittedSynchronizationName = PermittedSynchronization.BOTH_DIRECTIONS.getName();
        String serviceOptionsString = "null";

        if (permittedSynchronization != null)
        {
            permittedSynchronizationName = permittedSynchronization.getName();
        }

        if (serviceOptions != null)
        {
            serviceOptionsString = serviceOptions.toString();
        }

        if (integrationConnector instanceof SecurityIntegratorConnector serviceSpecificConnector)
        {
            auditLog.logMessage(methodName,
                                SecurityIntegratorAuditCode.CONNECTOR_CONTEXT_INITIALIZING.getMessageDefinition(connectorName,
                                                                                                                connectorId,
                                                                                                                metadataSourceQualifiedName,
                                                                                                                permittedSynchronizationName,
                                                                                                                serviceOptionsString));

            String externalSourceGUID = this.setUpMetadataSource(metadataSourceQualifiedName,
                                                                 null,
                                                                 null,
                                                                 null);
            String externalSourceName = metadataSourceQualifiedName;

            if (externalSourceGUID == null)
            {
                externalSourceName = null;
            }

            SecurityManagerEventClient eventClient = new SecurityManagerEventClient(partnerOMASServerName,
                                                                                    partnerOMASPlatformRootURL,
                                                                                    localServerUserId,
                                                                                    localServerPassword,
                                                                                    connectorId);

            SecurityIntegratorContext integratorContext = new SecurityIntegratorContext(connectorId,
                                                                                        connectorName,
                                                                                        connectorUserId,
                                                                                        partnerOMASServerName,
                                                                                        openIntegrationClient,
                                                                                        governanceConfiguration,
                                                                                        openMetadataStoreClient,
                                                                                        actionControlInterface,
                                                                                        securityManagerClient,
                                                                                        userIdentityClient,
                                                                                        eventClient,
                                                                                        generateIntegrationReport,
                                                                                        permittedSynchronization,
                                                                                        integrationConnectorGUID,
                                                                                        externalSourceGUID,
                                                                                        externalSourceName,
                                                                                        maxPageSize,
                                                                                        auditLog);
            serviceSpecificConnector.setContext(integratorContext);
            integrationConnector.setConnectorName(connectorName);

            return integratorContext;
        }
        else
        {
            final String  parameterName = "integrationConnector";

            throw new InvalidParameterException(
                    SecurityIntegratorErrorCode.INVALID_CONNECTOR.getMessageDefinition(connectorName,
                                                                                      IntegrationServiceDescription.SECURITY_INTEGRATOR_OMIS.getIntegrationServiceFullName(),
                                                                                      SecurityIntegratorConnector.class.getCanonicalName()),
                    this.getClass().getName(),
                    methodName,
                    parameterName);
        }
    }

}
