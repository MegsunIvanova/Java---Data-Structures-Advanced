package main;

import java.util.*;
import java.util.stream.Collectors;

public class Hierarchy<T> implements IHierarchy<T> {

    private Map<T, HierarchyNode<T>> data;
    private HierarchyNode<T> root;

    public Hierarchy(T element) {
        this.data = new HashMap<>();

        HierarchyNode<T> root = new HierarchyNode<>(element);
        this.root = root;

        this.data.put(element, root);

    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public void add(T element, T child) {
        HierarchyNode<T> parent = ensureExistsAndGet(element);

        if (data.containsKey(child)) {
            throw new IllegalArgumentException();
        }

        HierarchyNode<T> childNode = new HierarchyNode<>(child);
        childNode.setParent(parent);

        parent.getChildren().add(childNode);

        this.data.put(child, childNode);
    }

    @Override
    public void remove(T element) {

        HierarchyNode<T> toBeRemoved = ensureExistsAndGet(element);

        HierarchyNode<T> newParent = toBeRemoved.getParent();

        if (newParent == null) {
            throw new IllegalStateException();
        }

        List<HierarchyNode<T>> children = toBeRemoved.getChildren();
        children.forEach(child -> child.setParent(newParent));

        newParent.getChildren().addAll(children);
        newParent.getChildren().remove(toBeRemoved);

        this.data.remove(toBeRemoved.getValue());

    }

    @Override
    public Iterable<T> getChildren(T element) {
        HierarchyNode<T> current = ensureExistsAndGet(element);

        return current.getChildren().stream().map(HierarchyNode::getValue).collect(Collectors.toList());
    }

    @Override
    public T getParent(T element) {
        HierarchyNode<T> current = ensureExistsAndGet(element);

        return current.getParent() == null ? null : current.getParent().getValue();
    }

    @Override
    public boolean contains(T element) {
        return data.containsKey(element);
    }

    @Override
    public Iterable<T> getCommonElements(IHierarchy<T> other) {
        List<T> result = new ArrayList<>();

        this.data.keySet().forEach(k -> {
            if (other.contains(k)) {
                result.add(k);
            }
        });

        return result;
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            private Deque<HierarchyNode<T>> deque = new ArrayDeque<>(Collections.singletonList(root));

            @Override
            public boolean hasNext() {
                return deque.size() > 0;
            }

            @Override
            public T next() {
                HierarchyNode<T> nextElement = deque.poll();

                deque.addAll(nextElement.getChildren());

                return nextElement.getValue();
            }
        };
    }

    private HierarchyNode<T> ensureExistsAndGet(T key) {
        HierarchyNode<T> element = data.get(key);

        if (element == null) {
            throw new IllegalArgumentException();
        }

        return element;
    }
}
