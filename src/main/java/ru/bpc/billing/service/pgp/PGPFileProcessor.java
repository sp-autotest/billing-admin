package ru.bpc.billing.service.pgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PGPFileProcessor {

    private final String passphrase;
    private final String keyFileName;
    private final boolean asciiArmored = false;
    private final boolean integrityCheck = true;

    public PGPFileProcessor(String passphrase, String keyFileName) {
        this.passphrase = passphrase;
        this.keyFileName = keyFileName;
    }


    public File encrypt(File inputFile, String outputFileName) throws Exception {
        FileInputStream keyIn = new FileInputStream(keyFileName);
        FileOutputStream out = new FileOutputStream(outputFileName);
        PgpHelper.encryptFile(out, inputFile, PgpHelper.readPublicKey(keyIn),
                asciiArmored, integrityCheck);
        out.close();
        keyIn.close();
        return new File(outputFileName);
    }

    public File decrypt(File inputFile, String outputFileName) throws Exception {
        FileInputStream in = new FileInputStream(inputFile);
        FileInputStream keyIn = new FileInputStream(keyFileName);
        FileOutputStream out = new FileOutputStream(outputFileName);
        PgpHelper.decryptFile(in, out, keyIn, passphrase.toCharArray());
        in.close();
        out.close();
        keyIn.close();
        return new File(outputFileName);
    }


}
