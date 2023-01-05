import java.util.function.Consumer;

class AATree<T extends Comparable<T>> {
    private Node<T> root;

    public static class Node<T> {
        private T value;
        private Node<T> left;
        private Node<T> right;
        private int level;
        private int count;

        public Node(T value) {
            this.value = value;
            this.level = 1;
            this.count = 1;
        }
    }

    public AATree() {

    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void clear() {
        this.root = null;
    }

    public void insert(T element) {
        this.root = this.insert(root, element);
    }

    private Node<T> insert(Node<T> node, T element) {
        if (node == null) {
            return new Node<>(element);
        }

        int cmp = element.compareTo(node.value);

        if (cmp < 0) {
            node.left = insert(node.left, element);
        } else if (cmp > 0) {
            node.right = insert(node.right, element);
        }

        node = skew(node);
        node = split(node);

        node.count = countNodes(node.left) + countNodes(node.right) + 1;

        return node;
    }

    private Node<T> skew(Node<T> node) {
        if (node.left != null && node.left.level == node.level) {
            Node result = node.left;
            node.left = result.right;
            result.right = node;
            node.count = countNodes(node.left) + countNodes(node.right) + 1;

            return result;
        }

        return node;
    }

    private Node<T> split(Node<T> node) {
        if (node.right != null && node.right.right != null
                && node.right.right.level == node.level) {
            Node result = node.right;
            node.right = result.left;
            result.left = node;
            node.count = countNodes(node.left) + countNodes(node.right) + 1;
            result.level++;

            return result;
        }

        return node;
    }

    public int countNodes() {
        return countNodes(root);
    }

    private int countNodes(Node<T> node) {
        if (node == null) {
            return 0;
        }

        return node.count;
    }

    public boolean search(T element) {
        Node<T> current = this.root;

        while (current != null) {
            int cmp = element.compareTo(current.value);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return true;
            }
        }

        return false;
    }

    public void inOrder(Consumer<T> consumer) {
        inOrder(root, consumer);
    }

    private void inOrder(Node<T> node, Consumer<T> consumer) {
        if (node != null) {
            inOrder(node.left, consumer);
            consumer.accept(node.value);
            inOrder(node.right, consumer);
        }
    }

    public void preOrder(Consumer<T> consumer) {
        preOrder(root, consumer);
    }

    private void preOrder(Node<T> node, Consumer<T> consumer) {
        if (node != null) {
            consumer.accept(node.value);
            preOrder(node.left, consumer);
            preOrder(node.right, consumer);
        }
    }

    public void postOrder(Consumer<T> consumer) {
        postOrder(root, consumer);
    }

    private void postOrder(Node<T> node, Consumer<T> consumer) {
        if (node != null) {
            postOrder(node.left, consumer);
            postOrder(node.right, consumer);
            consumer.accept(node.value);
        }
    }
}