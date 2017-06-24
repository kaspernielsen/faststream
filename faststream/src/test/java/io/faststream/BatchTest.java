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
package io.faststream;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.test.sisyphus.javautil.CollectionRandomTestBuilder;
import org.junit.Ignore;

/**
 *
 * @author Kasper Nielsen
 */
public class BatchTest implements AllQueryOperations {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);

        for (int i = 0; i < 5; i++) {
            ses.scheduleAtFixedRate(() -> {}, 1, 1, TimeUnit.HOURS);
        }
        Thread.sleep(100000);
    }

    // @Test
    @Ignore
    public void test() {
        // ArrayListFactory<Integer> f = new
        // ArrayListFactoryBuilder().setSourcePath("../GeneratedClasses/src/main/java")
        // .setSourcePath("/Volumes/RAM Disk/src").build();
        ArrayListFactory<Integer> f = new ArrayListFactoryBuilder().build();

        CollectionRandomTestBuilder<Integer> builder = new CollectionRandomTestBuilder<Integer>() {
            @Override
            protected Collection<Integer> createActual(Collection<Integer> bootstrap) {
                return f.newArrayList(bootstrap);
            }

        };

        // Seed = 27238910071408
        // InitialSeed = 123456789
        builder.setExecutor(1);
        builder.setInitialSeed(System.currentTimeMillis());
        builder.start(1000000);
    }
}
