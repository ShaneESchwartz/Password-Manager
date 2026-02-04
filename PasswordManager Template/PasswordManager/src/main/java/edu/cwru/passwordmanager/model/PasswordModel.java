package edu.cwru.passwordmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
    static private Cipher cipher;
    static {
    
        try {
            cipher = Cipher.getInstance("AES");
        }
        catch(NoSuchPaddingException nspe) { //break into seperate methods - probably wrong password //wont get cookies back, will get this error -> retyurns boolean 
            //encrypt and decrypt methods can both create cipher
            //be able to text that it does what we want this to do. 
            System.out.println("NoSuchPaddingException");
        }
        catch(NoSuchAlgorithmException nsae) {
            System.out.println("NoSuchAlgorithmException");
        }
    }
    static private KeySpec spec = new PBEKeySpec(keyString.toCharArray(), salt, 600000, 256);

    // COMPLETED: You can set this to whatever you like to verify that the password the user entered is correct
    private static String verifyString = "Mattia";

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
    private static String encryptPassword(String password, String key) {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
    }


    //Generate Salt
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String saltString = Base64.getEncoder().encodeToString(bytes);
        return saltString;
    }

}
