package com.qsl.efficiency.spel;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author qianshuailong
 * @date 2022/5/26
 */
public class Demo {

    @Test
    public void testParserContext() {
        ExpressionParser parser = new SpelExpressionParser();
        ParserContext parserContext = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return true;
            }

            @Override
            public String getExpressionPrefix() {
                return "#{";
            }

            @Override
            public String getExpressionSuffix() {
                return "}";
            }
        };
        String template = "#{'Hello '}#{'World!'}";
        Expression expression = parser.parseExpression(template, parserContext);
        System.out.println(expression.getValue());
    }

}
