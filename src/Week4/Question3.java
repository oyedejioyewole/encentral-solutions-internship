package Week4;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Question3 {
    static class WordCloud {
        HashMap<String, Integer> wordCounts = new HashMap<>();

        public void processText(String text) {
            // Clean and split text into words
            String cleanedText = text.toLowerCase()
                    .replaceAll("[^a-zA-Z\\s]", "") // Remove punctuation
                    .replaceAll("\\s+", " ") // Replace multiple spaces with single space
                    .trim();

            String[] words = cleanedText.split(" ");

            // Count word occurrences
            for (String word : words) {
                if (!word.isEmpty()) {
                    this.wordCounts.put(word, this.wordCounts.getOrDefault(word, 0) + 1);
                }
            }
        }

        public void displayWordCloudArt() {
            // Create ASCII art representation
            System.out.printf("%n%s%n", "█".repeat(80));
            System.out.printf("%s ASCII WORD CLOUD%n", " ".repeat(27));
            System.out.println("█".repeat(80));

            List<Map.Entry<String, Integer>> sortedWords = wordCounts.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(20) // Show top 20 words
                    .toList();

            // Create a simple text-based cloud layout
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            StringBuilder line3 = new StringBuilder();

            for (int index = 0; index < Math.min(sortedWords.size(), 15); index++) {
                String word = sortedWords.get(index).getKey();
                int count = sortedWords.get(index).getValue();

                // Distribute words across three lines based on their position
                if (index % 3 == 0) {
                    line1.append(String.format("%-12s", word + "(" + count + ")"));
                } else if (index % 3 == 1) {
                    line2.append(String.format("%-12s", word + "(" + count + ")"));
                } else {
                    line3.append(String.format("%-12s", word + "(" + count + ")"));
                }
            }

            System.out.println(this.centerText(line1.toString()));
            System.out.println(this.centerText(line2.toString()));
            System.out.println(this.centerText(line3.toString()));

            System.out.println("█".repeat(80));
        }

        private String centerText(String text) {
            if (text.length() >= 80) return text;
            int padding = (80 - text.length()) / 2;
            return " ".repeat(padding) + text;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== WORD CLOUD GENERATOR ===");
        System.out.println("Enter one or more paragraphs of text (type 'END' on a new line to finish):");

        StringBuilder inputText = new StringBuilder();
        String line;

        while (!(line = scanner.nextLine()).equals("END")) {
            inputText.append(line).append(" ");
        }

        if (inputText.isEmpty()) {
            // Use the example text from the question if no input provided
            inputText = new StringBuilder("""
            How the Word Cloud Generator Works.
            The layout algorithm for positioning words without overlap is available on
            GitHub under an open source license as d3-cloud. Note that this is the only
            the layout algorithm and any code for converting text into words and
            rendering the final output requires additional development.
            As word placement can be quite slow for more than a few hundred words, the
            layout algorithm can be run asynchronously, with a configurable time step
            size. This makes it possible to animate words as they are placed without
            stuttering. It is recommended to always use a time step even without
            animations as it prevents the browser event loop from blocking while
            placing the words.
            The layout algorithm itself is incredibly simple. For each word, starting
            with the most important:
            Attempt to place the word at some starting point: usually near the middle,
            or somewhere on a central horizontal line.
            If the word intersects with any previously placed words, move it one step
            along an increasing spiral. Repeat until no intersections are found.
            The hard part is making it perform efficiently! According to Jonathan
            Feinberg, Wordle uses a combination of hierarchical bounding boxes and
            quadtrees to achieve reasonable speeds.
            Glyphs in JavaScript
            There is a way to retrieve precise glyph shapes via the DOM, except perhaps
            for SVG fonts. Instead, we draw each word to a hidden canvas element, and
            retrieve the pixel data.
            Retrieving the pixel data separately for each word is expensive, so we draw
            as many words as possible and then retrieve their pixels in a batch
            operation.
            
            Sprites and Masks
            My initial implementation performed collision detection using sprite masks.
            Once a word is placed, it doesn't move, so we can copy it to the
            appropriate position in a larger sprite representing the whole placement
            area.
            The advantage of this is that collision detection only involves comparing a
            candidate sprite with the relevant area of this larger sprite, rather than
            comparing with each previous word separately.
            Somewhat surprisingly, a simple low-level hack made a tremendous
            difference: when constructing the sprite I compressed blocks of 32 1-bit
            pixels into 32-bit integers, thus reducing the number of checks (and
            memory) by 32 times.
            In fact, this turned out to beat my hierarchical bounding box with quadtree
            implementation on everything I tried it on (even very large areas and font
            sizes). I think this is primarily because the sprite version only needs to
            perform a single collision test per candidate area, whereas the bounding
            box version has to compare with every other previously placed word that
            overlaps slightly with the candidate area.
            Another possibility would be to merge a word's tree with a single large
            tree once it is placed. I think this operation would be fairly expensive
            though compared with the analagous sprite mask operation, which is
            essentially ORing a whole block.
            
            """);

            System.out.println("Using example text from the question...");
        }

        WordCloud wordCloud = new WordCloud();
        wordCloud.processText(inputText.toString());

        // Display results in multiple creative formats
        wordCloud.displayWordCloudArt();
        scanner.close();
    }
}