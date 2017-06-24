/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.faststream.query.db.query.common.rewriter;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.faststream.query.db.query.common.rewriter.nodes.CollectionTypeRewriter;
import io.faststream.query.db.query.common.rewriter.nodes.MapTypeRewriter;
import io.faststream.query.db.query.common.rewriter.nodes.MultimapTypeRewriter;
import io.faststream.query.db.query.common.rewriter.nodes.NullableRewriter;
import io.faststream.query.db.query.common.rewriter.nodes.SizeableRewriter;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.plan.QueryNodeProcessor;
import io.faststream.query.db.query.plan.QueryPlan;

/**
 * A query plan rewriter does static optimization of a query.
 *
 * @author Kasper Nielsen
 */
public final class QueryPlanRewriter {

    /** The default plan rewriter. Uses a ServiceLoader to all {@link AbstractQueryNodeRewriter} instances. */
    public static final QueryPlanRewriter DEFAULT = new QueryPlanRewriter();

    /** The query node rewriters. */
    private final AbstractQueryNodeRewriter[] rewriters;

    public QueryPlanRewriter() {
        rewriters = new AbstractQueryNodeRewriter[] { new CollectionTypeRewriter(), new MapTypeRewriter(),
                new MultimapTypeRewriter(), new NullableRewriter(), new SizeableRewriter() };

    }

    /**
     * Creates a new QueryPlanRewriter from the specified node rewriters
     *
     * @param rewriters
     *            the node rewriter to use
     */
    public QueryPlanRewriter(Collection<? extends AbstractQueryNodeRewriter> rewriters) {
        this.rewriters = checkIterableForNullsAndCopyToArray("rewriters", AbstractQueryNodeRewriter.class, rewriters);
    }

    /**
     * Creates a new QueryPlanRewriter from the specified node rewriters
     *
     * @param rewriters
     *            the node rewriter to use
     */
    public QueryPlanRewriter(AbstractQueryNodeRewriter... rewriters) {
        this.rewriters = checkArrayForNullsAndCopy("rewriters", rewriters);
    }

    /**
     * Rewrites the specified plan. Returning <tt>true</tt> if the plan was modified (rewritten). Otherwise
     * <tt>false</tt>.
     *
     * @param plan
     *            the plan to rewrite
     * @return true if the plan was modified, otherwise false
     */
    public boolean rewrite(QueryPlan plan, boolean hasLogicalInformation) {
        int modCount = plan.getModCount();
        int original = modCount;
        int previousCount = -1;
        while (modCount != previousCount) { // Keep repeating until modcount is the same (no changes)
            previousCount = modCount;
            run(plan, modCount, hasLogicalInformation);
            modCount = plan.getModCount();
        }
        return original != previousCount;// have we made any changes at all
    }

    /**
     * Runs through all nodes and node rewriters to see if they can optimize.
     *
     * @param plan
     *            the query plan
     * @param modCount
     *            the current modCount
     * @param hasLogicalInformation
     *            whether or not the plan has been logical analyzed
     */
    private void run(QueryPlan plan, int modCount, boolean hasLogicalInformation) {
        for (QueryNode n : plan.getPq().getRoot().children()) {
            for (AbstractQueryNodeRewriter rewriter : rewriters) {
                if (hasLogicalInformation == rewriter.needsLogicalAnalysis()) {
                    for (QueryNodeProcessor qpp : rewriter.getTransformers()) {
                        qpp.processNode(n);
                        if (modCount != plan.getModCount()) {
                            plan.getDebugger().rewrite(plan, rewriter, qpp, n, modCount);
                            return;
                        }
                    }
                }
            }
        }
    }

    @SafeVarargs
    public static <T> T[] checkArrayForNulls(String parameterName, T... a) {
        requireNonNull(a, parameterName + " is null");
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                throw new NullPointerException(parameterName + " array contains a null at index " + i);
            }
        }
        return a;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] checkIterableForNullsAndCopyToArray(String name, Class<T> type, Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            Collection<T> col = (Collection<T>) iterable;
            return checkArrayForNulls(name, col.toArray((T[]) Array.newInstance(type, col.size())));
        }

        ArrayList<T> al = new ArrayList<>();
        for (T o : iterable) {
            al.add(o);
        }
        return checkArrayForNulls(name, al.toArray((T[]) Array.newInstance(type, al.size())));
    }

    /**
     * Equivalent to {@link #arrayNotNull(Object[])} except that this method will copy the specified array before
     * checking it. Checks whether or not the specified collection contains a {@code null}.
     *
     * @param a
     *            a copy of the checked array
     * @throws NullPointerException
     *             if the specified collection contains a null
     */
    @SafeVarargs
    public static <T> T[] checkArrayForNullsAndCopy(String parameterName, T... a) {
        return checkArrayForNulls(parameterName, Arrays.copyOf(a, a.length));
    }

    @SafeVarargs
    public static <T> List<T> checkArrayForNullsAndCopyToUnmodifiableList(String parameterName, T... elements) {
        return Collections.unmodifiableList(Arrays.asList(checkArrayForNullsAndCopy(parameterName, elements)));
    }

    @SafeVarargs
    public static <T> Set<T> checkArrayForNullsAndCopyToUnmodifiableSet(String parameterName, T... elements) {
        return Collections.unmodifiableSet(new HashSet<>(checkArrayForNullsAndCopyToUnmodifiableList(parameterName,
                elements)));
    }
}
