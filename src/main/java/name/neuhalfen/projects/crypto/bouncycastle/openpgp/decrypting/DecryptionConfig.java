package name.neuhalfen.projects.crypto.bouncycastle.openpgp.decrypting;


import java.io.*;

/**
 * Bundles everything needed for decryption. Dedicated sub-classes can override.
 */
public abstract class DecryptionConfig {

    /**
     * Create a decryption config by reading keyrings from files.
     *
     * @param publicKeyring                 E.g. src/test/resources/sender.gpg.d/pubring.gpg
     * @param secretKeyring                 E.g. src/test/resources/sender.gpg.d/secring.gpg
     * @param signatureCheckRequired        true: force the presence of a signature with a key from the pubring
     * @param decryptionSecretKeyPassphrase key to decrypt the secret key
     * @return the config
     */
    public static DecryptionConfig withKeyRingsFromFiles(final File publicKeyring,
                                                         final File secretKeyring,
                                                         boolean signatureCheckRequired, String decryptionSecretKeyPassphrase) {

        return new DecryptionConfig(signatureCheckRequired, decryptionSecretKeyPassphrase) {

            final File publicKeyringFile = publicKeyring;
            final File secretKeyringFile = secretKeyring;

            @Override
            public InputStream getPublicKeyRing() throws IOException {
                return new FileInputStream(publicKeyringFile);
            }

            @Override
            public InputStream getSecretKeyRing() throws FileNotFoundException {
                return new FileInputStream(secretKeyringFile);
            }
        };
    }

    /**
     * Create a decryption config by reading keyrings from the classpath.
     *
     * @param classLoader                   E.g. DecryptWithOpenPGPTest.class.getClassLoader()
     * @param publicKeyring                 E.g. "recipient.gpg.d/pubring.gpg"
     * @param secretKeyring                 E.g. "recipient.gpg.d/secring.gpg"
     * @param signatureCheckRequired        true: force the presence of a signature with a key from the pubring
     * @param decryptionSecretKeyPassphrase passphrase to decrypt the secret key
     * @return the config
     */
    public static DecryptionConfig withKeyRingsFromResources(final ClassLoader classLoader, final String publicKeyring,
                                                             final String secretKeyring,
                                                             boolean signatureCheckRequired, String decryptionSecretKeyPassphrase) {

        return new DecryptionConfig(signatureCheckRequired, decryptionSecretKeyPassphrase) {


            @Override
            public InputStream getPublicKeyRing() throws IOException {
                return classLoader.getResourceAsStream(publicKeyring);
            }

            @Override
            public InputStream getSecretKeyRing() throws FileNotFoundException {
                return classLoader.getResourceAsStream(secretKeyring);
            }
        };
    }


    private final boolean signatureCheckRequired;
    private final String decryptionSecretKeyPassphrase;

    protected DecryptionConfig(boolean signatureCheckRequired, String decryptionSecretKeyPassphrase) {
        this.signatureCheckRequired = signatureCheckRequired;
        this.decryptionSecretKeyPassphrase = decryptionSecretKeyPassphrase;
    }


    /**
     * @return force the presence of a signature with a key from the pubring
     */
    public boolean isSignatureCheckRequired() {
        return signatureCheckRequired;
    }

    /**
     * @return passphrase to decrypt the secret key
     */
    public String getDecryptionSecretKeyPassphrase() {
        return decryptionSecretKeyPassphrase;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DecryptionConfig{");
        sb.append("signatureCheckRequired=").append(signatureCheckRequired);
        sb.append(", decryptionSecretKeyPassphrase? :").append(decryptionSecretKeyPassphrase != null).append("\"");
        sb.append('}');
        return sb.toString();
    }

    /**
     * @return Stream that connects to  secring.gpg
     * @throws FileNotFoundException File not found
     */
    public abstract InputStream getSecretKeyRing() throws IOException;

    /**
     * @return Stream that connects to  pubring.gpg
     * @throws FileNotFoundException File not found
     */
    public abstract InputStream getPublicKeyRing() throws IOException;
}
