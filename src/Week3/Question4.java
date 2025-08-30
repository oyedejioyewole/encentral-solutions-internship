package Week3;

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

public class Question4 {
    public static ArrayList<String> generateOddBinaryNumbers(int limit) {
        Queue<String> queue = new LinkedList<>();
        ArrayList<String> results = new ArrayList<>();

        // Compile all odd numbers into a queue for processing
        for (int number = 1; number < limit; number++) {
            // If there's a remainder from the division, it's not an odd number.
            if (number % 2 == 1) {
                queue.offer(Integer.toString(number));
            }
        }

        while (!queue.isEmpty()) {
            String oddNumber = queue.poll();
            results.add(Integer.toBinaryString(Integer.parseInt(oddNumber)));
        }

        return results;
    }

    public static void main(String[] arguments) {
        int limit = 100;
        ArrayList<String> oddBinaries = Question4.generateOddBinaryNumbers(limit);
        System.out.println("Odd binary numbers from 1 to " + limit  + ": " +
                String.join(" ", oddBinaries));
    }
}