import java.util.*;
import edu.duke.*;
// import java.text.*;

public class VigenereBreaker {
    private void print(String msg) {
        System.out.println(msg);
    }
    
    public HashSet<String> readDictionary(FileResource fr) {
        HashSet<String> hs = new HashSet<String>();
        
        for( String line : fr.lines()) {
            hs.add(line.toLowerCase());
        }
        
        return hs;
    }
    
    public HashSet<String> readDictionary(String lang, FileResource fr) {
        HashSet<String> hs = new HashSet<String>();
        print("Reading the " + lang + " dictionary...");
        for( String line : fr.lines()) {
            hs.add(line.toLowerCase());
        }
        
        System.out.print('\u000c');
        return hs;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary) {
        char mostCommon = 'a';
        int charCount = 0;
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        for( String s : dictionary ) {
            for( char c : s.toCharArray() ) {
                if( charMap.containsKey(c) ) {
                    charMap.put(c, charMap.get(c) + 1);
                } else {
                    charMap.put(c, 1);
                }
            }
        }
        
        for( char c : charMap.keySet() ) {
            int value = charMap.get(c);
            
            if (value > charCount) {
                charCount = value;
                mostCommon = c;
            }
        }
        
        // System.out.println(mostCommon + " occurs most often, a total of " + NumberFormat.getInstance().format(charCount) + " times.");
        return mostCommon;
    }
    
    public int countWords(String message, HashSet<String> dictionary) {
        String[] words = message.toLowerCase().split("\\W+");
        int count = 0;
        
        for( String word : words ) {
            if (dictionary.contains(word)) {
                count++;
            }
        }
        
        return count;
    }
    
