package com.example.android.booklooker;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    int mBackGroundColor;

    public BookAdapter(Context context, ArrayList<Book> books, int backGroundColor) {
        super(context, 0, books);
        mBackGroundColor = backGroundColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // create (inflate) a new list item view if one isn't available already to reuse
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_view, parent, false);
        }


        // get the Book at this position
        Book currentBook = getItem(position);

        // populate the TextViews on this list item View with the current Book's values.
        TextView bookTitleView = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitleView.setText(currentBook.getBookTitle());

        TextView bookAuthorView = (TextView) listItemView.findViewById(R.id.book_author);
        bookAuthorView.setText(currentBook.getBookAuthor());

        ImageView wordImage = (ImageView) listItemView.findViewById(R.id.image);
        if (currentBook.hasImage()) {
            // unhide image
            wordImage.setVisibility(View.VISIBLE);
            // set image for this view
            wordImage.setImageResource(currentBook.getImageResourceID());
        } else {
            // remove imageview from layout for this item
            wordImage.setVisibility(View.GONE);
        }

        // in this list item view, grab the text box container for words/tranlations
        View listBox = listItemView.findViewById(R.id.list_item);

        // change background color for this BookAdapter to the right id
        listBox.setBackgroundColor(ContextCompat.getColor(getContext(), mBackGroundColor));

        // return this List ItemView to the ListView for display
        return listItemView;
    }


}
