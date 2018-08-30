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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.zatarox.squiggle.criteria.MatchCriteria;
import io.zatarox.squiggle.output.Output;
import io.zatarox.squiggle.output.Outputable;
import io.zatarox.squiggle.output.ToStringer;

public class SelectQuery implements Outputable, ValueSet {

    protected static final int INDENT_SIZE = 4;

    private final List<Selectable> selection = new ArrayList<Selectable>();
    private final List<Criteria> criteria = new ArrayList<Criteria>();
    private final List<Order> order = new ArrayList<Order>();

    private boolean isDistinct = false;
    private boolean isAllColumns = false;

    private int offset = 0;
	private int limit = 0;

    public List<Table> listTables() {
        LinkedHashSet<Table> tables = new LinkedHashSet<Table>();
        addReferencedTablesTo(tables);
        return new ArrayList<Table>(tables);
    }

	public void addToSelection(Selectable selectable) {
        selection.add(selectable);
    }

    /**
     * Syntax sugar for addToSelection(Column).
     */
    public void addColumn(Table table, String columName) {
        addToSelection(table.getColumn(columName));
    }

    public void addColumn(Table table, String columName, String columnAlias) {
        addToSelection(table.getColumn(columName, columnAlias));
    }

    public void removeFromSelection(Selectable selectable) {
        selection.remove(selectable);
    }

    /**
     * @return a list of {@link Selectable} objects.
     */
    public List<Selectable> listSelection() {
        return Collections.unmodifiableList(selection);
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    public void setDistinct(boolean distinct) {
        isDistinct = distinct;
    }

    public boolean isAllColumns() {
        return isAllColumns;
    }

    public void setAllColumns(boolean allColumns) {
        isAllColumns = allColumns;
    }

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

    public void addCriteria(Criteria criteria) {
        this.criteria.add(criteria);
    }

    public void removeCriteria(Criteria criteria) {
        this.criteria.remove(criteria);
    }

    public List<Criteria> listCriteria() {
        return Collections.unmodifiableList(criteria);
    }

    /**
     * Syntax sugar for addCriteria(JoinCriteria)
     */
    public void addJoin(Table srcTable, String srcColumnname, Table destTable, String destColumnname) {
        addCriteria(new MatchCriteria(srcTable.getColumn(srcColumnname), MatchCriteria.EQUALS, destTable.getColumn(destColumnname)));
    }

    /**
     * Syntax sugar for addCriteria(JoinCriteria)
     */
    public void addJoin(Table srcTable, String srcColumnName, String operator, Table destTable, String destColumnName) {
        addCriteria(new MatchCriteria(srcTable.getColumn(srcColumnName), operator, destTable.getColumn(destColumnName)));
    }

    public void addOrder(Order order) {
        this.order.add(order);
    }

    /**
     * Syntax sugar for addOrder(Order).
     */
    public void addOrder(Table table, String columnname, boolean ascending) {
        addOrder(new Order(table.getColumn(columnname), ascending));
    }

    public void removeOrder(Order order) {
        this.order.remove(order);
    }

    public List<Order> listOrder() {
        return Collections.unmodifiableList(order);
    }

    @Override
    public String toString() {
        return ToStringer.toString(this);
    }

    @Override
    public void write(Output out) {
        out.print("SELECT");
        if (isDistinct) {
            out.print(" DISTINCT");
        }

        if(isAllColumns) {
            out.print(" *").println();
        }else {
            out.println();
            appendIndentedList(out, selection, ",");
        }

        Set<Table> tables = findAllUsedTables();
        if (!tables.isEmpty()) {
            out.println("FROM");
            appendIndentedList(out, tables, ",");
        }

        // Add criteria
        if (criteria.size() > 0) {
            out.println("WHERE");
            appendIndentedList(out, criteria, "AND");
        }

        // Add order
        if (order.size() > 0) {
            out.println("ORDER BY");
            appendIndentedList(out, order, ",");
        }

        // Add offset
		if (offset > 0) {
            String label = (offset == 1 ? "ROW" : "ROWS");
            out.println(String.format("OFFSET %d %s", offset, label));
		}

        // Add limit
        if (limit > 0) {
            String label = (limit == 1 ? "ROW" : "ROWS");
            out.println(String.format("FETCH NEXT %d %s ONLY", limit, label));
		}
    }

    private void appendIndentedList(Output out, Collection<? extends Outputable> things, String seperator) {
        out.indent();
        appendList(out, things, seperator);
        out.unindent();
    }

    /**
     * Iterate through a Collection and append all entries (using .toString())
     * to a StringBuffer.
     */
    private void appendList(Output out, Collection<? extends Outputable> collection, String seperator) {
        Iterator<? extends Outputable> i = collection.iterator();
        boolean hasNext = i.hasNext();

        while (hasNext) {
            Outputable curr = (Outputable) i.next();
            hasNext = i.hasNext();
            curr.write(out);
            out.print(' ');
            if (hasNext) {
                out.print(seperator);
            }
            out.println();
        }
    }

    /**
     * Find all the tables used in the query (from columns, criteria and order).
     *
     * @return Set of {@link io.zatarox.squiggle.Table}s
     */
    private Set<Table> findAllUsedTables() {
        Set<Table> tables = new LinkedHashSet<Table>();
        addReferencedTablesTo(tables);
        return tables;
    }

    public void addReferencedTablesTo(Set<Table> tables) {
        for (Selectable s : selection) {
            s.addReferencedTablesTo(tables);
        }
        for (Criteria c : criteria) {
            c.addReferencedTablesTo(tables);
        }
        for (Order o : order) {
            o.addReferencedTablesTo(tables);
        }
    }
}
