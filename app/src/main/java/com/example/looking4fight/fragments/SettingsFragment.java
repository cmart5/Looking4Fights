package com.example.looking4fight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.looking4fight.fragments.MainActivity;
import com.example.looking4fight.ui.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private Button logoutButton, buttonDeleteAccount;
    private Switch darkModeSwitch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        logoutButton = v.findViewById(R.id.buttonSignOut);
        buttonDeleteAccount = v.findViewById(R.id.buttonDeleteAccount);
        darkModeSwitch = v.findViewById(R.id.switchDarkTheme);

        if(isNightMode(this.getContext())){
            darkModeSwitch.setChecked(true);
        }
        else {
            darkModeSwitch.setChecked(false);
        }

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onDarkModeToggled(darkModeSwitch.isChecked());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirm sign out
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Logout?");
                builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //sign out and return to login page
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancels the dialog.
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            //TODO: reorganize code, make login confirmation useable anywhere
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //Creating dialog window to take user login information again
                Dialog confirmLoginDialog = new Dialog(getActivity());
                confirmLoginDialog.setContentView(R.layout.dialog_confirm_login);
                confirmLoginDialog.show();

                Button buttonConfirmLogin = confirmLoginDialog.findViewById(R.id.login);
                Button buttonCancel = confirmLoginDialog.findViewById(R.id.cancel_button);
                SignInButton googleSignInButton = v.findViewById(R.id.google_sign_in);

                buttonCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        confirmLoginDialog.dismiss();
                    }
                });

                //confirming login via email and password
                buttonConfirmLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //gets entered email and password
                        EditText emailET = confirmLoginDialog.findViewById(R.id.email);
                        EditText passwordET = confirmLoginDialog.findViewById(R.id.password);
                        String email = emailET.getText().toString().trim();
                        String password = passwordET.getText().toString();

                        //attempts to reauthenticate user
                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Re-authentication Successful", Toast.LENGTH_LONG).show();

                                    //closes reauth dialog
                                    confirmLoginDialog.dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Confirm Delete Account?\nThis action cannot be reversed");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "Account Successfully Deleted", Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                                    Dialog confirmDelete = builder.create();
                                    confirmDelete.show();

                                }
                                else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            };
        });
        return v;
    }

    public boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public void onDarkModeToggled(boolean darkMode) {
        if(darkMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Restore Bottom Navigation Selection
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).restoreBottomNavigationSelection();
        }
        /*
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("open_settings_fragment", true); // Set the flag to open SettingsFragment
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish(); // Close the current activity (SettingsFragment)
        requireActivity().recreate(); // Restart the activity

         */
    }
}
