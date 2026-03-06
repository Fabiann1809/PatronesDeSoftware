package com.factory.encryption;

import com.factory.exception.EncryptionException;
import com.factory.model.Document;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handles AES-256-GCM encryption of sensitive document content.
 * All document types in the system are considered sensitive and must be encrypted.
 */
public class DocumentEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public DocumentEncryptor() throws EncryptionException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            this.secretKey = keyGen.generateKey();
            this.secureRandom = new SecureRandom();
        } catch (Exception e) {
            throw new EncryptionException(
                    "Error al inicializar el sistema de encriptación: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts the content of a document using AES-256-GCM.
     *
     * @param document The document whose content will be encrypted
     * @return The document with encrypted content
     * @throws EncryptionException if encryption fails
     */
    public Document encrypt(Document document) throws EncryptionException {
        if (document.isEncrypted()) {
            return document;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedBytes = cipher.doFinal(document.getContent().getBytes());

            // Combine IV + encrypted data and encode as Base64
            byte[] combined = new byte[IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, combined, IV_LENGTH, encryptedBytes.length);

            String encryptedContent = Base64.getEncoder().encodeToString(combined);
            document.setContent(encryptedContent);
            document.setEncrypted(true);

            return document;
        } catch (Exception e) {
            throw new EncryptionException(
                    String.format("Error al encriptar el documento [%s] de tipo '%s': %s",
                            document.getId(), document.getType().getDisplayName(), e.getMessage()), e);
        }
    }

    /**
     * Decrypts the content of an encrypted document.
     *
     * @param document The document whose content will be decrypted
     * @return The document with decrypted content
     * @throws EncryptionException if decryption fails
     */
    public Document decrypt(Document document) throws EncryptionException {
        if (!document.isEncrypted()) {
            return document;
        }

        try {
            byte[] combined = Base64.getDecoder().decode(document.getContent());

            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

            byte[] encryptedBytes = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            document.setContent(new String(decryptedBytes));
            document.setEncrypted(false);

            return document;
        } catch (Exception e) {
            throw new EncryptionException(
                    String.format("Error al desencriptar el documento [%s]: %s",
                            document.getId(), e.getMessage()), e);
        }
    }
}
