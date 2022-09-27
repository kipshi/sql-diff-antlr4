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

package com.sql.diff;

import com.sql.diff.component.DataBase;
import com.sql.diff.generator.MysqlGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class App {

    public static void main(String[] args) {
        String sourcePath = args[0];
        String targetPath = args[1];

        SqlFile sourceFile = new SqlFile(sourcePath);
        DataBase sourceDB = sourceFile.parse();

        SqlFile targetFile = new SqlFile(targetPath);
        DataBase targetDB = targetFile.parse();

        String upgradeSql = MysqlGenerator.upgradeSql(sourceDB, targetDB);
        String revertSql = MysqlGenerator.upgradeSql(targetDB, sourceDB);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("upgrade.sql"))) {
            bufferedWriter.write(upgradeSql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("revert.sql"))) {
            bufferedWriter.write(revertSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
