package com.android.gridapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

// based on
// http://developer.android.com/guide/tutorials/views/hello-gridview.html
public class GridActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridapp_main);
        
        GridView gridview = (GridView) findViewById(R.id.gridapp_view);
        gridview.setAdapter(new ImageAdapter(this));
    }
}