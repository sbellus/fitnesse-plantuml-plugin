package com.github.sbellus.fitnesse.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsProperties;
import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsSvg;
import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsWiki;
import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsWikiToSvgConvertionException;
import com.github.sbellus.fitnesse.plantuml.graphics.GraphicsWikiToSvgConvertor;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantumlConvertor implements GraphicsWikiToSvgConvertor {
    private String defaultStyle;
    
    public PlantumlConvertor(String defaultStyle) {
        if (defaultStyle != null)  {
            this.defaultStyle = defaultStyle;
        } else {
            this.defaultStyle = "";
        }
    }
    
    public GraphicsSvg convert(GraphicsWiki wiki) throws GraphicsWikiToSvgConvertionException   {
        
        try {
            String dimensions = "";
            if (wiki.getProperties().getWidth() != null) {
                dimensions += "scale " + wiki.getProperties().getWidth() + " width\n";
            }
            if (wiki.getProperties().getHeight() != null) {
                dimensions += "scale " + wiki.getProperties().getHeight() + " height\n";
            }
            
            String plantumlContext = "@startuml\n" + defaultStyle + "\n" + dimensions + wiki.getContent() + "@enduml";

            SourceStringReader reader = new SourceStringReader(plantumlContext);
            ByteArrayOutputStream picture = new ByteArrayOutputStream();

            reader.generateImage(picture, new FileFormatOption(FileFormat.SVG));
            picture.close();

            String svg = new String(picture.toByteArray(), Charset.forName("UTF-8"));
            
            GraphicsProperties svgProperties = new GraphicsProperties(wiki.getProperties());
            return new GraphicsSvg(svg, svgProperties);            
        } catch (IOException e) {
            throw new GraphicsWikiToSvgConvertionException("During plantuml picture generation following exception occures:\n" + e.toString());
        }        
    }
}
