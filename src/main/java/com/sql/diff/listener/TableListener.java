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
import com.sql.diff.component.Field;
import com.sql.diff.component.Index;
import com.sql.diff.component.PrimaryKey;
import com.sql.diff.component.Table;
import com.sql.diff.component.UniqueKey;
import com.sql.diff.util.RuleContextUtil;
import com.sql.parser.MySqlParser.ColumnConstraintContext;
import com.sql.parser.MySqlParser.ColumnCreateTableContext;
import com.sql.parser.MySqlParser.ColumnDeclarationContext;
import com.sql.parser.MySqlParser.ColumnDefinitionContext;
import com.sql.parser.MySqlParser.ConstraintDeclarationContext;
import com.sql.parser.MySqlParser.CreateDefinitionContext;
import com.sql.parser.MySqlParser.IndexColumnDefinitionContext;
import com.sql.parser.MySqlParser.IndexColumnNameContext;
import com.sql.parser.MySqlParser.IndexColumnNamesContext;
import com.sql.parser.MySqlParser.IndexDeclarationContext;
import com.sql.parser.MySqlParser.PrimaryKeyTableConstraintContext;
import com.sql.parser.MySqlParser.SimpleIndexDeclarationContext;
import com.sql.parser.MySqlParser.TableConstraintContext;
import com.sql.parser.MySqlParser.UniqueKeyTableConstraintContext;
import com.sql.parser.MySqlParserBaseListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class TableListener extends MySqlParserBaseListener {

    private Table table;

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void enterColumnCreateTable(ColumnCreateTableContext ctx) {
        this.table = new Table();
        String tableOriginSql = RuleContextUtil.getTextWithSemiColon(ctx);
        table.setOriginTableSql(tableOriginSql);
        String tableName = ctx.tableName().getText();
        table.setTableName(tableName);
        List<CreateDefinitionContext> definitionContexts = ctx.createDefinitions().createDefinition();
        for (CreateDefinitionContext definitionContext : definitionContexts) {
            if (definitionContext instanceof ColumnDeclarationContext) {
                ColumnDeclarationContext columnContext = (ColumnDeclarationContext) definitionContext;
                String originSql = RuleContextUtil.getText(columnContext);
                String columnName = columnContext.uid().getText();
                ColumnDefinitionContext columnDefinition = columnContext.columnDefinition();
                String dataType = columnDefinition.dataType().getText();
                String extraSql = "";
                for (ColumnConstraintContext constraintContext : columnDefinition.columnConstraint()) {
                    extraSql += RuleContextUtil.getText(constraintContext) + " ";
                }

                Field field = new Field(originSql, columnName, dataType, extraSql);
                table.getFields().put(field.getFieldName(), field);
            } else if (definitionContext instanceof ConstraintDeclarationContext) {
                ConstraintDeclarationContext constraintContext = (ConstraintDeclarationContext) definitionContext;
                TableConstraintContext tableConstraintContext = constraintContext.tableConstraint();
                if (tableConstraintContext instanceof UniqueKeyTableConstraintContext) {
                    UniqueKeyTableConstraintContext uniqueConstraint = (UniqueKeyTableConstraintContext) tableConstraintContext;
                    String originSql = uniqueConstraint.getText();
                    String uniqueName = uniqueConstraint.uid().get(0).getText();
                    List<String> fieldNames = createIndexColumns(uniqueConstraint.indexColumnNames());
                    UniqueKey uniqueKey = new UniqueKey(originSql, uniqueName, fieldNames);
                    table.getUniqueKeys().put(uniqueKey.getKeyName(), uniqueKey);
                } else if (tableConstraintContext instanceof PrimaryKeyTableConstraintContext) {
                    PrimaryKeyTableConstraintContext primaryConstraint = (PrimaryKeyTableConstraintContext) tableConstraintContext;
                    String originSql = primaryConstraint.getText();
                    List<String> fieldNames = createIndexColumns(primaryConstraint.indexColumnNames());
                    PrimaryKey primaryKey = new PrimaryKey(originSql, null, fieldNames.get(0));
                    table.setPrimaryKey(primaryKey);
                }
            } else if (definitionContext instanceof IndexDeclarationContext) {
                IndexDeclarationContext indexContext = (IndexDeclarationContext) definitionContext;
                String originSql = indexContext.getText();
                IndexColumnDefinitionContext columnDefinition = indexContext.indexColumnDefinition();
                if (columnDefinition instanceof SimpleIndexDeclarationContext) {
                    SimpleIndexDeclarationContext simpleIndexDeclarationContext = (SimpleIndexDeclarationContext) columnDefinition;
                    String indexName = simpleIndexDeclarationContext.uid().getText();
                    List<String> fieldNames = createIndexColumns(simpleIndexDeclarationContext.indexColumnNames());
                    Index index = new Index(originSql, indexName, fieldNames);
                    table.getIndexs().put(index.getKeyName(), index);
                }
            }
        }
    }

    private List<String> createIndexColumns(IndexColumnNamesContext indexColumnNamesContext) {
        List<IndexColumnNameContext> indexColumnNameContexts = indexColumnNamesContext.indexColumnName();
        return indexColumnNameContexts.stream().map(context -> context.uid().getText()).collect(
                Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void exitColumnCreateTable(ColumnCreateTableContext ctx) {
        String tableJson = JSON.toJSONString(table);
        log.info("table has been create = {}", tableJson);
    }

}
