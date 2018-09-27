package com.kash.kashsoft.ui.activities.intro;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class AppIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonCtaVisible(true);
        setButtonNextVisible(false);
        setButtonBackVisible(false);

        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title("MX Player(MP3)")
                .description(com.kash.kashsoft.R.string.welcome_to_phonograph)
                .image(com.kash.kashsoft.R.drawable.icon_web)
                .background(com.kash.kashsoft.R.color.md_blue_grey_100)
                .backgroundDark(com.kash.kashsoft.R.color.md_blue_grey_200)
                .layout(com.kash.kashsoft.R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(com.kash.kashsoft.R.string.label_playing_queue)
                .description(com.kash.kashsoft.R.string.open_playing_queue_instruction)
                .image(com.kash.kashsoft.R.drawable.tutorial_queue_swipe_up)
                .background(com.kash.kashsoft.R.color.lime)
                .backgroundDark(com.kash.kashsoft.R.color.lime)
                .layout(com.kash.kashsoft.R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(com.kash.kashsoft.R.string.label_playing_queue)
                .description(com.kash.kashsoft.R.string.rearrange_playing_queue_instruction)
                .image(com.kash.kashsoft.R.drawable.tutorial_rearrange_queue)
                .background(com.kash.kashsoft.R.color.lime)
                .backgroundDark(com.kash.kashsoft.R.color.lime)
                .layout(com.kash.kashsoft.R.layout.fragment_simple_slide_large_image)
                .build());
    }
}
