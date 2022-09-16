package com.example.autofillex.SavaAccount;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autofillex.DataBase.AccountDataBase;
import com.example.autofillex.DataBase.AccountEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SavaAccountViewModel extends ViewModel {
    MutableLiveData<List<AccountEntity>> accountList = new MutableLiveData<>();
    MutableLiveData<Boolean> isProgressShow = new MutableLiveData<>();
    private Context context;
    public SavaAccountViewModel(Context context){
        this.context = context;
    }
    void insetAccount(AccountEntity accountEntity){
        AccountDataBase.getInstance(context).getDataDao().insertData(accountEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isProgressShow.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: 新增成功");
                        isProgressShow.postValue(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: 新增失敗");
                        isProgressShow.postValue(false);
                    }
                });
    }

    void getAccount(){
        AccountDataBase.getInstance(context).getDataDao().displayAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<AccountEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("TAG", "onComplete: " );
                        isProgressShow.postValue(true);
                    }

                    @Override
                    public void onSuccess(@NonNull List<AccountEntity> accountEntities) {
                        Log.e("TAG", "onSuccess: " );
                        accountList.postValue(accountEntities);
                        isProgressShow.postValue(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: "+e.getMessage() );
                        isProgressShow.postValue(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: " );
                        isProgressShow.postValue(false);
                    }
                });
    }
}
