//package com.comp900019.MelbParking.DataManagement;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.LayerDrawable;
//import android.graphics.drawable.ShapeDrawable;
//import android.graphics.drawable.shapes.OvalShape;
//import android.util.SparseArray;
//import android.view.ViewGroup;
//
//import com.comp900019.MelbParking.R;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.clustering.Cluster;
//import com.google.maps.android.clustering.ClusterManager;
//import com.google.maps.android.clustering.view.DefaultClusterRenderer;
//import com.google.maps.android.ui.IconGenerator;
//import com.google.maps.android.ui.SquareTextView;
//
//public class ClusterRenderer extends DefaultClusterRenderer<MarkerItem> {
//
//    private Context context;
//
//    public ClusterRenderer(Context context, GoogleMap map,
//                             ClusterManager<MarkerItem> clusterManager) {
//        super(context, map, clusterManager);
//        this.context = context;
//    }
//
//    @Override
//    protected void onBeforeClusterItemRendered(MarkerItem item,
//                                               MarkerOptions markerOptions) {
//        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//        markerOptions.icon(markerDescriptor);
//    }
//
//    @Override
//    protected void onClusterItemRendered(MarkerItem clusterItem, Marker marker) {
//        super.onClusterItemRendered(clusterItem, marker);
//    }
//
//}