/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Verify the MatchCriteria enum contains unique ordinals, non-null names and descriptions and can be
 * serialized to JSON and back again.
 */
public class MatchCriteriaTest
{
    private List<Integer> existingOrdinals = null;

    /**
     * Validate that a supplied ordinal is unique.
     *
     * @param ordinal value to test
     * @return boolean result
     */
    private boolean isUniqueOrdinal(int  ordinal)
    {
        Integer       newOrdinal = ordinal;

        if (existingOrdinals.contains(newOrdinal))
        {
            return false;
        }
        else
        {
            existingOrdinals.add(newOrdinal);
            return true;
        }
    }


    /**
     * Validated the values of the enum.
     */
    @Test public void testEnumValues()
    {
        existingOrdinals = new ArrayList<>();

        MatchCriteria  testValue;

        testValue = MatchCriteria.ALL;
        assertTrue(isUniqueOrdinal(testValue.getOrdinal()));
        assertTrue(testValue.getName() != null);
        assertTrue(testValue.getDescription() != null);

        testValue = MatchCriteria.ANY;
        assertTrue(isUniqueOrdinal(testValue.getOrdinal()));
        assertTrue(testValue.getName() != null);
        assertTrue(testValue.getDescription() != null);

        testValue = MatchCriteria.NONE;
        assertTrue(isUniqueOrdinal(testValue.getOrdinal()));
        assertTrue(testValue.getName() != null);
        assertTrue(testValue.getDescription() != null);
    }



    /**
     * Validate that an object generated from a JSON String has the same content as the object used to
     * create the JSON String.
     */
    @Test public void testJSON()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String       jsonString   = null;

        try
        {
            jsonString = objectMapper.writeValueAsString(MatchCriteria.ALL);
        }
        catch (Exception   exc)
        {
            assertTrue(false, "Exception: " + exc.getMessage());
        }

        try
        {
            assertTrue(objectMapper.readValue(jsonString, MatchCriteria.class) == MatchCriteria.ALL);
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
        assertTrue(MatchCriteria.NONE.toString().contains("MatchCriteria"));
    }
}
