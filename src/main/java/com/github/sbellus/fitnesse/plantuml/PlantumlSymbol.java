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
        HtmlTag newLine = new HtmlTag("br");
        HtmlTag figure = new HtmlTag("figure");
        HtmlTag body = new HtmlTag("div");
        if (symbol.hasProperty(PropertyAlign)) {
            if (symbol.getProperty(PropertyAlign).equals("c")) {
                body.addAttribute("align", "center");
            }
            if (symbol.getProperty(PropertyAlign).equals("r")) {
                body.addAttribute("align", "right");
            }
            if (symbol.getProperty(PropertyAlign).equals("l")) {
                body.addAttribute("align", "left");
            }
        }
        HtmlTag table = new HtmlTag("table");
        table.addAttribute("style", "border-style: none;");

        HtmlTag row = new HtmlTag("tr");
        row.addAttribute("style", "border-style: none;");
        HtmlTag col = new HtmlTag("td");
        col.addAttribute("style", "border-style: none;");
        col.addAttribute("align", "center");
        col.add(new RawHtml(symbol.getProperty(PropertyPictureAsSvg)));

        if (symbol.hasProperty(PropertyTitle)) {
            col.add(newLine);
            HtmlTag figcaption = new HtmlTag("figcaption");
            figcaption.add(symbol.getProperty(PropertyTitle));
            col.add(figcaption);
        }

        figure.add(body);
        body.add(table);
        table.add(row);
        row.add(col);

        return figure.html();
    }
}
