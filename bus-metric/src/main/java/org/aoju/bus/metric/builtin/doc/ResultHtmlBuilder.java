/*
 * The MIT License
 *
 * Copyright (c) 2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.metric.builtin.doc;

import java.util.List;

/**
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8++
 */
public class ResultHtmlBuilder {

    public String buildHtml(ApiDocFieldDefinition definition) {
        StringBuilder html = new StringBuilder();
        html.append("<tr>")
                .append("<td>" + definition.getName() + "</td>")
                .append("<td>" + definition.getDataType() + "</td>")
                .append("<td>" + buildExample(definition) + "</td>")
                .append("<td>" + definition.getDescription() + "</td>");
        html.append("</tr>");

        return html.toString();
    }

    protected String buildExample(ApiDocFieldDefinition definition) {
        StringBuilder html = new StringBuilder();
        if (definition.getElements().size() > 0) {
            html.append("<table>")
                    .append("<tr>")
                    .append("<th>名称</th>")
                    .append("<th>类型</th>")
                    .append("<th>示例值</th>")
                    .append("<th>描述</th>")
                    .append("</tr>");

            List<ApiDocFieldDefinition> els = definition.getElements();
            for (ApiDocFieldDefinition apiDocFieldDefinition : els) {
                html.append("<tr>")
                        .append("<td>" + apiDocFieldDefinition.getName() + "</td>")
                        .append("<td>" + apiDocFieldDefinition.getDataType() + "</td>")
                        .append("<td>" + buildExample(apiDocFieldDefinition) + "</td>")
                        .append("<td>" + apiDocFieldDefinition.getDescription() + "</td>")
                        .append("</tr>");
            }
            html.append("</table>");
        } else {
            html.append(buildExampleValue(definition));
        }
        return html.toString();
    }

    protected String buildExampleValue(ApiDocFieldDefinition definition) {
        return definition.getExample();
    }

}
