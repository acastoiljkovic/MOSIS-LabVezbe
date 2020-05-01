package com.example.myplaces;

import java.util.ArrayList;

public class MyPlacesData {
    private ArrayList<MyPlace> myPlaces;

    private MyPlacesData() {
        myPlaces = new ArrayList<MyPlace>();
/*        myPlaces.add(new MyPlace("PlaceA"));
        myPlaces.add(new MyPlace("PlaceB"));
        myPlaces.add(new MyPlace("PlaceC"));
        myPlaces.add(new MyPlace("PlaceD"));
        myPlaces.add(new MyPlace("PlaceE"));*/
    }

    private static class SingletonHolder {
        public static final MyPlacesData instance = new MyPlacesData();
    }

    public static MyPlacesData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces(){
        return myPlaces;
    }

    public void addNewPlace(MyPlace place){
        myPlaces.add(place);
    }

    public MyPlace getPlace(int index){
        return myPlaces.get(index);
    }

    public void deletePlace(int index){
        myPlaces.remove(index);
    }
}
