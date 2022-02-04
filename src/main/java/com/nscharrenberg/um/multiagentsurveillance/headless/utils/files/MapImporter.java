package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MapImporter {
    public static void load(String path) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path); Scanner scanner = new Scanner(fileInputStream, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
    }

    private static void parseLine(String currentLine) {
        // Delimit line by "="
        String[] split = currentLine.split("=");

        // Retrieve ID and Value and remove excess spaces
        String id = split[0].trim();
        String value = split[1].trim();

        // Delimit by " " when multiple parameters are passed in one-line
        String[] items = value.split(" ");

        System.out.println(id);
    }
}
