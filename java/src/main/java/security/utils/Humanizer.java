package security.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ben on 23/12/15.
 */
public class Humanizer {
    static final int DICT_SIZE=65536;
    private int dictSize;
    private Vector<String> indexToWord;
    private Map<String, Integer> wordToIndex;
    private int bytesPerWord;

    public Humanizer(String dictpath) throws Exception {
        indexToWord = new Vector<>(DICT_SIZE);
        wordToIndex = new HashMap<>();
        FileInputStream fis = new FileInputStream(new File(dictpath));

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null && dictSize < DICT_SIZE) {
            indexToWord.add(line);
            wordToIndex.put(line, dictSize++);
        }

        if(dictSize < DICT_SIZE) {
            throw new Exception(String.format("Dictionary must have at least %d indexToWord", DICT_SIZE));
        }

        int roundedDictSize = 1;
        while(roundedDictSize <= dictSize) { roundedDictSize*=2; }
        dictSize = roundedDictSize / 2;
        System.out.println(dictSize);
        bytesPerWord = (int) (Math.log(dictSize) / 8);

        br.close();
    }

    public String humanize(byte[] buffer)
    {
        short numericValue = 0;
        String humanString = "";
        for(int i = 0; i < buffer.length; i++)
        {
            if(i % 2 == 0) {
                numericValue += buffer[i];
            }
            else
            {
                numericValue += buffer[i] << 8;
                humanString += (humanString == "" ? "" : " ") + indexToWord.get(numericValue);
                numericValue = 0;
            }
        }
        return humanString;
    }

    public byte[] dehumanize(String humanString)
    {
        String[] splitted = humanString.split(" ");
        byte[] buffer = new byte[splitted.length*2];
        int bufferindex = 0;
        for(String word : splitted)
        {
            Integer index = wordToIndex.get(word);
            buffer[bufferindex++] = (byte) (index.shortValue() & 0xFF);

            //Get the high bits
            buffer[bufferindex++] = (byte) (index.shortValue() >> 8);

        }
        return buffer;
    }

}