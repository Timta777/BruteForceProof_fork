import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JavaDecrypt {
    // Reads a binary file and returns its bytes
    public static byte[] readHexFile(String filename) {
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("File " + filename + " not found");
            return null;
        }
    }

    // Loads the conversion table into a Map<Byte, String> for reverse lookup
    public static HashMap<Byte, String> loadReverseTable(String filename) {
        HashMap<Byte, String> reverseTable = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=");
                String key = parts[0].trim();
                String value = parts[1].trim();
                byte byteVal = (byte) Integer.parseInt(value, 16);
                // If duplicate values, prefer '+' variant (arbitrary but consistent)
                if (!reverseTable.containsKey(byteVal) || key.startsWith("+")) {
                    reverseTable.put(byteVal, key);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading conversion table");
        }
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
        byte[] randomBytes = readHexFile("random_bytes.bin");
        byte[] changesBytes = readHexFile("changes.bin");
        if (randomBytes == null || changesBytes == null) return;

        Map<Byte, String> reverseTable = loadReverseTable("conversionTable.txt");

        int length = Math.min(randomBytes.length, changesBytes.length);
        byte[] outputBytes = new byte[length];

        for (int i = 0; i < length; i++) {
            int randomByte = randomBytes[i] & 0xFF;
            byte encryptedByte = changesBytes[i];
            int originalByte = recoverOriginalByte(randomByte, encryptedByte, reverseTable);
            if (originalByte == -1) {
                // Fallback for missing table values
                outputBytes[i] = (byte) 0xFF;
            } else {
                outputBytes[i] = (byte) originalByte;
            }
        }

        try (FileOutputStream fos = new FileOutputStream("reversed_bytes.bin")) {
            fos.write(outputBytes);
        } catch (IOException e) {
            System.out.println("Error writing reversed_bytes.bin");
        }
    }
}
