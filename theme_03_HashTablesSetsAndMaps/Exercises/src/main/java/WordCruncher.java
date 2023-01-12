import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class WordCruncher {
    private static Map<String, Integer> wordsByCounts = new HashMap<>();
    private static Map<Integer, TreeSet<String>> tree = new TreeMap<>();
    private static String target;
    private static List<String> buffer = new ArrayList<>();
    private static List<String> out = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        List<String> inputStrings = Arrays.stream(reader.readLine()
                        .split(", "))
                .collect(Collectors.toList());

        target = reader.readLine();

        inputStrings = inputStrings.stream()
                .filter(str -> target.contains(str))
                .collect(Collectors.toList());


        for (String string : inputStrings) {
            wordsByCounts.putIfAbsent(string, 0);
            wordsByCounts.put(string, wordsByCounts.get(string) + 1);

            int index = target.indexOf(string);

            while (index != -1) {
                if (!tree.containsKey(index)) {
                    tree.put(index, new TreeSet<>());
                }

                tree.get(index).add(string);

                index = target.indexOf(string, index + 1);
            }

        }

        dfsTraversal(0);

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < out.size(); i++) {
            builder.append(out.get(i))
                    .append(System.lineSeparator());
        }

        System.out.println(builder.toString().trim());
    }

    private static void dfsTraversal(int index) {
        if (index == target.length()) {
            printResult();
        } else {
            if (!tree.containsKey(index)) {
                return;
            }
            for (String str : tree.get(index)) {
                if (wordsByCounts.get(str) > 0) {
                    buffer.add(str);
                    wordsByCounts.put(str, wordsByCounts.get(str) - 1);
                    dfsTraversal(index + str.length());

                    wordsByCounts.put(str, wordsByCounts.get(str) + 1);
                    buffer.remove(buffer.size() - 1);
                }
            }
        }
    }

    private static void printResult() {
        String result = String.join("", buffer);
        if (result.equals(target)) {
            out.add(String.join(" ", buffer));
        }
    }


}
