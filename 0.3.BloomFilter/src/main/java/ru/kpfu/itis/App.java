package ru.kpfu.itis;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private final static String[] punctuationMarks = new String[]{";", ",", ":", ".", "!", "?", "–", "\"", "—"};
    private static Set<String> stringSet;

    public static void main(String[] args) {

        readFile();

        BloomFilter filter = new BloomFilter();
        int m = filter.optimalCBFLength(stringSet.size(), 0.1f);
        int k = filter.countHashFunctions(m, stringSet.size());

        System.out.println("m = " + m);
        System.out.println("k = " + k);

        filter.initBits(m);
        filter.setK(k);
        filter.setM(m);

        for (String word : stringSet) {
            filter.add(word);
        }

        System.out.println(Arrays.toString(filter.getBits()));
    }

    public static void readFile() {
        try (BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream("src/newData.txt")))) {
            String str = new String();

            String line = new String();
            while ((line = fin.readLine()) != null) {
                str += line;
            }

            String[] arrayString = str.split(" ");

            for (int i = 0; i < arrayString.length; i++) {
                arrayString[i] = arrayString[i].toLowerCase();
                for (int j = 0; j < punctuationMarks.length; j++) {
                    if (arrayString[i].contains(punctuationMarks[j])) {
                        arrayString[i] = arrayString[i].substring(0, arrayString[i].indexOf(punctuationMarks[j]));
                    }
                }
            }
            stringSet = new HashSet<>(Arrays.stream(arrayString).collect(Collectors.toSet()));
            if (stringSet.contains("")) {
                stringSet.remove("");
            }

        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
