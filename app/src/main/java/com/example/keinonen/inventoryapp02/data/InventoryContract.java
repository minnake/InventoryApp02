package com.example.keinonen.inventoryapp02.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract(){
    }

    public static final String CONTENT_AUTHORITY = "com.example.keinonen.inventoryapp02";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";


    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";

        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public final static String COLUMN_PRODUCT_PHONENUMBER = "phone_number";

        public final static int SUPPLIER_UNKNOWN = 0;
        public final static int SUPPLIER_EBAY = 1;
        public final static int SUPPLIER_MARKET = 2;

        public static boolean isValidSupplier(int supplier) {
            if (supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_EBAY || supplier == SUPPLIER_MARKET) {
                return true;
            }
            return false;
        }
    }
}
