//package com.comp900019.MelbParking.DataManagement;
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.fragment.app.FragmentActivity;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.maps.android.clustering.ClusterManager;
//
//import java.util.HashMap;
//import java.util.List;
//
//public class MarkerClusterManager {
//    private GoogleMap mMap;
//    private Context context;
//    private ClusterManager<MarkerItem> mClusterManager;
//    private GetClosestSpot calculate;
//    private FragmentActivity activity;
//    public MarkerClusterManager(Context context, GoogleMap mMap, FragmentActivity activity){
//        this.mMap = mMap;
//        this.context = context;
//        this.calculate = new GetClosestSpot();
//        mClusterManager = new ClusterManager<>(context , mMap);
//        mClusterManager.setRenderer(new ClusterRenderer(context, mMap, mClusterManager));
//        this.activity = activity;
//    }
//    public void setUpClusterer(List<ParkingSpot> spotLists, LatLng currentLocation) {
//        // Position the map.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
//
//        // Initialize the manager with the context and the map.
//        // (Activity extends context, so we can pass 'this' in the constructor.)
//
//        // Point the map's listeners at the listeners implemented by the cluster
//        // manager.
//        mMap.setOnCameraIdleListener(mClusterManager);
//        mMap.setOnMarkerClickListener(mClusterManager);
//
//        // Add cluster items (markers) to the cluster manager.
//        addItems(spotLists);
//    }
//    public void getRecommandation(final List<ParkingSpot> spotList, final LatLng currentLocation, final int numberOfRecom){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                calculate.setRecommendSpot(spotList, currentLocation, numberOfRecom);
//                HashMap<ParkingSpot, HashMap<String, String>> result = calculate.getDistanceRecom();
//                for(ParkingSpot ps: result.keySet()){
//                    Log.i("bugs", result.get(ps).get("distance")+" miles, "+result.get(ps).get("duration")+" mints");
//                }
//                Log.i("bugs","---------------------------");
//            }
//        }).start();
//    }
//
//
//    private void addItems(List<ParkingSpot> spotLists) {
//        for(ParkingSpot spot: spotLists){
//            if(spot.getState().equals("Unoccupied")){
//                LatLng latLng = new LatLng(spot.getLat(), spot.getLon());
//                MarkerItem offsetItem = new MarkerItem(latLng, spot.getBay_id(), spot.getState());
//                mClusterManager.addItem(offsetItem);
////                mClusterManager.setRenderer(new ClusterRenderer(context, mMap, mClusterManager));
//            }
//
//        }
//    }
//}
