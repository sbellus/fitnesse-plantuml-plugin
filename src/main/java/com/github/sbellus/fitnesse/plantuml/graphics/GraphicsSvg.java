package com.github.sbellus.fitnesse.plantuml.graphics;

import fitnesse.html.HtmlTag;
import fitnesse.html.RawHtml;
import fitnesse.wikitext.parser.Symbol;

public class GraphicsSvg {
    private static final String SymbolPropertySvg = "Svg";
    private String svg;
    private GraphicsProperties properties;

    public GraphicsSvg(String svg, GraphicsProperties properties) {
        this.svg = new String(svg);
        this.properties = new GraphicsProperties(properties);
    }

    public GraphicsSvg() {
        this.svg = null;
        this.properties = new GraphicsProperties();
    }

    public void readFromSymbol(Symbol symbol) {
        svg = symbol.getProperty(SymbolPropertySvg);
        properties.readFromSymbol(symbol);
    }

    public void writeToSymbol(Symbol symbol) {
        symbol.putProperty(SymbolPropertySvg, svg);
        properties.writeToSymbol(symbol);
    }

    public String toHtml() {
        final HtmlTag newLine = new HtmlTag("br");

        HtmlTag graphics = new HtmlTag("div");
        HtmlTag graphicsHeader = new HtmlTag("div");
        graphicsHeader.addAttribute("style", "display: block;margin: auto;");

        HtmlTag graphicsHolder = new HtmlTag("div");
        graphicsHolder.addAttribute("class", "graphics");

        String position = "left";
        if (properties.getAlignment() != null) {
            if (properties.getAlignment().equals("c")) {
                position = "center";
            }
            if (properties.getAlignment().equals("r")) {
                position = "right";
            }
        }

        graphicsHolder.addAttribute("style", "text-align: center;float: " + position + ";");

        HtmlTag graphicsPicture = new HtmlTag("div");
        graphicsPicture.addAttribute("class", "graphics_picture");
        graphicsPicture.add(new RawHtml(svg));
        graphicsHolder.add(graphicsPicture);

        if (properties.getCaption() != null) {
            graphicsHolder.add(newLine);
            HtmlTag caption = new HtmlTag("div");
            caption.add(properties.getCaption());
            caption.addAttribute("class", "graphics_caption");
            caption.addAttribute("style", "font-style: italic;");
            graphicsHolder.add(caption);
        }

        graphicsHeader.add(graphicsHolder);
        graphics.add(graphicsHeader);

        // fix of html formatting
        HtmlTag clearFix = new HtmlTag("div");
        clearFix.addAttribute("style", "clear: both;");

        graphics.add(clearFix);

        return graphics.html();
    }
}
