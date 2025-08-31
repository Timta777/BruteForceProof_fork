import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class JavaEncrypt {

    // Reads a binary file into a byte array
    public static byte[] readHexFile(String filename) {
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("File " + filename + " not found");
            return null;
        }
    }

    // Loads the conversion table into a HashMap
    public static HashMap<String, Byte> loadConversionTable(String filename) {
        HashMap<String, Byte> table = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=");
                String key = parts[0].trim();
                String value = parts[1].trim();
                // Parse value as hexadecimal
                byte byteVal = (byte) Integer.parseInt(value, 16);
                table.put(key, byteVal);
            }
        } catch (IOException e) {
            System.out.println("Error reading conversion table");
        }
        return table;
    }

    // Finds the lowest value (0-255) such that (hex1 + value) % 256 == result or (hex1 - value) % 256 == result
    // Returns an array: [operation+value, e.g., "+0A" or "-F1"]
    public static String getOpValue(int hex1, int result) {
        for (int i = 0; i < 256; i++) {
            if ((hex1 + i) % 256 == result) {
                return "+" + String.format("%02X", i);
            }
            if ((hex1 - i + 256) % 256 == result) {
                return "-" + String.format("%02X", i);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        byte[] randomBytes = readHexFile("random_bytes.bin");
        byte[] inputBytes = readHexFile("input_bytes.bin");
        if (randomBytes == null || inputBytes == null) return;

        HashMap<String, Byte> table = loadConversionTable("conversionTable.txt");

        int length = Math.min(randomBytes.length, inputBytes.length);
        byte[] outputBytes = new byte[length];

        for (int i = 0; i < length; i++) {
            int hex1 = randomBytes[i] & 0xFF;
            int result = inputBytes[i] & 0xFF;
            String opVal = getOpValue(hex1, result);
            if (opVal != null && table.containsKey(opVal)) {
                outputBytes[i] = table.get(opVal);
            } else {
                // If no valid operation, default to 0xFF (or choose another fallback)
                outputBytes[i] = (byte) 0xFF;
            }
        }

        try (FileOutputStream fos = new FileOutputStream("changes.bin")) {
            fos.write(outputBytes);
        } catch (IOException e) {
            System.out.println("Error writing changes.bin");
        }
    }
}
