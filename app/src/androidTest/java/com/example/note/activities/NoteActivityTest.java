package com.example.note.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.note.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class NoteActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testSaveNoteIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.save_note)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddTitleToNote() {
        String title = "Title";

        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteTitleEditText)).perform(typeText(title));

        onView(withId(R.id.save_note)).perform(click());

        onView(withText(title)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateDateTimeIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textDateTime)).check(matches(isDisplayed()));
    }

    @Test
    public void testTitleIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteTitleEditText)).check(matches(isDisplayed()));
    }

    @Test
    public void testContentIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteContentEditText)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddContentToNote() {
        String content = "Content";

        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.noteContentEditText)).perform(typeText(content));

        onView(withId(R.id.save_note)).perform(click());

        onView(withText(content)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddUrlToNote() {
        String URL = "https://www.google.com/";

        onView(withId(R.id.add_button)).perform(click());

        onView(withText(R.string.options)).perform(click());
        onView(withText(R.string.add_url)).perform(click());
        onView(withId(R.id.inputURL)).perform(typeText(URL));
        onView(withId(R.id.textAdd)).perform(click());

        onView(withId(R.id.save_note)).perform(click());
        onView(withId(R.id.layoutWebURL)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreatedDateTimeIsCorrect() {
        LocalDateTime localDateTime = LocalDateTime.now();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - E, dd MMMM yyyy");
        String currentDateTime = formatter.format(localDateTime);

        onView(withId(R.id.add_button)).perform(click());

        onView(withText(currentDateTime)).check(matches(isDisplayed()));
    }

    @Test
    public void testOptionsAreDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddImageIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.layoutAddImage)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddUrlIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.layoutAddUrl)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddNotificationIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.layoutAddNotification)).check(matches(isDisplayed()));
    }

    @Test
    public void testDefaultColorIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.viewColorDefault)).check(matches(isDisplayed()));
    }

    @Test
    public void testYellowColorIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.viewColorYellow)).check(matches(isDisplayed()));
    }

    @Test
    public void testRedColorIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.viewColorRed)).check(matches(isDisplayed()));
    }

    @Test
    public void testBlueColorIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.viewColorBlue)).check(matches(isDisplayed()));
    }

    @Test
    public void testBlackColorIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.textOptions)).perform(click());
        onView(withId(R.id.viewColorBlack)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteSingleNoteIsDisplayed() {
        onView(withId(R.id.add_button)).perform(click());

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_single)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteSingleNote() {
        onView(withId(R.id.add_button)).perform(click());

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.delete_single)).perform(click());
        onView(withText(R.string.delete_dialog_submit)).perform(click());
    }
}