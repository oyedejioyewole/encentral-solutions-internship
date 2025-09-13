package Month1.Week4;

import java.util.HashMap;
import java.util.Scanner;

public class Question1 {
    static class TreeNode {
        String payload;
        TreeNode leftBranch;
        TreeNode rightBranch;

        TreeNode(String payload) { this.payload = payload; }

        public HashMap<String, Integer> countAllNodes(HashMap<String, Integer> nodeElementsWithTheirCount) {
            if (this.payload != null) {
                nodeElementsWithTheirCount.put(this.payload, nodeElementsWithTheirCount.getOrDefault(this.payload, 0) + 1);

                if (this.leftBranch != null) {
                    this.leftBranch.countAllNodes(nodeElementsWithTheirCount);
                }
                if (this.rightBranch != null){
                    this.rightBranch.countAllNodes(nodeElementsWithTheirCount);
                }
            }

            return nodeElementsWithTheirCount;
        }
    }

    public static void main (String[] args) {
        // Build the binary tree
        TreeNode root = new TreeNode("start");
        root.leftBranch = new TreeNode("child");
        root.leftBranch.leftBranch = new TreeNode("movie");

        root.leftBranch.leftBranch.leftBranch = new TreeNode("steak");
        root.leftBranch.leftBranch.rightBranch = new TreeNode("child");

        root.leftBranch.leftBranch.rightBranch.leftBranch = new TreeNode("map");
        root.leftBranch.leftBranch.rightBranch.rightBranch = new TreeNode("menu");

        root.leftBranch.rightBranch = new TreeNode("menu");
        root.leftBranch.rightBranch.leftBranch = new TreeNode("pizza");
        root.leftBranch.rightBranch.rightBranch = new TreeNode("steak");

        root.rightBranch = new TreeNode("steak");
        root.rightBranch.leftBranch = new TreeNode("map");
        root.rightBranch.leftBranch.leftBranch = new TreeNode("start");

        root.rightBranch.leftBranch.leftBranch.leftBranch = new TreeNode("child");
        root.rightBranch.leftBranch.leftBranch.rightBranch = new TreeNode("steak");

        root.rightBranch.leftBranch.rightBranch = new TreeNode("pizza");

        root.rightBranch.rightBranch = new TreeNode("pizza");

        root.rightBranch.rightBranch.leftBranch = new TreeNode("menu");
        root.rightBranch.rightBranch.rightBranch = new TreeNode("steak");

        root.rightBranch.rightBranch.rightBranch.leftBranch = new TreeNode("map");

        // Traverse the binary tree
        HashMap<String, Integer> nodesWithCount = root.countAllNodes(new HashMap<>());

        System.out.print("Enter a word to check its occurrence: ");

        Scanner scanner = new Scanner(System.in);
        String query = scanner.next().trim().toLowerCase();
        scanner.close();

        System.out.printf("The word '%s' appears %s times.%n", query, nodesWithCount.getOrDefault(query, 0));
    }
}
