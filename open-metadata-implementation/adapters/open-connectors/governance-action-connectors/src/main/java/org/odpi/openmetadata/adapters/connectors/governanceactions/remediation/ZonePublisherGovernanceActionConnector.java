/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.governanceactions.remediation;

import org.odpi.openmetadata.adapters.connectors.governanceactions.ffdc.GovernanceActionConnectorsAuditCode;
import org.odpi.openmetadata.adapters.connectors.governanceactions.ffdc.GovernanceActionConnectorsErrorCode;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.OCFCheckedExceptionBase;
import org.odpi.openmetadata.frameworks.governanceaction.RemediationGovernanceActionService;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.frameworks.governanceaction.properties.*;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;
import org.odpi.openmetadata.frameworks.governanceaction.search.PropertyHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ZonePublisherGovernanceActionConnector sets the supplied governance zone names into the assets supplied as action targets.
 * If there is at least one asset, their zones are updated, and the output guard is set to zone-assigned.
 * If no Assets are passed as action targets the output guard is no-targets-detected.
 */
public class ZonePublisherGovernanceActionConnector extends RemediationGovernanceActionService
{
    /**
     * Value used to set up the zones.
     */
    private List<String> publishZones = null;

    /**
     * Indicates that the governance action service is completely configured and can begin processing.
     * This is a standard method from the Open Connector Framework (OCF) so
     * be sure to call super.start() at the start of your overriding version.
     *
     * @throws ConnectorCheckedException there is a problem within the governance action service.
     */
    @Override
    public void start() throws ConnectorCheckedException
    {
        final String methodName = "start";

        super.start();

        Map<String, Object> configurationProperties = connectionProperties.getConfigurationProperties();

        /*
         * Retrieve the configuration properties from the Connection object.  These properties affect all requests to this connector.
         */
        if (configurationProperties != null)
        {
            Object publishZonesOption = configurationProperties.get(ZonePublisherRequestParameter.PUBLISH_ZONES.getName());

            if (publishZonesOption != null)
            {
                publishZones = getZoneArrayFromString(publishZonesOption.toString());
            }
        }

        /*
         * Retrieve the zones to set in the assets.  This may override the value set in the configuration properties.
         */
        if (governanceContext.getRequestParameters() != null)
        {
            Map<String, String> requestParameters = governanceContext.getRequestParameters();

            for (String requestParameterName : requestParameters.keySet())
            {
                if (requestParameterName != null)
                {
                    if (ZonePublisherRequestParameter.PUBLISH_ZONES.getName().equals(requestParameterName))
                    {
                        publishZones = getZoneArrayFromString(requestParameters.get(requestParameterName));
                    }
                }
            }
        }

        List<String>        outputGuards = new ArrayList<>();
        CompletionStatus    completionStatus;

        try
        {
            if (governanceContext.getActionTargetElements() == null)
            {
                if (auditLog != null)
                {
                    auditLog.logMessage(methodName,
                                        GovernanceActionConnectorsAuditCode.NO_TARGETS.getMessageDefinition(governanceServiceName));
                }

                completionStatus = ZonePublisherGuard.NO_TARGETS_DETECTED.getCompletionStatus();
                outputGuards.add(ZonePublisherGuard.NO_TARGETS_DETECTED.getName());
            }
            else if ((publishZones == null) || (publishZones.isEmpty()))
            {
                if (auditLog != null)
                {
                    auditLog.logMessage(methodName, GovernanceActionConnectorsAuditCode.NO_ZONES.getMessageDefinition(governanceServiceName));
                }

                for (ActionTargetElement actionTarget : governanceContext.getActionTargetElements())
                {
                    if ((actionTarget != null) &&
                            (actionTarget.getTargetElement() != null) &&
                            (propertyHelper.isTypeOf(actionTarget.getTargetElement(), OpenMetadataType.ASSET.typeName)))
                    {
                        OpenMetadataElement element = actionTarget.getTargetElement();

                        if (auditLog != null)
                        {
                            auditLog.logMessage(methodName,
                                                GovernanceActionConnectorsAuditCode.SETTING_ZONES.getMessageDefinition(governanceServiceName,
                                                                                                                       element.getElementGUID(),
                                                                                                                       publishZones.toString()));
                        }

                        governanceContext.declassifyMetadataElement(element.getElementGUID(),
                                                                    OpenMetadataType.ASSET_ZONES_CLASSIFICATION_NAME,
                                                                    false,
                                                                    false,
                                                                    new Date());
                    }
                }

                completionStatus = ZonePublisherGuard.NO_ZONES_DETECTED.getCompletionStatus();
                outputGuards.add(ZonePublisherGuard.NO_ZONES_DETECTED.getName());
            }
            else
            {
                ElementProperties properties = this.getZoneProperties();

                for (ActionTargetElement actionTarget : governanceContext.getActionTargetElements())
                {
                    if ((actionTarget != null) &&
                            (actionTarget.getTargetElement() != null) &&
                            (propertyHelper.isTypeOf(actionTarget.getTargetElement(), OpenMetadataType.ASSET.typeName)))
                    {
                        OpenMetadataElement element = actionTarget.getTargetElement();

                        if (auditLog != null)
                        {
                            auditLog.logMessage(methodName,
                                                GovernanceActionConnectorsAuditCode.SETTING_ZONES.getMessageDefinition(governanceServiceName,
                                                                                                                       element.getElementGUID(),
                                                                                                                       publishZones.toString()));
                        }

                        governanceContext.classifyMetadataElement(element.getElementGUID(),
                                                                  OpenMetadataType.ASSET_ZONES_CLASSIFICATION_NAME,
                                                                  false,
                                                                  false,
                                                                  properties,
                                                                  new Date());
                    }
                }

                completionStatus = ZonePublisherGuard.ZONE_ASSIGNED.getCompletionStatus();
                outputGuards.add(ZonePublisherGuard.ZONE_ASSIGNED.getName());
            }

            governanceContext.recordCompletionStatus(completionStatus, outputGuards);
        }
        catch (OCFCheckedExceptionBase error)
        {
            throw new ConnectorCheckedException(error.getReportedErrorMessage(), error);
        }
        catch (Exception error)
        {
            throw new ConnectorCheckedException(GovernanceActionConnectorsErrorCode.UNEXPECTED_EXCEPTION.getMessageDefinition(governanceServiceName,
                                                                                                                              error.getClass().getName(),
                                                                                                                              error.getMessage()),
                                                error.getClass().getName(),
                                                methodName,
                                                error);
        }
    }


    /**
     * Return the new zones in a property.
     *
     * @return element properties for the AssetZoneMembership classification
     */
    private ElementProperties getZoneProperties()
    {
        return propertyHelper.addStringArrayProperty(null, OpenMetadataProperty.ZONE_MEMBERSHIP.name, publishZones);
    }


    /**
     * Return the supplied zones as an array.
     *
     * @param zones comma separated list of zone names
     * @return string array with a zone name in each element
     */
    private List<String> getZoneArrayFromString(String  zones)
    {
        List<String>    zoneArray       = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(zones, ",");

        while (stringTokenizer.hasMoreTokens())
        {
            String zone = stringTokenizer.nextToken();
            String trimmedZone = zone.trim();

            if (! trimmedZone.isEmpty())
            {
                zoneArray.add(trimmedZone);
            }
        }

        return zoneArray;
    }
}
