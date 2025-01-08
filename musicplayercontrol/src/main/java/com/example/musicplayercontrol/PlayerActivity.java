package com.example.musicplayercontrol;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
public class PlayerActivity extends AppCompatActivity {

    private PlayerFragment playerFragment;
    private PreferencesUtility preferencesUtility;
    private BottomSheetBehavior<View> behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        preferencesUtility = PreferencesUtility.getInstance(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new MusicAdapter(MusicInfo.getTestData()));
        // 初始化 Fragment
        if (savedInstanceState == null) {
            playerFragment = new PlayerFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .commit();
        }

        // 设置 BottomSheet
        View bottomSheet = findViewById(R.id.fragment_container);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.player_min_height));


        // 设置点击事件来展开 BottomSheet
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 只有在当前状态是 COLLAPSED 时才展开
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged( View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    preferencesUtility.setPlayerState(true);
                    if (playerFragment != null) {
                        playerFragment.onExpanded();
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    preferencesUtility.setPlayerState(false);
                    if (playerFragment != null) {
                        playerFragment.onCollapsed();
                    }
                }
            }

            @Override
            public void onSlide( View view, float slideOffset) {
                playerFragment.onSlide(slideOffset);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}