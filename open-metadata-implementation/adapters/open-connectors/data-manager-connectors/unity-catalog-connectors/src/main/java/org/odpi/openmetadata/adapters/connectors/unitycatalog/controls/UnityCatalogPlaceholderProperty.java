/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.unitycatalog.controls;


import org.odpi.openmetadata.frameworks.governanceaction.controls.PlaceholderProperty;
import org.odpi.openmetadata.frameworks.governanceaction.controls.PlaceholderPropertyType;

import java.util.ArrayList;
import java.util.List;

/**
 * PlaceholderProperty provides some standard definitions for placeholder properties used to pass properties
 * to services that use templates.
 */
public enum UnityCatalogPlaceholderProperty
{
    /**
     * The name of the catalog being described.
     */
    CATALOG_NAME ("ucCatalogName", "The name of a catalog within Unity Catalog (UC).", "string", "my_catalog"),

    /**
     * The name of a schema within a catalog in Unity Catalog (UC).
     */
    SCHEMA_NAME ("ucSchemaName", "The name of a schema within a catalog in Unity Catalog (UC).  This value is combined with the name of the catalog to get the full name.", "string", "my_schema"),

    /**
     * The location where the data associated with this element in Unity Catalog is stored.
     */
    STORAGE_LOCATION ("ucStorageLocation", "The location where the data associated with this element in Unity Catalog is stored.", "string", "a/b/c"),

    /**
     * The name of a table within a schema and catalog in Unity Catalog (UC).
     */
    TABLE_NAME ("ucTableName", "The name of a table within a schema and catalog in Unity Catalog (UC).  This value is combined with the name of the schema and catalog to get the full name.", "string", "my_table"),

    /**
     * The type of a table: MANAGED or EXTERNAL.
     */
    TABLE_TYPE ("ucTableType", "The type of a table: MANAGED or EXTERNAL.", "string", "MANAGED"),

    /**
     * The format of the data source: DELTA, CSV, JSON, AVRO, PARQUET, ORC, TEXT.
     */
    DATA_SOURCE_FORMAT ("ucDataSourceFormat", "The format of the data source: DELTA, CSV, JSON, AVRO, PARQUET, ORC, TEXT.", "string", "CSV"),



    /**
     * The name of a volume within a schema and catalog in Unity Catalog (UC).
     */
    VOLUME_NAME ("ucVolumeName", "The name of a volume within a schema and catalog in Unity Catalog (UC).  This value is combined with the name of the schema and catalog to get the full name.", "string", "my_volume"),

    /**
     * The type of a volume: MANAGED or EXTERNAL.
     */
    VOLUME_TYPE ("ucVolumeType", "The type of a volume: MANAGED or EXTERNAL.", "string", "MANAGED"),

    /**
     * The name of a function within a schema and catalog in Unity Catalog (UC).
     */
    FUNCTION_NAME ("ucFunctionName", "The name of a function within a schema and catalog in Unity Catalog (UC).  This value is combined with the name of the schema and catalog to get the full name.", "string", "my_function"),

    ;

    public final String           name;
    public final String           description;
    public final String           dataType;
    public final String           example;


    /**
     * Create a specific Enum constant.
     *
     * @param name name of the placeholder property
     * @param description description of the placeholder property
     * @param dataType type of value of the placeholder property
     * @param example example of the placeholder property
     */
    UnityCatalogPlaceholderProperty(String name,
                                    String description,
                                    String dataType,
                                    String example)
    {
        this.name        = name;
        this.description = description;
        this.dataType    = dataType;
        this.example     = example;
    }


    /**
     * Return the name of the placeholder property.
     *
     * @return string name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Return the placeholder to use when building templates.
     *
     * @return placeholder property
     */
    public String getPlaceholder()
    {
        return "{{" + name + "}}";
    }


    /**
     * Return the description of the placeholder property.
     *
     * @return text
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Return the data type for the placeholder property.
     *
     * @return data type name
     */
    public String getDataType()
    {
        return dataType;
    }


    /**
     * Return an example of the placeholder property to help users understand how to set it up.
     *
     * @return example
     */
    public String getExample()
    {
        return example;
    }


    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getServerPlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.HOST_URL.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.PORT_NUMBER.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());

        return placeholderPropertyTypes;
    }

    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getCatalogPlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getPlaceholderType());
        placeholderPropertyTypes.add(CATALOG_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());

        return placeholderPropertyTypes;
    }


    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getSchemaPlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getPlaceholderType());
        placeholderPropertyTypes.add(CATALOG_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(SCHEMA_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());

        return placeholderPropertyTypes;
    }



    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getTablePlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getPlaceholderType());
        placeholderPropertyTypes.add(CATALOG_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(SCHEMA_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(TABLE_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());
        placeholderPropertyTypes.add(UnityCatalogPlaceholderProperty.STORAGE_LOCATION.getPlaceholderType());
        placeholderPropertyTypes.add(UnityCatalogPlaceholderProperty.TABLE_TYPE.getPlaceholderType());
        placeholderPropertyTypes.add(UnityCatalogPlaceholderProperty.DATA_SOURCE_FORMAT.getPlaceholderType());

        return placeholderPropertyTypes;
    }


    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getVolumePlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getPlaceholderType());
        placeholderPropertyTypes.add(CATALOG_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(SCHEMA_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(VOLUME_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());
        placeholderPropertyTypes.add(UnityCatalogPlaceholderProperty.STORAGE_LOCATION.getPlaceholderType());
        placeholderPropertyTypes.add(UnityCatalogPlaceholderProperty.VOLUME_TYPE.getPlaceholderType());

        return placeholderPropertyTypes;
    }


    /**
     * Retrieve all the defined placeholder properties
     *
     * @return list of placeholder property types
     */
    public static List<PlaceholderPropertyType> getFunctionPlaceholderPropertyTypes()
    {
        List<PlaceholderPropertyType> placeholderPropertyTypes = new ArrayList<>();

        placeholderPropertyTypes.add(PlaceholderProperty.SERVER_NETWORK_ADDRESS.getPlaceholderType());
        placeholderPropertyTypes.add(CATALOG_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(SCHEMA_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(FUNCTION_NAME.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.DESCRIPTION.getPlaceholderType());
        placeholderPropertyTypes.add(PlaceholderProperty.VERSION_IDENTIFIER.getPlaceholderType());

        return placeholderPropertyTypes;
    }


    /**
     * Return a summary of this enum to use in a service provider.
     *
     * @return placeholder property type
     */
    public PlaceholderPropertyType getPlaceholderType()
    {
        PlaceholderPropertyType placeholderPropertyType = new PlaceholderPropertyType();

        placeholderPropertyType.setName(name);
        placeholderPropertyType.setDescription(description);
        placeholderPropertyType.setDataType(dataType);
        placeholderPropertyType.setExample(example);
        placeholderPropertyType.setRequired(true);

        return placeholderPropertyType;
    }


    /**
     * Output of this enum class and main value.
     *
     * @return string showing enum value
     */
    @Override
    public String toString()
    {
        return "PlaceholderProperty{ name=" + name + "}";
    }
}
