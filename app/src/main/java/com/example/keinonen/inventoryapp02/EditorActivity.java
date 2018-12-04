package com.example.keinonen.inventoryapp02;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.keinonen.inventoryapp02.data.InventoryContract.InventoryEntry;
import com.example.keinonen.inventoryapp02.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri currentProductUri;
    private String callSupplier;

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private Spinner supplierSpinner;
    private int supplier = InventoryEntry.SUPPLIER_UNKNOWN;
    private EditText phoneEditText;

    private Button incrementBtn;
    private Button decrementBtn;
    private Button deleteBtn;
    private ImageButton phoneBtn;

    private final int QUANTITY_MIN = 0;
    private final int QUANTITY_MAX = 1000;

    private boolean productHasChanged = false;
    public InventoryDbHelper newInventoryDbHelper;

    private View.OnTouchListener newTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        nameEditText = findViewById(R.id.edit_product_name);
        priceEditText = findViewById(R.id.edit_product_price);
        quantityEditText = findViewById(R.id.edit_product_quantity);
        supplierSpinner = findViewById(R.id.spinner_supplier);
        phoneEditText = findViewById(R.id.edit_product_phonenumber);

        incrementBtn = findViewById(R.id.increment);
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = quantityEditText.getText().toString();
                int quantity;
                if (quantityString.length() == 0) {
                    quantity = 1;
                    quantityEditText.setText(String.valueOf(quantity));
                } else {
                    quantity = Integer.parseInt(quantityString) + 1;
                    if (quantity <= QUANTITY_MAX) {
                        quantityEditText.setText(String.valueOf(quantity));
                    }
                }
            }
        });

        decrementBtn = findViewById(R.id.decrement);
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = quantityEditText.getText().toString();
                int quantity;
                if (quantityString.length() == 0) {
                    quantity = 0;
                    quantityEditText.setText(String.valueOf(quantity));
                } else {
                    quantity = Integer.parseInt(quantityString) - 1;
                    if (quantity >= QUANTITY_MIN) {
                        quantityEditText.setText(String.valueOf(quantity));
                    }
                }
            }
        });

        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });


        phoneBtn = findViewById(R.id.phone_call);
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent supplierNumberIntent = new Intent(Intent.ACTION_DIAL);
                supplierNumberIntent.setData(Uri.parse("tel:" + callSupplier));
                startActivity(supplierNumberIntent);
                if (supplierNumberIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(supplierNumberIntent);
                }
            }
        });


        newInventoryDbHelper = new InventoryDbHelper(this);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(newTouchListener);
        priceEditText.setOnTouchListener(newTouchListener);
        quantityEditText.setOnTouchListener(newTouchListener);
        supplierSpinner.setOnTouchListener(newTouchListener);
        phoneEditText.setOnTouchListener(newTouchListener);

        decrementBtn.setOnTouchListener(newTouchListener);
        incrementBtn.setOnTouchListener(newTouchListener);
        deleteBtn.setOnTouchListener(newTouchListener);
        phoneBtn.setOnTouchListener(newTouchListener);

        setupSpinner();

    }


    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        supplierSpinner.setAdapter(supplierSpinnerAdapter);

        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_ebay))) {
                        supplier = InventoryEntry.SUPPLIER_EBAY;
                    } else if (selection.equals(getString(R.string.supplier_market))) {
                        supplier = InventoryEntry.SUPPLIER_MARKET;
                    } else {
                        supplier = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplier = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                saveProduct();
                return true;
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!productHasChanged) {
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

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String phoneString = phoneEditText.getText().toString().trim();


        if (currentProductUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(phoneString) ||
                supplier == InventoryEntry.SUPPLIER_UNKNOWN) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        int priceInt = Integer.parseInt(priceString);
        int quantityInt = Integer.parseInt(quantityString);

        if (priceInt <= 0) {
            priceEditText.setError(getString(R.string.product_not_zero));
            return;
        }
        if (quantityInt <= 0) {
            quantityEditText.setError(getString(R.string.product_not_zero));
            return;
        }

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(phoneString) && supplier == InventoryEntry.SUPPLIER_UNKNOWN) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(InventoryEntry.COLUMN_PRODUCT_PHONENUMBER, phoneString);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt((quantityString));
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);


        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_PHONENUMBER,};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PHONENUMBER);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int[] quantity = {cursor.getInt(quantityColumnIndex)};
            int supplier = cursor.getInt(supplierColumnIndex);
            callSupplier = cursor.getString(phoneColumnIndex);

            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity[0]));
            phoneEditText.setText(String.valueOf(callSupplier));

            switch (supplier) {
                case InventoryEntry.SUPPLIER_EBAY:
                    supplierSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_MARKET:
                    supplierSpinner.setSelection(2);
                    break;
                default:
                    supplierSpinner.setSelection(0);
                    break;

            }

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierSpinner.setSelection(0);
        phoneEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        showDeleteConfirmationDialog();
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

