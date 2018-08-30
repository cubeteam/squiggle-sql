/*
 * Copyright 2004-2015 Joe Walnes, Guillaume Chauvet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zatarox.squiggle;

import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.Test;
import static org.junit.Assert.assertThat;

public class ColumnTest {

    @Test
    public void columnAliases() {
        Table employees = new Table("people", "employees");
        Table managers = new Table("people", "managers");

        SelectQuery select = new SelectQuery(); // base table

        select.addColumn(employees, "lastname", "employee");
        select.addColumn(managers, "lastname", "manager");

        select.addJoin(employees, "managerID", managers, "id");

        assertThat(select.toString(), IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    employees.lastname as employee , "
                + "    managers.lastname as manager "
                + "FROM "
                + "    people employees , "
                + "    people managers "
                + "WHERE "
                + "    employees.managerID = managers.id"));
    }
}
