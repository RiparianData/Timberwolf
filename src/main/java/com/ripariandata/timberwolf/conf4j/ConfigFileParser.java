/**
 * Copyright 2012 Riparian Data
 * http://www.ripariandata.com
 * contact@ripariandata.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ripariandata.timberwolf.conf4j;

import java.io.File;
import java.io.PrintStream;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Inspired by the CmdLineParser of args4j, this takes a class with ConfigEntry
 * annotations and a configuration file and sets the fields so annotated with
 * the values from the config file.
 * <p>
 * Since our needs for configuration handling are pretty simple, this does a lot
 * less than args4j.  It only handles fields, not methods, and doesn't do anything
 * other than match names to values.
 */
public class ConfigFileParser
{
    private Map<String, FieldSetter> fields = new HashMap<String, FieldSetter>();
    private Vector<ConfigEntry> entries = new Vector<ConfigEntry>();

    public ConfigFileParser(final Object bean)
    {
        for (Class c = bean.getClass(); c != null; c = c.getSuperclass())
        {
            for (Field f : c.getDeclaredFields())
            {
                ConfigEntry entry = f.getAnnotation(ConfigEntry.class);
                if (entry != null)
                {
                    fields.put(entry.name(), new FieldSetter(bean, f));
                }
            }
        }
    }

    /**
     * Takes the values from the named configuration file and apply them to the
     * target class.  Assumes that the file is a java properties file, readable
     * by <a href="http://commons.apache.org/configuration/apidocs/org/apache/commons/configuration/PropertiesConfiguration.html">
     * org.apache.commons.configuration.PropertiesConfiguration</a>.
     *
     * @throws ConfigFileMissingException If the named configuration file does not exist.
     * @throws ConfigFileException If there was any other problem reading the configuration file.
     */
    public void parseConfigFile(final String configFile) throws ConfigFileException
    {
        File f = new File(configFile);
        if (!f.exists())
        {
            throw new ConfigFileMissingException(configFile, this);
        }

        Configuration config;
        try
        {
            config = new PropertiesConfiguration(configFile);
        }
        catch (ConfigurationException e)
        {
            throw new ConfigFileException("There was an error loading the configuration file at " + configFile,
                                          e, this);
        }
        parseConfiguration(config);
    }

    /**
     * Takes the values from the given Configuration instance and applies them to
     * the target class.  Fields in the target class marked with ConfigEntry
     * annotations that don't correspond to any properties in the given configuration
     * are not modified.  Properties in the configuration that don't match any
     * fields marked as ConfigEntry are ignored.
     */
    public void parseConfiguration(final Configuration config)
    {
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext())
        {
            String key = keys.next();
            if (fields.containsKey(key))
            {
                fields.get(key).set(config.getProperty(key));
            }
        }
    }

    private static Vector<String> splitIntoLines(String s, int width)
    {
        String[] logicalLines = s.split("\\n");
        Vector<String> lines = new Vector<String>();
        for (String logicalLine : logicalLines)
        {
            if (logicalLine.length() <= width)
            {
                lines.add(logicalLine);
            }
            else
            {
                String[] tokens = logicalLine.split("\\s");
                String line = "";
                for (String token : tokens)
                {
                    String proposedLine = line + " " + token;
                    if (proposedLine.length() > width)
                    {
                        lines.add(line);
                        line = token;
                    }
                    else
                    {
                        line = proposedLine;
                    }
                }
                lines.add(line);
            }
        }
        return lines;
    }

    private static String splitAndPad(String s, int totalWidth, int targetWidth)
    {
        Vector<String> lines = splitIntoLines(s, targetWidth);

        if (lines.size() == 0)
        {
            return "";
        }
        else
        {
            char[] spaces = new char[totalWidth - targetWidth];
            for (int i = 0; i < spaces.length; i++)
            {
                spaces[i] = ' ';
            }
            String prefix = new String(spaces);
            String ret = lines.get(0);
            for (int i = 1; i < lines.size(); i++)
            {
                ret += prefix;
                ret += lines.get(i);
            }
            return ret;
        }
    }

    public void printUsage(PrintStream out)
    {
        out.println("Valid properties in the configuration file:");
        for (ConfigEntry entry : entries)
        {
            out.print(entry.name());
            out.print(" - ");
            out.println(splitAndPad(entry.usage(), 80, 77 - entry.name().length()));
        }
    }
}
