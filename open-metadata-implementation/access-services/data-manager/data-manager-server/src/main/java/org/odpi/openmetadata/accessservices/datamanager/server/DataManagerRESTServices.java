/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.datamanager.server;

import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceDescription;
import org.odpi.openmetadata.commonservices.ffdc.RESTCallLogger;
import org.odpi.openmetadata.commonservices.ffdc.RESTCallToken;
import org.odpi.openmetadata.commonservices.ffdc.RESTExceptionHandler;
import org.odpi.openmetadata.commonservices.ffdc.rest.*;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.SoftwareCapabilityElement;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.commonservices.generichandlers.SoftwareCapabilityHandler;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;

import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * The DataManagerRESTServices provides the server-side implementation of the services
 * that are generic for all types of data managers.
 */
public class DataManagerRESTServices
{
    private static final DataManagerInstanceHandler instanceHandler = new DataManagerInstanceHandler();

    private static final RESTCallLogger       restCallLogger       = new RESTCallLogger(LoggerFactory.getLogger(DataManagerRESTServices.class),
                                                                                        instanceHandler.getServiceName());
    private final RESTExceptionHandler restExceptionHandler = new RESTExceptionHandler();

    /**
     * Default constructor
     */
    public DataManagerRESTServices()
    {
    }


    /**
     * Return service description method.  This method is used to ensure Spring loads this module.
     *
     * @param serverName called server
     * @param userId calling user
     * @return service description
     */
    public RegisteredOMAGServiceResponse getServiceDescription(String serverName,
                                                               String userId)
    {
        final String methodName = "getServiceDescription";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        RegisteredOMAGServiceResponse response = new RegisteredOMAGServiceResponse();
        AuditLog                      auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            response.setService(instanceHandler.getRegisteredOMAGService(userId,
                                                                         serverName,
                                                                         AccessServiceDescription.DATA_MANAGER_OMAS.getAccessServiceCode(),
                                                                         methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Return the connection object for the Data Manager OMAS's out topic.
     *
     * @param serverName name of the service to route the request to.
     * @param userId identifier of calling user.
     * @param callerId unique identifier of the caller
     *
     * @return connection object for the out topic or
     * InvalidParameterException one of the parameters is null or invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException problem retrieving the discovery engine definition.
     */
    public OCFConnectionResponse getOutTopicConnection(String serverName,
                                                       String userId,
                                                       String callerId)
    {
        final String methodName = "getOutTopicConnection";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        OCFConnectionResponse response = new OCFConnectionResponse();
        AuditLog              auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            response.setConnection(instanceHandler.getOutTopicConnection(userId, serverName, methodName, callerId));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }



    /**
     * Files live on a file system.  This method creates a top level capability for a file system.
     *
     * @param serverName name of calling server
     * @param userId calling user
     * @param requestBody properties of the file system
     *
     * @return unique identifier for the file system or
     * InvalidParameterException one of the parameters is null or invalid or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public GUIDResponse   createFileSystemInCatalog(String                serverName,
                                                    String                userId,
                                                    FileSystemRequestBody requestBody)
    {
        final String methodName = "createFileSystemInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            if (requestBody != null)
            {
                SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                                  serverName,
                                                                                                                                  methodName);

                response.setGUID(handler.createFileSystem(userId,
                                                          requestBody.getExternalSourceGUID(),
                                                          requestBody.getExternalSourceName(),
                                                          requestBody.getQualifiedName(),
                                                          requestBody.getResourceName(),
                                                          requestBody.getResourceDescription(),
                                                          requestBody.getDeployedImplementationType(),
                                                          requestBody.getVersion(),
                                                          requestBody.getPatchLevel(),
                                                          requestBody.getSource(),
                                                          requestBody.getFormat(),
                                                          requestBody.getEncryption(),
                                                          requestBody.getAdditionalProperties(),
                                                          requestBody.getVendorProperties(),
                                                          null,
                                                          null,
                                                          false,
                                                          false,
                                                          new Date(),
                                                          methodName));
            }
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Files live on a file system.  This method creates a top level capability for a file system.
     *
     * @param serverName name of calling server
     * @param userId calling user
     * @param requestBody properties of the file system
     *
     * @return unique identifier for the file system or
     * InvalidParameterException one of the parameters is null or invalid or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public GUIDResponse  createFileManagerInCatalog(String                 serverName,
                                                    String                 userId,
                                                    FileManagerRequestBody requestBody)
    {
        final String methodName = "createFileManagerInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            if (requestBody != null)
            {
                SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                                  serverName,
                                                                                                                                  methodName);

                response.setGUID(handler.createSoftwareCapability(userId,
                                                                  requestBody.getExternalSourceGUID(),
                                                                  requestBody.getExternalSourceName(),
                                                                  OpenMetadataType.DATA_MANAGER.typeName,
                                                                  OpenMetadataType.FILE_MANAGER_CLASSIFICATION.typeName,
                                                                  requestBody.getQualifiedName(),
                                                                  requestBody.getResourceName(),
                                                                  requestBody.getResourceDescription(),
                                                                  requestBody.getDeployedImplementationType(),
                                                                  requestBody.getVersion(),
                                                                  requestBody.getPatchLevel(),
                                                                  requestBody.getSource(),
                                                                  requestBody.getAdditionalProperties(),
                                                                  requestBody.getExtendedProperties(),
                                                                  requestBody.getVendorProperties(),
                                                                  null,
                                                                  null,
                                                                  false,
                                                                  false,
                                                                  new Date(),
                                                                  methodName));
            }
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Create the software server capability for the database manager (DBMS).
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody description of the database manager
     *
     * @return unique identifier of the software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse createDatabaseManagerInCatalog(String                     serverName,
                                                       String                     userId,
                                                       DatabaseManagerRequestBody requestBody)
    {
        final String methodName = "createDatabaseManagerInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                              serverName,
                                                                                                                              methodName);
            response.setGUID(handler.createSoftwareCapability(userId,
                                                              requestBody.getExternalSourceGUID(),
                                                              requestBody.getExternalSourceName(),
                                                              OpenMetadataType.DATABASE_MANAGER.typeName,
                                                              null,
                                                              requestBody.getQualifiedName(),
                                                              requestBody.getResourceName(),
                                                              requestBody.getResourceDescription(),
                                                              requestBody.getDeployedImplementationType(),
                                                              requestBody.getVersion(),
                                                              requestBody.getPatchLevel(),
                                                              requestBody.getSource(),
                                                              requestBody.getAdditionalProperties(),
                                                              requestBody.getExtendedProperties(),
                                                              requestBody.getVendorProperties(),
                                                              null,
                                                              null,
                                                              false,
                                                              false,
                                                              new Date(),
                                                              methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }



    /**
     * Create the Software server capability for the API Manager.
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody description of the API manager
     *
     * @return unique identifier of the software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse createAPIManagerInCatalog(String                serverName,
                                                  String                userId,
                                                  APIManagerRequestBody requestBody)
    {
        final String methodName = "createAPIManagerInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                              serverName,
                                                                                                                              methodName);
            response.setGUID(handler.createSoftwareCapability(userId,
                                                              requestBody.getExternalSourceGUID(),
                                                              requestBody.getExternalSourceName(),
                                                              OpenMetadataType.API_MANAGER.typeName,
                                                              null,
                                                              requestBody.getQualifiedName(),
                                                              requestBody.getResourceName(),
                                                              requestBody.getResourceDescription(),
                                                              requestBody.getDeployedImplementationType(),
                                                              requestBody.getVersion(),
                                                              requestBody.getPatchLevel(),
                                                              requestBody.getSource(),
                                                              requestBody.getAdditionalProperties(),
                                                              requestBody.getExtendedProperties(),
                                                              requestBody.getVendorProperties(),
                                                              null,
                                                              null,
                                                              false,
                                                              false,
                                                              new Date(),
                                                              methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Create the Software server capability for the Event Broker.
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody description of the Event Broker
     *
     * @return unique identifier of the software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse createEventBrokerInCatalog(String                 serverName,
                                                   String                 userId,
                                                   EventBrokerRequestBody requestBody)
    {
        final String methodName = "createEventBrokerInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                              serverName,
                                                                                                                              methodName);
            response.setGUID(handler.createSoftwareCapability(userId,
                                                              requestBody.getExternalSourceGUID(),
                                                              requestBody.getExternalSourceName(),
                                                              OpenMetadataType.EVENT_BROKER.typeName,
                                                              null,
                                                              requestBody.getQualifiedName(),
                                                              requestBody.getResourceName(),
                                                              requestBody.getResourceDescription(),
                                                              requestBody.getDeployedImplementationType(),
                                                              requestBody.getVersion(),
                                                              requestBody.getPatchLevel(),
                                                              requestBody.getSource(),
                                                              requestBody.getAdditionalProperties(),
                                                              requestBody.getExtendedProperties(),
                                                              requestBody.getVendorProperties(),
                                                              null,
                                                              null,
                                                              false,
                                                              false,
                                                              new Date(),
                                                              methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Create the Software server capability for an Application.
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody description of the Application
     *
     * @return unique identifier of the software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse createApplicationInCatalog(String                 serverName,
                                                   String                 userId,
                                                   ApplicationRequestBody requestBody)
    {
        final String methodName = "createApplicationInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                              serverName,
                                                                                                                              methodName);
            response.setGUID(handler.createSoftwareCapability(userId,
                                                              requestBody.getExternalSourceGUID(),
                                                              requestBody.getExternalSourceName(),
                                                              OpenMetadataType.APPLICATION.typeName,
                                                              null,
                                                              requestBody.getQualifiedName(),
                                                              requestBody.getResourceName(),
                                                              requestBody.getResourceDescription(),
                                                              requestBody.getDeployedImplementationType(),
                                                              requestBody.getVersion(),
                                                              requestBody.getPatchLevel(),
                                                              requestBody.getSource(),
                                                              requestBody.getAdditionalProperties(),
                                                              requestBody.getExtendedProperties(),
                                                              requestBody.getVendorProperties(),
                                                              null,
                                                              null,
                                                              false,
                                                              false,
                                                              new Date(),
                                                              methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Create the Software server capability for a data processing engine such as a reporting engine.
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody description of the data processing engine
     *
     * @return unique identifier of the software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse createDataProcessingEngineInCatalog(String                          serverName,
                                                            String                          userId,
                                                            DataProcessingEngineRequestBody requestBody)
    {
        final String methodName = "createDataProcessingEngineInCatalog";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId,
                                                                                                                              serverName,
                                                                                                                              methodName);
            response.setGUID(handler.createSoftwareCapability(userId,
                                                              requestBody.getExternalSourceGUID(),
                                                              requestBody.getExternalSourceName(),
                                                              OpenMetadataType.ENGINE.typeName,
                                                              null,
                                                              requestBody.getQualifiedName(),
                                                              requestBody.getResourceName(),
                                                              requestBody.getResourceDescription(),
                                                              requestBody.getDeployedImplementationType(),
                                                              requestBody.getVersion(),
                                                              requestBody.getPatchLevel(),
                                                              requestBody.getSource(),
                                                              requestBody.getAdditionalProperties(),
                                                              requestBody.getExtendedProperties(),
                                                              requestBody.getVendorProperties(),
                                                              null,
                                                              null,
                                                              false,
                                                              false,
                                                              new Date(),
                                                              methodName));
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }


    /**
     * Retrieve the unique identifier of the integration daemon service.
     *
     * @param serverName name of the server to route the request to.
     * @param userId calling user
     * @param requestBody unique name of the integration daemon
     *
     * @return unique identifier of the integration daemon's software server capability or
     * InvalidParameterException  the bean properties are invalid or
     * UserNotAuthorizedException user not authorized to issue this request or
     * PropertyServerException    problem accessing the property server
     */
    public GUIDResponse  getMetadataSourceGUID(String serverName,
                                               String userId,
                                               NameRequestBody requestBody)
    {
        final String methodName = "getMetadataSourceGUID";
        final String parameterName = "qualifiedName";

        RESTCallToken token = restCallLogger.logRESTCall(serverName, userId, methodName);

        GUIDResponse response = new GUIDResponse();
        AuditLog     auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);

            if (requestBody != null)
            {
                SoftwareCapabilityHandler<SoftwareCapabilityElement> handler = instanceHandler.getSoftwareServerCapabilityHandler(userId, serverName, methodName);

                response.setGUID(handler.getBeanGUIDByQualifiedName(userId,
                                                                    OpenMetadataType.SOFTWARE_CAPABILITY.typeGUID,
                                                                    OpenMetadataType.SOFTWARE_CAPABILITY.typeName,
                                                                    requestBody.getName(),
                                                                    parameterName,
                                                                    false,
                                                                    false,
                                                                    new Date(),
                                                                    methodName));
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureRuntimeExceptions(response, error, methodName, auditLog);
        }

        restCallLogger.logRESTCallReturn(token, response.toString());

        return response;
    }
}
