package fingerptint.oto.fingerprintauth;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fingerptint.oto.fingerprintauth.exception.NoEnrolledFingerprints;
import fingerptint.oto.fingerprintauth.exception.NoHardwareException;
import fingerptint.oto.fingerprintauth.util.FingerPrintUtil;

/**
 * Created by Otar Iantbelidze on 11/24/16.
 */

public class FingerPrintDialog {
    private Dialog dialog;
    private ImageView imageView;
    private Activity activity;

    public void showDialog(final Activity activity, String msg) {
        this.activity = activity;

        boolean passed = checkDevice();

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.finger_print_dialog);

        FingerPrintUtil util = FingerPrintUtil.getInstance(this, activity);
        util.init();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        imageView = (ImageView) dialog.findViewById(R.id.finger_print_image);

        text.setText(msg);

        TextView cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        if (passed) {
            dialog.show();
        }
    }

    private boolean checkDevice() {
        try {
            return FingerPrintUtil.checkFingerprintHardware(activity);
        } catch (NoEnrolledFingerprints nex) {
            Toast.makeText(activity, nex.getMessage(), Toast.LENGTH_LONG);

        } catch (NoHardwareException nhe) {
            Toast.makeText(activity, nhe.getMessage(), Toast.LENGTH_LONG);
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG);
        }
        return false;
    }

    public void onAuthFailed() {
        Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
        imageView.startAnimation(shake);
        imageView.setImageResource(R.drawable.ic_finger_print_error);
    }

    public void hide() {
        dialog.hide();
    }
}
