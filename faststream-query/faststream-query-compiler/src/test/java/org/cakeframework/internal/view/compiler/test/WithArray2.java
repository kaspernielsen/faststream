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
package org.cakeframework.internal.view.compiler.test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenConfiguration;

/**
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings({ "unused" })
public class WithArray2 {

    public static void madin(String[] args) {
        CodegenConfiguration cc = new CodegenConfiguration();
        // cc.setSourcePath("/Users/kasperni/tmp");
        cc.addCodeWriter(System.out);
        // BufferedReader

        Codegen c = new Codegen(cc);

        // Create a data source
        // ChainedHashMapViewSource source = new ChainedHashMapViewSource();
        // source.setSourceType(Data.class);
        // source.setAccessor("aaSmall");
        Data data = new Data();
        data.aaSmall = new Object[] { 1019800440, 1362132786, -1933932397, 397902075, 64950077, 1957992572, 442180728,
                1697976025, 1039969854, 2062777118, -718691403, 132984726, 82783393 };
        // data.aaSmall = new Object[] {};
        // Combine the two and create a compiling CollectionView
        // CompiledViewConfiguration conf = new CompiledViewConfiguration(c);

        // MapView v = conf.createMapView(data, source, new ArrayList<QueryPlanner>());
        // System.out.println(v.toList());
        //
        // v.filterNulls().reduce(TestReducers.R);
        // //
        // v.gather(new Generator() {
        //
        // @Override
        // public Object next() {
        // return TestProcedures.ignore();
        // }
        // }).toList();

        // System.out.println(v.size());

        // v.filterNulls().order().take(123).order().toList();

        // System.out.println(v.one());
        // List<?> l = v.filterNulls().filter(Predicates.isFalse()).filter(Predicates.isTrue()).toList();
        // System.out.println(l);

        // Cleanup physical variable. Hvad er en physical variable egentlig??

    }
}
