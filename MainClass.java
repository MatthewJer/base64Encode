import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MainClass {

    private static final String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static List<byte[]> chunkByThree(byte[] inputArray) {
        List<byte[]> byteChunks = new ArrayList<>();
        for (int i = 0; i < inputArray.length; i += 3) {
            int remainingBytes = inputArray.length - i;
            int currentChunkSize = Math.min(remainingBytes, 3);
            byte[] chunk = new byte[currentChunkSize];
            System.arraycopy(inputArray, i, chunk, 0, currentChunkSize);
            byteChunks.add(chunk);
        }
        return byteChunks;
    }

    private static List<char[]> chunkByFour(char[] inputArray) {
        List<char[]> byteChunks = new ArrayList<>();
        for (int i = 0; i < inputArray.length; i += 4) {
            int remainingChars = inputArray.length - i;
            int currentChunkSize = Math.min(remainingChars, 4);
            char[] chunk = new char[currentChunkSize];
            System.arraycopy(inputArray, i, chunk, 0, currentChunkSize);
            byteChunks.add(chunk);
        }
        return byteChunks;
    }

    private static String encode(String input)
    {
        var bytes = input.getBytes();
        var chunkedByThree = chunkByThree (bytes);
        String result = "";
        for (var chunk : chunkedByThree) {
            byte firstByte = chunk.length > 0 ? chunk[0] : 0;
            byte secondByte = chunk.length > 1 ? chunk[1] : 0;
            byte thirdByte = chunk.length > 2 ? chunk[2] : 0;
            char firstChar = base64Chars.charAt((firstByte >> 2));
            char secondChar = base64Chars.charAt(((firstByte & 0b00000011) << 4) + (secondByte >> 4));
            char thirdChar = '=';
            if (chunk.length > 1)
                thirdChar = base64Chars.charAt(((secondByte << 2) & 0b00111111) + (thirdByte >> 6));
            char fourthChar = '=';
            if (chunk.length > 2)
                fourthChar = base64Chars.charAt(thirdByte & 0b00111111);
            result += Character.toString(firstChar) +
                      Character.toString(secondChar) +
                      Character.toString(thirdChar) +
                      Character.toString(fourthChar);
        }
        return result;
    }

    private static String decode(String input)
    {
        var chunkedByFour = chunkByFour(input.toCharArray());
        byte [] bytes = new byte[input.length() / 4 * 3];
        int startIndex = 0;
        boolean lasTwoMissing = false;
        boolean lastOneMissing = false;
        for (var chunk : chunkedByFour) {
            byte firstByte;
            int firstCharIndex = base64Chars.indexOf(chunk[0]);
            firstByte = (byte)(firstCharIndex << 2);
            int secondCharIndex = base64Chars.indexOf(chunk[1]);
            firstByte += (secondCharIndex & 0b00110000) >> 4;
            byte secondByte = (byte)((secondCharIndex & 0b00001111) << 4);
            byte thirdByte = 0;
            if (chunk.length > 2 && chunk[3] != '=') {
                int thirdCharIndex = base64Chars.indexOf(chunk[2]);
                secondByte += (byte)((thirdCharIndex & 0b00111100) >> 2);
                if (chunk.length > 3 && chunk[3] != '=') {
                    thirdByte = (byte)((thirdCharIndex & 0b00000011) << 6);
                    thirdByte += base64Chars.indexOf(chunk[3]);
                } else {
                    lastOneMissing = true;
                }
            } else {
                lasTwoMissing = true;
            }
            bytes[startIndex] = firstByte;
            bytes[startIndex + 1] = secondByte;
            bytes[startIndex + 2] = thirdByte;
            startIndex += 3;
        }
        if (lasTwoMissing || lastOneMissing) {
            var subArray = new byte[bytes.length - (lasTwoMissing ? 2 : 1)];
            System.arraycopy(bytes, 0, subArray, 0, subArray.length);
            return new String(subArray);
        }
        return new String(bytes);
    } 

    public static void main (String [] args)
    {
        if (args.length != 2 || !(args[0].equals("encode") || args[0].equals("decode"))) {
            System.out.println("Usage: <encode/decode> <input>");
            return;
        }
        String result = null;
        if (args[0].equals("encode"))
            result = encode(args[1]);
        else
            result = decode(args[1]);
        System.out.println(result);
    }

}
