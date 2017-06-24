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
package org.cakeframework.internal.db.query.compiler.render.util;

import static java.util.Objects.requireNonNull;

import java.util.IdentityHashMap;
import java.util.Map;

import org.cakeframework.internal.db.query.common.nodes.elements.MapElement;
import org.cakeframework.internal.db.query.common.nodes.elements.MultimapElement;
import org.cakeframework.internal.db.query.common.nodes.elements.SingleElement;
import org.cakeframework.internal.db.query.compiler.datasource.Composite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingMapComposite;
import org.cakeframework.internal.db.query.compiler.render.PartialQuery;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.placeholders.PlaceholderStatement;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.visitor.Visitors;

/**
 * Den er ikke ubruglig, den bruges til at hvis vi f.eks. bruger main[i] 2 steder. Saa kan det betale sig at cache den
 *
 * @author Kasper Nielsen
 */
public class CacheStatement extends PlaceholderStatement {

    private final static String CACHE_KEY = CacheStatement.class.getSimpleName();
    final Composite c;
    AbstractOperationNode first;

    public CacheStatement(Composite c, AbstractOperationNode first) {
        this.c = requireNonNull(c);
        this.first = first;
    }

    static Map<Expression, CacheStatement> cache(PartialQuery pq, boolean create) {
        @SuppressWarnings("unchecked")
        Map<Expression, CacheStatement> cacheIt = (IdentityHashMap<Expression, CacheStatement>) pq.getPlan()
        .getFromCache(CACHE_KEY);
        if (cacheIt == null && create) {
            pq.getPlan().putInCache(CACHE_KEY, cacheIt = new IdentityHashMap<>());
        }
        return cacheIt;
    }

    public static void addTo(SingleElement aon, StreamingComposite com) {
        lazyCache(com.getAccessor(), aon, com);
    }

    private static void lazyCache(Expression accessor, AbstractOperationNode node, Composite com) {
        Map<Expression, CacheStatement> cacheIt = cache(node.getTree(), true);

        if (!cacheIt.containsKey(accessor)) {
            CacheStatement cs = new CacheStatement(com, node);
            cacheIt.putIfAbsent(accessor, cs);
            node.firstBlock().add(cs);
        }
    }

    public static void addTo(MultimapElement aon, StreamingMapComposite com) {
        lazyCache(com.getKeyAccessor(), aon, com);
        lazyCache(com.getValueAccessor(), aon, com);
    }

    public static void addTo(MapElement aon, StreamingMapComposite com) {
        lazyCache(com.getKeyAccessor(), aon, com);
        lazyCache(com.getValueAccessor(), aon, com);
    }

    public static void doMagic(PartialQuery pq) {
        Map<Expression, CacheStatement> cacheItMaybe = cache(pq, false);
        if (cacheItMaybe == null) {
            return;
        }
        // First we will find all those expressions that occur more than once.
        // We run through all expressions and count them
        // We exclude NameExpressions because they are already as simple as possible
        CountableSet<Expression> countingSet = new CountableSet<>();
        Visitors.forEachExpression(pq.main, t -> {
            if (!(t instanceof NameExpression)) {
                if (cacheItMaybe.containsKey(t)) {
                    countingSet.add(t);
                }
            }
        });

        // All expressions that occur at least twice
        for (Expression e : countingSet.findGreaterThan(1)) {
            CacheStatement cs = cacheItMaybe.get(e);
            NameExpression na = new NameExpression(cs.c.getName());
            Visitors.replace(pq.main, e, na);

            // The expression declaring the temporary variable
            if (cs.c instanceof StreamingMapComposite) {

                VariableDeclarationExpression newVar = Expressions.newVar(na,
                        ((StreamingMapComposite) cs.c).getKeyType(), cs.c.getAccessor());
                cs.setStatement(new ExpressionStatement(newVar));
            } else {
                VariableDeclarationExpression newVar = Expressions.newVar(na, cs.c.getType(), cs.c.getAccessor());
                cs.setStatement(new ExpressionStatement(newVar));
            }
        }
        // now remove all these temporary statements we have inserted
        // Visitors.removeStatements(plan.main, e -> e instanceof CacheStatement);

        Visitors.replaceStatements(pq.main, t -> {
            if (t instanceof CacheStatement) {
                return ((CacheStatement) t).getStatement();
            }
            return t;
        });
        // plan.print(null);
    }
}
