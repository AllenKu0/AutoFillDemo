package com.example.autofillex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.autofillex.DataBase.AccountDataBase;
import com.example.autofillex.DataBase.AccountEntity;
import com.example.autofillex.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.cert.Certificate;

import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CredentialsClient credentialsClient;
    private CredentialRequest credentialRequest;
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.insertBtn.setOnClickListener(this::insertData);
        credentialsClient = Credentials.getClient(this);

        credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE, IdentityProviders.FACEBOOK)
                .build();

        credentialsClient.request(credentialRequest).addOnCompleteListener(
                new OnCompleteListener<CredentialRequestResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<CredentialRequestResponse> task) {
                        Log.e("TAG", "onComplete: 1" );
                        if (task.isSuccessful()) {
                            // See "Handle successful credential requests"
                            Log.e("TAG", "onComplete: 2e" );
                            onCredentialRetrieved(task.getResult().getCredential());
                            return;
                        }

                        // See "Handle unsuccessful and incomplete credential requests"
                        // ...
                    }
                });
    }

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            // Sign the user in with information from the Credential.
            Log.e("TAG", "accountType: null" );
            signInWithPassword(credential.getId(), credential.getPassword());
        } else if (accountType.equals(IdentityProviders.GOOGLE)) {
            // The user has previously signed in with Google Sign-In. Silently
            // sign in the user with the same ID.
            // See https://developers.google.com/identity/sign-in/android/
            Log.e("TAG", "accountType: google" );
            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();

            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
            Task<GoogleSignInAccount> task = signInClient.silentSignIn();
            // ...
        }
    }

    private void signInWithPassword(String id, String password) {
        Log.e("TAG", "signInWithPassword: id:"+id+" ,password: "+password);
    }
    //Onclick
    private void insertData(View view){
        String user = activityMainBinding.usernameEt.getText().toString();
        String password = activityMainBinding.passwordEt.getText().toString();
        AccountEntity accountEntity = new AccountEntity(user,password);
        AccountDataBase.getInstance(this).getDataDao().insertData(accountEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("TAG", "onSubscribe: " );
                    }

                    @Override
                    public void onComplete() {
                        finish();
                        Log.e("TAG", "onComplete: " );
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: "+e.getMessage() );
                    }
                });
    }
}
