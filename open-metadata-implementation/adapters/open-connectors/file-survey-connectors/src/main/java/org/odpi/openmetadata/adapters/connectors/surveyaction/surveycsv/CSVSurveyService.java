/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.surveyaction.surveycsv;

import org.odpi.openmetadata.adapters.connectors.datastore.csvfile.CSVFileStoreConnector;
import org.odpi.openmetadata.frameworks.surveyaction.controls.SurveyFileAnnotationType;
import org.odpi.openmetadata.adapters.connectors.surveyaction.extractors.FileStatsExtractor;
import org.odpi.openmetadata.frameworks.connectors.Connector;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.AssetUniverse;
import org.odpi.openmetadata.frameworks.connectors.properties.NestedSchemaType;
import org.odpi.openmetadata.frameworks.connectors.properties.SchemaAttributes;
import org.odpi.openmetadata.frameworks.openmetadata.enums.ElementStatus;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.SchemaAttribute;
import org.odpi.openmetadata.frameworks.governanceaction.OpenMetadataStore;
import org.odpi.openmetadata.frameworks.governanceaction.search.ElementProperties;
import org.odpi.openmetadata.frameworks.governanceaction.search.PropertyHelper;
import org.odpi.openmetadata.frameworks.openmetadata.properties.ArchiveProperties;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataProperty;
import org.odpi.openmetadata.frameworks.openmetadata.types.OpenMetadataType;
import org.odpi.openmetadata.frameworks.surveyaction.AnnotationStore;
import org.odpi.openmetadata.frameworks.surveyaction.SurveyActionServiceConnector;
import org.odpi.openmetadata.frameworks.surveyaction.SurveyAssetStore;
import org.odpi.openmetadata.frameworks.surveyaction.SurveyOpenMetadataStore;
import org.odpi.openmetadata.frameworks.surveyaction.controls.AnalysisStep;
import org.odpi.openmetadata.frameworks.surveyaction.properties.Annotation;
import org.odpi.openmetadata.frameworks.openmetadata.enums.AnnotationStatus;
import org.odpi.openmetadata.frameworks.surveyaction.properties.ResourceProfileAnnotation;
import org.odpi.openmetadata.frameworks.surveyaction.properties.SchemaAnalysisAnnotation;

import java.io.File;
import java.util.*;


/**
 * CSVSurveyService is a survey action service implementation for analysing CSV Files to
 * columns and profile the data in them.
 */
public class CSVSurveyService extends SurveyActionServiceConnector
{
    private final static String STRING_TYPE_NAME  = "string";
    private final static String CHAR_TYPE_NAME    = "char";
    private final static String DATE_TYPE_NAME    = "date";
    private final static String INT_TYPE_NAME     = "int";
    private final static String LONG_TYPE_NAME    = "long";
    private final static String BOOLEAN_TYPE_NAME = "boolean";
    private final static String FLOAT_TYPE_NAME   = "float";
    private final static String BOOLEAN_UC_TRUE   = "TRUE";
    private final static String BOOLEAN_LC_TRUE   = "true";
    private final static String BOOLEAN_UC_FALSE  = "FALSE";
    private final static String BOOLEAN_LC_FALSE  = "false";
    private final static String schemaType  = OpenMetadataType.TABULAR_SCHEMA_TYPE.typeName;
    private final static String schemaName  = "CSV";

    private final PropertyHelper propertyHelper = new PropertyHelper();

    private Connector connector = null;


    /**
     * Return the best guess for the data field type.
     *
     * @param existingType current type guess
     * @param newFieldValue next field value to process
     * @return new best guess
     */
    private String getDataFieldType(String  existingType, String newFieldValue)
    {
        if (STRING_TYPE_NAME.equals(existingType))
        {
            return STRING_TYPE_NAME;
        }

        if (newFieldValue == null)
        {
            return existingType;
        }

        if ((BOOLEAN_LC_TRUE.equals(newFieldValue)) ||
            (BOOLEAN_UC_TRUE.equals(newFieldValue)) ||
            (BOOLEAN_LC_FALSE.equals(newFieldValue)) ||
            (BOOLEAN_UC_FALSE.equals(newFieldValue)))

        {
            if ((existingType == null) || (existingType.equals(BOOLEAN_TYPE_NAME)))
            {
                return BOOLEAN_TYPE_NAME;
            }
            else
            {
                return STRING_TYPE_NAME;
            }
        }

        return STRING_TYPE_NAME;
    }


