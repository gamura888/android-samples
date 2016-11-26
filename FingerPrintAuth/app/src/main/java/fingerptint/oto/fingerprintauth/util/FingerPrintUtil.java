package fingerptint.oto.fingerprintauth.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import fingerptint.oto.fingerprintauth.FingerPrintDialog;
import fingerptint.oto.fingerprintauth.exception.NoEnrolledFingerprints;
import fingerptint.oto.fingerprintauth.exception.NoHardwareException;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by Otar Iantbelidze on 11/25/16.
 */

public class FingerPrintUtil {

    private static FingerPrintUtil self;

    private final String KEY_NAME = "KEY_NAME";
    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerPrintDialog dialog;
    private Context context;

    private FingerPrintUtil() {
    }

    private FingerPrintUtil(FingerPrintDialog dialog, Context context) {
        this.dialog = dialog;
        this.context = context;
    }

    public static FingerPrintUtil getInstance(FingerPrintDialog dialog, Context context) {
        if (self == null) {
            self = new FingerPrintUtil(dialog, context);
        }
        return self;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void init() {

        keyguardManager =
                (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);


        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(context, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();
            return;
        }

        if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();

            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(context,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(context,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        generateKey();

        if (cipherInit()) {
            cryptoObject =
                    new FingerprintManager.CryptoObject(cipher);
            FingerHandler helper = new FingerHandler(context, dialog);
            helper.authenticate(fingerprintManager, cryptoObject);
        }

    }

    public void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to init Cipher", t);
        }
    }

    public static boolean checkFingerprintHardware(Context context) throws Exception {
        boolean check = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "User foes not have FingerPrint permission grandted...", Toast.LENGTH_LONG).show();
                return check;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                throw new NoHardwareException("Device doesn't support fingerprint authentication");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                throw new NoEnrolledFingerprints("User hasn't enrolled any fingerprints to authenticate with");
            } else {
                // Everything is ready for fingerprint authentication
                check = true;
            }
        }
        return check;
    }
}
