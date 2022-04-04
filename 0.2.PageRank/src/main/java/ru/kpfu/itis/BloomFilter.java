package ru.kpfu.itis;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;

public class BloomFilter {
    public boolean[] bits;
    private int m;
    private int k;

    public int optimalCBFLength(int count, float precision) {
        return -(int)Math.round((count*Math.log(precision))/Math.pow(Math.log(2), 2));
    }

    public int countHashFunctions(int length, int count) {
        return (int)Math.round((length/count)*Math.log(2));
    }

    public List<byte[]> getHashedStrings(int hashesCount, String word)  {
        List<byte[]> salts = new ArrayList<>();
        for(int i = 0; i < hashesCount; i ++) {
            double randomNumber = Math.random() * 50 + 10;
            byte[] salt = new byte[(int)randomNumber];
            salts.add(salt);
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        List<byte[]> hashedStrings = new ArrayList<>();
        for (int i = 0; i < hashesCount; i++) {
            md.update(salts.get(i));
            byte[] hashedString = md.digest(word.getBytes(StandardCharsets.UTF_8));
            md.reset();
            hashedStrings.add(hashedString);
        }
        return hashedStrings;
    }

    public int hashIndex(byte[] hashedString, int length) {
        Adler32 ad = new Adler32();
        ad.update(hashedString);
        int value = (int)ad.getValue() % length;
        if (value < 0) {
            value = -value;
        }
        return value;
    }

    public boolean contains(String word) {
        List<byte[]> hashedStrings = getHashedStrings(10, word);
        boolean result = true;

        for(int  i = 0; i < hashedStrings.size(); i++) {
            int value = hashIndex(hashedStrings.get(i), bits.length);
            if (!bits[value]) {
                result = false;
                break;
            }
        }
        return result;
    }

    public void add(String word) {
        List<byte[]> hashedStrings = getHashedStrings(k,  word);
        for(int  i = 0; i < hashedStrings.size(); i++) {
            int value = hashIndex(hashedStrings.get(i), bits.length);
            bits[value] = true;
        }
    }

    public void initBits(int size) {
        bits = new boolean[size];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = false;
        }
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setK(int k) {
        this.k = k;
    }
}
