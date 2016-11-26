package fingerptint.oto.fingerprintauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {
    @BindView(R.id.rootLayout)
    public LinearLayout la_root;
    @BindView(R.id.et_username)
    public EditText et_userName;
    @BindView(R.id.et_password)
    public EditText et_password;
    @BindView(R.id.bt_login)
    protected Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FingerPrintDialog().showDialog(this, getString(R.string.finger_auth_message));
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_login)
    public void onLoginClick() {
        // login button click event goes here
    }


    private void showAboutDialog() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("App Name : ")
                    .append("FingerPrint Auth Demo")
                    .append("\n")
                    .append("Author : ")
                    .append("Otar Iantbelidze")
                    .append("\n")
                    .append("Contact : ")
                    .append("Otar.iantbelidze@gmail.com")
                    .append("\n")
                    .append("Date : ").append("24/11/2016");

            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setTitle("About")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();

            alertDialog.setMessage(sb.toString());
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.show();

        } catch (Exception ex) {
            Log.d(this.getClass().getSimpleName(), ex.getMessage());
        }
    }
}
