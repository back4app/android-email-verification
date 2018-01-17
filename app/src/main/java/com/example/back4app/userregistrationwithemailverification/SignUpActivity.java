package com.example.back4app.userregistrationwithemailverification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;
    private EditText emailView;
    private EditText passwordAgainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this);
        setContentView(R.layout.activity_sign_up);

        final Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
                dlg.setTitle("Please, wait a moment.");
                dlg.setMessage("Returning to the login section...");
                dlg.show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                dlg.dismiss();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        passwordAgainView = (EditText) findViewById(R.id.passwordAgain);

        final Button signup_button = findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating the log in data
                boolean validationError = false;

                StringBuilder validationErrorMessage = new StringBuilder("Please, insert ");
                if (isEmpty(usernameView)) {
                    validationError = true;
                    validationErrorMessage.append("an username");
                }
                if (isEmpty(emailView)||!isEmailValid(emailView.getText().toString())) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("a valid email address");
                }
                if (isEmpty(passwordView)||!isPasswordValid(passwordView.getText().toString())) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("a valid password");
                }
                if (isEmpty(passwordAgainView)) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("your password again");
                }
                else {
                    if (!isMatching(passwordView, passwordAgainView)) {
                        if (validationError) {
                            validationErrorMessage.append(" and ");
                        }
                        validationError = true;
                        validationErrorMessage.append("the same password twice.");
                    }
                }
                validationErrorMessage.append(".");

                if (validationError) {
                    Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
                dlg.setTitle("Please, wait a moment.");
                dlg.setMessage("Signing up...");
                dlg.show();

                try {
                    // Reset errors.
                    emailView.setError(null);
                    passwordView.setError(null);

                    ParseUser user = new ParseUser();
                    user.setUsername(usernameView.getText().toString());
                    user.setPassword(passwordView.getText().toString());
                    user.setEmail(emailView.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                dlg.dismiss();
                                ParseUser.logOut();
                                alertDisplayer("Account Created Successfully!", "Please verify your email before Login", false);
                            } else {
                                dlg.dismiss();
                                ParseUser.logOut();
                                alertDisplayer("Error Account Creation failed", "Account could not be created" + " :" + e.getMessage(), true);
                            }
                        }
                    });
                } catch (Exception e) {
                    dlg.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isEmpty(EditText text) {
        if (text.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText text1, EditText text2){
        if(text1.getText().toString().equals(text2.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void alertDisplayer(String title,String message, final boolean error){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(!error) {
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}
