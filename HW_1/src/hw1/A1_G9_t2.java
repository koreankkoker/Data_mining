package hw1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class FPNode {
    String itemName;
    int count;
    FPNode parent;
    FPNode next; // Pointer to the next node with the same item name
    List<FPNode> children;

    public FPNode(String itemName, int count, FPNode parent) {
        this.itemName = itemName;
        this.count = count;
        this.parent = parent;
        this.children = new ArrayList<>();
    }
}

public class A1_G9_t2 {
	private LinkedHashMap<String, Integer> headerTable;
    private FPNode root;
    private double minSupport;
    private List<List<String>> transactions;

    public A1_G9_t2(String filename, double minSupport) {
        this.minSupport = minSupport;
        this.transactions = new ArrayList<>();
        this.headerTable = new LinkedHashMap<>();
        readTransactions(filename);
        
//        if (!transactions.isEmpty()) {
//            System.out.print("First Transaction : ");
//            List<String> firstTransaction = transactions.get(0);
//            for (String item : firstTransaction) {
//                System.out.print(item + ",");
//            }
//            System.out.println();
//        } else {
//            System.out.println("No transactions found.");
//        }
        
    }

    private void readTransactions(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                transactions.add(Arrays.asList(items));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findFrequent1Itemsets() {
        Map<String, Integer> itemCounts = new HashMap<>();
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            if ((double) entry.getValue() / transactions.size() >= minSupport) {
                headerTable.put(entry.getKey(), entry.getValue());
            }
        }
        
//     // Print the contents of the headerTable HashMap
//        System.out.println(transactions.size());
//        System.out.println("Frequent 1-itemsets:");        
//        for (Map.Entry<String, Integer> entry : headerTable.entrySet()) {
//            System.out.println(entry.getKey() + ": " + (double) entry.getValue() / transactions.size());
//        }
    }

    private void sortFList() {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(headerTable.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

/*        // Print the sorted entries
        for (Map.Entry<String, Integer> entry : sortedEntries) {
        	String results = String.format("%.7f", (double) entry.getValue() / transactions.size());
            System.out.println(entry.getKey() + " " + results);
        }
*/
        
        headerTable.clear();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            headerTable.put(entry.getKey(), entry.getValue());
        }
        
        // Print the contents of the headerTable HashMap
        System.out.println("Frequent 1-itemsets:");        
        for (Map.Entry<String, Integer> entry : headerTable.entrySet()) {
        	System.out.println(entry.getKey() + ": " + (double) entry.getValue() / transactions.size());
        }
        
    }
    
    private void buildFPtree() {
        root = new FPNode(null, 0, null);
        for (List<String> transaction : transactions) {
            FPNode currentNode = root;
            for (String item : transaction) {
                if (headerTable.containsKey(item)) {
                    FPNode child = findChild(currentNode, item);
                    if (child == null) {
                        child = new FPNode(item, 1, currentNode);
                        currentNode.children.add(child);
                        updateHeaderTable(item, child);
                        currentNode = child;
                    } else {
                        child.count++;
                        currentNode = child;
                    }
                }
            }
        }
    }
    
    private FPNode findChild(FPNode parent, String itemName) {
        for (FPNode child : parent.children) {
            if (child.itemName.equals(itemName)) {
                return child;
            }
        }
        return null;
    }
    
    private void updateHeaderTable(String itemName, FPNode node) {
        if (headerTable.containsKey(itemName)) {
            int index = headerTable.get(itemName);
            FPNode lastNode = findLastNode(index);
            if (lastNode != null) {
                lastNode.next = node;
            }
        } else {
            headerTable.put(itemName, transactions.size());
        }
    }
    
    private FPNode findLastNode(int index) {
        if (root == null) {
            return null;
        }
        FPNode currentNode = root;
        for (int i = 0; i < index; i++) {
            if (currentNode == null) {
                return null;
            }
            currentNode = currentNode.next;
        }
        return currentNode;
    }
    
    private void printFrequentItemsets() {
        for (Map.Entry<String, Integer> entry : headerTable.entrySet()) {
            double support = (double) entry.getValue() / transactions.size();
            System.out.printf("%s %.7f\n", entry.getKey(), support);
        }
    }

    public void mineFrequentItemsets() {
        findFrequent1Itemsets();
        sortFList();
        buildFPtree();
        printFrequentItemsets();
    }

    public static void main(String[] args) {
    	
    	long startTime = System.currentTimeMillis();
    	
        String filename = args[0];
        double minSupport = Double.parseDouble(args[1]);
        A1_G9_t2 fpgrowth = new A1_G9_t2(filename, minSupport);
        fpgrowth.mineFrequentItemsets();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Execution Time: " + duration + " milliseconds");
    }
}
