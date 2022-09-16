package com.example.autofillex;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autofillex.databinding.CustomDialogBinding;

public abstract class BaseActivity extends AppCompatActivity {
    private CustomDialogBinding customDialogBinding;
    private AlertDialog alertDialog ;


    public void showDialog(String hint) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        customDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this));
        customDialogBinding.hintTxt.setText(hint);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertDialog = alertBuilder.setCancelable(false)
                .setView(view)
                .create();
        alertDialog.show();

    }

    public void dismissDialog() {
        if (alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

}
