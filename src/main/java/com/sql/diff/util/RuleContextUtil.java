/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sql.diff.util;

import org.antlr.v4.runtime.tree.ParseTree;

public class RuleContextUtil {

    public static String getTextWithSemiColon(ParseTree context) {
        return getText(context) + ";";
    }

    public static String getText(ParseTree context) {
        if (context.getChildCount() == 0) {
            return context.getText();
        } else {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < context.getChildCount(); ++i) {
                builder.append(getText(context.getChild(i))).append(" ");
            }

            return builder.toString().trim();
        }
    }

}