    public String breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages) {
        // Try breaking the encryption for each language, and see which gives the best results!
        // Remember that you can iterate over the languages.keySet() to get the name of each language,
        // and then you can use .get() to look up the corresponding dictionary for that language. You
        // will want to use the breakForLanguage and countWords methods that you already wrote to do
        // most of the work (it is slightly inefficient to re-count the words here, but it is simpler,
        // and the inefficiency is not significant). You will want to print out the decrypted message
        // as well as the language that you identified for the message.
        int wordCountMax = 0;
        String langWithMostWords = "Unknown";
        
        for (String s : languages.keySet()) {
            String decrypted = breakForLanguage(s, encrypted, languages.get(s));
            int wordCount = countWords(decrypted, languages.get(s));
            
            if (wordCount > wordCountMax) {
                wordCountMax = wordCount;
                langWithMostWords = s;
            }
        }
        
        print("wordCountMax: " + wordCountMax + "\nlangWithMostWords: " + langWithMostWords);
        return breakForLanguage(langWithMostWords, encrypted, languages.get(langWithMostWords));
    }
    
    public String breakForLanguage(String lang, String encrypted, HashSet<String> dictionary) {
        int bestCount = 0;
        int bestKey = 0;
        VigenereCipher vc;
        String decrypted = "";
        
        for (int i = 1; i <= 100; i++) {
            System.out.println("Processing " + lang + " Dictionary: " + i + "%");
            vc = new VigenereCipher(tryKeyLength(encrypted, i, mostCommonCharIn(dictionary)));
            decrypted = vc.decrypt(encrypted);
            int newCount = countWords(decrypted, dictionary);
            if (newCount > bestCount) {
                bestCount = newCount;
                bestKey = i;
            }
            System.out.print('\u000C');
        }
        
        vc = new VigenereCipher(tryKeyLength(encrypted, bestKey, mostCommonCharIn(dictionary)));
        print("The bestCount is " + bestCount + "\nThe keyLength is " + bestKey + "\nThe bestKey is " + Arrays.toString(tryKeyLength(encrypted, bestKey, 'e')));
        
        print("TOTAL WORDS: " + decrypted.split("\\W+").length);
        print("DETECTED LANGUAGE: " + lang);
        return vc.decrypt(encrypted);
    }
    
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //REPLACE WITH YOUR CODE
        StringBuilder sb = new StringBuilder();
        String[] msg = message.split("");
        
        for (int i = whichSlice; i < msg.length; i += totalSlices) {
            sb.append(msg[i]);
        }
        
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        //WRITE YOUR CODE HERE
        CaesarCracker cc = new CaesarCracker();
        
        for( int i = 0; i < klength; i++) {
            key[i] = cc.getKey(sliceString(encrypted, i, klength));
        }
        return key;
    }

    public void breakVigenere () {
        //WRITE YOUR CODE HERE
        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        map.put("Danish", readDictionary("Danish", new FileResource("dictionaries/Danish")));
        map.put("Dutch", readDictionary("Dutch", new FileResource("dictionaries/Dutch")));
        map.put("English", readDictionary("English", new FileResource("dictionaries/English")));
        map.put("French", readDictionary("French", new FileResource("dictionaries/French")));
        map.put("German", readDictionary("German", new FileResource("dictionaries/German")));
        map.put("Italian", readDictionary("Italian", new FileResource("dictionaries/Italian")));
        map.put("Portuguese", readDictionary("Portuguese", new FileResource("dictionaries/Portuguese")));
        map.put("Spanish", readDictionary("Spanish", new FileResource("dictionaries/Spanish")));
        
        String decrypted = breakForAllLangs(encrypted, map);
        print("--------------------\n");
        System.out.println(decrypted);
        
    }
    
    
    // TESTING METHODS
    public void testSliceString() {
        print(sliceString("abcdefghijklm", 0, 3)); // should return "adgjm"
        print(sliceString("abcdefghijklm", 1, 3)); // should return "behk"
        print(sliceString("abcdefghijklm", 2, 3)); // should return "cfil"
        
        print(sliceString("abcdefghijklm", 0, 4)); // should return "aeim"
        print(sliceString("abcdefghijklm", 1, 4)); // should return "bfj"
        print(sliceString("abcdefghijklm", 2, 4)); // should return "cgk"
        print(sliceString("abcdefghijklm", 3, 4)); // should return "dhl"
        
        print(sliceString("abcdefghijklm", 0, 5)); // should return "afk"
        print(sliceString("abcdefghijklm", 1, 5)); // should return "bgl"
        print(sliceString("abcdefghijklm", 2, 5)); // should return "chm"
        print(sliceString("abcdefghijklm", 3, 5)); // should return "di"
        print(sliceString("abcdefghijklm", 4, 5)); // should return "ej"
    }
    
    public void testTryKeyLength() {
        FileResource fr = new FileResource();
        
        print(Arrays.toString(tryKeyLength(fr.asString(), 4, 'e')));
    }
    
    public void testBreakForLanguage(){
        FileResource fr = new FileResource();
        FileResource dr = new FileResource();
        
        breakForLanguage("English", fr.asString(), readDictionary(dr));
    }
    
    public void testMostCommonCharIn() {
        mostCommonCharIn(readDictionary(new FileResource()));
    }
    
    public void testBreakForAllLangs() {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        map.put("Danish", readDictionary(new FileResource("dictionaries/Danish")));
        map.put("Dutch", readDictionary(new FileResource("dictionaries/Dutch")));
        map.put("English", readDictionary(new FileResource("dictionaries/English")));
        map.put("French", readDictionary(new FileResource("dictionaries/French")));
        map.put("German", readDictionary(new FileResource("dictionaries/German")));
        map.put("Italian", readDictionary(new FileResource("dictionaries/Italian")));
        map.put("Portuguese", readDictionary(new FileResource("dictionaries/Portuguese")));
        map.put("Spanish", readDictionary(new FileResource("dictionaries/Spanish")));
        
        breakForAllLangs(new FileResource("testing/athens_keyflute.txt").asString(), map);
    }
}