package com.example.biank.meuapp.Activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biank.meuapp.R;

public class help extends Fragment {
TextView ajuda, item1, item2, item3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_help,container,false);

        ajuda= (TextView)v.findViewById(R.id.txtajuda);
        item1= (TextView)v.findViewById(R.id.item1);
        item2= (TextView)v.findViewById(R.id.item2);
        item3= (TextView)v.findViewById(R.id.item3);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Ajuda");
    }
}
