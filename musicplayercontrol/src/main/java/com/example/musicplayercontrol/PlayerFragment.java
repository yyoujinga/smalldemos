package com.example.musicplayercontrol;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.Nullable;

import java.util.List;
public class PlayerFragment extends Fragment {

    private View rootView;
    private View miniPlayer;
    private View fullPlayer;
    private ImageView albumArtMini;
    private ImageView albumArtFull;
    private TextView titleMini;
    private TextView titleFull;
    private ImageButton playPauseButton;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isPrepared = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        initViews();
        initdata();
        setupListeners();
        return rootView;
    }

    private void initdata() {

    }

    private void initViews() {
        miniPlayer = rootView.findViewById(R.id.mini_player);
        fullPlayer = rootView.findViewById(R.id.full_player);
        albumArtMini = rootView.findViewById(R.id.album_art_mini);
        albumArtFull = rootView.findViewById(R.id.album_art_full);
        titleMini = rootView.findViewById(R.id.title_mini);
        titleFull = rootView.findViewById(R.id.title_full);
        playPauseButton = rootView.findViewById(R.id.play_pause_button);
        seekBar = rootView.findViewById(R.id.seek_bar);

        // 初始状态
        fullPlayer.setVisibility(View.GONE);
        fullPlayer.setAlpha(0);
        miniPlayer.setVisibility(View.VISIBLE);
        miniPlayer.setAlpha(1);
    }

    private void setupListeners() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void onExpanded() {
        miniPlayer.animate()
                .alpha(0)
                .setDuration(200)
                .withEndAction(() -> miniPlayer.setVisibility(View.GONE));

        fullPlayer.setVisibility(View.VISIBLE);
        fullPlayer.animate()
                .alpha(1)
                .setDuration(200);
    }

    public void onCollapsed() {
        fullPlayer.animate()
                .alpha(0)
                .setDuration(200)
                .withEndAction(() -> fullPlayer.setVisibility(View.GONE));

        miniPlayer.setVisibility(View.VISIBLE);
        miniPlayer.animate()
                .alpha(1)
                .setDuration(200);
    }

    public void onSlide(float slideOffset) {
        miniPlayer.setAlpha(1 - slideOffset);
        fullPlayer.setAlpha(slideOffset);

        if (slideOffset > 0 && fullPlayer.getVisibility() == View.GONE) {
            fullPlayer.setVisibility(View.VISIBLE);
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            }
            isPlaying = !isPlaying;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}