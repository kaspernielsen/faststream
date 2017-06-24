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
package org.cakeframework.internal.view.interpreter;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import org.cakeframework.util.view.CollectionView;

/**
 * 
 * @author Kasper Nielsen
 */
public final class ErrorMessages implements Serializable {

    /** The error message used when trying to apply {@link CollectionView#any()} on an empty view. */
    public static final String EMPTY_VIEW_ANY_ERROR_MESSAGE = errorMessageForEmptyView("any element", "any(Object)");

    /** The error message used when trying to apply {@link CollectionView#first()} on an empty view. */
    public static final String EMPTY_VIEW_HEAD_ERROR_MESSAGE = errorMessageForEmptyView("head element", "head(Object)");

    /** The error message used when trying to apply {@link CollectionView#one()} on an empty view. */
    public static final String EMPTY_VIEW_ONE_ERROR_MESSAGE = errorMessageForEmptyView("one element", "one(Object)");

    /** The error message used when trying to apply {@link CollectionView#reduce(BinaryOperator)} on an empty view. */
    public static final String EMPTY_VIEW_REDUCE_ERROR_MESSAGE = errorMessageForEmptyView("reduction of elements",
            "reduce(Reducer, Object)");

    /** The error message used when trying to apply {@link CollectionView#last()} on an empty view. */
    public static final String EMPTY_VIEW_TAIL_ERROR_MESSAGE = errorMessageForEmptyView("tail element", "tail(Object)");

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private ErrorMessages() {}

    static String errorMessageForEmptyView(String action, String method) {
        return "The view is empty so cannot return " + action + ". You can use " + CollectionView.class.getSimpleName()
                + "#" + method + " in case you want to return a default value"
                + " for an empty view instead of throwing this exception";
    }

    /**
     * The error message used when trying to apply {@link CollectionView#one()} or {@link CollectionView#one(Object)} on
     * a view with more than 1 element.
     */
    public static final String MORE_THAN_ONE_ELEMENT_VIEW_ERROR_MESSAGE = "The view contained more than 1 element";

}
