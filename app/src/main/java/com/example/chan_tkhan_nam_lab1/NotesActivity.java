package com.example.chan_tkhan_nam_lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.chan_tkhan_nam_lab1.Adapters.NotesAdapter;
import com.example.chan_tkhan_nam_lab1.Models.Notes;
import com.example.chan_tkhan_nam_lab1.Services.DataProvider;

import java.util.ArrayList;
import java.util.Calendar;

public class NotesActivity extends AppCompatActivity {
    ListView listView;
    NotesAdapter adapter;
    DataProvider dataProvider = new DataProvider(NotesActivity.this);
    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listView = findViewById(R.id.listView);
        ArrayList<Notes> notesList = dataProvider.getNotes();
        adapter = new NotesAdapter(NotesActivity.this, R.layout.notes_list, notesList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
                final int itemToDelete = position;
                new AlertDialog.Builder(NotesActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.del)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    dataProvider.DeleteNote(adapter.getPos(itemToDelete).getNotesId());
                                    updateSearchListAfterDelete(adapter.getPos(itemToDelete).getNotesId());
                                    adapter.clear();
                                    adapter.addAll(adapter.searchedNotesList);
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).setNegativeButton(R.string.no, null).show();
                return true;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        ArrayList<Notes> notesList = dataProvider.getNotes();
        adapter = new NotesAdapter(NotesActivity.this, R.layout.notes_list, notesList);
        listView.setAdapter(adapter);
    }

    public void updateSearchListAfterDelete(int itemToDelete) {
        ArrayList<Notes> notes = new ArrayList<>(adapter.searchedNotesList);
        for(Notes note: notes) {
            if(note.getNotesId() == itemToDelete) {
                adapter.searchedNotesList.remove(note);
                adapter.notesList.remove(note);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    public void filterNotesByImportance(String importance, String allFilter) {
        ArrayList<Notes> notesList = dataProvider.getNotes();
        ArrayList<Notes> notes = new ArrayList<>();
        adapter = new NotesAdapter(NotesActivity.this, R.layout.notes_list, notesList);
        if(importance.equals(allFilter)) {
            notes.addAll(adapter.searchedNotesList);
        }
        for (Notes note : adapter.searchedNotesList) {
            if(note.getPriority().equals(importance)) {
                notes.add(note);
            }
        }
        adapter = new NotesAdapter(NotesActivity.this, R.layout.notes_list, notes);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.add:
            Intent intent = new Intent(NotesActivity.this, NotesCRUD.class);
            intent.putExtra("noteId", "");
            intent.putExtra("noteName", "");
            intent.putExtra("noteText", "");
            intent.putExtra("dateTime", Calendar.getInstance().getTime());
            intent.putExtra("priority", "");
            intent.putExtra("image", "");
            startActivity(intent);
            break;
        case R.id.search:
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    adapter.getFilter().filter(searchText);
                    return false;
                }
            });
            break;
        case R.id.menu_spinner:
             final Spinner spinner = (Spinner) item.getActionView();
             final String[] filterList = getResources().getStringArray(R.array.priority);
             final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(NotesActivity.this,
                     android.R.layout.simple_spinner_item, filterList);
             adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             spinner.setAdapter(adapterSpinner);

             spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                         if(searchText.equals("")) {
                             filterNotesByImportance(spinner.getSelectedItem().toString(), filterList[0]);
                         }
                         else {
                             filterNotesByImportance(spinner.getSelectedItem().toString(), filterList[0]);
                             adapter.getFilter().filter(searchText);
                         }
                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {

                 }
             });
             break;
        }

        return(super.onOptionsItemSelected(item));
    }
}
