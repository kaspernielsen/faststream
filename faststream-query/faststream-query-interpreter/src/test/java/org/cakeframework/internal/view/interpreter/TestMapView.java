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

import java.io.PrintWriter;
import java.util.Map;

import org.cakeframework.test.random.view.MapViewRandomTestBuilder;
import org.cakeframework.util.view.MapView;

/**
 * 
 * @author Kasper Nielsen
 */
public abstract class TestMapView {

    public static void main(String[] args) throws Exception {
        try (final PrintWriter pw = new PrintWriter("c:/java/tmp/tmp.txt");) {
            // final Procedure<AbstractView> p = new Procedure<AbstractView>() {
            // @Override
            // public void apply(AbstractView element) {
            // pw.println(element.toString());
            // }
            // };

            MapViewRandomTestBuilder<Long, Long> builder = new MapViewRandomTestBuilder<Long, Long>() {
                @Override
                protected MapView<Long, Long> createTestStructure(Map<Long, Long> bootstrap) {
                    // MapViewConfiguration<Long, Long> cwv = new MapViewConfiguration<Long, Long>();
                    // cwv.debugSetExecutingViewProcedure(p);
                    return ViewInterpreters.createMapView(bootstrap);
                }
            };

            builder.start(10000000L);
        }
    }
}
