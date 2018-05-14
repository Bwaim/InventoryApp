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

package com.bwaim.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Fabien Boismoreau on 14/05/2018.
 * <p>
 */
public final class BookStoreContract {

    // To prevent instantiation
    private BookStoreContract() {}

    public static class BookEntrie implements BaseColumns {

        public static final String TABLE_NAME = "Books";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_PRODUCT_NAME = "Product Name";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "Supplier Name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier Phone Number";

    }
}
