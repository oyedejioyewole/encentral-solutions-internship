package Week4;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Question4 {
    static class Scrabble {
        ArrayList<Character> availableLetters;
        String[] dictionary;

        Scrabble(String word, String[] dictionary) {
            this.availableLetters = new ArrayList<>();
            for (char characters : word.toCharArray()) {
                this.availableLetters.add(characters);
            }
            this.dictionary = dictionary;
        }

        // Check if a word can be formed with remaining letters
        public boolean canFormWord(String word, HashSet<Integer> usedIndices) {
            ArrayList<Character> tempAvailable = new ArrayList<>();

            // Build list of available letters (excluding already used ones)
            for (int index = 0; index < availableLetters.size(); index++) {
                if (!usedIndices.contains(index)) {
                    tempAvailable.add(availableLetters.get(index));
                }
            }

            // Try to form the word
            ArrayList<Character> wordCharacters = new ArrayList<>();
            for (char character : word.toCharArray()) {
                wordCharacters.add(character);
            }

            // Check if we can remove all characters of the word from available letters
            for (char character : wordCharacters) {
                if (!tempAvailable.remove((Character) character)) {
                    return false; // Character not available
                }
            }

            return true;
        }

        // Find which letter indices would be consumed by forming a word
        public HashSet<Integer> getConsumedIndices(String word, HashSet<Integer> usedIndices) {
            HashSet<Integer> consumed = new HashSet<>();
            ArrayList<Character> wordChars = new ArrayList<>();
            for (char character : word.toCharArray()) {
                wordChars.add(character);
            }

            for (char needed : wordChars) {
                for (int index = 0; index < availableLetters.size(); index++) {
                    if (!usedIndices.contains(index) && !consumed.contains(index) &&
                            availableLetters.get(index) == needed) {
                        consumed.add(index);
                        break;
                    }
                }
            }

            return consumed;
        }

        // Recursive method to find the best combination of words
        public ArrayList<String> findBestCombination() {
            ArrayList<String> bestCombination = new ArrayList<>();
            HashSet<Integer> usedIndices = new HashSet<>();

            findCombinationRecursive(new ArrayList<>(), usedIndices, bestCombination);

            return bestCombination;
        }

        private void findCombinationRecursive(ArrayList<String> currentCombination,
                                              HashSet<Integer> usedIndices,
                                              ArrayList<String> bestCombination) {

            // Update the best combination if current is better (more words, or same words but fewer unused letters)
            if (isBetterCombination(currentCombination, bestCombination)) {
                bestCombination.clear();
                bestCombination.addAll(currentCombination);
            }

            // Try adding each dictionary word to current combination
            for (String word : dictionary) {
                word = word.trim();

                // Skip if word already in current combination
                if (currentCombination.contains(word)) continue;

                // Check if we can form this word with remaining letters
                if (canFormWord(word, usedIndices)) {
                    HashSet<Integer> consumedByThisWord = getConsumedIndices(word, usedIndices);

                    // Add word to combination and mark letters as used
                    currentCombination.add(word);
                    HashSet<Integer> newUsedIndices = new HashSet<>(usedIndices);
                    newUsedIndices.addAll(consumedByThisWord);

                    // Recurse with this word added
                    findCombinationRecursive(currentCombination, newUsedIndices, bestCombination);

                    // Backtrack
                    currentCombination.removeLast();
                }
            }
        }

        private boolean isBetterCombination(ArrayList<String> current, ArrayList<String> best) {
            if (best.isEmpty()) return !current.isEmpty();

            // Prefer more words
            return current.size() > best.size();
        }

        public String getUnusedLetters(HashSet<Integer> usedIndices) {
            StringBuilder unused = new StringBuilder();
            for (int index = 0; index < availableLetters.size(); index++) {
                if (!usedIndices.contains(index)) {
                    unused.append(availableLetters.get(index));
                }
            }
            return unused.toString();
        }

        public HashSet<Integer> getUsedIndicesForCombination(ArrayList<String> combination) {
            HashSet<Integer> allUsed = new HashSet<>();

            for (String word : combination) {
                HashSet<Integer> wordIndices = getConsumedIndices(word, allUsed);
                allUsed.addAll(wordIndices);
            }

            return allUsed;
        }
    }

    public static void main(String[] args) {
        String[] dictionary = """
        lake
        hair
        year
        road
        tale
        food
        map
        ear
        poet
        hall
        sir
        menu
        son
        art
        exam
        city
        ad
        goal
        gene
        way
        math
        dirt
        loss
        debt
        dad
        mall
        love
        fact
        town
        king
        oven
        song
        lady
        area
        mode
        girl
        gate
        bird
        poem
        mom
        news
        meat
        desk
        bath
        wife
        data
        wood
        unit
        idea
        lab
        """.trim().split("\n");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter string of letters: ");
        String letters = scanner.next().trim().toLowerCase();
        scanner.close();

        Scrabble scrabble = new Scrabble(letters, dictionary);
        ArrayList<String> bestCombination = scrabble.findBestCombination();

        if (bestCombination.isEmpty()) {
            System.out.println("No words can be formed from the given letters.");
        } else {
            HashSet<Integer> usedIndices = scrabble.getUsedIndicesForCombination(bestCombination);
            String unusedLetters = scrabble.getUnusedLetters(usedIndices);

            System.out.printf("Matching words are %s unused letters are '%s'.%n", bestCombination, unusedLetters);
        }
    }
}