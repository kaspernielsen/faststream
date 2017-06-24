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
package io.faststream.query.view.interpreter;

import java.io.PrintWriter;
import java.util.List;

import io.faststream.query.util.view.CollectionView;
import io.faststream.query.view.interpreter.ViewInterpreters;
import io.faststream.sisyphus.view.CollectionViewRandomTestBuilder;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class TestCollectionView {

    public static void main(String[] args) throws Exception {
        try (final PrintWriter pw = new PrintWriter("c:/java/tmp/tmp.txt");) {
            // final Procedure<AbstractView> p = new Procedure<AbstractView>() {
            // @Override
            // public void apply(AbstractView element) {
            // pw.println(element.toString());
            // }
            // };

            CollectionViewRandomTestBuilder<Long> builder = new CollectionViewRandomTestBuilder<Long>() {
                @Override
                protected CollectionView<Long> createTestStructure(List<Long> bootstrap) {
                    return ViewInterpreters.createCollectionView(bootstrap);
                    // CollectionViewConfiguration<Long> cwv = new CollectionViewConfiguration<Long>();
                    // // cwv.debugSetExecutingViewProcedure(p);
                    // return cwv.createFromIterable(bootstrap);
                }
            };
            // lo///ng start = System.nanoTime();
            builder.start(10000000L);
            // System.out.println(DurationFormatter.DEFAULT.formatNanos(System.nanoTime() - start));
        }
    }
}
