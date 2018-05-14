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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bwaim.inventoryapp.data.BookStoreContract.BookEntrie;
import com.bwaim.inventoryapp.data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

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

    private void insertDummyData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookEntrie.COLUMN_PRODUCT_NAME, "BookName");
        values.put(BookEntrie.COLUMN_PRICE, 10);
        values.put(BookEntrie.COLUMN_QUANTITY, 5);
        values.put(BookEntrie.COLUMN_SUPPLIER_NAME, "The Supplier!");
        values.put(BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER, "1526545");

        long newRowId = db.insert(BookEntrie.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, R.string.new_book_inserted, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "New Book inserted: " + newRowId);
        } else {
            Toast.makeText(this, R.string.problem_creating_book
                    , Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Problem when creating the new book.");
        }
    }

    private void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(BookEntrie.TABLE_NAME,
                null,
                null);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                insertDummyData();
                displayDatabase();
                return true;
            case R.id.delete_all:
                deleteAll();
                displayDatabase();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
