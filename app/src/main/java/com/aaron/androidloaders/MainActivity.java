package com.aaron.androidloaders;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aaron.androidloaders.model.InTheaters;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<InTheaters>, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mData = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R
                .id.text1, mData);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(findViewById(R.id.empty_item));

        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        });

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<InTheaters> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader, id " + id);

        return new InTheatersTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<InTheaters> loader, InTheaters data) {
        for(InTheaters.Subject subject : data.getSubjects()) {
            mData.add(subject.getTitle());
        }

        mAdapter.addAll(mData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<InTheaters> loader) {
        Log.d(TAG, "onLoaderReset");

        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            startActivity(new Intent(this, FooActivity.class));
        }
    }
}
