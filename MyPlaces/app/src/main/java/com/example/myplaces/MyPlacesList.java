package com.example.myplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MyPlacesList extends AppCompatActivity {

    static int NEW_PLACE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyPlacesList.this,EditMyPlaceActvity.class);
                startActivityForResult(i,NEW_PLACE);
            }
        });


        ListView myPlacesList = (ListView)findViewById(R.id.my_places);
        myPlacesList.setAdapter((new ArrayAdapter<MyPlace>(this,android.R.layout.simple_list_item_1,MyPlacesData.getInstance().getMyPlaces())));
        myPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle positionBundle =new Bundle();
                positionBundle.putInt("position",position);
                Intent i = new Intent(MyPlacesList.this, ViewMyPlaceActivity.class);
                i.putExtras(positionBundle);
                startActivity(i);
            }
        });
        myPlacesList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
                menu.setHeaderTitle(place.getName());
                menu.add(0,1,1,"View place");
                menu.add(0,2,2,"Edit place");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            ListView myPlacesList = (ListView)findViewById(R.id.my_places);
            myPlacesList.setAdapter((new ArrayAdapter<MyPlace>(this,android.R.layout.simple_list_item_1,MyPlacesData.getInstance().getMyPlaces())));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.show_map_item){
            Toast.makeText(this,"Show Map", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.my_places_list_item){
            Intent i = new Intent(this, MyPlacesList.class);
            startActivity(i);
        }else if(id == R.id.about_item){
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
        //else if(id == R.id.home){
        else  {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Bundle positionBundle =new Bundle();
        positionBundle.putInt("position",info.position);
        Intent i = null;
        if(item.getItemId() == 1) {
            i = new Intent(this, ViewMyPlaceActivity.class);
            i.putExtras(positionBundle);
            startActivity(i);
        }
        else if(item.getItemId() == 2) {
            i = new Intent(this, EditMyPlaceActvity.class);
            i.putExtras(positionBundle);
            startActivityForResult(i,1);
        }
        return super.onContextItemSelected(item);
    }

}