    /**
     * Return the updated value count for this column.
     *
     * @param existingValueCount current value count
     * @param newFieldValue next field value to process
     * @return new value count
     */
    private Map<String, Integer> getValueCount(Map<String, Integer> existingValueCount, String newFieldValue)
    {
        if (existingValueCount == null)
        {
            Map<String, Integer> newValueCount = new HashMap<>();

            newValueCount.put(newFieldValue, 1);

            return newValueCount;
        }

        Integer   countForValue = existingValueCount.get(newFieldValue);

        if (countForValue == null)
        {
            existingValueCount.put(newFieldValue, 1);
        }
        else
        {
            existingValueCount.put(newFieldValue, countForValue + 1);
        }

        return existingValueCount;
    }


    /**
     * Return the updated value list.
     *
     * @param existingValueList existing value list
     * @param newFieldValue next field value to process
     * @return updated value list
     */
    private List<String> getValueList(List<String> existingValueList, String newFieldValue)
    {
        if (existingValueList == null)
        {
            List<String> newValueList = new ArrayList<>();

            newValueList.add(newFieldValue);

            return newValueList;
        }

        if (! existingValueList.contains(newFieldValue))
        {
            existingValueList.add(newFieldValue);
        }

        return existingValueList;
    }


    /**
     * Indicates that the survey action service is completely configured and can begin processing.
     *
     * @throws ConnectorCheckedException there is a problem within the discovery service.
     */
    @Override
    public void start() throws ConnectorCheckedException
    {
        final String  methodName = "start";

        super.start();

        try
        {
            AnnotationStore          annotationStore   = surveyContext.getAnnotationStore();
            SurveyOpenMetadataStore  openMetadataStore = surveyContext.getOpenMetadataStore();
            SurveyAssetStore         assetStore        = surveyContext.getAssetStore();

            /*
             * The asset should have a special connector for CSV files.  If the connector is wrong,
             * the cast will fail.
             */
            connector = super.performCheckAssetAnalysisStep(CSVFileStoreConnector.class,
                                                            OpenMetadataType.CSV_FILE.typeName);

            AssetUniverse            assetUniverse   = assetStore.getAssetProperties();
            CSVFileStoreConnector    assetConnector  = (CSVFileStoreConnector)connector;
            long                     recordCount     = assetConnector.getRecordCount();

            File file = assetConnector.getFile();

            annotationStore.setAnalysisStep(AnalysisStep.MEASURE_RESOURCE.getName());

            FileStatsExtractor fileStatsExtractor = new FileStatsExtractor(file,
                                                                           surveyContext.getFileClassifier(),
                                                                           this);

            Annotation measurementAnnotation = fileStatsExtractor.getAnnotation();

            if (measurementAnnotation != null)
            {
                annotationStore.addAnnotation(measurementAnnotation, surveyContext.getAssetGUID());
            }


            /*
             * If a schema type is attached, it must be of the correct type so that information from the asset can
             * be matched to it.
             */
            annotationStore.setAnalysisStep(AnalysisStep.SCHEMA_EXTRACTION.getName());

            String           schemaTypeGUID;
            SchemaAttributes schemaAttributes;
            NestedSchemaType nestedSchemaType = super.getNestedSchemaType(assetUniverse, schemaType);

            if (nestedSchemaType == null)
            {
                /*
                 * Set up a new schema type.
                 */
                schemaTypeGUID   = createSchemaType(openMetadataStore, assetUniverse);
                schemaAttributes = null;
            }
            else
            {
                schemaTypeGUID   = nestedSchemaType.getGUID();
                schemaAttributes = nestedSchemaType.getSchemaAttributes();
            }

            List<String>             columnNames     = assetConnector.getColumnNames();

            /*
             * Store the key dimensions of the schema.
             */
            SchemaAnalysisAnnotation  schemaAnnotation = new SchemaAnalysisAnnotation();

            schemaAnnotation.setAnnotationType(SurveyFileAnnotationType.DERIVE_SCHEMA_FROM_DATA.getName());
            schemaAnnotation.setSummary(SurveyFileAnnotationType.DERIVE_SCHEMA_FROM_DATA.getSummary());
            schemaAnnotation.setExplanation(SurveyFileAnnotationType.DERIVE_SCHEMA_FROM_DATA.getExplanation());
            schemaAnnotation.setAnalysisStep(SurveyFileAnnotationType.DERIVE_SCHEMA_FROM_DATA.getAnalysisStep());
            schemaAnnotation.setSchemaName(schemaName);
            schemaAnnotation.setSchemaTypeName(schemaType);
            schemaAnnotation.setAnnotationStatus(AnnotationStatus.NEW_ANNOTATION);

            Map<String, String> additionalProperties = new HashMap<>();

            if (columnNames != null)
            {
                additionalProperties.put("Column Count", Integer.toString(columnNames.size()));
                additionalProperties.put("Column Names", columnNames.toString());
            }
            else
            {
                additionalProperties.put("Column Count", "0");
                additionalProperties.put("Column Names", "[]");
            }

            schemaAnnotation.setAdditionalProperties(additionalProperties);

            annotationStore.addAnnotation(schemaAnnotation, schemaTypeGUID);

            annotationStore.setAnalysisStep(AnalysisStep.PROFILE_DATA.getName());

            /*
             * Perform the analysis on the store.
             */
            Map<Integer, DataField> dataFields  = new HashMap<>();

            if (columnNames != null)
            {
                int position = 0;

                for (String  columnName : columnNames)
                {
                    if (columnName != null)
                    {
                        DataField  dataField = new DataField();

                        dataField.setDataFieldPosition(position);
                        dataField.setDataFieldName(columnName);

                        ResourceProfileAnnotation dataProfile = dataField.getDataProfileAnnotation();

                        dataProfile.setAnnotationType("InspectDataValues");
                        dataProfile.setSummary("Iterate through values to determine values present and how often they appear.");

                        dataFields.put(position, dataField);
                        position++;
                    }
                }

                for (long recordNumber=0; recordNumber < recordCount ; recordNumber++)
                {
                    List<String>  recordValues = assetConnector.readRecord(recordNumber);

                    if ((recordValues != null) && (! recordValues.isEmpty()))
                    {
                        int columnPosition = 0;
                        int recordLength = 0;

                        for (String fieldValue : recordValues)
                        {
                            DataField                 dataField   = dataFields.get(columnPosition);
                            ResourceProfileAnnotation dataProfile = dataField.getDataProfileAnnotation();

                            dataField.setDataFieldType(this.getDataFieldType(dataField.getDataFieldType(), fieldValue));

                            dataProfile.setValueCount(this.getValueCount(dataProfile.getValueCount(), fieldValue));
                            dataProfile.setValueList(this.getValueList(dataProfile.getValueList(), fieldValue));

                            recordLength = recordLength + fieldValue.length();

                            columnPosition++;
                        }
                    }
                }

                /*
                 * Match the schema attributes and the annotations
                 */
                if (schemaAttributes == null)
                {
                    /*
                     * Add a completely new schema based on the discovered columns.
                     */
                    for (int columnNumber=0 ; columnNumber < columnNames.size(); columnNumber++)
                    {
                        String  schemaAttributeGUID = this.addSchemaAttributeToSchemaType(openMetadataStore,
                                                                                          assetUniverse,
                                                                                          schemaTypeGUID,
                                                                                          dataFields.get(columnNumber));

                        annotationStore.addAnnotation(dataFields.get(columnNumber).getDataProfileAnnotation(), schemaAttributeGUID);
                    }
                }
                else
                {
                    /*
                     * Need to match the discovered data fields with the existing schema attributes.
                     * The aim is to have the open metadata schema exactly match the columns found in the
                     * CSV file.  We also want to preserve schema attributes that do match so previous annotations
                     * and lineage relationships are not lost.
                     *
                     * CSV files are positional.  They may have multiple columns with the same names,
                     * so we can only cater for small changes to the schema, such as adding/deleting columns.
                     * A comprehensive reorganization of the columns will result in a new schema.
                     */
                    int columnNumber = 0;
                    while (schemaAttributes.hasNext())
                    {
                        SchemaAttribute schemaAttribute = schemaAttributes.next();
                        boolean found = false;

                        for (int i=columnNumber; i<columnNames.size(); i++)
                        {
                            DataField dataField = dataFields.get(i);
                            if (dataField.getDataFieldName().equals(schemaAttribute.getDisplayName()))
                            {
                                dataField.setMatchingSchemaAttributeGUID(schemaAttribute.getGUID());
                                found = true;
                                break;
                            }
                        }

                        if (! found)
                        {
                            ArchiveProperties archiveProperties = new ArchiveProperties();

                            archiveProperties.setArchiveDate(new Date());
                            archiveProperties.setArchiveProcess(surveyActionServiceName);
                            openMetadataStore.archiveMetadataElementInStore(schemaAttribute.getGUID(),
                                                                            archiveProperties);
                        }
                    }

                    /*
                     * At this point, all the excess schema attributes have been archived.
                     * Just need to update the schema with the new values from the data fields.
                     */
                    for (int i=0; i<columnNames.size(); i++)
                    {
                        DataField dataField = dataFields.get(i);

                        if (dataField.getMatchingSchemaAttributeGUID() == null)
                        {
                            String schemaAttributeGUID = addSchemaAttributeToSchemaType(openMetadataStore,
                                                                                        assetUniverse,
                                                                                        schemaTypeGUID,
                                                                                        dataField);
                            dataField.setMatchingSchemaAttributeGUID(schemaAttributeGUID);
                        }
                        else
                        {
                            updateSchemaAttribute(openMetadataStore,
                                                  dataField.getMatchingSchemaAttributeGUID(),
                                                  dataField);
                        }

                        annotationStore.addAnnotation(dataField.getDataProfileAnnotation(),
                                                      dataField.getMatchingSchemaAttributeGUID());
                    }
                }
            }
        }
        catch (ConnectorCheckedException error)
        {
            throw error;
        }
        catch (Exception error)
        {
            super.handleUnexpectedException(methodName, error);
        }
    }


