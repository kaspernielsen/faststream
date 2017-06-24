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
package io.faststream.sisyphus.view.collectionview;

import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionEqualInAnyOrder;
import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionIsCollectionSubset;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.faststream.sisyphus.view.spi.ExpectedCollectionView;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link CollectionView#take(long)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Take<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void take() {
        int expectedSize = expected().size();
        long count = randomTakeCount(expectedSize);
        if (isOrdered()) {
            ExpectedCollectionView<E> l;
            if (count > 0) {
                l = expected().subList(0, count > expectedSize ? expectedSize : (int) count);
            } else {
                int startIndex = (count + expectedSize < 0) ? 0 : (int) (count + expectedSize);
                l = expected().subList(startIndex, expectedSize);
            }
            setNested(l, actual().take(count));
        } else {
            List<E> l = actual().take(count).toList();
            if (count > 0) {
                if (count >= expectedSize) {
                    // No reason not to continue here I think instead of consuming
                    assertEquals(expectedSize, l.size());
                    assertCollectionEqualInAnyOrder(expected(), l);
                } else {
                    assertEquals(count, l.size());
                    assertCollectionIsCollectionSubset(expected(), l);
                }
            } else {
                if (count <= -expectedSize) {
                    assertEquals(expectedSize, l.size());
                    assertCollectionEqualInAnyOrder(expected(), l);
                } else {
                    assertEquals(count, -l.size());
                    assertCollectionIsCollectionSubset(expected(), l);
                }
            }
        }
    }
}
