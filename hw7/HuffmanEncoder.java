import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class HuffmanEncoder {
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> freqTable = new HashMap<>();

        for (char symbol : inputSymbols) {
            if (!freqTable.containsKey(symbol)) {
                freqTable.put(symbol, 1);
            } else {
                freqTable.put(symbol, freqTable.get(symbol) + 1);
            }
        }

        return freqTable;
    }

    public static void main(String[] args) {
        String file = args[0];
        ObjectWriter ow = new ObjectWriter(file + ".huf");

        char[] inputSymbols = FileUtils.readFile(file);
        Map<Character, Integer> freqTable = buildFrequencyTable(inputSymbols);
        BinaryTrie binaryTrie = new BinaryTrie(freqTable);
        Map<Character, BitSequence> lookupTable = binaryTrie.buildLookupTable();

        ow.writeObject(binaryTrie);
        ow.writeObject(inputSymbols.length);

        LinkedList<BitSequence> bitSequences = new LinkedList<>();
        for (char symbol : inputSymbols) {
            BitSequence bitSeq = lookupTable.get(symbol);
            bitSequences.add(bitSeq);
        }

        /**
         * Do not call writeObject once for each symbol!
         * This will result in huge files, very slow performance,
         * and a very complex decoder! Use BitSequence.assemble!
         */
        ow.writeObject(BitSequence.assemble(bitSequences));

        return;
    }
}