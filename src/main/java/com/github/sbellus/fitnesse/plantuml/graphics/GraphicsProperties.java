package com.github.sbellus.fitnesse.plantuml.graphics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fitnesse.wikitext.parser.Symbol;

public class GraphicsProperties {
    private Integer width;
    private Integer height;
    private String caption;
    private String alignment;
    private static final String SymbolPropertyWidht = "Width";
    private static final String SymbolPropertyHeight = "Height";
    private static final String SymbolPropertyCaption = "Caption";
    private static final String SymbolPropertyAligment = "Aligment";

    public GraphicsProperties() {
        this.width = null;
        this.height = null;
        this.caption = null;
        this.alignment = null;
    }

    public GraphicsProperties(GraphicsProperties p) {
        this.width = p.width;
        this.height = p.height;
        this.caption = p.caption;
        this.alignment = p.alignment;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getCaption() {
        return caption;
    }

    public String getAlignment() {
        return alignment;
    }

    public void ReadFromLine(String line) {
        Pattern pattern = Pattern.compile("[ \t]*(\".*\")?[ \t]*(l|r|c)?[ \t]*([0-9]+)?[ \t]*([0-9]+)?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                Pattern patternTitle = Pattern.compile("\"[ \t]*(.*?)[ \t]*\"");
                java.util.regex.Matcher matcherTitle = patternTitle.matcher(matcher.group(1));
                if (matcherTitle.matches()) {
                    caption = matcherTitle.group(1);
                }
            }
            if (matcher.group(2) != null) {
                alignment = matcher.group(2);
            }
            if (matcher.group(3) != null) {
                width = Integer.parseInt(matcher.group(3));
            }
            if (matcher.group(4) != null) {
                height = Integer.parseInt(matcher.group(4));
            }
        }
    }

    public void ReadFromSymbol(Symbol symbol) {
        if (symbol.hasProperty(SymbolPropertyWidht)) {
            width = Integer.parseInt(symbol.getProperty(SymbolPropertyWidht));
        }
        if (symbol.hasProperty(SymbolPropertyHeight)) {
            height = Integer.parseInt(symbol.getProperty(SymbolPropertyHeight));
        }
        if (symbol.hasProperty(SymbolPropertyCaption)) {
            caption = symbol.getProperty(SymbolPropertyCaption);
        }
        if (symbol.hasProperty(SymbolPropertyAligment)) {
            alignment = symbol.getProperty(SymbolPropertyAligment);
        }                        
    }

    public void WriteToSymbol(Symbol symbol) {
        if (width != null) {
            symbol.putProperty(SymbolPropertyWidht, width.toString());
        }
        if (height != null) {
            symbol.putProperty(SymbolPropertyHeight, height.toString());
        }
        if (caption != null) {
            symbol.putProperty(SymbolPropertyCaption, caption);            
        }
        if (alignment != null) {
            symbol.putProperty(SymbolPropertyAligment, alignment);
        }
    }
}
