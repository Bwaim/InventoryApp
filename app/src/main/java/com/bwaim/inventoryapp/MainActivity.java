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

package com.bwaim.inventoryapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bwaim.inventoryapp.data.BookStoreContract.BookEntrie;
import com.bwaim.inventoryapp.data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookStoreDbHelper mDbHelper;

    private TextView displayTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTV = findViewById(R.id.display_TV);

        mDbHelper = new BookStoreDbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabase();
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void displayDatabase() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {BookEntrie.COLUMN_PRODUCT_NAME,
                BookEntrie.COLUMN_PRICE,
                BookEntrie.COLUMN_QUANTITY,
                BookEntrie.COLUMN_SUPPLIER_NAME,
                BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER};

        Cursor c = db.query(BookEntrie.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            displayTV.setText(getString(R.string.db_count, c.getCount()));

            int indexProductName = c.getColumnIndex(BookEntrie.COLUMN_PRODUCT_NAME);
            int indexPrice = c.getColumnIndex(BookEntrie.COLUMN_PRICE);
            int indexQuantity = c.getColumnIndex(BookEntrie.COLUMN_QUANTITY);
            int indexSupplierName = c.getColumnIndex(BookEntrie.COLUMN_SUPPLIER_NAME);
            int indexSupplierPhone = c.getColumnIndex(BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER);

            displayTV.append("\n\n " +
                    BookEntrie.COLUMN_PRODUCT_NAME + " - " +
                    BookEntrie.COLUMN_PRICE + " - " +
                    BookEntrie.COLUMN_QUANTITY + " - " +
                    BookEntrie.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            while (c.moveToNext()) {
                displayTV.append("\n " +
                        c.getString(indexProductName) + " - " +
                        c.getInt(indexPrice) + " - " +
                        c.getInt(indexQuantity) + " - " +
                        c.getString(indexSupplierName) + " - " +
                        c.getString(indexSupplierPhone));
            }
        } finally {
            c.close();
        }
    }
}
