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
package io.faststream.query.db.query.node.defaults;

import static io.faststream.query.db.query.node.Operation.aggregate;
import static io.faststream.query.db.query.node.Operation.of;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.faststream.query.db.query.node.Operation;

/**
 * Tags used single element based data structures such as {@link io.faststream.query.util.view.CollectionView},
 * {@link Stream}, {@link IntStream}, {@link LongStream} and {@link DoubleStream}.
 *
 * @author Kasper Nielsen
 */
public interface CollectionQueryOperations {

    // @formatter:off
    /** ALL COLLECTION NODES. */
    public static final Operation C                  = of("C");

    /** NON-TERMINAL NODES */
    public static final Operation C_AS_STREAM          = of("C_AS_STREAM", C);
    public static final Operation C_AS_LONG_STREAM     = of("C_AS_LONG_STREAM", C_AS_STREAM);
    public static final Operation C_AS_DOUBLE_STREAM   = of("C_AS_DOUBLE_STREAM", C_AS_STREAM);
    public static final Operation C_AS_OBJECT_STREAM   = of("C_AS_OBJECT_STREAM", C_AS_STREAM);
    //
    //    public static final OperationType C_STREAM_CAST        = of("C_STREAM", C);
    //    public static final OperationType C_STREAM_CAST_LONG   = of("C_STREAM_CAST_LONG", C_STREAM_CAST);
    //    public static final OperationType C_STREAM_CAST_DOUBLE = of("C_STREAM_CAST_DOUBLE", C_STREAM_CAST);

    public static final Operation C_FILTER              = of("C_FILTER", C);
    public static final Operation C_FILTER_NULLS        = of("C_FILTER_NULLS", C_FILTER);
    public static final Operation C_FILTER_PREDICATE    = of("C_FILTER_PREDICATE", C_FILTER);
    public static final Operation C_FILTER_ON_TYPE      = of("C_FILTER_TYPE", C_FILTER);

    public static final Operation C_MAP               = of("C_MAP", C);
    public static final Operation C_MAP_TO_INDEX      = of("C_MAP_TOINDEX", C_MAP);
    public static final Operation C_MAP_BOXING        = of("C_MAP_BOXING", C_MAP);
    public static final Operation C_MAP_TO            = of("C_MAP_TO", C_MAP);
    public static final Operation C_MAP_TO_OBJECT     = of("C_MAP_TO_OBJECT", C_MAP_TO);
    public static final Operation C_MAP_TO_DOUBLE     = of("C_MAP_TO_DOUBLE", C_MAP_TO);
    public static final Operation C_MAP_TO_LONG       = of("C_MAP_TO_LONG", C_MAP_TO);
    public static final Operation C_MAP_TO_INT        = of("C_MAP_TO_INT", C_MAP_TO);

    public static final Operation C_PEEK             = of("C_PEEK", C);
    public static final Operation C_FREQUENCY_COUNT  = of("C_FREQUENCY_COUNT", C);
    public static final Operation C_GATHER           = of("C_GATHER", C);
    public static final Operation C_GROUP_BY         = of("C_GROUP_BY", C);

    public static final Operation C_DISTINCT         = of("C_DISTINCT", C);

    public static final Operation C_FLAT_MAP             = of("C_FLAT_MAP", C);
    public static final Operation C_FLAT_MAP_TO_OBJECT   = of("C_FLAT_MAP_TO_OBJECT", C_FLAT_MAP);
    public static final Operation C_FLAT_MAP_TO_INT      = of("C_FLAT_MAP_TO_INT", C_FLAT_MAP);
    public static final Operation C_FLAT_MAP_TO_LONG     = of("C_FLAT_MAP_TO_LONG", C_FLAT_MAP);
    public static final Operation C_FLAT_MAP_TO_DOUBLE   = of("C_FLAT_MAP_TO_DOUBLE", C_FLAT_MAP);

