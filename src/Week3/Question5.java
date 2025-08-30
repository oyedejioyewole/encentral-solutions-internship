package Week3;

import java.util.Arrays;

public class Question5 {
    static class Sorter {
        /**
         * Quick Sort implementation
         */
        public static void quickSort(int[] arr, int low, int high) {
            if (low < high) {
                // Partition the array and get pivot index
                int pivotIndex = partition(arr, low, high);

                // Recursively sort elements before and after partition
                quickSort(arr, low, pivotIndex - 1);
                quickSort(arr, pivotIndex + 1, high);
            }
        }
        public static void quickSort(int[] arr) {
            quickSort(arr, 0, arr.length - 1);
        }

        /**
         * Partition function using last element as pivot
         */
        private static int partition(int[] arr, int low, int high) {
            // Choose last element as pivot
            int pivot = arr[high];

            // Index of smaller element
            int i = low - 1;

            for (int j = low; j < high; j++) {
                // If current element is smaller than or equal to pivot
                if (arr[j] <= pivot) {
                    i++;
                    swap(arr, i, j);
                }
            }

            // Place pivot in correct position
            swap(arr, i + 1, high);
            return i + 1;
        }

        /**
         * Utility method to swap elements
         */
        private static void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void main(String[] arguments) {
        int[] simpleSort = {9, -3, 5, 2, 6, 8, -6, 1, 3};
        Sorter.quickSort(simpleSort);
        System.out.println("Verification: " + Arrays.toString(simpleSort));
    }
}