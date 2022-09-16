package com.example.autofillex.SavaAccount;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ShowAccountViewModelFactory implements ViewModelProvider.Factory {
    private Context context;
    public ShowAccountViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ShowAccountViewModel.class)){
            return (T) new ShowAccountViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
