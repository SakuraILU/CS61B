public class HuffmanDecoder {
    public static void main(String[] args) {
        // String hufFile = args[0];
        // String outFile = args[1];
        String hufFile = args[0];
        String outFile = args[1];

        ObjectReader or = new ObjectReader(hufFile);
        BinaryTrie binaryTrie = (BinaryTrie) or.readObject();
        int symbolNum = (int) or.readObject();
        BitSequence bitSeq = (BitSequence) or.readObject();

        char[] symbols = new char[symbolNum];
        for (int i = 0; i < symbolNum; i++) {
            Match match = binaryTrie.longestPrefixMatch(bitSeq);
            symbols[i] = match.getSymbol();

            bitSeq = bitSeq.allButFirstNBits(match.getSequence().length());
        }

        FileUtils.writeCharArray(outFile, symbols);
    }
}
