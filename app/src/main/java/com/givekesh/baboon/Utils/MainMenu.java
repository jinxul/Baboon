package com.givekesh.baboon.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
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

        final NavigationView navigationView = view.findViewById(R.id.nav_view);
        final int[] ids = {R.id.angular, R.id.vagrant, R.id.laravel, R.id.jq, R.id.php,
                R.id.bootstrap, R.id.ruby_on_rails, R.id.express, R.id.symfony};
        final Menu menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.more_categories || item.getItemId() == R.id.less_categories) {
                    boolean visibility = item.getItemId() == R.id.more_categories;
                    for (int id : ids)
                        menu.findItem(id).setVisible(visibility);
                    menu.findItem(R.id.more_categories).setVisible(!visibility);
                    menu.findItem(R.id.less_categories).setVisible(visibility);
                } else
                    onNavClickListener.onSelect(item);
                return true;
            }
        });
        return view;
    }
}
