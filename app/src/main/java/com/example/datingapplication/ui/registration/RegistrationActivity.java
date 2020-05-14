package com.example.datingapplication.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapplication.R;
import com.example.datingapplication.ui.choose_time_place.ChooseTimePlaceActivity;
import com.example.datingapplication.ui.date_map.DateMapActivity;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


//        final EditText usernameEditText = findViewById(R.id.username);
//        final EditText passwordEditText = findViewById(R.id.password);
        final Button registrationButton = findViewById(R.id.registration);
        final Button backButton = findViewById(R.id.back);

        /**/

//        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
//            if (loginFormState == null) {
//                return;
//            }
//            loginButton.setEnabled(loginFormState.isDataValid());
//            if (loginFormState.getUsernameError() != null) {
//                usernameEditText.setError(getString(loginFormState.getUsernameError()));
//            }
//            if (loginFormState.getPasswordError() != null) {
//                passwordEditText.setError(getString(loginFormState.getPasswordError()));
//            }
//        });

//        loginViewModel.getLoginResult().observe(this, loginResult -> {
//            if (loginResult == null) {
//                return;
//            }
//            loadingProgressBar.setVisibility(View.GONE);
//            if (loginResult.getError() != null) {
//                showLoginFailed(loginResult.getError());
//            }
//            if (loginResult.getSuccess() != null) {
//                updateUiWithUser(loginResult.getSuccess());
//            }
//            setResult(Activity.RESULT_OK);

        //Complete and destroy login activity once successful
//            finish();
//        });

//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        };
//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//            return false;
//        });

        registrationButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, ChooseTimePlaceActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

    }
}
