/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.connectors.properties.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.odpi.openmetadata.frameworks.openmetadata.enums.MediaType;
import org.odpi.openmetadata.frameworks.openmetadata.enums.MediaUsage;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ElementClassification;
import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ElementType;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Validate that the RelatedMediaReference bean can be cloned, compared, serialized, deserialized and printed as a String.
 */
public class TestRelatedMediaReference
{
    private ElementType                 type                 = new ElementType();
    private List<ElementClassification> classifications      = new ArrayList<>();
    private Map<String, String>         additionalProperties = new HashMap<>();
    private List<MediaUsage>    mediaUsage           = new ArrayList<>();



    /**
     * Default constructor
     */
    public TestRelatedMediaReference()
    {

    }


    /**
     * Set up an example object to test.
     *
     * @return filled in object
     */
    private RelatedMediaReference getTestObject()
    {
        RelatedMediaReference testObject = new RelatedMediaReference();

        testObject.setType(type);
        testObject.setGUID("TestGUID");
        testObject.setClassifications(classifications);

        testObject.setQualifiedName("TestQualifiedName");
        testObject.setAdditionalProperties(additionalProperties);

        testObject.setReferenceId("TestReferenceId");
        testObject.setLinkDescription("TestLinkDescription");
        testObject.setDisplayName("TestDisplayName");
        testObject.setURI("TestURI");
        testObject.setResourceDescription("TestResourceDescription");
        testObject.setVersion("TestVersion");
        testObject.setOrganization("TestOrganization");

        testObject.setMediaType(MediaType.VIDEO);
        testObject.setMediaUsageList(mediaUsage);


        return testObject;
    }


    /**
     * Validate that the object that comes out of the test has the same content as the original test object.
     *
     * @param resultObject object returned by the test
     */
    private void validateResultObject(RelatedMediaReference  resultObject)
    {
        assertTrue(resultObject.getType().equals(type));
        assertTrue(resultObject.getGUID().equals("TestGUID"));
        assertTrue(resultObject.getClassifications() != null);

        assertTrue(resultObject.getQualifiedName().equals("TestQualifiedName"));
        assertTrue(resultObject.getAdditionalProperties() != null);

        assertTrue(resultObject.getReferenceId().equals("TestReferenceId"));
        assertTrue(resultObject.getLinkDescription().equals("TestLinkDescription"));
        assertTrue(resultObject.getDisplayName().equals("TestDisplayName"));
        assertTrue(resultObject.getURI().equals("TestURI"));
        assertTrue(resultObject.getResourceDescription().equals("TestResourceDescription"));
        assertTrue(resultObject.getVersion().equals("TestVersion"));
        assertTrue(resultObject.getOrganization().equals("TestOrganization"));

        assertTrue(resultObject.getMediaType().equals(MediaType.VIDEO));
        assertTrue(resultObject.getMediaUsageList().equals(mediaUsage));
    }


    /**
     * Validate that the object is initialized properly
     */
    @Test public void testNullObject()
    {
        RelatedMediaReference    nullObject = new RelatedMediaReference();

        assertTrue(nullObject.getType() == null);
        assertTrue(nullObject.getGUID() == null);
        assertTrue(nullObject.getClassifications() == null);

        assertTrue(nullObject.getQualifiedName() == null);
        assertTrue(nullObject.getAdditionalProperties() == null);

        assertTrue(nullObject.getReferenceId() == null);
        assertTrue(nullObject.getLinkDescription() == null);
        assertTrue(nullObject.getDisplayName() == null);
        assertTrue(nullObject.getURI() == null);
        assertTrue(nullObject.getResourceDescription() == null);
        assertTrue(nullObject.getVersion() == null);
        assertTrue(nullObject.getOrganization() == null);

        assertTrue(nullObject.getMediaUsageList() == null);
        assertTrue(nullObject.getMediaType() == null);

        nullObject = new RelatedMediaReference(null);

        assertTrue(nullObject.getType() == null);
        assertTrue(nullObject.getGUID() == null);
        assertTrue(nullObject.getClassifications() == null);

        assertTrue(nullObject.getQualifiedName() == null);
        assertTrue(nullObject.getAdditionalProperties() == null);

        assertTrue(nullObject.getReferenceId() == null);
        assertTrue(nullObject.getLinkDescription() == null);
        assertTrue(nullObject.getDisplayName() == null);
        assertTrue(nullObject.getURI() == null);
        assertTrue(nullObject.getResourceDescription() == null);
        assertTrue(nullObject.getVersion() == null);
        assertTrue(nullObject.getOrganization() == null);

        assertTrue(nullObject.getMediaUsageList() == null);
        assertTrue(nullObject.getMediaType() == null);
    }


    /**
     * Validate that 2 different objects with the same content are evaluated as equal.
     * Also that different objects are considered not equal.
     */
    @Test public void testEquals()
    {
        assertFalse(getTestObject().equals(null));
        assertFalse(getTestObject().equals("DummyString"));
        assertTrue(getTestObject().equals(getTestObject()));

        RelatedMediaReference  sameObject = getTestObject();
        assertTrue(sameObject.equals(sameObject));

        RelatedMediaReference  differentObject = getTestObject();
        differentObject.setGUID("Different");
        assertFalse(getTestObject().equals(differentObject));

        differentObject = getTestObject();
        differentObject.setMediaType(MediaType.AUDIO);
        assertFalse(getTestObject().equals(differentObject));
    }


    /**
     *  Validate that 2 different objects with the same content have the same hash code.
     */
    @Test public void testHashCode()
    {
        assertTrue(getTestObject().hashCode() == getTestObject().hashCode());
    }


    /**
     *  Validate that an object cloned from another object has the same content as the original
     */
    @Test public void testClone()
    {
        validateResultObject(new RelatedMediaReference(getTestObject()));
    }


    /**
     * Validate that an object generated from a JSON String has the same content as the object used to
     * create the JSON String.
     */
    @Test public void testJSON()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String       jsonString   = null;

        /*
         * This class
         */
        try
        {
            jsonString = objectMapper.writeValueAsString(getTestObject());
        }
        catch (Exception  exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        try
        {
            validateResultObject(objectMapper.readValue(jsonString, RelatedMediaReference.class));
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        /*
         * Through superclass
         */
        Referenceable  referenceable = getTestObject();

        try
        {
            jsonString = objectMapper.writeValueAsString(referenceable);
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        try
        {
            validateResultObject((RelatedMediaReference) objectMapper.readValue(jsonString, Referenceable.class));
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        /*
         * Through superclass
         */
        ElementBase elementBase = getTestObject();

        try
        {
            jsonString = objectMapper.writeValueAsString(elementBase);
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        try
        {
            validateResultObject((RelatedMediaReference) objectMapper.readValue(jsonString, ElementBase.class));
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        /*
         * Through superclass
         */
        ElementBase  propertyBase = getTestObject();

        try
        {
            jsonString = objectMapper.writeValueAsString(propertyBase);
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        try
        {
            validateResultObject((RelatedMediaReference) objectMapper.readValue(jsonString, ElementBase.class));
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }
    }


    /**
     * Test that toString is overridden.
     */
    @Test public void testToString()
    {
        assertTrue(getTestObject().toString().contains("RelatedMediaReference"));
    }
}
