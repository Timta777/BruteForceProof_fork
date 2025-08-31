import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JavaEncrypt {

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
                byte byteVal = (byte) Integer.parseInt(value, 16);
                table.put(key, byteVal);
            }
        } catch (IOException e) {
            System.out.println("Error reading conversion table");
        }
        return table;
    }

    // Finds the lowest value (0-255) such that (hex1 + value) % 256 == result or (hex1 - value) % 256 == result
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
        String randomFile = "random_bytes.bin";
        String inputFile = "input_bytes.bin";
        String tableFile = "conversionTable.txt";
        String outputFile = "changes.bin";
        int chunkSize = 1024 * 1024; // 1MB chunks

        HashMap<String, Byte> table = loadConversionTable(tableFile);

        try (InputStream randStream = new BufferedInputStream(new FileInputStream(randomFile));
             InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
             OutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            byte[] randBuffer = new byte[chunkSize];
            byte[] inputBuffer = new byte[chunkSize];

            int randRead, inputRead;
            long processed = 0;
            while ((randRead = randStream.read(randBuffer)) > 0 &&
                   (inputRead = inputStream.read(inputBuffer)) > 0) {

                int length = Math.min(randRead, inputRead);
                byte[] outputBytes = new byte[length];

                for (int i = 0; i < length; i++) {
                    int hex1 = randBuffer[i] & 0xFF;
                    int result = inputBuffer[i] & 0xFF;
                    String opVal = getOpValue(hex1, result);
                    if (opVal != null && table.containsKey(opVal)) {
                        outputBytes[i] = table.get(opVal);
                    } else {
                        outputBytes[i] = (byte) 0xFF;
                    }
                }
                outStream.write(outputBytes, 0, length);
                processed += length;

                System.out.printf("\rProcessed %d bytes...", processed);
            }
            System.out.println("\nDone!");

        } catch (IOException e) {
            System.out.println("Error processing files: " + e.getMessage());
        }
    }
}
