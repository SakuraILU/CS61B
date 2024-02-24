import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

public class BinaryTrie implements Serializable {

    private class Node implements Comparable<Node>, Serializable {
        public Character key;
        public int weight;
        public Node left;
        public Node right;

        Node(Character key, int weight) {
            this(key, weight, null, null);
        }

        Node(int weight, Node left, Node right) {
            this(null, weight, left, right);
        }

        Node(Character key, int weight, Node left, Node right) {
            this.key = key;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }

        public int compareTo(Node other) {
            return this.weight - other.weight;
        }
    }

    private Node root;

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyTable.entrySet()) {
            Character key = entry.getKey();
            Integer weight = entry.getValue();
            pq.add(new Node(key, weight));
        }

        while (pq.size() > 1) {
            Node node0 = pq.remove();
            Node node1 = pq.remove();
            Integer weight = node0.weight + node1.weight;
            pq.add(new Node(weight, node0, node1));
        }

        root = pq.remove();
    }

    public Match longestPrefixMatch(BitSequence querySequence) {
        return get(root, querySequence, 0);
    }

    private Match get(Node node, BitSequence querySequence, int i) {
        if (node.key != null) {
            BitSequence seq = querySequence.firstNBits(i);
            return new Match(seq, node.key);
        }

        int bit = querySequence.bitAt(i);
        if (bit == 0) {
            return get(node.left, querySequence, i + 1);
        } else {
            return get(node.right, querySequence, i + 1);
        }
    }

    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> table = new HashMap<>();
        for (Map.Entry<Character, String> entry : buildLookupTable(root).entrySet()) {
            Character key = entry.getKey();
            String bitSeqStr = entry.getValue();
            table.put(key, new BitSequence(bitSeqStr));
        }

        return table;
    }

    private Map<Character, String> buildLookupTable(Node node) {
        Map<Character, String> table = new HashMap<Character, String>();
        if (node.key != null) {
            table.put(node.key, "");
            return table;
        }

        for (Map.Entry<Character, String> entry : buildLookupTable(node.left).entrySet()) {
            Character key = entry.getKey();
            String bitSeqStr = entry.getValue();

            table.put(key, "0" + bitSeqStr);
        }

        for (Map.Entry<Character, String> entry : buildLookupTable(node.right).entrySet()) {
            Character key = entry.getKey();
            String bitSeqStr = entry.getValue();

            table.put(key, "1" + bitSeqStr);
        }

        return table;
    }
}