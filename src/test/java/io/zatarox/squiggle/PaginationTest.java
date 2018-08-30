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

import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.hamcrest.text.IsEqualIgnoringWhiteSpace;

public class PaginationTest {

    @Test
    public void offsetOne() {
        Table people = new Table("people");

        SelectQuery select = new SelectQuery();

        select.addToSelection(people.getWildcard());

        select.setOffset(1);

        assertThat(select.toString(),
        IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    people.* "
                + "FROM "
                + "    people "
                + "OFFSET 1 ROW"));
    }

    @Test
    public void offsetMany() {
        Table people = new Table("people");

        SelectQuery select = new SelectQuery();

        select.addToSelection(people.getWildcard());

        select.setOffset(5);

        assertThat(select.toString(),
        IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    people.* "
                + "FROM "
                + "    people "
                + "OFFSET 5 ROWS"));
    }

    @Test
    public void limitOne() {
        Table people = new Table("people");

        SelectQuery select = new SelectQuery();

        select.addToSelection(people.getWildcard());

        select.setLimit(1);

        assertThat(select.toString(),
        IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    people.* "
                + "FROM "
                + "    people "
                + "FETCH NEXT 1 ROW ONLY"));
    }

    @Test
    public void limitMany() {
        Table people = new Table("people");

        SelectQuery select = new SelectQuery();

        select.addToSelection(people.getWildcard());

        select.setLimit(5);

        assertThat(select.toString(),
        IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    people.* "
                + "FROM "
                + "    people "
                + "FETCH NEXT 5 ROWS ONLY"));
    }

    @Test
    public void offsetAndLimit() {
        Table people = new Table("people");

        SelectQuery select = new SelectQuery();

        select.addToSelection(people.getWildcard());

        select.setOffset(20);
        select.setLimit(10);

        assertThat(select.toString(),
        IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(
                "SELECT "
                + "    people.* "
                + "FROM "
                + "    people "
                + "OFFSET 20 ROWS "
                + "FETCH NEXT 10 ROWS ONLY"));
    }
}
