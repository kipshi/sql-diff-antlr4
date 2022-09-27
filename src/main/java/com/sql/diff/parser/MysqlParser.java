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

package com.sql.diff.parser;

import com.sql.diff.component.DataBase;
import com.sql.diff.component.InsertSql;
import com.sql.diff.component.Table;
import com.sql.diff.listener.DbListener;
import com.sql.diff.listener.InsertSqlListener;
import com.sql.diff.listener.TableListener;
import com.sql.parser.MySqlLexer;
import com.sql.parser.MySqlParser;
import com.sql.parser.MySqlParser.RootContext;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Locale;

public class MysqlParser {

    public static DataBase parse(BufferedReader reader) throws IOException {
        String sql = reader.readLine();
        String fragmentSql = "";
        DataBase dataBase = null;
        while (sql != null) {
            String trimSql = sql.trim();
            if (trimSql.startsWith("--")
                    || trimSql.startsWith("/*")
                    || trimSql.startsWith("*")
            ) {
                sql = reader.readLine();
                continue;
            }
            fragmentSql += sql;
            if (!sql.endsWith(";")) {
                sql = reader.readLine();
                continue;
            }

            MySqlLexer mySqlLexer = new MySqlLexer(
                    new ANTLRInputStream(new CharArrayReader(fragmentSql.toCharArray())));

            CommonTokenStream tokens = new CommonTokenStream(mySqlLexer);
            MySqlParser mysqlParser = new MySqlParser(tokens);
            RootContext rootContext = mysqlParser.root();
            DbListener dbListener = new DbListener();
            TableListener tableListener = new TableListener();
            InsertSqlListener insertSqlListener = new InsertSqlListener();
            ParseTreeWalker.DEFAULT.walk(dbListener, rootContext);
            if (dbListener.getDataBase() != null) {
                dataBase = dbListener.getDataBase();
            }
            ParseTreeWalker.DEFAULT.walk(tableListener, rootContext);
            if (tableListener.getTable() != null && dataBase != null) {
                Table table = tableListener.getTable();
                dataBase.getTables().put(table.getTableName(), table);
            }
            ParseTreeWalker.DEFAULT.walk(insertSqlListener, rootContext);
            if (insertSqlListener.getInsertSql() != null && dataBase != null) {
                InsertSql insertSql = insertSqlListener.getInsertSql();
                dataBase.getInsertTableSql().put(insertSql.getTable(), insertSql);
            }
            fragmentSql = "";
            sql = reader.readLine();
        }

        return dataBase;
    }

}
