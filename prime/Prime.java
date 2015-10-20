import java.math.BigInteger;
import java.util.Scanner;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Prime {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            String arg = "";
            if (args.length > 0) {
                arg = args[0];
            }
            switch (arg) {
                case "exit":
                    System.exit(0);
                case "sieve":
                    // Sieve of Eratosthenes
                    int N = Integer.parseInt(args[1]);
                    boolean[] comp = new boolean[N + 1];
                    int count = 0;
                    for (int i = 2; i < N + 1; i++) {
                        if (!comp[i]) {
                            System.out.println(" " + i);
                            count++;
                            for (int j = i + 1; j < N + 1; j++) {
                                if (!comp[j] && j % i == 0) {
                                    comp[j] = true;
                                }
                            }
                        }
                    }
                    System.out.println(" count: " + count);
                    break;
                case "factor":
                    // Prime factorization
                    long N2 = Long.parseLong(args[1]);
                    String output = N2 + " = ";
                    long root = (long) Math.sqrt(N2);
                    for (long i = 2; i < root; i++) {
                        if (N2 % i == 0) {
                            N2 = N2 / i;
                            output = output + i + " ";
                            i--;
                        }
                        if (N2 == 1) {
                            break;
                        }
                    }
                    if (N2 > 1) {
                        output = output + N2;
                    }
                    System.out.println(" " + output);
                    break;
                case "radix":
                    // radix conversion
                    System.out.println(" "
                            + new BigInteger(args[2], args.length == 4 ? Integer.parseInt(args[3]) : 10)
                            .toString(Integer.parseInt(args[1]))
                            .toUpperCase());
                    break;
                case "hmac":
                    byte[] key = base32Decode(args[1]);
                    long message = System.currentTimeMillis() / 30000L;
                    byte[] data = new byte[8];
                    for (int i = 8; i-- > 0; message >>>= 8) {
                        data[i] = (byte) message;
                    }
                    byte[] hash = null;
                    try {
                        Mac mac = Mac.getInstance("HmacSHA1");
                        mac.init(new SecretKeySpec(key, "HmacSHA1"));
                        hash = mac.doFinal(data);
                    } catch (Exception e) {
                    }
                    int offset = hash[19] & 0xF;
                    long trunc = 0;
                    for (int i = 0; i < 4; i++) {
                        trunc <<= 8;
                        trunc |= (hash[offset + i] & 0xFF);
                    }
                    trunc &= 0x7FFFFFFF;
                    trunc %= 1000000;
                    System.out.println(" " + trunc);
                    break;
                case "sudoku":
                    new Sudoku(args[1], args.length > 2);
                    break;
                case "qr":
                    String qrText = String.join(" ", args).substring(3);
                    int length = qrText.length();
                    if (length > 47) {
                        System.out.println(" 47 characters max.");
                        break;
                    }
                    String lengthString = Integer.toString(length, 2);
                    String qrData = "0010" + "000000000".substring(lengthString.length()) + lengthString;
                    for (int i = 0; i < length / 2 * 2; i += 2) {
                        String str = Integer.toString(qrLookup(qrText.charAt(i)) * 45 + qrLookup(qrText.charAt(i + 1)), 2);
                        qrData += "00000000000".substring(str.length()) + str;
                    }
                    if (length % 2 == 1) {
                        String str = Integer.toString(qrLookup(qrText.charAt(length - 1)), 2);
                        qrData += "000000".substring(str.length()) + str;
                    }
                    qrData += new String(new char[272 - qrData.length()]).replace("\0", "0");

                    break;
                case "tetro":
                    if (args.length == 4) {
                        new Tetromino(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]).start();
                    } else {
                        System.out.println(" Input error.");
                    }
                    break;
                case "test":
                    System.out.println("" + '\u25AF' + '\u25AE' + '\u25AF' + '\u25AE' + '\u25AF');
                    break;
                default:
                    System.out.println("\n"
                            + " Usage:\n"
                            + "\n"
                            + " exit\n"
                            + " sieve  [N]\n"
                            + " factor [N]\n"
                            + " radix  [to] [N] ([from])\n"
                            + " hmac  [secret]\n"
                            + " sudoku [grid] ([show progress])\n"
                            + " tetro  [rows] [cols] [tetrominoes: IOTJLSZ]");
            }
            System.out.print("\n ");
            args = sc.nextLine().split(" ");
        }
    }

    static int qrLookup(char c) {
        int val = Character.getNumericValue(c);
        if (val == -1) {
            switch (c) {
                case ' ':
                    val = 36;
                    break;
                case '$':
                    val = 37;
                    break;
                case '%':
                    val = 38;
                    break;
                case '*':
                    val = 39;
                    break;
                case '+':
                    val = 40;
                    break;
                case '-':
                    val = 41;
                    break;
                case '.':
                    val = 42;
                    break;
                case '/':
                    val = 43;
                    break;
                case ':':
                    val = 44;
                    break;
                default:
                    val = 36;
            }
        }
        return val;
    }

    static byte[] base32Decode(String input) {
        String value = "";
        for (int i = 0; i < input.length(); i++) {
            int digit = Character.getNumericValue(input.charAt(i)) - 10;
            if (digit < 0) {
                digit += 34;
            }
            String unpadded = Integer.toString(digit, 2);
            value += "00000".substring(unpadded.length()) + unpadded;
        }
        value = value.length() % 8 == 0 ? value : value.substring(0, value.length() / 8 * 8);
        byte[] output = new byte[value.length() / 8];
        for (int i = 0; i < value.length(); i++) {
            output[i / 8] |= (value.charAt(i) == '1' ? 1 : 0) << (7 - (i % 8));
        }
        return output;
    }
}
