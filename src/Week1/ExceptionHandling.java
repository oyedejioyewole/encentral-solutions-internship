package Week1;

import java.util.Scanner;
import java.util.InputMismatchException;

public class ExceptionHandling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // These exceptions are very similar to those in JavaScript.
        try {
            System.out.print("What's the 1st number? ");
            int x = scanner.nextInt();
            System.out.print("What's the 2nd number? ");
            int y = scanner.nextInt();
            System.out.println(x / y);
        } catch (InputMismatchException | ArithmeticException error) {
            System.out.printf("Oops, there's an issue: (%s)", error.getMessage());
        }
        scanner.close();
    }
}
