package Week4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Question2 {
    static class Sorter {
        List<String> rows;

        Sorter(List<String> rows) {
            this.rows = rows;
        }

        public void byAge() {
            this.rows.stream().sorted((firstPerson, secondPerson) -> {
                int firstPersonsAge = Integer.parseInt(firstPerson.split(",")[2]);
                int secondPersonsAge = Integer.parseInt(secondPerson.split(",")[2]);

                return Integer.compare(firstPersonsAge, secondPersonsAge);
            }).forEach(row -> {
                String[] columns = row.split(",");
                System.out.printf("Uhm, %s %s is %d years old%n", columns[1], columns[0], Integer.parseInt(columns[2]));
            });
        }

        public void byFirstName() {
            this.rows.stream().sorted((firstPerson, secondPerson) -> {
                String firstPersonsFirstName = firstPerson.split(",")[0];
                String secondPersonsFirstName = secondPerson.split(",")[0];

                return firstPersonsFirstName.compareTo(secondPersonsFirstName);
            }).forEach(row -> {
                String[] columns = row.split(",");
                System.out.printf("Uhm, %s %s is %d years old%n", columns[1], columns[0].toUpperCase(), Integer.parseInt(columns[2]));
            });
        }

        public void byLastName() {
            this.rows.stream().sorted((firstPerson, secondPerson) -> {
                String firstPersonsLastName = firstPerson.split(",")[1];
                String secondPersonsLastName = secondPerson.split(",")[1];

                return firstPersonsLastName.compareTo(secondPersonsLastName);
            }).forEach(row -> {
                String[] columns = row.split(",");
                System.out.printf("Uhm, %s %s is %d years old%n", columns[1].toUpperCase(), columns[0], Integer.parseInt(columns[2]));
            });
        }
    }

    public static void main(String[] args) {
        Sorter sorter = getSorter();

        System.out.println("""
        Enter 1 to sort by "First Name".
        Enter 2 to sort by "Last Name".
        Enter 3 to sort by "Age".
        
        Tip: Focus on the capitalized bits.
        """);
        System.out.print("I'm picking option: ");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        scanner.close();
        System.out.println();

        switch (option) {
            case 1: {
                sorter.byFirstName();
                break;
            }
            case 2: {
                sorter.byLastName();
                break;
            }
            case 3: {
                sorter.byAge();
                break;
            }
            default: {
                System.out.println("Uh oh, you picked an invalid option.");
            }
        }
    }

    private static Sorter getSorter() {
        String dataSource = """
        First Name,Last Name,Age
        Onome,Ehigiator,45
        Adegoke,Akeem-omosanya,67
        Bukola,Ehigiator,66
        Olufunmi,Aremu,34
        Ifeanyichukwu,Ekwueme,54
        Isioma,Mustapha,57
        Ayebatari,Joshua,25
        Nnamdi,Olawale,76
        Lola,Abosede,45
        Emeka,Oyelude,34
        Aminu,Ogunbanwo,67
        Simisola,Ekwueme,98
        Ayebatari,Busari,56
        Chinyere,Uchechi,52
        Adeboye,Jamiu,84
        Titilayo,Kimberly,56
        Chimamanda,Ehigiator,34
        Bukola,Adegoke,57
        Cherechi,Elebiyo,59
        Titilayo,Afolabi,90
        """;

        String[] lines = dataSource.trim().split("\n");
        List<String> rows = new ArrayList<>();

        for (int row = 1; row < lines.length; row++) {
            rows.add(lines[row].trim());
        }

        return new Sorter(rows);
    }
}
