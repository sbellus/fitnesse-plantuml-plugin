package com.github.sbellus.fitnesse.plantuml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

/**
 * Register plantuml symbol.
 */
public class PlantumlPlugin extends PluginFeatureFactoryBase {
    private Properties properties;

    public PlantumlPlugin() {
        this.properties = makeProperties();
    }
    
    public void registerSymbolTypes(SymbolProvider symbolProvider) throws PluginException {
        symbolProvider.add(PlantumlSymbol.make(properties));
    }
    
    private Properties makeProperties() {
        Properties pluginProperties = new Properties();
        
        try {
            File currentJarFile =  new File(PlantumlPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String currentJarPath =  FilenameUtils.getFullPath(currentJarFile.getAbsolutePath());
            File pluginPropertiesFile = new File(currentJarPath + "fitnesse-plantuml-plugin.properties");
            if (pluginPropertiesFile.exists())  {
                InputStream is = new FileInputStream(pluginPropertiesFile);
                pluginProperties.load(is);
                is.close(); 
            }
        } catch (Exception e) {
            // do nothing
        }
        
        return pluginProperties;
    }
}
