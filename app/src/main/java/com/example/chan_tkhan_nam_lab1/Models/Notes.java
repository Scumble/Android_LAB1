package com.example.chan_tkhan_nam_lab1.Models;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notes {
    @SerializedName("noteId")
    public int NotesId;
    @SerializedName("name")
    public String Name;
    @SerializedName("text")
    public String Text;
    @SerializedName("dateTime")
    public Date DateTime;
    @SerializedName("priority")
    public String Priority;
    @SerializedName("image")
    public String Image;

    public Notes(int notesId, String name, String text, Date dateTime, String priority, String image) {
        NotesId = notesId;
        Name = name;
        Text = text;
        DateTime = dateTime;
        Priority = priority;
        Image = image;
    }

    public Notes() {
    }

    public int getNotesId() {
        return NotesId;
    }

    public void setNotesId(int notesId) {
        NotesId = notesId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public Date getDateTime() {
        return DateTime;
    }

    public void setDateTime(Date dateTime) {
        DateTime = dateTime;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}