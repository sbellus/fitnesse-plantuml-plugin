package com.github.sbellus.fitnesse.plantuml.graphics;

import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Preformat;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

public class GraphicsSymbol extends SymbolType implements Rule, Translation {
    private String specifier;
    private GraphicsWikiToSvgConvertor convertor;

    public GraphicsSymbol(String specifier, GraphicsWikiToSvgConvertor convertor) {
        super("start" + specifier);
        wikiMatcher(new Matcher().startLine().ignoreWhitespace().string("!start" + specifier));
        wikiRule(this);
        htmlTranslation(this);

        this.specifier = specifier;
        this.convertor = convertor;
    }

    public Maybe<Symbol> parse(Symbol current, Parser parser) {

        try {
            GraphicsWiki wikiGraphics = new GraphicsWiki(specifier);
            wikiGraphics.ParseFromWiki(parser);
            wikiGraphics.replaceVariables(new GraphicsVariableReplacer(parser.getVariableSource()));
            GraphicsSvg svgGraphics = convertor.convert(wikiGraphics);
            svgGraphics.WriteToSymbol(current);
        } catch (Exception exception) {
            Symbol error = new Symbol(new Preformat(), "").add(exception.getLocalizedMessage());
            return new Maybe<Symbol>(error);
        }

        return new Maybe<Symbol>(current);
    }

    public String toTarget(Translator translator, Symbol symbol) {
        GraphicsSvg svgGraphics = new GraphicsSvg();
        svgGraphics.ReadFromSymbol(symbol);
        return svgGraphics.toHtml();
    }
}
