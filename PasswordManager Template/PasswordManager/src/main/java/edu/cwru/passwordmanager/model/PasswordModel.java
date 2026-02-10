package edu.cwru.passwordmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class PasswordModel {
    private ObservableList<Password> passwords = FXCollections.observableArrayList();

    // !!! DO NOT CHANGE - VERY IMPORTANT FOR GRADING !!!
    static private File passwordFile = new File("passwords.txt");

    static private String separator = "\t";

    static private String passwordFilePassword = "";
    static private byte [] passwordFileKey;
    static private byte [] passwordFileSalt;
    static private byte [] savedKey;
    // static private Cipher cipher;
    // static {
    
    //     try {
    //         cipher = Cipher.getInstance("AES");
    //     }
    //     catch(NoSuchPaddingException nspe) { //break into seperate methods - probably wrong password //wont getLabel();
    // try{} cookies back, will getLabel();
    // /
    // bufferedWriter()
    // label + "\t" + encryptedString/ try{} this error -> retyurns boolean 
    // /
    // bufferedWriter()
    // label + "\t" + encryptedString/         //encrypt and decrypt methods can both create cipher
    //         //be able to text that it does what we want this to do. 
    //         System.out.println("NoSuchPaddingException");
    //     }
    //     catch(NoSuchAlgorithmException nsae) {
    //         System.out.println("NoSuchAlgorithmException");
    //     }
    // }
    // static private KeySpec spec = new PBEKeySpec(keyString.toCharArray(), salt, 600000, 256);

    // COMPLETED: You can set this to whatever you like to verify that the password the user entered is correct
    private static String verifyString = "Mattia";
    private static String encodedVerifyString;

    private void loadPasswords() {
        // // TODO: Replace with loading passwords from file, you will want to add them to the passwords list defined above
        // try{
        //     BufferedReader b = new BufferedReader(new FileReader(passwordFile));
        //     String line;
        //     while ((line = b.readLine()) != null) {
        //         // line by line actions
        //         String[] parts = line.split(separator);
        //         String password = parts[1];
        //         // decrypt function to be made later
        //     }
        //     b.close();
        // }
        // catch(IOException e){
        //     System.out.println("IOException, error ocurred");
        // }
        // // TODO: Tips: Use buffered reader, make sure you split on separator, make sure you decrypt password



        // Clear current in-memory list
        passwords.clear();

        try {
            BufferedReader b = new BufferedReader(new FileReader(passwordFile));
            String line;
            int lineNumber = 0;

            while ((line = b.readLine()) != null) {
                // Skip first line (salt + encrypted verify string)
                if (lineNumber == 0) {
                    lineNumber++;
                    continue;
                }

                // Split line by tab
                String[] parts = line.split(separator);  // separator = "\t"

                //THIS LINE IS WRONG = LINES ARE THREE LONG SOMETIMES
                if (parts.length != 2) continue;         // skip malformed lines

                String label = parts[0];
                String encrypted = parts[1];

                try {
                    String decrypted = decryptString(encrypted, savedKey);
                    Password p = new Password(label, decrypted);
                    passwords.add(p);
                } catch (Exception e) {
                    System.out.println("Decrypt failed on line " + lineNumber);
                }

                lineNumber++;
            }

            b.close();
        } catch (IOException e) {
            System.out.println("IOException: error occurred while reading file");
        }
        

    }

    public PasswordModel() {
        loadPasswords();
    }

    static public boolean passwordFileExists() {
        return passwordFile.exists();
    }

    static public void initializePasswordFile(String password) throws IOException, NoSuchAlgorithmException, 
    InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        passwordFile.createNewFile();
        // TODO: Use password to create token and save in file with salt (TIP: Save these just like you would save password)
        byte salt[] = generateSalt();

        byte encodedKey[] = createKey(salt, password);
        savedKey = encodedKey;
        String encryptedToken = encryptString(verifyString, encodedKey);

        String saltString = new String(salt, StandardCharsets.UTF_8);

        try (FileWriter writer = new FileWriter(passwordFile)) {
            writer.write(saltString + "\t" + encryptedToken + System.lineSeparator());
        }

    }

    static public boolean verifyPassword(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
     NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        passwordFilePassword = password; // DO NOT CHANGE

        // TODO: Check first line and use salt to verify that you can decrypt the token using the password from the user
        // TODO: TIP !!! If you getLabel();
        // try{} an exception trying to decrypt, that also means they have the wrong passcode, return false!
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {

            String line = reader.readLine();
            if (line == null) return false;

            String[] parts = line.split("\t");
            if(parts.length != 2) return false;

            String saltString = parts[0];
            String encryptedToken = parts[1];

            byte[] salt = saltString.getBytes(StandardCharsets.UTF_8);

            byte[] key = createKey(salt, password);
            
            // NEED TO ADD THE FOLLOWING IN HERE:
            savedKey = key;
            String decrypted = decryptString(encryptedToken, key);

            return verifyString.equals(decrypted);

            

        } catch (Exception e) {
            return false;
        }
        
        
    }

    public ObservableList<Password> getPasswords() {
        return passwords;
    }

    public void deletePassword(int index) {
        // Defensive check: invalid selection
        if (index < 0 || index >= passwords.size()) {
            return;
        }

        try {
            // Remove from in-memory list
            passwords.remove(index);

            // Read all lines from file
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // line 0 = salt + verification token
            // line i+1 = password entry
            int fileLineIndex = index + 1;

            if (fileLineIndex <= 0 || fileLineIndex >= lines.size()) {
                throw new IOException("Password entry not found in file.");
            }

            lines.remove(fileLineIndex);

            // Rewrite file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            // Log the error but DO NOT crash JavaFX
            System.err.println("Failed to delete password:");
            e.printStackTrace();
        }
    }

    public void updatePassword(Password password, int index) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, 
    InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        passwords.set(index, password);

        // TODO: Update the file with the new password information
        String passString = password.getPassword();
        String encryptedString = encryptString(passString, savedKey);
        String label = password.getLabel();
        String newLine = label + "\t" + encryptedString;

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        int fileLineIndex = index + 1;
        if (fileLineIndex < lines.size()) {
            lines.set(fileLineIndex, newLine);
        } else {
            throw new IOException("Password entry not found in file.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        }
    }

    public void addPassword(Password password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, 
    InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

        // TODO: Add the new password to the file
        // String passString = password.toString();
        // String encryptedString = encryptString(passString, savedKey);
        
        // old code taken out -raaghuv
        /* 
        passwords.add(password);
        String label = password.getLabel();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile, true))) { 
            writer.write(label + System.lineSeparator());
        } */

        // try{
        //     bufferedWriter()
        //     \n + label + "\t" + encryptedString
        // }

        //i dont think this will help.,... but idk 
        passwords.add(password);

        String label = password.getLabel();
        String encrypted = encryptString(password.getPassword(), savedKey);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile, true))) {
            writer.write(label + separator + encrypted);
            writer.write(System.lineSeparator());
        }

    }

    // TODO: Tip: Break down each piece into individual methods, for example: generateSalt(), encryptPassword, generateKey(), saveFile, etc ...
    // TODO: Use these functions above, and it will make it easier! Once you know encryption, decryption, etc works, you just need to tie them in

    // Generate Key with PBKDF2

    //Encrypt
    // SAVE SALT SOMEWHERE
    // SAVE ENCRYPTED VERIFICATION MESSAGE SOMEWHERE
    // Call Generate Salt
    // Generate Key 
    // then encrypt password
    private static String encryptString(String string, byte encodedKey[]) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Generate salt, then encode salt

        // creating a key using PBKDF2 from the inputted password using the salt we made
        // then encode that key

        // encoding the verification string "Mattia"
        // byte verifyByte[] = verifyString.getBytes();
        // encodedVerifyString = Base64.getEncoder().encodeToString(verifyByte);

        // take encoded key from PBKDF2 then encrypt verification string "Mattia" using that key
        // return the encrypted and encoded verification string
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(encodedKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte encryptedString[] = cipher.doFinal(string.getBytes());
        String encodedString = new String(Base64.getEncoder().encode(encryptedString));

        return encodedString;
    }

    // Decrypt
    // Use inputted password to attempt to decrypt verification string "Mattia"
    // if output is not "Mattia" then you know the entered password was incorrect
    // pass output into corect password? function
    private static String decryptString(String string, byte encodedKey[]) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // getLabel();
        // try{} salt from password file 
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(encodedKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte decodedData[] = Base64.getDecoder().decode(string); //changed input to .decode() from .decode(encodedVerifyString) to .decode(string)
        byte decryptedData[] = cipher.doFinal(decodedData);
        String decryptedString = new String(decryptedData, StandardCharsets.UTF_8);
        return decryptedString;
        
    }

    private static byte[] createKey(byte salt[], String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        // creating a key using PBKDF2 from the inputted password using the salt we made
        // then encode that key
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 600000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey privateKey = factory.generateSecret(spec);
        byte encodedKey[] = privateKey.getEncoded();
        return encodedKey;
    }

    //Generate Salt then encode it
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String saltString = Base64.getEncoder().encodeToString(bytes);
        byte saltBytes[] = saltString.getBytes();
        return saltBytes;
    }

}
