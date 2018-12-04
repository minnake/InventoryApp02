package com.example.keinonen.inventoryapp02;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keinonen.inventoryapp02.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // Find the columns of inventory's attributes
        int productIdColumIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the inventory attributes from the Cursor for the current book
        String productName = cursor.getString(nameColumnIndex);
        final String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);


        // Read the attributes from the Cursor for the current product for "Sale" button
        final long productId = Integer.parseInt(cursor.getString(productIdColumIndex));
        final int currentQuantity = cursor.getInt(quantityColumnIndex);

        /*
         * Each list view item will have a "Sale" button.
         * Which will decrease the product quantity by one.
         * Update is only carried out if quantity is greater than 0.
         */
        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productId);
                String updatedQuantity = String.valueOf(currentQuantity - 1);

                if(Integer.parseInt(updatedQuantity)>=0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, updatedQuantity);
                    context.getContentResolver().update(currentUri, values, null, null);
                    Toast.makeText(v.getContext(), R.string.quantity_changed, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(v.getContext(), R.string.sold_out, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(productName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);
    }
}