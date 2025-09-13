package Month1.Week3;

import java.util.LinkedList;
import java.util.Queue;

public class Question3 {
    /**
     * Calculate difference between sum of levels (1,2) and (3,4)
     * <p>
     * Using pre-order traversal
     */
    public static int levelDifference(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int level = 1;

        // Index 0 unused, levels 1-4
        int[] levelSums = new int[5];

        while (!queue.isEmpty() && level <= 4) {
            int levelSize = queue.size();
            int levelSum = 0;

            for (int index = 0; index < levelSize; index++) {
                TreeNode node = queue.poll();
                assert node != null;
                levelSum += node.value;

                if (node.leftBranch != null) {
                    queue.offer(node.leftBranch);
                }
                if (node.rightBranch != null) {
                    queue.offer(node.rightBranch);
                }
            }

            levelSums[level] = levelSum;
            level++;
        }

        // Calculate difference: (L1 + L2) - (L3 + L4)
        int sumLevels12 = levelSums[1] + levelSums[2];
        int sumLevels34 = levelSums[3] + levelSums[4];

        return sumLevels12 - sumLevels34;
    }

    public static void main(String[] arguments) {
        TreeNode root = new TreeNode(5);
        root.leftBranch = new TreeNode(4);
        root.leftBranch.leftBranch = new TreeNode(3);
        root.leftBranch.leftBranch.leftBranch = new TreeNode(2);
        root.leftBranch.leftBranch.rightBranch = new TreeNode(6);
        root.leftBranch.rightBranch = new TreeNode(7);
        root.rightBranch = new TreeNode(7);
        root.rightBranch.leftBranch = new TreeNode(5);
        root.rightBranch.rightBranch = new TreeNode(8);
        root.rightBranch.rightBranch.leftBranch = new TreeNode(6);
        root.rightBranch.rightBranch.rightBranch = new TreeNode(9);

        // It should output: -30
        System.out.println("Level difference: " + Question3.levelDifference(root));
    }
}