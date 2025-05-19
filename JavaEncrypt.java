import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaEncrypt {
    public static byte[] readHexFile(String filename) {
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("File " + filename + " not found");
            return null;
        }
    }

    // Finds the lowest value (0-255) such that (hex1 + value) % 256 == result or (hex1 - value) % 256 == result
    public static String[] findSecondValue(int hex1, int result) {
        int lowestValue = -1;
        char operation = ' ';
        for (int i = 0; i < 256; i++) {
            if ((hex1 + i) % 256 == result) {
                lowestValue = i;
                operation = '+';
                break;
            }
            if ((hex1 - i + 256) % 256 == result) {
                lowestValue = i;
                operation = '-';
                break;
            }
        }
        if (lowestValue != -1) {
            return new String[] {String.format("%02X", lowestValue), String.valueOf(operation)};
        } else {
            return new String[] {null, null};
        }
    }

    public static void main(String[] args) {
        byte[] randomBytes = readHexFile("random_bytes.bin");
        byte[] inputBytes = readHexFile("input_bytes.bin");
        if (randomBytes == null || inputBytes == null) return;

        StringBuilder output = new StringBuilder();
        int length = Math.min(randomBytes.length, inputBytes.length);
        for (int i = 0; i < length; i++) {
            int hex1 = randomBytes[i] & 0xFF;
            int result = inputBytes[i] & 0xFF;
            String[] valueOp = findSecondValue(hex1, result);
            String lowestValue = valueOp[0];
            String operation = valueOp[1];
            if (lowestValue != null) {
                output.append(operation).append(lowestValue);
            } else {
                output.append("-FF");
            }
        }
        try (FileWriter fw = new FileWriter("changes.txt")) {
            fw.write(output.toString());
        } catch (IOException e) {
            System.out.println("Error writing changes.txt");
        }
    }
}