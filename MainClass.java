import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MainClass {

    public static List<byte[]> chunkByThree(byte[] inputArray) {
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

    public static void main (String [] args)
    {
        Console console = System.console(); 
        String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        String input = console.readLine();
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
        console.println(result);
    }

}
