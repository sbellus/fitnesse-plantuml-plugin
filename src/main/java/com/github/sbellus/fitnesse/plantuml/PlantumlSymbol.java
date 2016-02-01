package com.github.sbellus.fitnesse.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import fitnesse.html.HtmlTag;
import fitnesse.html.RawHtml;
import fitnesse.wiki.VariableTool;
import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Preformat;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

/**
 * Generates picture from plantuml source.
 */
public class PlantumlSymbol extends SymbolType implements Rule, Translation {
    private final String PropertyPictureAsSvg = "pictureAsSVG";
    private final String PropertyTitle = "Title";
    private final String PropertyAlign = "Align";
    private final String PropertyWidth = "Width";
    private final String PropertyHigh = "High";

    private String defaultStyle = "";

    public PlantumlSymbol(Properties properties) {
        super("startuml");
        wikiMatcher(new Matcher().startLine().ignoreWhitespace().string("!startuml"));
        wikiRule(this);
        htmlTranslation(this);

        defaultStyle = readDefaultPlantumlStyle(properties);
    }

    private String readDefaultPlantumlStyle(Properties properties) {
        String defaultStyle = properties.getProperty("plantuml.defaultStyle");

        if (defaultStyle != null) {
            return defaultStyle + "\n";
        }

        return "";
    }

    public Maybe<Symbol> parse(Symbol current, Parser parser) {

        final SymbolType Enduml = new SymbolType("enduml")
                .wikiMatcher(new Matcher().startLine().ignoreWhitespace().string("!enduml"));

        String pictureAttributes = parser.parseToAsString(SymbolType.Newline).getValue();
        if (parser.atEnd())
            return Symbol.nothing;

        String plantumlContext = parser.parseLiteral(Enduml);
        if (parser.atEnd())
            return Symbol.nothing;

        // replace variables in content
        VariableTool variableReplacer = new VariableTool(parser.getPage());
        plantumlContext = variableReplacer.replace(plantumlContext);
        
        // get picture attributes
        String width = null;
        String height = null;

        Pattern pattern = Pattern.compile("[ \t]*(\".*\")?[ \t]*(l|r|c)?[ \t]*([0-9]+)?[ \t]*([0-9]+)?");
        java.util.regex.Matcher matcher = pattern.matcher(pictureAttributes);
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                Pattern patternTitle = Pattern.compile("\"[ \t]*(.*?)[ \t]*\"");
                java.util.regex.Matcher matcherTitle = patternTitle.matcher(matcher.group(1));
                if (matcherTitle.matches()) {
                    current.putProperty(PropertyTitle, matcherTitle.group(1));
                }
            }
            if (matcher.group(2) != null) {
                current.putProperty(PropertyAlign, matcher.group(2));
            }
            if (matcher.group(3) != null) {
                width = matcher.group(3);
                current.putProperty(PropertyWidth, width);
            }
            if (matcher.group(4) != null) {
                height = matcher.group(4);
                current.putProperty(PropertyHigh, height);
            }
        }

        // convert it to picture
        try {
            String dimensions = "";
            if (width != null) {
                dimensions += "scale " + width + " width\n";
            }
            if (height != null) {
                dimensions += "scale " + height + " height\n";
            }
            plantumlContext = "@startuml\n" + defaultStyle + "\n" + dimensions + plantumlContext + "@enduml";

            SourceStringReader reader = new SourceStringReader(plantumlContext);
            final ByteArrayOutputStream picture = new ByteArrayOutputStream();

            reader.generateImage(picture, new FileFormatOption(FileFormat.SVG));
            picture.close();

            String pictureSvg = new String(picture.toByteArray(), Charset.forName("UTF-8"));

            current.putProperty(PropertyPictureAsSvg, pictureSvg);
        } catch (IOException e) {
            Symbol error = new Symbol(new Preformat(), "").add("Picture generation error:\n" + e.toString());
            return new Maybe<Symbol>(error);
        }

        return new Maybe<Symbol>(current);
    }

    public String toTarget(Translator translator, Symbol symbol) {

        final HtmlTag newLine = new HtmlTag("br");
        
        HtmlTag plantuml = new HtmlTag("div");
        HtmlTag plantumlHeader = new HtmlTag("div");
        plantumlHeader.addAttribute("style", "display: block;margin: auto;");
        
        HtmlTag plantumlHolder = new HtmlTag("div");
        plantumlHolder.addAttribute("class", "plantuml");
        
        String position = "left";
        if (symbol.hasProperty(PropertyAlign)) {
            if (symbol.getProperty(PropertyAlign).equals("c")) {
                position =  "center";
            }
            if (symbol.getProperty(PropertyAlign).equals("r")) {
                position =  "right";
            }
        }        
        
        plantumlHolder.addAttribute("style", "text-align: center;float: " + position + ";");
        
        HtmlTag plantumlPicture = new HtmlTag("div");
        plantumlPicture.addAttribute("class", "plantuml_picture");
        plantumlPicture.add(new RawHtml(symbol.getProperty(PropertyPictureAsSvg)));
        plantumlHolder.add(plantumlPicture);
        
        if (symbol.hasProperty(PropertyTitle)) {
            plantumlHolder.add(newLine);
            HtmlTag caption = new HtmlTag("div");
            caption.add(symbol.getProperty(PropertyTitle));
            caption.addAttribute("class", "plantuml_caption");
            caption.addAttribute("style", "font-style: italic;");
            plantumlHolder.add(caption);
        }

        
        plantumlHeader.add(plantumlHolder);
        plantuml.add(plantumlHeader);

        // fix of html formatting
        HtmlTag clearFix = new HtmlTag("div");
        clearFix.addAttribute("style", "clear: both;");
        
        plantuml.add(clearFix);
        
        return plantuml.html();
    }
}