    public static final Operation C_ORDERING                = of("C_ORDERING",                C);
    public static final Operation C_SHUFFLE                 = of("C_SHUFFLE",                 C_ORDERING);
    public static final Operation C_REVERSE                 = of("C_REVERSE",                 C_ORDERING);
    public static final Operation C_SORTED                    = of("C_SORTED",                    C_ORDERING);
    public static final Operation C_SORTED_NATURAL            = of("C_SORTED_NATURAL",            C_SORTED);
    public static final Operation C_SORTED_COMPARATOR         = of("C_SORTED_COMPARATOR",         C_SORTED);
    public static final Operation C_SORTED_NATURAL_REVERSE    = of("C_SORTED_NATURAL_REVERSE",    C_SORTED);
    public static final Operation C_SORTED_COMPARATOR_REVERSE = of("C_SORTED_COMPARATOR_REVERSE", C_SORTED);

    public static final Operation C_TRUNCATE          = of("C_TRUNCATE",          C);
    public static final Operation C_TRUNCATE_LIMIT    = of("C_TRUNCATE_LIMIT",    C_TRUNCATE);
    public static final Operation C_TRUNCATE_TAKE     = of("C_TRUNCATE_TAKE",     C_TRUNCATE);
    public static final Operation C_TRUNCATE_SKIP     = of("C_TRUNCATE_SKIP",     C_TRUNCATE);

    public static final Operation C_PARALLEL      = of("C_PARALLEL",      C);
    public static final Operation C_SEQUENTIAL    = of("C_SEQUENTIAL",    C);
    public static final Operation C_UNORDERED     = of("C_UNORDERED",     C);

    public static final Operation C_ON_CLOSE      = of("C_ON_CLOSE", C);

    /** TERMINAL NODES */
    public static final Operation CT_ANYFIRSTLAST = of("CT_ANYFIRSTLAST", C);
    public static final Operation CT_ANY          = of("CT_ANY",          CT_ANYFIRSTLAST);
    public static final Operation CT_FIRST        = of("CT_FIRST",        CT_ANYFIRSTLAST);
    public static final Operation CT_LAST         = of("CT_LAST",         CT_ANYFIRSTLAST);
    public static final Operation CT_ONE          = of("CT_ONE",          C);

    public static final Operation CT_FOR_EACH            = of("CT_FOR_EACH", C);
    public static final Operation CT_FOR_EACH_ANYORDER   = of("CT_FOR_EACH_ANYORDER", CT_FOR_EACH);
    public static final Operation CT_FOR_EACH_ORDERED    = of("CT_FOR_EACH_ORDERED", CT_FOR_EACH);


    public static final Operation CT_COLLECT            = of("CT_COLLECT", C);
    public static final Operation CT_COLLECT_COLLECTOR  = of("CT_COLLECT_COLLECTOR", CT_COLLECT);
    public static final Operation CT_COLLECT_FUSED      = of("CT_COLLECT_FUSED", CT_COLLECT);

    public static final Operation CT_MATCH        = of("CT_MATCH", C);
    public static final Operation CT_MATCH_ALL    = of("CT_MATCH_ALL", CT_MATCH);
    public static final Operation CT_MATCH_ANY    = of("CT_MATCH_ANY", CT_MATCH);
    public static final Operation CT_MATCH_NONE   = of("CT_MATCH_NONE", CT_MATCH);

    public static final Operation CT_MINMAX               = of("CT_MINMAX", C);
    public static final Operation CT_MAX                  = of("CT_MAX", CT_MINMAX);
    public static final Operation CT_MAX_NATURAL_ORDERING = of("CT_MAX_NATURAL_ORDERING", CT_MAX);
    public static final Operation CT_MAX_COMPARATOR       = of("CT_MAX_COMPARATOR", CT_MAX);
    public static final Operation CT_MIN                  = of("CT_MIN", CT_MINMAX);
    public static final Operation CT_MIN_NATURAL_ORDERING = of("CT_MIN_NATURAL_ORDERING", CT_MIN);
    public static final Operation CT_MIN_COMPARATOR       = of("CT_MIN_COMPARATOR", CT_MIN);

