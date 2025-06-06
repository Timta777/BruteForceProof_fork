import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    // Reads a text file and returns its content as a String
    public static String readChangesFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            System.out.println("File " + filename + " not found");
            return null;
        }
    }

    // Performs + or - operation in mod 256 arithmetic
    public static int calculate(String hex1, String hex2, String operation) {
        int num1 = Integer.parseInt(hex1, 16);
        int num2 = Integer.parseInt(hex2, 16);
        int result;
        if (operation.equals("+")) {
            result = (num1 + num2) % 256;
        } else if (operation.equals("-")) {
            result = (num1 - num2 + 256) % 256;
        } else {
            throw new IllegalArgumentException("Unknown operation: " + operation);
        }
        return result;
    }

    // Reverse the changes using the changes string and random bytes
    public static byte[] reverseChanges(String changes, byte[] randomBytes) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int i = 0;
        int j = 0;
        while (i < changes.length()) {
            char op = changes.charAt(i);
            if (op == '+' || op == '-') {
                if (i + 3 > changes.length() || j >= randomBytes.length) {
                    System.out.println("Invalid changes file");
                    return null;
                }
                String hex2 = changes.substring(i + 1, i + 3);
                String hex1 = String.format("%02X", randomBytes[j] & 0xFF);
                int result = calculate(hex1, hex2, String.valueOf(op));
                output.write(result);
                i += 3;
                j += 1;
            } else {
                System.out.println("Invalid changes file");
                return null;
            }
        }
        return output.toByteArray();
    }

    public static void main(String[] args) {
        byte[] randomBytes = readHexFile("random_bytes.bin");
        String changes = readChangesFile("changes.txt");
        if (randomBytes == null || changes == null) {
            return;
        }

        byte[] output = reverseChanges(changes, randomBytes);
        if (output != null) {
            try (FileOutputStream fos = new FileOutputStream("reversed_bytes.bin")) {
                fos.write(output);
            } catch (IOException e) {
                System.out.println("Error writing reversed_bytes.bin");
            }
        }
    }
}