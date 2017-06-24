package org.cakeframework.internal.view.compiler;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cakeframework.internal.db.query.node.Operation;

public class NodePresent {

    final ConcurrentHashMap<Operation, Boolean> MAP = new ConcurrentHashMap<>();

    public static NodePresent create(Operation... types) {
        NodePresent in = new NodePresent();
        in.add(types);
        return in;
    }

    protected void add(Operation... types) {
        Arrays.asList(types).forEach(e -> MAP.put(e, true));
    }

    public boolean isPresent(Operation tag) {
        Boolean b = MAP.get(tag);
        if (b == null) {
            for (Map.Entry<Operation, Boolean> e : MAP.entrySet()) {
                if (e.getValue() && tag.is(e.getKey())) {
                    MAP.put(tag, true);
                    return true;
                }
            }
            MAP.put(tag, b = false);
        }
        return b;
    }
}
