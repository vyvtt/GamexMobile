package com.gamex.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {
    TextView txtToRegister;
    Button btnLogin;
    Validator validator;

    @NotEmpty(message = Constant.ERR_REQUIRED)
    TextInputLayout tilUsername;

    @NotEmpty(message = Constant.ERR_REQUIRED)
    TextInputLayout tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bind view
        txtToRegister = findViewById(R.id.txtToRegister);
        btnLogin = findViewById(R.id.btnLogin);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);

        // create validator
        initValidator();

        btnLogin.setOnClickListener(v -> validator.validate());

        txtToRegister.setOnClickListener(v -> {
//            txtToRegister.setTextColor(getResources().getColor(R.color.btn_pressed));
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initValidator() {
        // Create validator
        validator = new Validator(this);
        validator.setValidationListener(this);
        // Reset error
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidator());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        // TODO call API to check
        Toast.makeText(this, "Valid!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
//        errors.get(0).getView().requestFocus();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
