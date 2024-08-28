package com.app;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;


public class BajajHealth {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar \"D:\\Cdac\\Java\\ProjectRepository\\BajajHealth.jar\" 240343120015 "
            		+ "");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            // Read and parse the JSON file
            JSONObject root = new JSONObject(readFile(jsonFilePath));
            String destinationValue = findDestinationValue(root);

            if (destinationValue == null) {
                System.err.println(" 'destination' not found in JSON file.");
                System.exit(1);
            }

            // Generate random alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate PRN Number, Destination Value, and Random String
            String concatenatedString = prnNumber + destinationValue + randomString;

            // Generate MD5 hash
            String md5Hash = md5Hash(concatenatedString);

            // Output in the required format
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String readFile(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(new FileReader(filePath, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            return sb.toString();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        if (jsonObject.has("destination")) {
            return jsonObject.getString("destination");
        }
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                for (Object obj : (JSONArray) value) {
                    if (obj instanceof JSONObject) {
                        String result = findDestinationValue((JSONObject) obj);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String md5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
