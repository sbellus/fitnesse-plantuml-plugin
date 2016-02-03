package com.github.sbellus.fitnesse.plantuml.graphics;

import java.util.regex.Pattern;

import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.VariableSource;

public class GraphicsVariableReplacer {
    private static final Pattern VariablePattern = Pattern.compile("\\$\\{((?!\\$\\{).)*?\\}");
    private static final Integer MaxRecursionDepth = 30;
    private VariableSource variableSource;
    private Integer recursionDepth;

    public GraphicsVariableReplacer(VariableSource variableSource) {
        this.variableSource = variableSource;
        this.recursionDepth = 0;
    }

    public String replaceVariablesIn(String str) {
        recursionDepth = 0;
        return replaceVariablesRecursively(str);
    }

    public String replaceVariablesRecursively(String str) {
        boolean isAtLeastOneVariableReplaced = false;
        java.util.regex.Matcher m = VariablePattern.matcher(str);
        while (m.find()) {
            String var = m.group();
            Maybe<String> value = variableSource.findVariable(var.substring(2, var.length() - 1));
            if (!value.isNothing()) {
                isAtLeastOneVariableReplaced = true;
                str = str.replace(var, value.getValue());
            }
        }

        if (isAtLeastOneVariableReplaced && recursionDepth < MaxRecursionDepth) {
            recursionDepth++;
            return replaceVariablesRecursively(str);
        }

        return str;
    }

}
