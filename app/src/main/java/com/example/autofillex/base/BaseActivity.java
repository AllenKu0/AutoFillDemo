package com.example.autofillex.base;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autofillex.R;
import com.example.autofillex.databinding.CustomDialogBinding;

public abstract class BaseActivity extends AppCompatActivity {
    private CustomDialogBinding customDialogBinding;
    private AlertDialog alertDialog ;


    protected void showDialog(String hint) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        customDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this));
        customDialogBinding.hintTxt.setText(hint);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertDialog = alertBuilder.setCancelable(false)
                .setView(view)
                .create();
        alertDialog.show();

    }

    protected void dismissDialog() {
        if (alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    protected void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
