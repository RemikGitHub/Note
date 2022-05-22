package com.example.note.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.note.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testDeleteAllNotesEmptyImage() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_all)).perform(click());
        onView(withId(R.id.textDeleteNote)).perform(click());

        onView(withId(R.id.empty_image)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDeleteAllNotesEmptyText() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_all)).perform(click());
        onView(withId(R.id.textDeleteNote)).perform(click());

        onView(withId(R.id.empty_text)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.empty_text)).check(matches(withText(R.string.empty)));
    }

    @Test
    public void testNoteIsNotDisplayed() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_all)).perform(click());
        onView(withId(R.id.textDeleteNote)).perform(click());

        onView(withId(R.id.cardNote)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testDeleteAllNotesIsDisplayed() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_all)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddSingleNote() {
        String title = "Title";
        String content = "Content";

        onView(withId(R.id.add_button)).perform(click());

        onView(withId(R.id.noteTitleEditText)).perform(typeText(title));
        onView(withId(R.id.noteContentEditText)).perform(typeText(content));

        onView(withId(R.id.save_note)).perform(click());

        pressBack();

        onView(withText(title)).check(matches(isDisplayed()));
        onView(withText(content)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchInputIsVisible() {
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()));
        onView(withId(R.id.searchInput)).check(matches(withHint(R.string.search_input_hint)));
    }

    @Test
    public void testSearchIconIsVisible() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddNoteButtonIsVisible() {
        onView(withId(R.id.add_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdIsDisplayed() {
        onView(withId(R.id.ad_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchNoteByTitle() {
        String title = "Searched title";

        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteTitleEditText)).perform(typeText(title));
        onView(withId(R.id.save_note)).perform(click());

        pressBack();

        onView(withId(R.id.searchInput)).perform(typeText(title));
        onView(withText(title)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchNoteByContent() {
        String content = "Searched content";

        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteContentEditText)).perform(typeText(content));
        onView(withId(R.id.save_note)).perform(click());

        pressBack();

        onView(withId(R.id.searchInput)).perform(typeText(content));
        onView(withText(content)).check(matches(isDisplayed()));
    }
}