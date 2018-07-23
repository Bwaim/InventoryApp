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

package com.bwaim.inventoryapp.ui;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bwaim.inventoryapp.R;
import com.bwaim.inventoryapp.data.BookStoreContract.BookEntrie;
import com.bwaim.inventoryapp.data.BookStoreDbHelper;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_EDIT_LOADER = 1;

    private EditText mBookNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private boolean mBookHasChanged = false;

    private BookStoreDbHelper mDbHelper;
    private Uri mUri;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mUri = null;
        Intent intent = getIntent();

        if (intent != null) {
            mUri = intent.getData();
        }

        if (mUri != null) {
            setTitle(R.string.editor_activity_title_modify_book);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
            getLoaderManager().initLoader(BOOK_EDIT_LOADER, null, this);
        } else {
            setTitle(R.string.editor_activity_title_new_book);
        }

        mBookNameEditText = findViewById(R.id.book_name);
        mBookPriceEditText = findViewById(R.id.book_price);
        mBookQuantityEditText = findViewById(R.id.book_quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        ImageView addIv = findViewById(R.id.quantity_add_ic);
        addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Initialize quantity to 1 because if the quantity is empty, it will be 1 */
                int quantity = 1;
                if (!TextUtils.isEmpty(mBookQuantityEditText.getText())) {
                    quantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim());
                }
                mBookQuantityEditText.setText(String.valueOf(quantity + 1));
            }
        });

        ImageView minusIv = findViewById(R.id.quantity_minus_ic);
        minusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Initialize quantity to 1 because if the quantity is empty, it will be 1 */
                int quantity = 0;
                if (!TextUtils.isEmpty(mBookQuantityEditText.getText())) {
                    quantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim());
                }
                /* Control to not have a quantity less than 0 */
                if (quantity - 1 >= 0) {
                    quantity--;
                }
                mBookQuantityEditText.setText(String.valueOf(quantity));
            }
        });

        mDbHelper = new BookStoreDbHelper(this);

        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private boolean validData() {
        boolean validBookName = !TextUtils.isEmpty(mBookNameEditText.getText());
        boolean validPrice = false;
        if (!TextUtils.isEmpty(mBookPriceEditText.getText())) {
            validPrice = Integer.parseInt(mBookPriceEditText.getText().toString().trim()) > 0;
        }
        boolean validQuantity = false;
        if (!TextUtils.isEmpty(mBookQuantityEditText.getText())) {
            validQuantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim()) >= 0;
        }
        boolean validSupplierName = !TextUtils.isEmpty(mSupplierNameEditText.getText());

        return validBookName && validPrice && validQuantity && validSupplierName;
    }

    private void saveBook() {

        if (!validData()) {
            return;
        }

        String bookName = mBookNameEditText.getText().toString().trim();
        int price = Integer.parseInt(mBookPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookEntrie.COLUMN_PRODUCT_NAME, bookName);
        values.put(BookEntrie.COLUMN_PRICE, price);
        values.put(BookEntrie.COLUMN_QUANTITY, quantity);
        values.put(BookEntrie.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        if (mUri != null) {
            int nbUpdated = getContentResolver().update(mUri, values, null, null);
            if (nbUpdated > 0) {
                Toast.makeText(this, R.string.editBook, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editErrot), Toast.LENGTH_SHORT).show();
            }

        } else {
            Uri newUri = getContentResolver().insert(BookEntrie.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.insertError, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertBook), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        int nbRowAffected = getContentResolver().delete(mUri, null, null);
        if (nbRowAffected > 0) {
            Toast.makeText(this, R.string.deleteBook, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.deleteError), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookEntrie._ID, BookEntrie.COLUMN_PRODUCT_NAME
                , BookEntrie.COLUMN_PRICE, BookEntrie.COLUMN_QUANTITY
                , BookEntrie.COLUMN_SUPPLIER_NAME, BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                mUri,
                projection,
                null,
                null,
                null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {link CursorAdapter#CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String bookName = data.getString(data.getColumnIndex(BookEntrie.COLUMN_PRODUCT_NAME));
            int price = data.getInt(data.getColumnIndex(BookEntrie.COLUMN_PRICE));
            int quantity = data.getInt(data.getColumnIndex(BookEntrie.COLUMN_QUANTITY));
            String supplierName = data.getString(data.getColumnIndex(BookEntrie.COLUMN_SUPPLIER_NAME));
            String supplierPhone = data.getString(data.getColumnIndex(BookEntrie.COLUMN_SUPPLIER_PHONE_NUMBER));

            mBookNameEditText.setText(bookName);
            mBookPriceEditText.setText(String.valueOf(price));
            mBookQuantityEditText.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookNameEditText.setText(null);
        mBookPriceEditText.setText(null);
        mBookQuantityEditText.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierPhoneEditText.setText(null);
    }
}
