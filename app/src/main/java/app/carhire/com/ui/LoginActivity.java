package app.carhire.com.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressDialog progressDialog;
    String email, password;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String user_id, user_type;
    private String prefFile = BuildConfig.APPLICATION_ID + ".PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //if user is already logged in proceed
        mAuth = FirebaseAuth.getInstance();


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        sharedPref = getApplicationContext().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if(!sharedPref.getString("UserId","").equals("")){
            startActivity(new Intent(LoginActivity.this, ViewCars.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if(validateForm()){
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null)
                                {
                                    getUserDetails(user.getUid());
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    protected boolean validateForm(){
        if(email.isEmpty()){
            etEmail.setError("Email can't be blank");
            return false;
        } else if (password.isEmpty()){
            etPassword.setError("Password can't be blank");
            return false;
        }
        return true;
    }

    private void getUserDetails(final String u_id){

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                editor.putString("UserId", u_id);
                editor.putString("UserEmail", email);
                editor.putString("UserName", dataSnapshot.child("username").getValue(String.class));
                editor.putString("UserType", dataSnapshot.child("userType").getValue(String.class));
                editor.apply();
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), ViewCars.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
                progressDialog.dismiss();
            }
        };
        mDatabaseReference.child("users").child(u_id).addValueEventListener(postListener);
    }
}
