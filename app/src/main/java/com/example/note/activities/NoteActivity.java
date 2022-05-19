package com.example.note.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.note.R;
import com.example.note.database.NoteDatabase;
import com.example.note.databinding.ActivityNoteBinding;
import com.example.note.entities.Note;
import com.example.note.notification.AlarmReceiver;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {

    private static final String COLOR_DEFAULT = "#444444";
    private static final String COLOR_YELLOW = "#FDBE3B";
    private static final String COLOR_RED = "#9B2335";
    private static final String COLOR_BLUE = "#34568B";
    private static final String COLOR_BLACK = "#000000";

    private boolean isNewNote;
    private Note note;

    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private TextView noteCreationDateTime;
    private View titleIndicator;
    private ImageView imageView;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private String selectedNoteColor;
    private String selectedImagePath;
    private String writtenWebUrl;

    private Calendar calendar;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    selectImage();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> activityResultLauncherImageActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        if (result.getData() != null) {
                            this.selectedImagePath = result.getData().getData().getLastPathSegment();
                            Picasso.get().load(new File(this.selectedImagePath)).into(imageView);

                            imageView.setVisibility(View.VISIBLE);
                            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        }
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        com.example.note.databinding.ActivityNoteBinding binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        this.noteTitleEditText = findViewById(R.id.noteTitleEditText);
        this.noteContentEditText = findViewById(R.id.noteContentEditText);
        this.noteCreationDateTime = findViewById(R.id.textDateTime);
        this.titleIndicator = findViewById(R.id.titleIndicator);
        this.imageView = findViewById(R.id.imageNote);
        this.textWebURL = findViewById(R.id.textWebURL);
        this.layoutWebURL = findViewById(R.id.layoutWebURL);
        this.isNewNote = getIntent().getBooleanExtra("isNewNote", true);

        findViewById(R.id.imageRemoveImage).setOnClickListener(v -> {
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
            selectedImagePath = "";
        });

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(v -> {
            textWebURL.setText(null);
            layoutWebURL.setVisibility(View.GONE);
            this.writtenWebUrl = "";
        });

        setupActivity();
        initOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.save_note) {
            saveNote();

            return true;
        }
        if (itemId == R.id.action_settings) {
            confirmDeleteDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void setupActivity() {
        if (!this.isNewNote) {

            this.note = (Note) getIntent().getSerializableExtra("note");
            this.noteTitleEditText.setText(note.getTitle());
            this.noteContentEditText.setText(note.getContent());
            this.noteCreationDateTime.setText(note.getCreationDateTime());
            this.selectedNoteColor = note.getColor();
            this.selectedImagePath = note.getImagePath();
            this.writtenWebUrl = note.getWebLink();

            if (this.selectedImagePath != null && !this.selectedImagePath.trim().isEmpty()) {
                Picasso.get().load(new File(this.selectedImagePath)).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            }

            if (this.writtenWebUrl != null && !this.writtenWebUrl.trim().isEmpty()) {
                textWebURL.setText(this.writtenWebUrl);
                layoutWebURL.setVisibility(View.VISIBLE);
            }

        } else {
            this.note = new Note();
            this.selectedNoteColor = COLOR_DEFAULT;
            this.selectedImagePath = "";
            this.writtenWebUrl = "";
            this.noteCreationDateTime.setText(new SimpleDateFormat(
                    "HH:mm - EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date().getTime())
            );
        }
    }

    private void confirmDeleteDialog() {
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.layout_delete_note_dialog, findViewById(R.id.layoutDeleteNoteContainer));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);

        AlertDialog dialogDeleteNote = builder.create();

        if (dialogDeleteNote.getWindow() != null) {
            dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialogLayout.findViewById(R.id.textDeleteNote).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (this.isNewNote) {
                setResult(MainActivity.REQUEST_CODE_DELETE_NEW_NOTE, intent);
                dialogDeleteNote.dismiss();
                finish();
            } else {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteNote(note);

                    handler.post(() -> {
                        setResult(MainActivity.REQUEST_CODE_DELETE_NOTE, intent);
                        dialogDeleteNote.dismiss();
                        finish();
                    });
                });
            }
        });
        dialogLayout.findViewById(R.id.deleteNoteCancel).setOnClickListener(v -> dialogDeleteNote.dismiss());

        dialogDeleteNote.show();
    }

    private void saveNote() {
        final String noteTitle = this.noteTitleEditText.getText().toString();
        final String noteText = this.noteContentEditText.getText().toString();
        final String noteDateTime = this.noteCreationDateTime.getText().toString();

        this.note.setTitle(noteTitle);
        this.note.setContent(noteText);
        this.note.setCreationDateTime(noteDateTime);
        this.note.setColor(this.selectedNoteColor);
        this.note.setImagePath(this.selectedImagePath);
        this.note.setWebLink(this.writtenWebUrl);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            long newId = NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().insertNote(note);

            handler.post(() -> {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                if (isNewNote) {
                    setResult(MainActivity.REQUEST_CODE_ADD_NOTE, intent);
                    note.setId(newId);
                } else {
                    setResult(MainActivity.REQUEST_CODE_UPDATE_NOTE, intent);
                }
            });
        });
    }

    private void setTitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) this.titleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(this.selectedNoteColor));
    }

    private void setImageViewsColor(String color, int chosenColor) {
        this.selectedNoteColor = color;
        final LinearLayout layoutOptions = findViewById(R.id.layoutOptions);

        final ImageView imageColorDefault = layoutOptions.findViewById(R.id.imageColorDefault);
        final ImageView imageColorYellow = layoutOptions.findViewById(R.id.imageColorYellow);
        final ImageView imageColorRed = layoutOptions.findViewById(R.id.imageColorRed);
        final ImageView imageColorBlue = layoutOptions.findViewById(R.id.imageColorBlue);
        final ImageView imageColorBlack = layoutOptions.findViewById(R.id.imageColorBlack);

        imageColorDefault.setImageResource(0);
        imageColorYellow.setImageResource(0);
        imageColorRed.setImageResource(0);
        imageColorBlue.setImageResource(0);
        imageColorBlack.setImageResource(0);

        switch (chosenColor) {
            case 0: {
                imageColorDefault.setImageResource(R.drawable.ic_done);
                break;
            }
            case 1: {
                imageColorYellow.setImageResource(R.drawable.ic_done);
                break;
            }
            case 2: {
                imageColorRed.setImageResource(R.drawable.ic_done);
                break;
            }
            case 3: {
                imageColorBlue.setImageResource(R.drawable.ic_done);
                break;
            }
            case 4: {
                imageColorBlack.setImageResource(R.drawable.ic_done);
                break;
            }
        }
        setTitleIndicatorColor();
    }

    private void initOptions() {
        final LinearLayout layoutOptions = findViewById(R.id.layoutOptions);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutOptions);

        final View viewColorDefault = layoutOptions.findViewById(R.id.viewColorDefault);
        final View viewColorYellow = layoutOptions.findViewById(R.id.viewColorYellow);
        final View viewColorRed = layoutOptions.findViewById(R.id.viewColorRed);
        final View viewColorBlue = layoutOptions.findViewById(R.id.viewColorBlue);
        final View viewColorBlack = layoutOptions.findViewById(R.id.viewColorBlack);

        layoutOptions.findViewById(R.id.textOptions).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        viewColorDefault.setOnClickListener(v -> setImageViewsColor(COLOR_DEFAULT, 0));
        viewColorYellow.setOnClickListener(v -> setImageViewsColor(COLOR_YELLOW, 1));
        viewColorRed.setOnClickListener(v -> setImageViewsColor(COLOR_RED, 2));
        viewColorBlue.setOnClickListener(v -> setImageViewsColor(COLOR_BLUE, 3));
        viewColorBlack.setOnClickListener(v -> setImageViewsColor(COLOR_BLACK, 4));

        switch (this.selectedNoteColor) {
            case COLOR_DEFAULT:
                viewColorDefault.performClick();
                break;
            case COLOR_YELLOW:
                viewColorYellow.performClick();
                break;
            case COLOR_RED:
                viewColorRed.performClick();
                break;
            case COLOR_BLUE:
                viewColorBlue.performClick();
                break;
            case COLOR_BLACK:
                viewColorBlack.performClick();
                break;
        }


        layoutOptions.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        });

        layoutOptions.findViewById(R.id.layoutAddUrl).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });

        layoutOptions.findViewById(R.id.layoutAddNotification).setOnClickListener(v -> showDatePicker());
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            this.activityResultLauncherImageActivity.launch(intent);
        }
    }

    private void showAddURLDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url_dialog, findViewById(R.id.layoutAddUrlContainer));
        builder.setView(view);

        AlertDialog dialogAddUrl = builder.create();
        if (dialogAddUrl.getWindow() != null) {
            dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        final EditText inputUrl = view.findViewById(R.id.inputURL);
        inputUrl.requestFocus();

        view.findViewById(R.id.textAdd).setOnClickListener(v -> {
            final String inputURLStr = inputUrl.getText().toString().trim();

            if (inputURLStr.isEmpty()) {
                Toast.makeText(NoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.WEB_URL.matcher(inputURLStr).matches()) {
                Toast.makeText(NoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
            } else {
                this.writtenWebUrl = inputUrl.getText().toString();
                textWebURL.setText(this.writtenWebUrl);
                layoutWebURL.setVisibility(View.VISIBLE);

                dialogAddUrl.dismiss();
            }
        });

        view.findViewById(R.id.addUrlCancel).setOnClickListener(v -> dialogAddUrl.dismiss());

        dialogAddUrl.show();
    }

    private void showDatePicker() {
        calendar = Calendar.getInstance();
        final DatePickerDialog StartTime = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            showTimePicker();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        StartTime.show();
    }

    private void showTimePicker() {
        final MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select notification time")
                .build();

        timePicker.show(getSupportFragmentManager(), "noteNotification");

        timePicker.addOnPositiveButtonClickListener(v -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            setAlarm();
        });
    }

    private void setAlarm() {
        AlarmReceiver.setNotificationTitle(note.getTitle());
        AlarmReceiver.setNotificationContent(note.getContent());

        Intent intent = new Intent(NoteActivity.this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(NoteActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "noteNotificationReminderChannel";
            String description = "Channel for note notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("noteNotification", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}