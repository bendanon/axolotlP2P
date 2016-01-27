package security.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by ben on 24/12/15.
 */
public class HexHumanizer {
    static final int DICT_SIZE=65536;
    private final HashMap<String, String> hexToWord;
    private final HashMap<String, String> wordToHex;


    /**
     * Generates a dictionary from the file in dictpath.
     * The file should contain at least DICT_SIZE words (the rest are not used)
     * @param dictpath
     * @throws Exception
     */
    public HexHumanizer(String dictpath) throws Exception {
        hexToWord = new HashMap<>();
        wordToHex = new HashMap<>();
        FileInputStream fis = new FileInputStream(new File(dictpath));

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        int dictSize = 0;
        while ((line = br.readLine()) != null && (dictSize++ < DICT_SIZE)) {
            String hexStr = String.format("%02x %02x", ((dictSize >> 8) & 0xFF) , dictSize & 0xFF);
            wordToHex.put(line, hexStr);
            hexToWord.put(hexStr, line);
        }

        if(dictSize < DICT_SIZE) {
            throw new Exception(String.format("Dictionary must have at least %d indexToWord", DICT_SIZE));
        }

        br.close();
    }


    /**
     * Translates every pair of bytes to a word from the dictionary
     * @param hexString
     * @return
     */
    public String humanize(String hexString)
    {
        String humanized = "";
        for(int i = 0; i < hexString.length(); i+=6)
        {
            humanized += hexToWord.get(hexString.substring(i, i+5)) + " ";
        }

        return humanized;
    }

    /**
     * Translates every word in the sentence to two formatted hex bytes
     * @param sentence
     * @return
     */
    public String dehumanize(String sentence)
    {
        String dehumanized = "";
        String[] words = sentence.split(" ");
        for(String word : words)
        {
            dehumanized += wordToHex.get(word) + " ";
        }
        return dehumanized;
    }

    }
