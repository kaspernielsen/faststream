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

import io.faststream.query.db.query.node.Operation;

/**
 *
 * @author Kasper Nielsen
 */
public interface MultimapOperations {

    // @formatter:off
    /** ALL COLLECTION NODES. */
    public static final Operation U                  = of("U");

    public static final Operation U_MAP          = of("U_MAP_ENTRY",    U);
    public static final Operation U_MAP_BI    = of("U_MAP_ENTRY",    U_MAP);
    public static final Operation U_MAP_KEY      = of("U_MAP_KEY",      U_MAP);
    public static final Operation U_MAP_VALUE    = of("U_MAP_VALUE",    U_MAP);
    public static final Operation U_VIEW_KEYS   = of("U_MAP_TO_KEY",   U_MAP);
    public static final Operation U_VIEW_VALUES = of("U_MAP_TO_VALUE", U_MAP);

    public static final Operation U_FILTER                    = of("U_FILTER",                       U);
    public static final Operation U_FILTER_PREDICATE          = of("U_FILTER_PREDICATE",             U_FILTER);
    public static final Operation U_FILTER_BY_KEY             = of("U_FILTER_BY_KEY",                U_FILTER);
    public static final Operation U_FILTER_BY_KEY_PREDICATE   = of("U_FILTER_BY_KEY_PREDICATE",      U_FILTER_BY_KEY);
    public static final Operation U_FILTER_BY_KEY_TYPE        = of("U_FILTER_BY_KEY_TYPE",           U_FILTER_BY_KEY);
    public static final Operation U_FILTER_BY_VALUE           = of("U_FILTER_BY_VALUE",              U_FILTER);
    public static final Operation U_FILTER_BY_VALUE_NULLS     = of("U_FILTER_BY_VALUE_NULLS",        U_FILTER_BY_VALUE);
    public static final Operation U_FILTER_BY_VALUE_PREDICATE = of("U_FILTER_BY_VALUE_PREDICATE",    U_FILTER_BY_VALUE);
    public static final Operation U_FILTER_BY_VALUE_TYPE      = of("U_FILTER_BY_VALUE_TYPE",         U_FILTER_BY_VALUE);

    public static final Operation U_ORDERING                         = of("U_ORDERING",                            U);
    public static final Operation U_SHUFFLE                          = of("U_SHUFFLE",                             U_ORDERING);
    public static final Operation U_REVERSE                          = of("U_REVERSE",                             U_ORDERING);
    public static final Operation U_SORTED                           = of("U_SORTED",                              U_ORDERING);
    public static final Operation U_SORTED_COMPARATOR                = of("U_SORTED_COMPARATOR",                   U_SORTED);
    public static final Operation U_SORTED_KEYS_NATURAL              = of("U_SORTED_BY_KEY_NATURAL",               U_SORTED);
    public static final Operation U_SORTED_KEYS_COMPARATOR           = of("U_SORTED_BY_KEY_COMPARATOR",            U_SORTED);
    public static final Operation U_SORTED_KEYS_NATURAL_REVERSE      = of("U_SORTED_BY_KEY_NATURAL_REVERSE",       U_SORTED);
    public static final Operation U_SORTED_KEYS_COMPARATOR_REVERSE   = of("U_SORTED_BY_KEY_COMPARATOR_REVERSE",    U_SORTED);
    public static final Operation U_SORTED_VALUES_NATURAL            = of("U_SORTED_BY_VALUE_NATURAL",             U_SORTED);
    public static final Operation U_SORTED_VALUES_COMPARATOR         = of("U_SORTED_BY_VALUE_COMPARATOR",          U_SORTED);
    public static final Operation U_SORTED_VALUES_NATURAL_REVERSE    = of("U_SORTED_BY_VALUE_NATURAL_REVERSE",     U_SORTED);
    public static final Operation U_SORTED_VALUES_COMPARATOR_REVERSE = of("U_SORTED_BY_VALUE_COMPARATOR_REVERSE",  U_SORTED);

    public static final Operation U_TRUNCATE                = of("U_TRUNCATE",              U);
    public static final Operation U_TRUNCATE_TAKE           = of("U_TRUNCATE_TAKE",         U_TRUNCATE);
    public static final Operation U_TRUNCATE_TAKE_VALUES    = of("U_TRUNCATE_TAKE_VALUES",  U_TRUNCATE);


    public static final Operation U_REDUCE = of("U_REDUCE", U);
    public static final Operation U_COUNT = of("U_COUNT", U);
    public static final Operation U_DISTINCT = of("U_DISTINCT", U);

    public static final Operation U_ANY   = of("U_ANY", U);
    public static final Operation U_FIRST = of("U_FIRST", U);
    public static final Operation U_LAST  = of("U_LAST", U);
    public static final Operation U_ONE   = of("U_ONE", U);

    /** TERMINAL NODES */
    public static final Operation UT_SIZEABLE    = of("UT_SIZEABLE", U);
    public static final Operation UT_IS_EMPTY    = of("UT_IS_EMPTY", UT_SIZEABLE);
    public static final Operation UT_SIZE        = of("UT_SIZE",     UT_SIZEABLE);

    public static final Operation UT_TO           = of("UT_TO",       U);
    public static final Operation UT_TO_TYPE      = of("UT_TO_TYPE",  UT_TO);
    public static final Operation UT_TO_MULTIMAP  = of("UT_TO_MAP",   UT_TO);

    public static final Operation UT_FOR_EACH            = of("UT_FOR_EACH", U);
    public static final Operation UT_FOR_EACH_ANYORDER   = of("UT_FOR_EACH_ANYORDER", UT_FOR_EACH);
    public static final Operation UT_FOR_EACH_ORDERED    = of("UT_FOR_EACH_ORDERED", UT_FOR_EACH);


    public static final Operation UA_ITERABLE = aggregate(U_MAP, U_FILTER, UT_SIZEABLE, UT_FOR_EACH);

    public static final Operation UA_SIZE_CONSTANT = aggregate(U_MAP, U_ORDERING);
    public static final Operation UA_SIZE_SHRINKABLE_NOT_EMPTY = aggregate(U_TRUNCATE_TAKE, U_REDUCE, U_COUNT, U_DISTINCT, U_ANY, U_FIRST, U_LAST, U_ONE);
    public static final Operation UA_SIZE_SHRINKABLE = aggregate(U_FILTER, U_TRUNCATE_TAKE, U_REDUCE, U_COUNT, U_DISTINCT, U_ANY, U_FIRST, U_LAST, U_ONE);
    public static final Operation UA_SIZE_NOT_INCREASING = aggregate(UA_SIZE_CONSTANT, UA_SIZE_SHRINKABLE);

    // @formatter:on

}
