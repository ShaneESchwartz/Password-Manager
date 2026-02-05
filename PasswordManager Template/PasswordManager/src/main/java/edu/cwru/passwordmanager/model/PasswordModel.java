package edu.cwru.passwordmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;


public class PasswordModel {
    private ObservableList<Password> passwords = FXCollections.observableArrayList();

    // !!! DO NOT CHANGE - VERY IMPORTANT FOR GRADING !!!
    static private File passwordFile = new File("passwords.txt");

    static private String separator = "\t";

    static private String passwordFilePassword = "";
    static private byte [] passwordFileKey;
    static private byte [] passwordFileSalt;
    // static private Cipher cipher;
    // static {
    
    //     try {
    //         cipher = Cipher.getInstance("AES");
    //     }
    //     catch(NoSuchPaddingException nspe) { //break into seperate methods - probably wrong password //wont get cookies back, will get this error -> retyurns boolean 
    //         //encrypt and decrypt methods can both create cipher
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
        // TODO: Replace with loading passwords from file, you will want to add them to the passwords list defined above
        try{
            BufferedReader b = new BufferedReader(new FileReader(passwordFile));
            String line;
            while ((line = b.readLine()) != null) {
                // line by line actions
                String[] parts = line.split(separator);
                String password = parts[1];
                // decrypt function to be made later
            }
            b.close();
        }
        catch(IOException e){
            System.out.println("IOException, error ocurred");
        }
        // TODO: Tips: Use buffered reader, make sure you split on separator, make sure you decrypt password
    }

    public PasswordModel() {
        loadPasswords();
    }

    static public boolean passwordFileExists() {
        return passwordFile.exists();
    }

    static public void initializePasswordFile(String password) throws IOException {
        passwordFile.createNewFile();
        // TODO: Use password to create token and save in file with salt (TIP: Save these just like you would save password)
        
        String salt = generateSalt();
        String passwordSalt = password + salt;
        passwordFileSalt.add(salt)
        encryptedPasswordSalt = passwordsalt.encrypt();
        passwordFilePassword.add(encryptedPasswordSalt);


    }

    static public boolean verifyPassword(String password) {
        passwordFilePassword = password; // DO NOT CHANGE

        // TODO: Check first line and use salt to verify that you can decrypt the token using the password from the user
        // TODO: TIP !!! If you get an exception trying to decrypt, that also means they have the wrong passcode, return false!

        return false;
    }

    public ObservableList<Password> getPasswords() {
        return passwords;
    }

    public void deletePassword(int index) {
        passwords.remove(index);

        // TODO: Remove it from file
    }

    public void updatePassword(Password password, int index) {
        passwords.set(index, password);

        // TODO: Update the file with the new password information
    }

    public void addPassword(Password password) {
        passwords.add(password);

        // TODO: Add the new password to the file
    }

    // TODO: Tip: Break down each piece into individual methods, for example: generateSalt(), encryptPassword, generateKey(), saveFile, etc ...
    // TODO: Use these functions above, and it will make it easier! Once you know encryption, decryption, etc works, you just need to tie them in

    //Decrypt Helper Function
    private String decrypt(String input) {

        return input;

    }

    // Generate Key with PBKDF2

    //Encrypt
    // SAVE SALT SOMEWHERE
    // SAVE ENCRYPTED VERIFICATION MESSAGE SOMEWHERE
    // Call Generate Salt
    // Generate Key 
    // then encrypt password
    private static String encryptPassword(String password, byte encodedKey[]) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
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
        byte encryptedData[] = cipher.doFinal(verifyString.getBytes());
        String messageString = new String(Base64.getEncoder().encode(encryptedData));

        return messageString;
    }

    // Decrypt
    // Use inputted password to attempt to decrypt verification string "Mattia"
    // if output is not "Mattia" then you know the entered password was incorrect
    // pass output into corect password? function
    private static String decryptPassword(String password, byte salt[]) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // get salt from password file 
        
        byte encodedKey[] = createKey(salt, password);
        
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(encodedKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte decodedData[] = Base64.getDecoder().decode(encodedVerifyString);
        byte decryptedData[] = cipher.doFinal(decodedData);
        String decryptedVerificationString = new String(decryptedData);
        return decryptedVerificationString;
        
    }

    private static byte[] createKey(byte salt[], String password) throws NoSuchAlgorithmException, InvalidKeySpecException{

        // creating a key using PBKDF2 from the inputted password using the salt we made
        // then encode that key
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 600000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PDBKDF2WithHmacSHA256");
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
