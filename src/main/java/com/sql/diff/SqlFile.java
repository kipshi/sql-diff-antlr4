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
import com.sql.diff.parser.MysqlParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;

@Data
@Slf4j
public class SqlFile {

    private String filePathAndName;

    public SqlFile(String filePathAndName) {
        this.filePathAndName = filePathAndName;
    }

    public DataBase parse() {
        try (FileReader fileReader = new FileReader(filePathAndName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            return MysqlParser.parse(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
