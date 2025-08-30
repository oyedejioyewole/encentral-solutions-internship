package Week1;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class StringHandler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Grab the number of test cases and parse to remove the \n
        int testCases = Integer.parseInt(scanner.nextLine());
        while (testCases > 0) {
            String line = scanner.nextLine();
            boolean matchFound = false;

            // Regex pattern to match starting and ending tags (using a back-reference)
            // The middle bit of the regex gets items in-between the tags.
            Pattern pattern = Pattern.compile("<(.+)>([^<]+)</\\1>");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                System.out.println(matcher.group(2));
                matchFound = true;
            }

            if (!matchFound) {
                System.out.println("None");
            }
            testCases--;
        }
        scanner.close();
    }
}
