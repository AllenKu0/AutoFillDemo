package com.example.autofillex.SavaAccount;

import android.content.Context;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SaveAccountViewModelFactory implements ViewModelProvider.Factory {
    private Context context;
    public SaveAccountViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(SavaAccountViewModel.class)){
            return (T) new SavaAccountViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
