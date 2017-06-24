package flatmapIteratorImprove;

import io.faststream.ArrayListFactory;
import io.faststream.internal.AbstractList;
import io.faststream.internal.GeneratorUtil;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Stream;

import org.cakeframework.internal.db.nodes.stream.stream.AbstractStream;
import org.cakeframework.internal.db.query.compiler.anew.QueryCompiler;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNodeProcessor;

public class ArrayList extends AbstractList implements List, RandomAccess {

    public Object[] a;

    public int size;

    ArrayListInfo info;

    public ArrayList() {
        a = new Object[10];
    }

    public ArrayList(int initialCapacity) {
        a = new Object[checkInitialCapacity(initialCapacity)];
    }

    public ArrayList(Collection c) {
        a = new Object[c.size()];
        addAll(c);
    }

    public Object get(int index) {
        return a[index];
    }

    public int size() {
        return size;
    }

    public boolean add(Object e) {
        growIfNeeded(size + 1);
        a[size++] = e;
        return true;
    }

    void growIfNeeded(int minimumCapacity) {
        if (minimumCapacity - a.length > 0) {
            a = grow(a, minimumCapacity);
        }
    }

    public Stream stream() {
        ArrayListInfo i = info;
        return AbstractStream.from(i == null ? (info = new ArrayListInfo(this)) : i);
    }

    public static final class ArrayListInfo implements TerminalQueryOperationNodeProcessor {

        private final ArrayList l;

        ArrayListInfo(ArrayList l) {
            this.l = l;
        }

        private Processor find(TerminalQueryOperationNode node) {
            return (Processor) Factory.P.create(node);
        }

        public Object process(TerminalQueryOperationNode node) {
            return find(node).process(l.a, l.size, node);
        }
    }

    public static class Processor {

        public Object process(Object[] array, int size, TerminalQueryOperationNode node) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    public static class Factory implements ArrayListFactory {

        static final QueryCompiler P = GeneratorUtil.getIt();

        public List newArrayList() {
            return new ArrayList();
        }

        public List newArrayList(int size) {
            return new ArrayList(size);
        }

        public List newArrayList(Collection c) {
            return new ArrayList(c);
        }
    }
}
