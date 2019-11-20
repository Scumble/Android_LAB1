package com.example.chan_tkhan_nam_lab1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.example.chan_tkhan_nam_lab1.Models.Notes;
import com.example.chan_tkhan_nam_lab1.NotesCRUD;
import com.example.chan_tkhan_nam_lab1.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotesAdapter extends ArrayAdapter<Notes> {
    private Context context;
    public ArrayList<Notes> notesList;
    public ArrayList<Notes> searchedNotesList;
    private NotesSearch searchedFilter;

    public NotesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Notes> objects) {
        super(context, resource, objects);
        this.context = context;
        this.notesList = new ArrayList<>();
        notesList.addAll(objects);
        this.searchedNotesList = new ArrayList<>();
        searchedNotesList.addAll(objects);
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notes_list, parent, false);

        TextView txtName = rowView.findViewById(R.id.txtName);
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtPriority = rowView.findViewById(R.id.txtPriority);
        ImageView txtImage = rowView.findViewById(R.id.image);

        if(searchedNotesList.size() == 0) {
            return rowView;
        }
        else {
            final Notes notePos = searchedNotesList.get(pos);

            txtName.setText(String.format(context.getString(R.string.noteName), notePos.getName()));
            txtDate.setText(String.format(context.getString(R.string.noteDate), notePos.getDateTime()));
            txtPriority.setText(String.format(context.getString(R.string.notePriority), notePos.getPriority()));
            txtImage.setImageURI(Uri.parse(notePos.getImage()));

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NotesCRUD.class);
                    intent.putExtra("noteId", String.valueOf(notePos.getNotesId()));
                    intent.putExtra("noteName", notePos.getName());
                    intent.putExtra("noteText", notePos.getText());
                    intent.putExtra("priority", notePos.getPriority());
                    intent.putExtra("image", notePos.getImage());
                    context.startActivity(intent);
                }
            });

            return rowView;
        }
    }

    public Notes getPos(int pos) {
        return searchedNotesList.get(pos);
    }

    @Override
    public Filter getFilter()
    {
        if (searchedFilter == null) {
            searchedFilter = new NotesSearch();
        }

        return searchedFilter;
    }

    private class NotesSearch extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint.toString().length() > 0)
            {
                ArrayList<Notes> filteredItems = new ArrayList<>();

                for(int i = 0; i < notesList.size(); i++)
                {
                    try {
                        Pattern pattern = Pattern.compile(constraint.toString());
                        Notes note = notesList.get(i);
                        Matcher matcher = pattern.matcher(note.getText());
                        if (note.getName().toLowerCase().contains(constraint) ||
                                note.getText().toLowerCase().contains(constraint) ||
                                matcher.find()) {
                            filteredItems.add(note);
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = notesList;
                    result.count = notesList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            searchedNotesList = (ArrayList<Notes>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0; i < searchedNotesList.size(); i++) {
                add(searchedNotesList.get(i));
            }
             notifyDataSetInvalidated();
        }
    }
}
