package com.example.autofillex.SavaAccount;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.autofillex.BaseActivity;
import com.example.autofillex.DataBase.AccountEntity;
import com.example.autofillex.MainActivity;
import com.example.autofillex.R;
import com.example.autofillex.databinding.ActivitySaveAcconutBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SaveAccountActivity extends BaseActivity {

    private ActivitySaveAcconutBinding activitySaveAcconutBinding;
    private SaveAccountAdapter adapter;
    private SavaAccountViewModel savaAccountViewModel;
    private SaveAccountViewModelFactory factory;
    @RequiresApi(api = 33)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_acconut);

        adapter = new SaveAccountAdapter();
        factory = new SaveAccountViewModelFactory(this);
        savaAccountViewModel = ViewModelProviders.of(this,factory).get(SavaAccountViewModel.class);

        activitySaveAcconutBinding = ActivitySaveAcconutBinding.inflate(getLayoutInflater());


        setContentView(activitySaveAcconutBinding.getRoot());
        activitySaveAcconutBinding.accountRcy.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        activitySaveAcconutBinding.accountRcy.setLayoutManager(new LinearLayoutManager(this));
        activitySaveAcconutBinding.accountRcy.setAdapter(adapter);

        activitySaveAcconutBinding.addBtn.setOnClickListener(this::goToNewAccountActivity);
        savaAccountViewModel.accountList.observe(this, new Observer<List<AccountEntity>>() {
            @Override
            public void onChanged(List<AccountEntity> accountEntities) {
                Log.e("TAG", "onChanged: 更新" );
                adapter.setAccountList(accountEntities);
            }
        });
        savaAccountViewModel.isProgressShow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    showDialog("資料庫更新中");
                }else{
                    dismissDialog();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        savaAccountViewModel.getAccount();
    }

    private void goToNewAccountActivity(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}