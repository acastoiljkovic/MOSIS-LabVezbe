package com.example.myplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = null;
    Button btnRegister;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        btnRegister = findViewById(R.id.btn_register);
        etEmail = findViewById(R.id.editText_email);
        etPassword = findViewById(R.id.editText_password);
        etPasswordConfirm = findViewById(R.id.editText_password_confirm);

        createDialog();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = etEmail.getText().toString();
                String Password = etPassword.getText().toString();
                String PasswordConf = etPasswordConfirm.getText().toString();

                if(TextUtils.isEmpty(Email)){
                    etEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    etPassword.setError("Password is required");
                }

                if(etPassword.length()<6){
                    etPassword.setError("Password must be >= 6 characters");
                    return;
                }

                if(Password.compareTo(PasswordConf) != 0){
                    etPassword.setError("Password must be the same");
                    etPasswordConfirm.setError("Password must be the same");

                }

                if(firebaseAuth != null) {
                    dialogShow();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                MyPlacesData.getInstance().setUser(new User(Email));
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                dialogDismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                finish();
                            } else {
                                dialogHide();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getApplicationContext(), "Error while creating user ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void createDialog(){

        dialog =new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    public void dialogShow(){
        try {
            if (!dialog.isShowing())
                dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogHide(){
        try {
            if (dialog.isShowing())
                dialog.hide();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogDismiss(){
        try{
            dialog.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
