/*
 *    Copyright 2018 Fabien Boismoreau
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.bwaim.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bwaim.inventoryapp.R;
import com.bwaim.inventoryapp.data.BookStoreContract.BookEntrie;

/**
 * Created by Fabien Boismoreau on 11/06/2018.
 * <p>
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item,
                parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookNameTV = view.findViewById(R.id.book_name);
        TextView bookPriceTV = view.findViewById(R.id.book_price);
        TextView bookQuantityTV = view.findViewById(R.id.book_quantity);
        ImageView sellActionIV = view.findViewById(R.id.sell_book_button);

        String bookName = cursor.getString(cursor.getColumnIndex(BookEntrie.COLUMN_PRODUCT_NAME));
        int bookPrice = cursor.getInt(cursor.getColumnIndex(BookEntrie.COLUMN_PRICE));
        int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntrie.COLUMN_QUANTITY));

        if (bookQuantity == 0) {
            sellActionIV.setImageResource(R.drawable.ic_attach_money_red);
        } else {
            sellActionIV.setImageResource(R.drawable.ic_attach_money_blue);
        }

        bookNameTV.setText(bookName);
        bookPriceTV.setText(String.valueOf(bookPrice));
        bookQuantityTV.setText(String.valueOf(bookQuantity));
    }
}
