package com.example.shuber;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.shuber.model.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    private TextView emailView, nameView, passView, passCornfirmView, cardView;
    private RadioButton visa, master;
    private Button register;
    private RadioGroup radioGroup;
    private String cardType = "visa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SHUBer");

        register = (Button) findViewById(R.id.btn_register);
        register.setOnClickListener(this);

        emailView = (TextView) findViewById(R.id.emailView);
        nameView = (TextView) findViewById(R.id.nameView);
        passView = (TextView) findViewById(R.id.passView);
        passCornfirmView = (TextView) findViewById(R.id.passCornfirmView);
        cardView = (TextView) findViewById(R.id.cardView);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radio_visa);
        visa = (RadioButton) findViewById(R.id.radio_visa);
        master = (RadioButton) findViewById(R.id.radio_master);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_visa){
                    cardType = "visa";
                }else if(i == R.id.radio_master){
                    cardType = "master";
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        registerUser();
    }

    private void registerUser(){
        String email = emailView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        String pass = passView.getText().toString().trim();
        String passcornfirm = passCornfirmView.getText().toString().trim();
        String cardnumber = cardView.getText().toString().trim();

        if(email.isEmpty()){
            emailView.setError("this field is required!");
            emailView.requestFocus();
            return;
        }
//        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            emailView.setError("this is not valid email");
//            emailView.requestFocus();
//            return;
//        }
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
        if(cardnumber.isEmpty()){
            cardView.setError("this field is required!");
            cardView.requestFocus();
            return;
        }
        if (cardnumber.contains("[a-zA-Z]+") == false && cardnumber.length() != 16){
            cardView.setError("this is not valid number");
            cardView.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(task);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Customer customer = new Customer();
                            customer.setName(name);
                            customer.setCardnumber(cardnumber);
                            customer.setCardType(cardType);
                            customer.setPassword(pass);
                            customer.setEmail(user.getEmail());
                            customer.setIdToken(user.getUid());

                            mDatabaseRef.child("Customer").child(user.getUid()).setValue(customer);
                            Toast.makeText(CustomerRegisterActivity.this, "It has been registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CustomerRegisterActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(CustomerRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}