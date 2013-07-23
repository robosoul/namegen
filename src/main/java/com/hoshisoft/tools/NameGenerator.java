package com.hoshisoft.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameGenerator {   
    public static final int VOCAL     = 1;
    public static final int CONSONANT = 2;
    
    private List<Syllable> prefixes = new ArrayList<Syllable>();
    private List<Syllable> middles  = new ArrayList<Syllable>();
    private List<Syllable> suffixes = new ArrayList<Syllable>();
    
    private String fileName;
    
    public NameGenerator(String fileName) throws IOException{
        this.fileName = fileName;
        refresh();
    }
    
    public void refresh() throws IOException{        
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        
        Syllable syllable = null;
        String line = null;                 
        while((line = in.readLine()) != null){                          
            line = line.trim();
            
            if (line.length() <= 0){
                continue;
            }
            
            syllable = new Syllable(line);
            
            switch (syllable.type) {
                case PREFIX: prefixes.add(syllable);                   
                    break;
                case MIDDLE: middles.add(syllable);                   
                    break;
                case SUFFIX: suffixes.add(syllable);                   
                    break;
                default:
            }
        }  
        
        in.close();
    }
    
    public String compose(int numberOfSyllables){
        if (prefixes.size() == 0) {
            return null;
        }
        
        if (suffixes.size() == 0) {
            return null;
        }
        
        if (numberOfSyllables < 1) { 
            return null;
        }
        
        if(numberOfSyllables > 2 && middles.size() == 0) {
            return null;
        }
        
        int expecting = 0; // 1 for vocal, 2 for consonant
        int last = 0;      // 1 for vocal, 2 for consonant
        
        int a = (int)(Math.random() * prefixes.size());
        
        Syllable prefix = prefixes.get(a);
        if (prefix.endsWithVocal()) {
            last = VOCAL;
        } else { 
            last = CONSONANT;
        }
            
        if (numberOfSyllables > 2) {
            if (prefix.expectsVocal()) { 
                expecting = VOCAL;              
                
                if (!containsSyllableStartingWithVocal(middles)) {
                    return null;
                }
            }
            
            if (prefix.expectsConsonant()) { 
                expecting = CONSONANT;              
                
                if (!containsSyllableStartingWithConsonant(middles)) {
                    return null;
                }
            }
        } else {
            if (prefix.expectsVocal()) { 
                expecting = VOCAL;              
                
                if(!containsSyllableStartingWithVocal(suffixes)) {
                    return null;
                }
            }
            
            if (prefix.expectsConsonant()) { 
                expecting = CONSONANT;  
                
                if(!containsSyllableStartingWithConsonant(suffixes)) {
                    return null;
                }
            }
        }
        
        if (prefix.endsWithVocal() && !allowsVocals(middles)) {
            return null;
        }
        
        if (prefix.endsWithConsonant() && !allowsConsonants(middles)) {
            return null;
        }
        
        int b;
        Syllable[] middle = new Syllable[numberOfSyllables];         
        for (int i = 0; i < middle.length - 2; i++) {                        
            do {
                b = (int)(Math.random() * middles.size());
                middle[i] = middles.get(b);
            } while (expecting == VOCAL && !middle[i].startsWithVocal()
                  || expecting == CONSONANT && !middle[i].startsWithConsonant()
                  || last == VOCAL && middle[i].hatesPreviousVocal() 
                  || last == CONSONANT && middle[i].hatesPreviousConsonant());
            
            expecting = 0;
            if (middle[i].expectsVocal()) { 
                expecting = VOCAL;              
                
                if (i < middle.length - 3 
                        && !containsSyllableStartingWithVocal(middles)) {
                    return null;
                }
                
                if (i == middle.length - 3 
                        && !containsSyllableStartingWithVocal(suffixes)) {
                    return null;
                }
            }
            
            if (middle[i].expectsConsonant()) { 
                expecting = CONSONANT;              
                
                if (i < (middle.length - 3) 
                        && !containsSyllableStartingWithConsonant(middles)) {
                    return null;
                }
                
                if (i == (middle.length - 3) 
                        && !containsSyllableStartingWithConsonant(suffixes)) {
                    return null;
                }
            }
            
            if (middle[i].endsWithVocal() 
                    && !allowsVocals(middles) 
                    && numberOfSyllables > 3) {
                return null;
            }
                    
            if(middle[i].endsWithConsonant() 
                    && !allowsConsonants(middles) 
                    && numberOfSyllables > 3) {
                return null;
            }
            
            if (i == (middle.length - 3)) {                
                if (middle[i].endsWithVocal() 
                        && !allowsVocals(suffixes)) {
                    return null;
                }
                        
                if (middle[i].endsWithConsonant() 
                        && allowsConsonants(suffixes)) {
                    return null;
                            
                }
            }
            
            if (middle[i].endsWithVocal()) {
                last = VOCAL;
            } else {
                last = CONSONANT;
            }
        }       
        
        int c;
        Syllable suffix = null;
        do{
            c = (int)(Math.random() * suffixes.size());
            suffix = suffixes.get(c);
        } while(expecting == VOCAL && !suffix.startsWithVocal()
                || expecting == CONSONANT && !suffix.startsWithConsonant()
                || last == VOCAL && suffix.hatesPreviousVocal() 
                || last == CONSONANT && suffix.hatesPreviousConsonant());
        
        /*
         * Starting with prefix
         */
        String name = prefix.toString();        
        
        /*
         * Adding middles
         */
        for(int i = 0; i < middle.length-2; i++){
            name = name.concat(middle[i].toString());           
        }
        
        /*
         * Adding suffix.
         */
        if(numberOfSyllables > 1) {
            name = name.concat(suffix.toString());
        }
        
        return name;        
    }
    
    private static boolean containsSyllableStartingWithConsonant(
            List<Syllable> syllables){
        
        for (Syllable s: syllables) {
            if (s.startsWithConsonant()) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean containsSyllableStartingWithVocal(
            List<Syllable> syllables) {
        
        for (Syllable s: syllables) {
            if (s.startsWithVocal()) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean allowsConsonants(List<Syllable> syllables){
        for (Syllable s: syllables) {
            if(s.hatesPreviousVocal() || !s.hatesPreviousConsonant()) return true;
        }
        
        return false;
    }
    
    private static boolean allowsVocals(List<Syllable> syllables){
        for (Syllable s: syllables) {
            if(!s.hatesPreviousVocal() || s.hatesPreviousConsonant()) return true;
        }
        
        return false;
    }
    
    public static final class Syllable {
        public static enum SyllableType {
            PREFIX, MIDDLE, SUFFIX
        };
        
        public static final ArrayList<Character> VOCALS = new ArrayList<Character>();
        
        static {
            VOCALS.add('a');
            VOCALS.add('i');
            VOCALS.add('o');
            VOCALS.add('u');
            VOCALS.add('e');            
        }
        
        public static final ArrayList<Character> CONSONANTS = new ArrayList<Character>();
        
        static {
            CONSONANTS.add('b');
            CONSONANTS.add('c');
            CONSONANTS.add('d');
            CONSONANTS.add('f');
            CONSONANTS.add('g');
            CONSONANTS.add('h');
            CONSONANTS.add('j');
            CONSONANTS.add('k');
            CONSONANTS.add('l');
            CONSONANTS.add('m');
            CONSONANTS.add('n');
            CONSONANTS.add('p');
            CONSONANTS.add('q');
            CONSONANTS.add('r');
            CONSONANTS.add('s');
            CONSONANTS.add('t');
            CONSONANTS.add('v');
            CONSONANTS.add('w');
            CONSONANTS.add('x');
            CONSONANTS.add('y');
        }
        
        public static final String PREFIX_SIGN = "-";
        public static final String SUFFIX_SIGN = "+";
        
        private String value;
        private SyllableType type;
        
        private boolean expectsVocal     = false;
        private boolean expectsConsonant = false;
        
        private boolean hatesPreviousVocal     = false;
        private boolean hatesPreviousConsonant = false; 
        
        public Syllable(String input) {
            if (input != null) {
                String[] splitted = input.trim().split(" ");
                
                String tmp;
                if (splitted.length > 0) {
                    tmp = splitted[0];
                    
                    if (tmp.startsWith(PREFIX_SIGN)) {
                        value = tmp.substring(1);
                        type = SyllableType.PREFIX;
                    } else if (tmp.startsWith(SUFFIX_SIGN)) {
                        value = tmp.substring(1);
                        type = SyllableType.SUFFIX;
                    } else {
                        value = tmp;
                        type = SyllableType.MIDDLE;
                    }
                    
                    if (value != null) {
                        value = value.toLowerCase();
                    }
                    
                    for (int i = 1; i < splitted.length; i++) {
                        tmp = splitted[i];
                        
                        if (tmp.equals("+v")) {
                            expectsVocal = true;
                        } else if (tmp.equals("+c")) {
                            expectsConsonant = true;
                        } else if (tmp.equals("-v")) {
                            hatesPreviousVocal = true;
                        } else if (tmp.equals("-c")) {
                            hatesPreviousConsonant = true;
                        }
                    }
                }
            }
        }
        
        public boolean startsWithVocal() {
            return VOCALS.contains(this.getFirstChar());        
        }
        
        public boolean startsWithConsonant() {
            return CONSONANTS.contains(this.getFirstChar());         
        }
        
        public boolean endsWithVocal() {
            return VOCALS.contains(this.getLastChar());
        }
        
        public boolean endsWithConsonant() {
            return CONSONANTS.contains(this.getLastChar());
        }
        
        public char getLastChar() {
            return value.charAt(value.length() - 1);
        }
        
        public char getFirstChar() {
            return value.charAt(0);
        }

        public String getValue() {
            return value;
        }
       
        public SyllableType getType() {
            return type;
        }
        
        public boolean expectsVocal() {
            return expectsVocal;
        }
        
        public boolean expectsConsonant() {
            return expectsConsonant;
        }

        public boolean hatesPreviousVocal() {
            return hatesPreviousVocal;
        }
        
        public boolean hatesPreviousConsonant() {
            return hatesPreviousConsonant;
        } 
        
        public String toString() {
            return value;
        }
    }
}