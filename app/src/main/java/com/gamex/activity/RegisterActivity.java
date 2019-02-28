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
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    TextView txtToLogin;
    Validator validator;
    Button btnRegister;

    @Pattern(regex = "[a-zA-Z]", message = Constant.ERR_AZ_CHARACTERS)
    @Length(min = 6, max = 12, message = Constant.ERR_LENGTH_6_12)
    TextInputLayout tilFirstname;

    @Pattern(regex = "[a-zA-Z]", message = Constant.ERR_AZ_CHARACTERS)
    @Length(min = 6, max = 12, message = Constant.ERR_LENGTH_6_12)
    TextInputLayout tilLastname;

    @Length(min = 6, max = 12, message = Constant.ERR_LENGTH_6_12)
    TextInputLayout tilUsername;

    @Password
    TextInputLayout tilPassword;

    @ConfirmPassword
    TextInputLayout tilRePassword;

    @Email
    TextInputLayout tilEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Bind view
        btnRegister = findViewById(R.id.btnRegister);
        txtToLogin = findViewById(R.id.txtToLogin);
        tilFirstname = findViewById(R.id.tilFirstName);
        tilLastname = findViewById(R.id.tilLastName);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilRePassword = findViewById(R.id.tilRePassword);
        tilEmail = findViewById(R.id.tilEmail);

        initValidator();

        txtToLogin.setOnClickListener(v -> {
            txtToLogin.setTextColor(getResources().getColor(R.color.btn_pressed));
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> validator.validate());
    }

    private void initValidator() {
        // Create validator
        validator = new Validator(this);
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                processOnValid();
            }
            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                processOnInValid(errors);
            }
        });
        // Reset error
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidator());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    private void processOnValid() {
        // TODO call API to check
        Toast.makeText(this, "Valid!", Toast.LENGTH_LONG).show();
    }


    private void processOnInValid(List<ValidationError> errors) {
//        errors.get(0).getView().requestFocus();
        for (ValidationError error : errors) {
            View view = error.getView();
//            String message = error.getCollatedErrorMessage(this);
            String message = error.getFailedRules().get(0).getMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
//                ((TextInputLayout) view).setHintEnabled(false);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
