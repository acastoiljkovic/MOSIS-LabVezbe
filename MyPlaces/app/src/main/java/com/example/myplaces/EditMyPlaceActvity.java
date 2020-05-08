package com.example.myplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditMyPlaceActvity extends AppCompatActivity implements View.OnClickListener{
    boolean editMode= true;
    int position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_place_actvity);
        try{
            Intent listIntent = getIntent();
            Bundle positionBundle = listIntent.getExtras();
            if(positionBundle != null) {
                position = positionBundle.getInt("position");
            }
            else
                editMode = false;
        } catch (Exception e)
        {
            editMode = false;
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        setSupportActionBar(toolbar);
        final Button finishedBtn = (Button)findViewById(R.id.editmyplace_finished_button);
        finishedBtn.setOnClickListener(this);
        if(!editMode) {
            finishedBtn.setEnabled(false);
            finishedBtn.setText("Add");
        }
        else if ( position> 0) {
            finishedBtn.setText("Save");
            MyPlace place = MyPlacesData.getInstance().getPlace(position);
            EditText etName = (EditText)findViewById(R.id.editmyplace_name_edit);
            EditText descName = (EditText)findViewById(R.id.editmyplace_desc_edit);
            etName.setText(place.getName());
            descName.setText(place.getDescription());
        }
        Button cancelBtn = (Button)findViewById(R.id.editmyplace_cancle_button);
        cancelBtn.setOnClickListener(this);
        EditText nameEdit = (EditText)findViewById(R.id.editmyplace_name_edit);
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                finishedBtn.setEnabled(true);
            }
        });

        Button locationButton=findViewById(R.id.editmyplace_location_button);
        locationButton.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_my_place, menu);
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editmyplace_cancle_button: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }

            case R.id.editmyplace_finished_button: {
                EditText etName = (EditText)findViewById(R.id.editmyplace_name_edit);
                String name = etName.getText().toString();
                EditText descName = (EditText)findViewById(R.id.editmyplace_desc_edit);
                String desc = descName.getText().toString();
                EditText latEdit=findViewById(R.id.editmyplace_lat_edit);
                String lat=latEdit.getText().toString();
                EditText lonEdit=findViewById(R.id.editmyplace_lon_edit);
                String lon=lonEdit.getText().toString();


                if(!editMode){
                    MyPlace myPlace=new MyPlace(name,desc);
                    myPlace.setLatitude(lat);
                    myPlace.setLongitude(lon);
                    MyPlacesData.getInstance().addNewPlace(myPlace);
                }
                else{
                    MyPlacesData.getInstance().updatePlace(position,name,desc,lon,lat);
//                    MyPlace place= MyPlacesData.getInstance().getPlace(position);
//                    place.setName(name);
//                    place.setDescription(desc);
//                    place.setLatitude(lat);
//                    place.setLongitude(lon);

                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }

            case R.id.editmyplace_location_button:
            {
                Intent i=new Intent(this,MyPlacesMapsActivity.class);
                i.putExtra("state",MyPlacesMapsActivity.SELECT_COORDINATES);
                startActivityForResult(i,1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode== Activity.RESULT_OK){
                String lon=data.getExtras().getString("lon");
                EditText lonText=findViewById(R.id.editmyplace_lon_edit);
                lonText.setText(lon);

                String lat=data.getExtras().getString("lat");
                EditText latText=findViewById(R.id.editmyplace_lat_edit);
                latText.setText(lat);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.show_map_item){
            Intent i=new Intent(this,MyPlacesMapsActivity.class);
            i.putExtra("state",MyPlacesMapsActivity.SHOW_MAP);
            startActivity(i);
        }else if(id == R.id.my_places_list_item){
            Intent i = new Intent(this, MyPlacesList.class);
            startActivity(i);
        }else if(id == R.id.about_item){
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
        //else if(id == R.id.home){
        else{
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