    /**
     * Return the unique identifier of a new root schema type for the CSV file.
     *
     * @param openMetadataStore the client to the open metadata store
     * @param assetUniverse unique identifier of the asset.
     * @return string guid
     *
     * @throws InvalidParameterException invalid property
     * @throws PropertyServerException no working metadata repository
     * @throws UserNotAuthorizedException insufficient security
     */
    private String createSchemaType(SurveyOpenMetadataStore openMetadataStore, 
                                    AssetUniverse           assetUniverse) throws InvalidParameterException,
                                                                                  PropertyServerException,
                                                                                  UserNotAuthorizedException
    {
        ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                               OpenMetadataProperty.QUALIFIED_NAME.name, 
                                                                               assetUniverse.getQualifiedName() + "_rootSchemaType");
        
        String schemaTypeGUID = openMetadataStore.createMetadataElementInStore(schemaType,
                                                                               ElementStatus.ACTIVE,
                                                                               elementProperties);
        if (schemaTypeGUID != null)
        {
            openMetadataStore.createRelatedElementsInStore(OpenMetadataType.ASSET_SCHEMA_TYPE_RELATIONSHIP.typeName,
                                                           assetUniverse.getGUID(),
                                                           schemaTypeGUID,
                                                           null,
                                                           null,
                                                           null);
        }
        
