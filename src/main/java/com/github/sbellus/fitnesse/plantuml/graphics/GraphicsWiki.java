package com.github.sbellus.fitnesse.plantuml.graphics;

import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.SymbolType;

public class GraphicsWiki {
    private String content;
    private GraphicsProperties properties;
    private final SymbolType graphicsTerminator;
    private final String specifier;

    public GraphicsWiki(String specifier) {
        this.content = null;
        this.properties = new GraphicsProperties();
        this.graphicsTerminator = new SymbolType("end" + specifier)
                .wikiMatcher(new Matcher().startLine().ignoreWhitespace().string("!end" + specifier));
        this.specifier = specifier;
    }

    public void parseFromWiki(Parser parser) throws GraphicsWikiParserException {

        String propertiesText = parser.parseToAsString(SymbolType.Newline).getValue();
        if (parser.atEnd()) {
            throw new GraphicsWikiParserException("No new line after !start" + specifier);
        }
        
        content = parser.parseLiteral(graphicsTerminator);
        if (parser.atEnd()) {
            throw new GraphicsWikiParserException("No !end" + specifier + " found");
        }

        if (content == null || content.isEmpty()) {
            throw new GraphicsWikiParserException("No content for " + specifier);
        }

        properties.readFromLine(propertiesText);
    }

    public void replaceVariables(GraphicsVariableReplacer replacer) {
        content = replacer.replaceVariablesIn(content);
    }

    public String getContent() {
        return content;
    }

    public GraphicsProperties getProperties() {
        return properties;
    }

}
