package com.stbamb.musicplayer.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.stbamb.musicplayer.R;
import com.stbamb.musicplayer.fragments.TopSongsFragment;
import com.stbamb.musicplayer.fragments.TopAlbumsFragment;
import com.stbamb.musicplayer.adapters.ViewPagerAdapter;

/*
The whole ViewPager and Tabs stuff was based on this tutorial:
http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
and adapted to the necessities of this project
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Setting the two fragments to the tabs
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TopSongsFragment(), "Top Songs");
        adapter.addFragment(new TopAlbumsFragment(), "Top Albums");
        viewPager.setAdapter(adapter);
    }
}