package app.carhire.com.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;
import app.carhire.com.models.ClientModel;

public class SignUpActivity extends AppCompatActivity {
//    private int RC_SIGN_IN = 1236;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private Button btnRegister;
    private EditText etUsername, etEmail, etPassword, etConfirmPwd;
    private ProgressDialog progressDialog;
    private String userName, email, password, confirmPassword, userType;
    private Spinner spinnerUserType;
    private String[] userTypesArray = {"Owner", "Client"};

//    List<AuthUI.IdpConfig> providers = Arrays.asList(
//            new AuthUI.IdpConfig.EmailBuilder().build(),
//            new AuthUI.IdpConfig.PhoneBuilder().build(),
//            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);

//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                String providerId;
//                String uid;
//                String name;
//                String email;
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    for (UserInfo profile : user.getProviderData()) {
//                        // Id of the provider (ex: google.com)
//                        // UID specific to the provider
//                        // Name, email address, and profile photo Url
//                        name = profile.getDisplayName();
//                        email = profile.getEmail();
//                        ClientModel newUser = new ClientModel(getUserId(), name, email, "client", null);
//                        writeNewUser(newUser);
//                    }
//
//                }
//
//                startActivity(new Intent(getApplicationContext(), ViewCars.class));
//            } else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                String error = "Error:" + response.getError().getErrorCode();
//                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//
//            }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btRegister);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPwd = findViewById(R.id.etRepeatPassword);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        progressDialog = new ProgressDialog(this);

        ArrayAdapter userTypeAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, userTypesArray);
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerUserType.setAdapter(userTypeAdapter);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = userTypesArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO show progress dialog
                getUserData();
                if(validatePassword()) {
                    progressDialog.setMessage("Registering...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //sign up the user
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //TODO remove progress dialog
                            if(task.isSuccessful()){
                                //write user to the database
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null)
                                {
                                    writeNewUser(new ClientModel(user.getUid(), userName, email, userType));
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            startActivity(new Intent(getApplicationContext(), ViewCars.class));
//        } else {
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    RC_SIGN_IN
//            );
//        }

    }

    private void getUserData(){
        userName = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPwd.getText().toString().trim();
    }

    private boolean validatePassword(){
        if(password.length() < 6){
            etPassword.setError("Password must be more than 6 characters!");
            return false;
        } else if(!password.equals(confirmPassword)){
            etConfirmPwd.setError("Password do not match!");
            return false;
        }
        return true;
    }

    private void writeNewUser(ClientModel client) {
        mDatabaseReference.child("users").child(client.getUserId()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Couldn't save your details to database", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
