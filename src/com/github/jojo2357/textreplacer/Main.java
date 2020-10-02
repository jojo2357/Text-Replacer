package com.github.jojo2357.textreplacer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    /**
     * main method
     *
     * @param args -r (replace string)
     *             -s/-x (string or regex, respectively, to use)
     *             -i (optional, file type (include '.'))
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments are required!");
        }
        StringBuilder argLine = new StringBuilder();
        for (String str : args) {
            argLine.append(str).append(" ");
        }
        if (argLine.toString().contains("-s") == argLine.toString().contains("-x")) {
            throw new IllegalArgumentException("Cant do regex and simple search");
        }
        boolean regex = argLine.toString().contains("-x");
        System.out.println("Is regex: " + regex);
        int i = argLine.toString().contains("-i") ? argLine.indexOf("-i") + 3 : argLine.length();
        int maxOfSAndX = Math.max(argLine.indexOf("-s"), argLine.indexOf("-x"));
        System.out.println(3 + maxOfSAndX + " " + (argLine.length() - i));
        String replace = argLine.substring(argLine.indexOf("-r") + 3, maxOfSAndX - argLine.indexOf("-r") - 1).trim();
        String find = argLine.substring(3 + maxOfSAndX, 3 + maxOfSAndX + argLine.length() - i).trim();
        String include = argLine.toString().contains("-i") ? argLine.substring(argLine.indexOf("-i") + 3).trim() : "";
        int filesInspected = findFilesAndReplace(System.getProperty("user.dir"), find, replace, include, regex);
        System.out.println(filesInspected + " |" + find + "|" + replace + "|" + include + "|");
    }

    /**
     * finds files that match the search term and replaces
     *
     * @param absolutePath path
     * @param find         search term
     * @param replace      str to replace with
     * @param filter       file extension
     * @param regex        is the find a regex
     * @return amount of files inspected
     */
    private static int findFilesAndReplace(String absolutePath, String find, String replace, String filter, boolean regex) {
        File thisFolder = new File(absolutePath);
        String[] directories = thisFolder.list((current, name) -> new File(current, name).isDirectory());
        for (String str : Objects.requireNonNull(directories)) {
            findFilesAndReplace(absolutePath + "/" + str, find, replace, filter, regex);
        }
        System.gc();
        File[] files = Arrays.stream(Objects.requireNonNull(thisFolder.list((current, name) -> !new File(current, name).isDirectory() && (filter.isEmpty() || name.endsWith(filter)))))
                .map(s -> new File(absolutePath + "/" + s))
                .toArray(File[]::new);
        int filesInspected = 0;
        for (File file : Objects.requireNonNull(files)) {
            replaceText(file, find, replace, regex);
            filesInspected++;
        }
        return filesInspected;
    }

    private static void replaceText(File file, String find, String replace, boolean regex) {
        int changesMade = 0;
        Pair<Boolean, String> result = doesFileContain(file, find, replace, regex);
        if (result.getFirst()) {
            String fileText = result.getSecond().replaceAll(find, replace);
            try {
                try (FileWriter outWriter = new FileWriter(file)) {
                    outWriter.append(fileText);
                }
            } catch (IOException exception) {
                System.err.println("IOException during writing to file: " + exception);
            }
            System.out.println(file.getAbsolutePath() + " : " + (++changesMade));
        }
    }

    private static Pair<Boolean, String> doesFileContain(File file, String find, String replace, boolean regex) {
        try {
            StringBuilder fileText = new StringBuilder();
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    fileText.append(reader.nextLine()).append("\n");
                }
            }
            boolean returnVal = regex ? Pattern.compile(find).matcher(fileText.toString()).find() : fileText.toString().contains(find);
            System.out.println(find + " " + replace + " " + returnVal);
            System.out.println(fileText);
            return new Pair<>(returnVal, fileText.toString());
        } catch (FileNotFoundException exception) {
            System.err.println("File " + file.getAbsolutePath() + " does not exist!");
            return new Pair<>(false, "");
        }
    }
}