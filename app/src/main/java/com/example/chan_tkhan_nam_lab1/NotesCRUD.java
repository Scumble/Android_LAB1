package com.example.chan_tkhan_nam_lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chan_tkhan_nam_lab1.Models.Notes;
import com.example.chan_tkhan_nam_lab1.Services.DataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class NotesCRUD extends AppCompatActivity {
    EditText edtNoteName;
    EditText edtNoteText;
    Spinner edtNotePriority;
    ImageView edtNoteImage;
    Button btnSave;
    Button btnImage;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_crud);


        edtNoteName = findViewById(R.id.edtNoteName);
        edtNoteText = findViewById(R.id.edtText);
        edtNotePriority = findViewById(R.id.edtPrioritySpinner);
        edtNoteImage = findViewById(R.id.edtImage);
        btnSave = findViewById(R.id.btnSave);
        btnImage = findViewById(R.id.btnImage);

        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(getResources().getStringArray(R.array.priority)));
        list.remove(0);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(NotesCRUD.this, android.R.layout.simple_list_item_1, list);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtNotePriority.setAdapter(priorityAdapter);

        Bundle extras = getIntent().getExtras();
        final String noteId = extras.getString("noteId");
        String noteName = extras.getString("noteName");
        String noteText = extras.getString("noteText");
        String notePriority = extras.getString("priority");
        String image = extras.getString("image");

        edtNoteName.setText(noteName);
        edtNoteText.setText(noteText);
        edtNotePriority.setSelection(list.indexOf(notePriority));
        edtNoteImage.setImageURI(Uri.parse(image));
        selectedImageUri = Uri.parse(image);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notes note = new Notes();
                note.setName(edtNoteName.getText().toString());
                note.setText(edtNoteText.getText().toString());
                note.setDateTime(Calendar.getInstance().getTime());
                note.setPriority(edtNotePriority.getSelectedItem().toString());
                note.setImage(selectedImageUri.toString());
                if(note.getName().equals("") || note.getText().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.warning, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    if (noteId != null && noteId.trim().length() > 0) {
                        note.setNotesId(Integer.parseInt(noteId));
                        UpdateNote(note);
                    } else {
                        AddNote(note);
                    }
                    finish();
                }
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    selectedImageUri = data.getData();
                    final String path = getPathFromURI(selectedImageUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        this.getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    edtNoteImage.setImageURI(selectedImageUri);
                }
            }
        } catch (Exception ex) {
            Log.e("NotesCRUD", "File select error", ex);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if ((cursor).moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void AddNote(Notes note){
        DataProvider dataProvider = new DataProvider(this);
        dataProvider.AddNote(note);
    }

    public void UpdateNote(Notes note) {
        DataProvider dataProvider = new DataProvider(this);
        dataProvider.UpdateNote(note);
    }
}
