package sdpd.com.blereminderapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText loginET;
    EditText passEt;
    Button loginB;
    Button registerB;
    Firebase firebaseRef;
    ProgressDialog progress;
    EditText fullName, userName, pass_signupEt, confirmpassEt, phoneEt;
    String pass;
    User userData;
    String userId;
    final String USERS = "users";
    AlertDialog regDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);
        loginET = (EditText) findViewById(R.id.nameEt);
        passEt = (EditText) findViewById(R.id.passwordEt);
        loginB = (Button) findViewById(R.id.loginButton);
        registerB = (Button) findViewById(R.id.registerButton);
        firebaseRef = new Firebase(AppConstants.URL_FIREBASE);
        loginB.setOnClickListener(this);
        registerB.setOnClickListener(this);
        if(firebaseRef.getAuth()!=null)
        {
            userId=firebaseRef.getAuth().getUid();
           storeUserId(userId);
            progress = ProgressDialog.show(this, "", getString(R.string.loading), true);

            getUserData();
        }


    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                final String username = loginET.getText().toString();
                String password = passEt.getText().toString();
                if (username.length() > 0 && password.length() > 0) {
                    progress = ProgressDialog.show(this, "", getString(R.string.loading), true);
                    firebaseRef.authWithPassword(username, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            userId = authData.getUid();
                            storeUserId(userId);
                            Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                            getUserData();


                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            progress.dismiss();
                            showError(firebaseError.getMessage());
                        }
                    });
                } else
                    showError(getString(R.string.empty_login));
                break;
            case R.id.registerButton:
                View dialView = LayoutInflater.from(this).inflate(R.layout.dialog_signup, null);
                regDialog = new AlertDialog.Builder(this).create();
                regDialog.setView(dialView);
                fullName = (EditText) dialView.findViewById(R.id.nameEt);
                userName = (EditText) dialView.findViewById(R.id.emailEt);
                pass_signupEt = (EditText) dialView.findViewById(R.id.passEt);
                confirmpassEt = (EditText) dialView.findViewById(R.id.confirmpassEt);
                phoneEt = (EditText) dialView.findViewById(R.id.phoneEt);
                userData = new User();
                Button reg = (Button) dialView.findViewById(R.id.regButton);
                reg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userData.fullName = fullName.getText().toString();
                        userData.emailId = userName.getText().toString();
                        pass = pass_signupEt.getText().toString();
                        userData.phone = phoneEt.getText().toString();
                        if (verifyInput(userData.fullName, userData.emailId, pass, userData.phone))
                            signUp(userData, pass);
                    }
                });
                regDialog.show();
                break;


        }
    }

    public boolean verifyInput(String name, String email, String pass, String phone) {
        if (name == null || name.length() < 1) {
            showError("Please enter your full name");
            return false;
        }
        if (email == null || email.length() < 1) {
            showError("Please enter a valid email");
            return false;
        }
        if (phone == null || phone.length() < 10) {
            showError("Please enter a valid mobile number");
            return false;
        }
        if (pass == null || pass.length() < 1) {
            showError("Please enter a password");
            return false;
        }
        if (pass.equals(confirmpassEt.getText().toString()) == false) {
            showError("Passwords do not match");
            return false;
        }
        return true;
    }

    public void signUp(final User user, String pass) {
        progress = ProgressDialog.show(this, "", getString(R.string.loading), true);
        firebaseRef.createUser(user.emailId, pass, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                storeData(stringObjectMap.get("uid").toString(), user);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progress.dismiss();
                showError(firebaseError.getMessage());
            }
        });
    }

    public void storeData(String uid, User user) {
        Firebase userRef = firebaseRef.child(AppConstants.USERS).child(uid).child(AppConstants.INFO);
        userId=uid;
        storeUserId(userId);
        userRef.setValue(user);
        progress.dismiss();
        regDialog.dismiss();
        Toast.makeText(this, "You have succesfuly signed up. Please login to continue ", Toast.LENGTH_SHORT).show();

    }

    public void showError(String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    void getUserData() {
        firebaseRef = new Firebase(AppConstants.URL_FIREBASE + "/" + AppConstants.USERS + "/" + userId + "/" + AppConstants.INFO);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("info", dataSnapshot.toString());
                userData = dataSnapshot.getValue(User.class);
                progress.dismiss();
                launchMain();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                progress.dismiss();
                showError("Could not connect to the server. Please try again later.");
            }
        });
    }

    void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppConstants.UID_INTENT, userId);
        intent.putExtra(AppConstants.USER_INTENT, userData);
        startActivity(intent);
        finish();

    }

    public void storeUserId(String userId)
    {
        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e=sharedpreferences.edit();
        e.putString(AppConstants.UID,userId);
    }


}
