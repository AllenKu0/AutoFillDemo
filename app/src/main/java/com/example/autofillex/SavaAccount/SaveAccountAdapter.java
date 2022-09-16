package com.example.autofillex.SavaAccount;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autofillex.Account;
import com.example.autofillex.DataBase.AccountEntity;
import com.example.autofillex.R;
import com.example.autofillex.databinding.SaveAccountListBinding;

import java.util.ArrayList;
import java.util.List;

public class SaveAccountAdapter extends RecyclerView.Adapter<SaveAccountAdapter.ViewHolder> {
    private List<AccountEntity> accountList = new ArrayList<>();
    private SaveAccountListBinding saveAccountListBinding;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.save_account_list,parent,false);
        saveAccountListBinding = SaveAccountListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(saveAccountListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("TAG", "onBindViewHolder: "+accountList.get(position).getName() );
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public void setAccountList(List<AccountEntity> accountList){
        this.accountList = accountList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void bind(int position){
            Log.e("TAG", "rrrr: "+accountList.get(position).getName() );
            saveAccountListBinding.accountTxt.setText(accountList.get(position).getName());
            saveAccountListBinding.passwordTxt.setText(accountList.get(position).getPassword());
        }
    }
}
