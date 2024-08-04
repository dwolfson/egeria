/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.openmetadata.properties.governance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.frameworks.openmetadata.properties.FindProperties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * LevelIdentifierProperties describes the properties for searching for a governance action classification by its level.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class LevelIdentifierProperties extends FindProperties
{
    private boolean returnSpecificLevel = false;
    private int     levelIdentifier     = 0;


    /**
     * Default constructor
     */
    public LevelIdentifierProperties()
    {
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public LevelIdentifierProperties(LevelIdentifierProperties template)
    {
        super(template);

        if (template != null)
        {
            this.returnSpecificLevel = template.getReturnSpecificLevel();
            this.levelIdentifier = template.getLevelIdentifier();
        }
    }


    /**
     * Return whether the level identifier is in use
     *
     * @return boolean
     */
    public boolean getReturnSpecificLevel()
    {
        return returnSpecificLevel;
    }


    /**
     * Set up whether the level identifier is in use.
     *
     * @param flag boolean
     */
    public void setReturnSpecificLevel(boolean flag)
    {
        returnSpecificLevel = flag;
    }


    /**
     * Return the level to match on.
     *
     * @return int
     */
    public int getLevelIdentifier()
    {
        return levelIdentifier;
    }


    /**
     * Set up the level to match on.
     *
     * @param levelIdentifier int
     */
    public void setLevelIdentifier(int levelIdentifier)
    {
        this.levelIdentifier = levelIdentifier;
    }


    /**
     * JSON-style toString.
     *
     * @return list of properties and their values.
     */
    @Override
    public String toString()
    {
        return "LevelIdentifierProperties{" +
                       "returnSpecificLevel=" + returnSpecificLevel +
                       ", levelIdentifier=" + levelIdentifier +
                       '}';
    }
}
