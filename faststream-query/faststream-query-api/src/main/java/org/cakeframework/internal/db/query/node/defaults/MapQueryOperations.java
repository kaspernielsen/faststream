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
package org.cakeframework.internal.db.query.node.defaults;

import static org.cakeframework.internal.db.query.node.Operation.aggregate;
import static org.cakeframework.internal.db.query.node.Operation.of;

import org.cakeframework.internal.db.query.node.Operation;

/**
 * Tags used for pair based data structures with unique keys mapping to a single value such as
 * {@link org.cakeframework.util.view.MapView}.
 *
 * @author Kasper Nielsen
 */
public interface MapQueryOperations {

    // @formatter:off
    /** ALL COLLECTION NODES. */
    public static final Operation M                  = of("M");

    /** NON-TERMINAL NODES */
    public static final Operation M_MAP           = of("M_MAP", M);
    public static final Operation M_MAP_FUNCTION  = of("M_MAP_FUNCTION", M_MAP);
    public static final Operation M_MAP_BI        = of("M_MAP_BI",       M_MAP_FUNCTION);
    public static final Operation M_MAP_KEY       = of("M_MAP_KEY",      M_MAP_FUNCTION);
    public static final Operation M_MAP_VALUE     = of("M_MAP_VALUE",    M_MAP_FUNCTION);
    public static final Operation M_VIEW          = of("M_VIEW",         M_MAP);
    public static final Operation M_VIEW_KEYS     = of("M_VIEW_KEYS",    M_VIEW);
    public static final Operation M_VIEW_VALUES   = of("M_VIEW_VALUES",  M_VIEW);
    public static final Operation M_VIEW_ENTRIES  = of("M_VIEW_ENTRIES", M_VIEW);

    public static final Operation M_FILTER                    = of("M_FILTER",                      M);
    public static final Operation M_FILTER_PREDICATE          = of("M_FILTER_PREDICATE",            M_FILTER);
    public static final Operation M_FILTER_BY_KEY             = of("M_FILTER_BY_KEY",               M_FILTER );
    public static final Operation M_FILTER_BY_KEY_PREDICATE   = of("M_FILTER_BY_KEY_PREDICATE",     M_FILTER_BY_KEY);
    public static final Operation M_FILTER_BY_KEY_TYPE        = of("M_FILTER_BY_KEY_TYPE",          M_FILTER_BY_KEY);
    public static final Operation M_FILTER_BY_VALUE           = of("M_FILTER_BY_VALUE",             M_FILTER );
    public static final Operation M_FILTER_BY_VALUE_NULLS     = of("M_FILTER_BY_VALUE_NULLS",       M_FILTER_BY_VALUE);
    public static final Operation M_FILTER_BY_VALUE_PREDICATE = of("M_FILTER_BY_VALUE_PREDICATE",   M_FILTER_BY_VALUE);
    public static final Operation M_FILTER_BY_VALUE_TYPE      = of("M_FILTER_BY_VALUE_TYPE",        M_FILTER_BY_VALUE);

    public static final Operation M_ORDERING                           = of("M_ORDERING",                            M);
    public static final Operation M_SHUFFLE                            = of("M_SHUFFLE",                             M_ORDERING);
    public static final Operation M_REVERSE                            = of("M_REVERSE",                             M_ORDERING);
    public static final Operation M_SORTED                             = of("M_SORTED",                              M_ORDERING);
    public static final Operation M_SORTED_COMPARATOR                  = of("M_SORTED_COMPARATOR",                   M_SORTED);
    public static final Operation M_SORTED_BY_KEY_NATURAL              = of("M_SORTED_BY_KEY_NATURAL",               M_SORTED);
    public static final Operation M_SORTED_BY_KEY_COMPARATOR           = of("M_SORTED_BY_KEY_COMPARATOR",            M_SORTED);
    public static final Operation M_SORTED_BY_KEY_NATURAL_REVERSE      = of("M_SORTED_BY_KEY_NATURAL_REVERSE",       M_SORTED);
    public static final Operation M_SORTED_BY_KEY_COMPARATOR_REVERSE   = of("M_SORTED_BY_KEY_COMPARATOR_REVERSE",    M_SORTED);
    public static final Operation M_SORTED_BY_VALUE_NATURAL            = of("M_SORTED_BY_VALUE_NATURAL",             M_SORTED);
    public static final Operation M_SORTED_BY_VALUE_COMPARATOR         = of("M_SORTED_BY_VALUE_COMPARATOR",          M_SORTED);
    public static final Operation M_SORTED_BY_VALUE_NATURAL_REVERSE    = of("M_SORTED_BY_VALUE_NATURAL_REVERSE",     M_SORTED);
    public static final Operation M_SORTED_BY_VALUE_COMPARATOR_REVERSE = of("M_SORTED_BY_VALUE_COMPARATOR_REVERSE",  M_SORTED);

    public static final Operation M_TRUNCATE_TAKE = of("M_TRUNCATE_TAKE", M);

    /** TERMINAL NODES */
    public static final Operation MT_SIZEABLE    = of("MT_SIZEABLE", M);
    public static final Operation MT_IS_EMPTY    = of("MT_IS_EMPTY", MT_SIZEABLE);
    public static final Operation MT_SIZE        = of("MT_SIZE",     MT_SIZEABLE);

    public static final Operation MT_TO      = of("MT_TO",       M);
    public static final Operation MT_TO_TYPE = of("MT_TO_TYPE",  MT_TO);
    public static final Operation MT_TO_MAP  = of("MT_TO_MAP",   MT_TO);

    public static final Operation MT_FOR_EACH            = of("MT_FOR_EACH", M);
    public static final Operation MT_FOR_EACH_ANYORDER   = of("MT_FOR_EACH_ANYORDER", MT_FOR_EACH);
    public static final Operation MT_FOR_EACH_ORDERED    = of("MT_FOR_EACH_ORDERED", MT_FOR_EACH);

    /** AGGREGATE NODES */
    public static final Operation MA_ITERABLE = aggregate(M_VIEW_ENTRIES, M_MAP_VALUE, M_FILTER, M_VIEW_KEYS, M_VIEW_VALUES, M_MAP, MT_FOR_EACH, MT_SIZE, MT_IS_EMPTY);

    public static final Operation MA_SIZE_CONSTANT = aggregate(M_MAP, M_ORDERING);
    public static final Operation MA_SIZE_SHRINKABLE = aggregate(M_FILTER, M_TRUNCATE_TAKE);
    public static final Operation MA_SIZE_SHRINKABLE_NOT_EMPTY = aggregate(M_TRUNCATE_TAKE);
    public static final Operation MA_SIZE_NOT_INCREASING = aggregate(MA_SIZE_CONSTANT, MA_SIZE_SHRINKABLE);

    // @formatter:on

}
