package com.github.sbellus.fitnesse.plantuml.graphics;

public interface GraphicsWikiToSvgConvertor {
    public GraphicsSvg convert(GraphicsWiki wiki) throws GraphicsWikiToSvgConvertionException;
}
