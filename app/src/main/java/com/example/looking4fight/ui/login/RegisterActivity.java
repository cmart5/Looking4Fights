package com.example.looking4fight.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.looking4fight.MainActivity;
import com.example.looking4fight.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextInputLayout editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonReg;
    ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.usernameLayout);
        editTextPassword = findViewById(R.id.passwordLayout);
        editTextConfirmPassword = findViewById(R.id.password_confirm_layout);
        buttonReg = findViewById(R.id.btn_register);
        buttonBack = findViewById(R.id.btn_back);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO:
                    1.Fix crash when register button is clicked
                    2.Add back button to register page
                    3.Add forgot password to login page
                 */
                String email, password, confirmPassword;
                email = String.valueOf(editTextEmail.getEditText().getText()); //ignore the warning, it works
                password = String.valueOf(editTextPassword.getEditText().getText()); //ignore the warning, it works
                confirmPassword = String.valueOf(editTextConfirmPassword.getEditText().getText()); //ignore the warning, it works

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!TextUtils.equals(password, confirmPassword))
                {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(RegisterActivity.this, "Registering...", Toast.LENGTH_SHORT).show();
                //creates new user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();

                                    //goes back to Log in Activity
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goes back to login page
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