        return schemaTypeGUID;
    }



    /**
     * Create a new schema attribute.
     *
     * @param openMetadataStore client to access the open metadata repositories
     * @param assetUniverse details of the asset
     * @param schemaTypeGUID unique identifier of the schema type to link to
     * @param dataField details of the latest retrieved values
     * @return unique identifier of the schema attribute
     * @throws InvalidParameterException invalid parameter
     * @throws PropertyServerException repository not working
     * @throws UserNotAuthorizedException insufficient security
     */
    private String addSchemaAttributeToSchemaType(OpenMetadataStore openMetadataStore,
                                                  AssetUniverse     assetUniverse,
                                                  String            schemaTypeGUID,
                                                  DataField         dataField) throws InvalidParameterException,
                                                                                      PropertyServerException,
                                                                                      UserNotAuthorizedException
    {
        ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                                               OpenMetadataProperty.QUALIFIED_NAME.name,
                                                                               assetUniverse.getQualifiedName() + "_column_" + dataField.dataFieldPosition + "_" + dataField.dataFieldName);

        elementProperties = propertyHelper.addStringProperty(elementProperties,
                                                             OpenMetadataProperty.DISPLAY_NAME.name,
                                                             dataField.getDataFieldName());

        elementProperties = propertyHelper.addIntProperty(elementProperties,
                                                          OpenMetadataProperty.POSITION.name,
                                                          dataField.getDataFieldPosition());

        Map<String, ElementProperties> initialClassifications = new HashMap<>();

        ElementProperties classificationProperties = propertyHelper.addStringProperty(null,
                                                                                      OpenMetadataProperty.SCHEMA_TYPE_NAME.name,
                                                                                      OpenMetadataType.PRIMITIVE_SCHEMA_TYPE.typeName);
        classificationProperties = propertyHelper.addStringProperty(classificationProperties,
                                                                    OpenMetadataProperty.DATA_TYPE.name,
                                                                    dataField.getDataFieldType());

        initialClassifications.put(OpenMetadataType.TYPE_EMBEDDED_ATTRIBUTE_CLASSIFICATION.typeName,
                                   classificationProperties);

        return openMetadataStore.createMetadataElementInStore(OpenMetadataType.TABULAR_COLUMN.typeName,
                                                              ElementStatus.ACTIVE,
                                                              initialClassifications,
                                                              assetUniverse.getGUID(),
                                                              false,
                                                              null,
                                                              null,
                                                              null,
                                                              elementProperties,
                                                              schemaTypeGUID,
                                                              OpenMetadataType.ATTRIBUTE_FOR_SCHEMA_RELATIONSHIP.typeName,
                                                              null,
                                                              true);
    }


    /**
     * Update the values in an existing schema attribute.
     *
     * @param openMetadataStore client to access the open metadata repositories
     * @param schemaAttributeGUID unique identifier of the schema attribute to update
     * @param dataField details of the latest retrieved values
     * @throws InvalidParameterException invalid parameter
     * @throws PropertyServerException repository not working
     * @throws UserNotAuthorizedException insufficient security
     */
    private void updateSchemaAttribute(SurveyOpenMetadataStore openMetadataStore,
                                       String                  schemaAttributeGUID,
                                       DataField               dataField) throws InvalidParameterException,
                                                                                 PropertyServerException,
                                                                                 UserNotAuthorizedException
    {
        ElementProperties elementProperties = propertyHelper.addStringProperty(null,
                                                             OpenMetadataProperty.DISPLAY_NAME.name,
                                                             dataField.getDataFieldName());

        elementProperties = propertyHelper.addIntProperty(elementProperties,
                                                          OpenMetadataProperty.POSITION.name,
                                                          dataField.getDataFieldPosition());

        openMetadataStore.updateMetadataElementInStore(schemaAttributeGUID, false, elementProperties);

        ElementProperties classificationProperties = propertyHelper.addStringProperty(null,
                                                                                      OpenMetadataProperty.SCHEMA_TYPE_NAME.name,
                                                                                      OpenMetadataType.PRIMITIVE_SCHEMA_TYPE.typeName);
        classificationProperties = propertyHelper.addStringProperty(classificationProperties,
                                                                    OpenMetadataProperty.DATA_TYPE.name,
                                                                    dataField.getDataFieldType());

        openMetadataStore.reclassifyMetadataElementInStore(schemaAttributeGUID,
                                                           OpenMetadataType.TYPE_EMBEDDED_ATTRIBUTE_CLASSIFICATION.typeName,
                                                           false,
                                                           classificationProperties);
    }


    /**
     * DataField acts as a cache of information about a single column in the CSV File
     */
    private static class DataField
    {
        private       String                    dataFieldName               = null;
        private       String                    dataFieldType               = null;
        private       int                       dataFieldPosition           = 0;
        private final ResourceProfileAnnotation resourceProfileAnnotation   = new ResourceProfileAnnotation();
        private       String                    matchingSchemaAttributeGUID = null;


        /**
         * Return the name of the column.
         *
         * @return string
         */
        public String getDataFieldName()
        {
            return dataFieldName;
        }

        /**
         * Set up the name of the column.
         *
         * @param dataFieldName string
         */
        public void setDataFieldName(String dataFieldName)
        {
            this.dataFieldName = dataFieldName;
        }


        /**
         * Return the type of the column as best that can be determined.
         *
         * @return string
         */
        public String getDataFieldType()
        {
            return dataFieldType;
        }


        /**
         * Set up the type of the column.
         *
         * @param dataFieldType string
         */
        public void setDataFieldType(String dataFieldType)
        {
            this.dataFieldType = dataFieldType;
        }


        /**
         * Return the position of the column in the file.
         *
         * @return int
         */
        public int getDataFieldPosition()
        {
            return dataFieldPosition;
        }


        /**
         * Set up the position of the column in the file.
         *
         * @param dataFieldPosition int
         */
        public void setDataFieldPosition(int dataFieldPosition)
        {
            this.dataFieldPosition = dataFieldPosition;
        }


        /**
         * Return the matching unique identifier for the schema attribute (if any).
         *
         * @return string guid
         */
        public String getMatchingSchemaAttributeGUID()
        {
            return matchingSchemaAttributeGUID;
        }


        /**
         * Set up the matching unique identifier for the schema attribute.
         *
         * @param matchingSchemaAttributeGUID string guid
         */
        public void setMatchingSchemaAttributeGUID(String matchingSchemaAttributeGUID)
        {
            this.matchingSchemaAttributeGUID = matchingSchemaAttributeGUID;
        }


        /**
         * Return the data profile annotation for this data field.
         *
         * @return data profile annotation
         */
        public ResourceProfileAnnotation getDataProfileAnnotation()
        {
            return resourceProfileAnnotation;
        }
    }


    /**
     * Free up any resources held since the connector is no longer needed.
     *
     * @throws ConnectorCheckedException there is a problem within the connector.
     */
    @Override
    public  void disconnect() throws ConnectorCheckedException
    {
        if (connector != null)
        {
            connector.disconnect();
        }

        super.disconnect();
    }
}
