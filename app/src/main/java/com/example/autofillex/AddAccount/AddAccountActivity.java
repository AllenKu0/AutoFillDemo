package com.example.autofillex.AddAccount;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.autofillex.base.BaseActivity;
import com.example.autofillex.DataBase.AccountDataBase;
import com.example.autofillex.DataBase.AccountEntity;
import com.example.autofillex.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialsClient;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddAccountActivity extends BaseActivity {

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
    }

    //Onclick
    private void insertData(View view){
        String username = activityMainBinding.usernameEt.getText().toString();
        String password = activityMainBinding.passwordEt.getText().toString();
        AccountEntity accountEntity = new AccountEntity(username,password);
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
                        showToast(e.getMessage());
                        Log.e("TAG", "onError: "+e.getMessage() );
                    }
                });
    }
}
