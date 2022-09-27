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
import com.sql.diff.component.DataBase;
import com.sql.diff.util.RuleContextUtil;
import com.sql.parser.MySqlParser;
import com.sql.parser.MySqlParserBaseListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DbListener extends MySqlParserBaseListener {

    private DataBase dataBase;

    public DbListener() {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUseStatement(MySqlParser.UseStatementContext ctx) {
        this.dataBase = new DataBase();
        String dbName = ctx.uid().getText();
        dataBase.setDbName(dbName);
        String originSql = RuleContextUtil.getTextWithSemiColon(ctx);
        dataBase.setOriginSql(originSql);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitUseStatement(MySqlParser.UseStatementContext ctx) {
        String databaseJson = JSON.toJSONString(dataBase);
        log.info("database has been create = {}", databaseJson);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void enterCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        this.dataBase = new DataBase();
        String dbName = ctx.uid().getText();
        dataBase.setDbName(dbName);
        String originSql = RuleContextUtil.getTextWithSemiColon(ctx);
        dataBase.setOriginSql(originSql);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void exitCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        String databaseJson = JSON.toJSONString(dataBase);
        log.info("database has been create = {}", databaseJson);
    }

}
