package com.example.shuber;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.shuber.model.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private Button login;
    private Button register;
    private TextView emailView, passView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SHUBer");

        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(this);

        register = (Button) findViewById(R.id.btn_register);
        register.setOnClickListener(this);

        emailView = (TextView) findViewById(R.id.et_id);
        passView = (TextView) findViewById(R.id.et_pass);

        Singleton.getInstance().userType = "customer";

        Switch switchButton = findViewById(R.id.btn_switch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) Singleton.getInstance().userType = "driver";
                else Singleton.getInstance().userType = "customer";
            }
        });
    }
    private void login(){
        String email = emailView.getText().toString().trim();
        String pass = passView.getText().toString().trim();

        if(email.isEmpty()){
            emailView.setError("this field is required!");
            emailView.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            passView.setError("this field is required!");
            passView.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainMapsCustomerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Faild login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(){
        Intent intent;
        if(Singleton.getInstance().userType.equals("customer")){
            intent = new Intent(LoginActivity.this, CustomerRegisterActivity.class);
        }else{
            intent = new Intent(LoginActivity.this, DriverRegisterActivity.class);
        }
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }
}