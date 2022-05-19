package com.example.note.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.note.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Test
    public void testActivityInView() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.layout.activity_main)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyListIsVisible() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.empty_text)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.searchInput)).check(matches(withText(R.string.empty)));

        onView(withId(R.id.empty_image)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testSearchInput() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.searchInput)).check(matches(isDisplayed()));
        onView(withId(R.id.searchInput)).check(matches(withHint(R.string.search_input_hint)));
    }

}