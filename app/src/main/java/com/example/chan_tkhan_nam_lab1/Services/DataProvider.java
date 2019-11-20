package com.example.chan_tkhan_nam_lab1.Services;

import android.content.Context;
import android.util.Log;

import com.example.chan_tkhan_nam_lab1.Models.Notes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class DataProvider implements IDataProvider{
    private Context context;
    public DataProvider(Context context) {
        this.context = context;
    }

    private String readJSONFromFile() {
        String json = "";
        InputStream inputStream = null;
        try {
            inputStream = context.openFileInput("data.json");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                json = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("data provider", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("data provider", "Can not read file: " + e.toString());
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private void WriteJSONToFile(String json) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public ArrayList<Notes> getNotes() {
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ArrayList<Notes> notes = new ArrayList<>();
        try {
            notes = gson.fromJson(readJSONFromFile(), new TypeToken<ArrayList<Notes>>() {
            }.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return notes;
    }

    public void AddNote(Notes note) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ArrayList<Notes> notes = new ArrayList<>(getNotes());
        note.setNotesId(new Random().nextInt(50)+1);
        notes.add(note);
        WriteJSONToFile(gson.toJson(notes));
    }

   public void UpdateNote(Notes note) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ArrayList<Notes> notes = new ArrayList<>(getNotes());
        for (Notes n : notes) {
            if(n.NotesId == note.NotesId) {
                n.setName(note.Name);
                n.setText(note.Text);
                n.setPriority(note.Priority);
                n.setDateTime(note.DateTime);
                n.setImage(note.Image);
            }
        }
        WriteJSONToFile(gson.toJson(notes));
    }

    public void DeleteNote(int id) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ArrayList<Notes> notes = new ArrayList<>(getNotes());
        for(int i=0;i<notes.size();i++) {
            if(notes.get(i).getNotesId() == id) {
                notes.remove(i);
            }
        }
        WriteJSONToFile(gson.toJson(notes));
    }
}
