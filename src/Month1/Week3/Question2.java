package Month1.Week3;

import java.util.Set;
import java.util.HashSet;

public class Question2 {
    public static String findDuplicate(int[] numbers) {
        Set<Integer> recordedNumbers = new HashSet<>();
        for (int number : numbers) {
            if (recordedNumbers.contains(number)) {
                return "The duplicate element is " + number;
            } else {
                recordedNumbers.add(number);
            }
        }

        return "Couldn't find a duplicate, unlucky much?";
    }

    public static void main(String[] arguments) {
        int[] numbers = { 1, 2, 3, 4, 4 };
        System.out.println(Question2.findDuplicate(numbers));
    }
}