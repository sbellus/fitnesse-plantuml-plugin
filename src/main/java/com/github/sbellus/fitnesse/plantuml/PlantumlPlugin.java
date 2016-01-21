package com.github.sbellus.fitnesse.plantuml;


import java.util.Properties;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

/**
 * Register plantuml symbol.
 */
public class PlantumlPlugin extends PluginFeatureFactoryBase  {
	private Properties properties;
	
    public PlantumlPlugin(Properties properties) {
        this.properties = properties;
     }

    public void registerSymbolTypes(SymbolProvider symbolProvider) throws PluginException {
    	symbolProvider.add(new PlantumlSymbol(properties));
    }
}
