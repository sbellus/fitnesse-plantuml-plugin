package com.github.sbellus.fitnesse.plantuml;


import java.util.Properties;

import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsSymbol;

import fitnesse.wikitext.parser.SymbolType;

/**
 * Generates picture from plantuml source.
 */
public class PlantumlSymbol {
    private GraphicsSymbol symbol;
    private PlantumlConvertor convertor;

    public static SymbolType make(Properties properties) {
        return new PlantumlSymbol(properties).symbol;
    }
    
    public PlantumlSymbol(Properties properties) {
        convertor = new PlantumlConvertor(properties.getProperty("plantuml.defaultStyle"));
        symbol = new GraphicsSymbol("uml", convertor);
    }
}
