package com.givekesh.baboon.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.givekesh.baboon.R;

public class MainMenu extends Fragment {

    private Interfaces.OnNavClickListener onNavClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onNavClickListener = (Interfaces.OnNavClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_menu, container, false);

        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavClickListener.onSelect(item);
                return true;
            }
        });
        return view;
    }
}
