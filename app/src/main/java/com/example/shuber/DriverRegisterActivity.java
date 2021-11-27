package com.example.shuber;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.shuber.model.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegisterActivity extends AppCompatActivity {

    private TextView emailView, nameView, passView, passCornfirmView, carTypeView, carNumberView;
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SHUBer");

        register = (Button) findViewById(R.id.btn_register);

        emailView = (TextView) findViewById(R.id.emailView);
        nameView = (TextView) findViewById(R.id.nameView);
        passView = (TextView) findViewById(R.id.passView);
        passCornfirmView = (TextView) findViewById(R.id.passCornfirmView);
        carTypeView = (TextView) findViewById(R.id.carTypeView);
        carNumberView = (TextView) findViewById(R.id.carNumberView);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register(){
        String email = emailView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        String pass = passView.getText().toString().trim();
        String passcornfirm = passCornfirmView.getText().toString().trim();
        String carType = carTypeView.getText().toString().trim();
        String carNumber = carNumberView.getText().toString().trim();

        if(email.isEmpty()){
            emailView.setError("this field is required!");
            emailView.requestFocus();
            return;
        }
        if(name.isEmpty()){
            nameView.setError("this field is required!");
            nameView.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            passView.setError("this field is required!");
            passView.requestFocus();
            return;
        }
        if(passcornfirm.isEmpty()){
            passCornfirmView.setError("this field is required!");
            passCornfirmView.requestFocus();
            return;
        }
        if(!pass.equals(passcornfirm)){
            passCornfirmView.setError("password is not matched");
            passCornfirmView.requestFocus();
            return;
        }
        if(carType.isEmpty()){
            carTypeView.setError("this field is required!");
            carTypeView.requestFocus();
            return;
        }
        if(carNumber.isEmpty()){
            carNumberView.setError("this field is required!");
            carNumberView.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(task);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Driver driver = new Driver();
                            driver.setName(name);
                            driver.setCarType(carType);
                            driver.setCarNumber(carNumber);
                            driver.setPassword(pass);
                            driver.setEmail(user.getEmail());
                            driver.setIdToken(user.getUid());

                            mDatabaseRef.child("Driver").child(user.getUid()).setValue(driver);
                            Toast.makeText(DriverRegisterActivity.this, "It has been registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DriverRegisterActivity.this, MainMapsCustomerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(DriverRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}