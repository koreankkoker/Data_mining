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
    private Map<String, Integer> headerTable;
    private FPNode root;
    private double minSupport;
    private List<List<String>> transactions;

    public A1_G9_t2(String filename, double minSupport) {
        this.minSupport = minSupport;
        this.transactions = new ArrayList<>();
        this.headerTable = new HashMap<>();
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

        // Print the sorted entries
        for (Map.Entry<String, Integer> entry : sortedEntries) {
        	String results = String.format("%.7f", (double) entry.getValue() / transactions.size());
            System.out.println(entry.getKey() + " " + results);
        }
        
    }


    public void mineFrequentItemsets() {
        findFrequent1Itemsets();
        sortFList();
    }

    public static void main(String[] args) {
        String filename = args[0];
        double minSupport = Double.parseDouble(args[1]);
        A1_G9_t2 fpgrowth = new A1_G9_t2(filename, minSupport);
        fpgrowth.mineFrequentItemsets();
    }
}
