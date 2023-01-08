import java.util.ArrayDeque;
import java.util.Deque;

public class RedBlackTree<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private Value val;         // associated data
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link
        private int size;          // subtree count

        public Node(Key key, Value val, boolean color, int size) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.size = size;
        }
    }

    public RedBlackTree() {
    }

    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) {
            return false;
        }

        return x.color;
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) {
            return 0;
        }

        return x.size;
    }


    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return this.size(root);
    }

    /**
     * Is this symbol table empty?
     *
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    public Value get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null in put() call");
        }

        Node current = this.root;

        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current.val;
            }
        }

        return null;
    }

    // value associated with the given key in subtree rooted at x; null if no such key
    private Value get(Node x, Key key) {
        return null;
    }

    public boolean contains(Key key) {
        return this.get(key) != null;
    }

    public void put(Key key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null in put() call");
        }

        this.root = this.put(root, key, val);
        this.root.color = BLACK;
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node h, Key key, Value val) {
        if (h == null) {
            return new Node(key, val, RED, 1);
        }

        int cmp = key.compareTo(h.key);

        if (cmp < 0) {
            h.left = put(h.left, key, val);
        } else if (cmp > 0) {
            h.right = put(h.right, key, val);
        } else {
            h.val = val;
        }

        if (!isRed(h.left) && isRed(h.right)) {
            h = this.rotateLeft(h);
        }

        if (isRed(h.left) && isRed(h.left.left)) {
            h = this.rotateRight(h);
        }

        if (isRed(h.left) && isRed(h.right)) {
            this.flipColors(h);
        }

        h.size = size(h.left) + size(h.right) + 1;

        return h;
    }

    public void deleteMin() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Tree is empty");
        }

        if (this.root.left == null) {
            this.root = null;
            return;
        }

        this.root = deleteMin(this.root);
    }

    // delete the key-value pair with the minimum key rooted at h
    private Node deleteMin(Node h) {
        if (h.left == null) {
            return null;
        }

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);

        return balance(h);
    }

    public void deleteMax() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Tree is empty");
        }

        if (this.root.right == null) {
            this.root = null;
            return;
        }

        this.root = deleteMax(this.root);
    }

    // delete the key-value pair with the maximum key rooted at h
    private Node deleteMax(Node h) {
        if (isRed(h.left)) {
            h = rotateRight(h);
        }

        if (h.right == null) {
            return null;
        }

        if (!isRed(h.right) && !isRed(h.right.right)) {
            h = moveRedRight(h);
        }

        h.right = deleteMax(h.right);

        return balance(h);

    }

    public void delete(Key key) {
        if (key == null || isEmpty()) {
            throw new IllegalStateException("Delete called within illegal state");
        }

        if (!contains(key)) {
            return;
        }

        this.root = delete(root, key);

        if (!isEmpty()) {
            this.root.color = BLACK;
        }
    }

    // delete the key-value pair with the given key rooted at h
    private Node delete(Node h, Key key) {
        if (h == null) {
            return null;
        }

        int cmp = key.compareTo(h.key);

        if (cmp < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }
            if (cmp == 0 && h.right == null) {
                return null;
            }
            if (!isRed(h.right) && !isRed(h.right.right)) {
                h = moveRedRight(h);
            }
            if (cmp == 0) {
                Node min = min(h.right);
                h.key = min.key;
                h.val = min.val;
                h.right = deleteMin(h.right);
            } else {
                h.right = delete(h.right, key);
            }

        }

        return balance(h);
    }

    private Node rotateRight(Node h) {
        Node temp = h.left;
        h.left = temp.right;
        temp.right = h;
        temp.color = temp.right.color;
        temp.right.color = RED;
        temp.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;

        return temp;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        Node temp = h.right;
        h.right = temp.left;
        temp.left = h;
        temp.color = temp.left.color;
        temp.left.color = RED;
        temp.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;

        return temp;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }

        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }

        return h;
    }

    // restore red-black tree invariant
    private Node balance(Node h) {
        if (isRed(h.right)) {
            h = rotateLeft(h);
        }

        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }

        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }

        h.size = size(h.left) + size(h.right) + 1;

        return h;
    }

    public int height() {
        return this.height(root);
    }

    private int height(Node x) {
        if (x == null) {
            return -1;
        }

        return Math.max(height(x.left), height(x.right)) + 1;
    }

    public Key min() {
        Node min = this.min(this.root);
        return min != null ? min.key : null;
    }

    // the smallest key in subtree rooted at x; null if no such key
    private Node min(Node x) {
        while (x.left != null) {
            x = x.left;
        }

        return x;
    }

    public Key max() {
        Node max = this.max(this.root);
        return max != null ? max.key : null;
    }

    // the largest key in the subtree rooted at x; null if no such key
    private Node max(Node x) {
        if (x.right == null) {
            return x;
        }

        return max(x.right);
    }

    public Key floor(Key key) {
        Node floor = floor(root, key);
        if (floor == null) {
            throw new IllegalArgumentException();
        }

        return floor.key;
    }

    // the largest key in the subtree rooted at x less than or equal to the given key
    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        }

        int cmp = key.compareTo(x.key);

        if (cmp == 0) {
            return x;
        }

        if (cmp < 0) {
            return floor(x.left, key);
        }

        Node node = floor(x.right, key);

        if (node != null) {
            return node;
        }

        return x;
    }

    public Key ceiling(Key key) {
        Node ceiling = ceiling(root, key);
        if (ceiling == null) {
            return null;
        }

        return ceiling.key;
    }

    // the smallest key in the subtree rooted at x greater than or equal to the given key
    private Node ceiling(Node x, Key key) {
        if (x == null) {
            return null;
        }

        int cmp = key.compareTo(x.key);

        if (cmp == 0) {
            return x;
        }

        if (cmp > 0) {
            return ceiling(x.right, key);
        }

        Node node = ceiling(x.left, key);

        if (node != null) {
            return node;
        }

        return x;
    }

    public Key select(int rank) {
        return select(root, rank);
    }

    // Return key in BST rooted at x of given rank.
    // Precondition: rank is in legal range.
    private Key select(Node x, int rank) {
        if (x == null) {
            return null;
        }

        int leftSize = size(x.left);

        if (leftSize > rank) {
            return select(x.left, rank);
        } else if (leftSize < rank) {
            return select(x.right, rank - leftSize - 1);
        } else {
            return x.key;
        }
    }

    public int rank(Key key) {
        return rank(root, key);
    }

    // number of keys less than key in the subtree rooted at x
    private int rank(Node x, Key key) {
        if (x == null) {
            return 0;
        }

        int cmp = key.compareTo(x.key);

        if (cmp < 0) {
            return rank(x.left, key);
        } else if (cmp > 0) {
            return 1 + size(x.left) + rank(x.right, key);
        } else {
            return size(x.left);
        }

    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Deque<Key> deque = new ArrayDeque<>();
        keys(this.root, deque, lo, hi);
        return deque;
    }

    // add the keys between lo and hi in the subtree rooted at x
    // to the queue
    private void keys(Node x, Deque<Key> queue, Key lo, Key hi) {
        if (x == null) {
            return;
        }

        int cmp1 = lo.compareTo(x.key);
        int cmp2 = hi.compareTo(x.key);

        if (cmp1 < 0) {
            keys(x.left, queue, lo, hi);
        }
        if (cmp1 <= 0 && cmp2 >= 0) {
            queue.offer(x.key);
        }
        if (cmp2 > 0) {
            keys(x.right, queue, lo, hi);
        }

    }

    public int size(Key lo, Key hi) {
        int cmp = lo.compareTo(hi);

        if (cmp > 0) {
            return 0;
        }

        return rank(hi) - rank(lo);
    }

    private boolean check() {
        return false;
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return false;
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    private boolean isBST(Node x, Key min, Key max) {
        return false;
    }

    // are the size fields correct?
    private boolean isSizeConsistent() {
        return false;
    }

    private boolean isSizeConsistent(Node x) {
        return false;
    }

    // check that ranks are consistent
    private boolean isRankConsistent() {
        return false;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean isTwoThree() {
        return false;
    }

    private boolean isTwoThree(Node x) {
        return false;
    }

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        return false;
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(Node x, int black) {
        return false;
    }
}
