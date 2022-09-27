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

package com.sql.diff.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sql.diff.component.InsertSql;
import com.sql.diff.util.RuleContextUtil;
import com.sql.parser.MySqlParser;
import com.sql.parser.MySqlParser.ExpressionOrDefaultContext;
import com.sql.parser.MySqlParser.ExpressionsWithDefaultsContext;
import com.sql.parser.MySqlParser.InsertStatementValueContext;
import com.sql.parser.MySqlParser.UidListContext;
import com.sql.parser.MySqlParserBaseListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class InsertSqlListener extends MySqlParserBaseListener {

    private InsertSql insertSql;

    public InsertSqlListener() {

    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void enterInsertStatement(MySqlParser.InsertStatementContext ctx) {
        this.insertSql = new InsertSql();
        String originSql = RuleContextUtil.getTextWithSemiColon(ctx);
        String tableName = ctx.tableName().getText();
        UidListContext uidListContext = ctx.uidList().get(0);
        InsertStatementValueContext insertStatementValueContext = ctx.insertStatementValue();
        List<String> fieldNames = uidListContext.uid().stream().map(uidContext -> uidContext.getText()).collect(
                Collectors.toList());

        List<List<String>> values = Lists.newArrayList();
        List<ExpressionsWithDefaultsContext> expressions = insertStatementValueContext.expressionsWithDefaults();
        for (ExpressionsWithDefaultsContext expressionContext : expressions) {
            List<ExpressionOrDefaultContext> expressionList = expressionContext.expressionOrDefault();
            List<String> value = expressionList.stream().map(expression->expression.expression().getText()).collect(
                    Collectors.toList());
            values.add(value);
        }
        insertSql.setOriginSql(originSql);
        insertSql.setTable(tableName);
        insertSql.setFieldNames(fieldNames);
        insertSql.setValues(values);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void exitInsertStatement(MySqlParser.InsertStatementContext ctx) {
        String insertSqlJson = JSON.toJSONString(insertSql);
        log.info("insertSql has been create = {}", insertSqlJson);
    }
}
