package Week3;

public class Question1 {
    /**
     * Count nodes that have both left and right children
     */
    public static int countNodesWithBothChildren(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int count = 0;

        // Check if current node has both children
        if (root.leftBranch != null && root.rightBranch != null) {
            count = 1;
        }

        // Recursively count in left and right subtrees
        count += Question1.countNodesWithBothChildren(root.leftBranch);
        count += Question1.countNodesWithBothChildren(root.rightBranch);

        return count;
    }

    public static void main(String[] arguments) {
        // Create the binary tree
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

        System.out.printf("Nodes with both children: %s",
                Question1.countNodesWithBothChildren(root));
    }
}