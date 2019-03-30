package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import java.net.URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    //PetDbHelper mDbHelper = new PetDbHelper(this);
    String[] projection = {
            PetEntry._ID,
            PetEntry.COLUMN_PET_NAME,
            PetEntry.COLUMN_PET_BREED,
    };

    PetCursorAdapter cursorAdapter;
    private static final int PET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Cursor cursor = getContentResolver().query( PetContract.CONTENT_URI , projection,null,null,null);

        ListView displayView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);

        cursorAdapter = new PetCursorAdapter(this,null);

        displayView.setAdapter(cursorAdapter);

        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(CatalogActivity.this,EditorActivity.class);

                Uri currentUri = ContentUris.withAppendedId(PetContract.CONTENT_URI,id);

                i.setData(currentUri);

                startActivity(i);
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
// or start a new one.
        getLoaderManager().initLoader(PET_LOADER, null, this);
        //  displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertRows()
    {
        // Create and/or open a database to read from it
        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(PetEntry.COLUMN_PET_BREED,"tabby");
        row.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        row.put(PetEntry.COLUMN_PET_NAME,"toto");
        row.put(PetEntry.COLUMN_PET_WEIGHT,14);
        //  row.put(PetEntry._ID,1);
        Uri newUri = getContentResolver().insert(PetContract.CONTENT_URI,row);
        // Log.v("CATALOGACTIVITY","new row id "+id);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertRows();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
            //       return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetContract.CONTENT_URI, null, null);
        Log.v("EditorActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,PetContract.CONTENT_URI,projection,null,null,null);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}