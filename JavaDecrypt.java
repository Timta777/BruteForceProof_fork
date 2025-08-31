import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JavaDecrypt {

    // Loads the conversion table into a Map<Byte, String> for reverse lookup
    public static HashMap<Byte, String> loadReverseTable(String filename) {
        HashMap<Byte, String> reverseTable = new HashMap<>();
        HashMap<Byte, String> plusTable = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=");
                String key = parts[0].trim();
                String value = parts[1].trim();
                byte byteVal = (byte) Integer.parseInt(value, 16);
                if (key.startsWith("+")) {
                    if (!plusTable.containsKey(byteVal) ||
                        Integer.parseInt(key.substring(1), 16) < Integer.parseInt(plusTable.get(byteVal).substring(1), 16)) {
                        plusTable.put(byteVal, key);
                    }
                } else {
                    if (!plusTable.containsKey(byteVal)) {
                        plusTable.put(byteVal, key);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading conversion table");
        }
        reverseTable.putAll(plusTable);
        return reverseTable;
    }

    // Given randomByte and encryptedByte, finds the original input byte
    public static int recoverOriginalByte(int randomByte, byte encryptedByte, Map<Byte, String> reverseTable) {
        String opVal = reverseTable.get(encryptedByte);
        if (opVal == null || opVal.length() < 3) return -1;
        char op = opVal.charAt(0);
        int value = Integer.parseInt(opVal.substring(1), 16);
        int original;
        if (op == '+') {
            original = (randomByte + value) % 256;
        } else if (op == '-') {
            original = (randomByte - value + 256) % 256;
        } else {
            return -1;
        }
        return original;
    }

    public static void main(String[] args) {
        String randomFile = "random_bytes.bin";
        String changesFile = "changes.bin";
        String tableFile = "conversionTable.txt";
        String outputFile = "reversed_bytes.bin";
        int chunkSize = 1024 * 1024; // 1MB chunks

        Map<Byte, String> reverseTable = loadReverseTable(tableFile);

        try (InputStream randStream = new BufferedInputStream(new FileInputStream(randomFile));
             InputStream changesStream = new BufferedInputStream(new FileInputStream(changesFile));
             OutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            byte[] randBuffer = new byte[chunkSize];
            byte[] changesBuffer = new byte[chunkSize];

            int randRead, changesRead;
            long processed = 0;
            while ((randRead = randStream.read(randBuffer)) > 0 &&
                   (changesRead = changesStream.read(changesBuffer)) > 0) {

                int length = Math.min(randRead, changesRead);
                byte[] outputBytes = new byte[length];

                for (int i = 0; i < length; i++) {
                    int randomByte = randBuffer[i] & 0xFF;
                    byte encryptedByte = changesBuffer[i];
                    int originalByte = recoverOriginalByte(randomByte, encryptedByte, reverseTable);
                    outputBytes[i] = (originalByte == -1) ? (byte) 0xFF : (byte) originalByte;
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
