package fingerptint.oto.fingerprintauth.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

import fingerptint.oto.fingerprintauth.FingerPrintDialog;

/**
 * Created by Otar Iantbelidze on 11/26/16.
 */

public class FingerHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FingerPrintDialog dialog;
    private CancellationSignal cancellationSignal;

    public FingerHandler(Context context, FingerPrintDialog dialog) {
        this.context = context;
        this.dialog = dialog;
    }

    public void authenticate(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "User does not have fingerprint permission", Toast.LENGTH_LONG).show();
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "Authentication succeeded.", Toast.LENGTH_LONG).show();
        dialog.hide();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_LONG).show();
        dialog.onAuthFailed();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
        dialog.onAuthFailed();
    }
}