    public static final Operation CT_REDUCE          = of("CT_REDUCE", C);
    public static final Operation CT_REDUCE_OPERATOR = of("CT_REDUCE_OPERATOR", CT_REDUCE);
    public static final Operation CT_REDUCE_FUSED    = of("CT_REDUCE_FUSED", CT_REDUCE);

    public static final Operation CT_SIZEABLE   = of("CT_SIZEABLE", C);
    public static final Operation CT_IS_EMPTY   = of("CT_IS_EMPTY", CT_SIZEABLE);
    public static final Operation CT_SIZE       = of("CT_SIZE", CT_SIZEABLE);

    public static final Operation CT_TO                = of("CT_TO", C);
    public static final Operation CT_TO_TYPE           = of("CT_TO_TYPE", CT_TO);
    public static final Operation CT_TO_ITERATOR       = of("CT_TO_ITERATOR", CT_TO);
    public static final Operation CT_TO_SPLITERATOR    = of("CT_TO_SPLITERATOR", CT_TO);
    public static final Operation CT_TO_LIST           = of("CT_TO_LIST", CT_TO);
    public static final Operation CT_TO_ARRAY          = of("CT_TO_ARRAY", CT_TO);
    public static final Operation CT_TO_ARRAY_FUNCTION = of("CT_TO_ARRAY_FUNCTION", CT_TO);

    public static final Operation CT_MATH                     = of("CT_MATH", C);
    public static final Operation CT_MATH_ARITHMETIC_MEAN     = of("CT_MATH_AVERAGE", CT_MATH);
    public static final Operation CT_MATH_SUM                 = of("CT_MATH_SUM", CT_MATH);
    public static final Operation CT_MATH_SUMMARY_STATISTICS  = of("CT_MATH_SUMMARY_STATISTICS", CT_MATH);

    public static final Operation CT_CLOSE            = of("CT_CLOSE", C);

    /** AGGREGATE NODES */
    public static final Operation CA_REDUCING = aggregate(CT_MINMAX, CT_REDUCE_OPERATOR, CT_ONE);

    //ONE, CT_TO, ORDERING, TRUNCATE missing
    public static final Operation CA_ITERABLE = aggregate(C_FILTER, C_MAP, C_PEEK, C_FLAT_MAP, C_AS_STREAM, C_GROUP_BY,C_DISTINCT
            ,C_PARALLEL,C_SEQUENTIAL,C_UNORDERED

            , CT_TO_ITERATOR, CT_TO_SPLITERATOR, CT_ANYFIRSTLAST, CT_FOR_EACH, CT_SIZEABLE, CA_REDUCING, CT_MATH, CT_MATCH, CT_COLLECT
            );
    //Should never start an iteration of these elements from an array
    public static final Operation CA_ITERABLE_NON_STARTING = aggregate(CT_ANYFIRSTLAST, CT_SIZE, CT_IS_EMPTY, CT_TO_TYPE, CT_TO_LIST, CT_TO_ARRAY, CT_TO_ARRAY_FUNCTION);

    public static final Operation CA_SIZE_UNKNOWN = aggregate(C_FLAT_MAP);
    public static final Operation CA_SIZE_CONSTANT = aggregate(C_AS_STREAM, C_MAP, C_PEEK, C_GROUP_BY, C_ORDERING, C_PARALLEL, C_SEQUENTIAL, C_ORDERING, C_ON_CLOSE,
            C_PARALLEL,C_SEQUENTIAL,C_UNORDERED);
    public static final Operation CA_SIZE_SHRINKABLE = aggregate(C_FILTER, C_FREQUENCY_COUNT, C_GATHER, C_DISTINCT, C_TRUNCATE);
    public static final Operation CA_SIZE_SHRINKABLE_NOT_EMPTY = aggregate(C_FREQUENCY_COUNT, C_GATHER, C_DISTINCT, C_TRUNCATE_TAKE);
    public static final Operation CA_SIZE_NOT_INCREASING = aggregate(CA_SIZE_SHRINKABLE, CA_SIZE_CONSTANT);

    // @formatter:on
}
