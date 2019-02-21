package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.example.a12745.easytravel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Util.HttpUtil;
import common.ConstValue;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 展示给给司机的界面
 * @author XuCong
 */
public class PassengerActivity extends AppCompatActivity implements AMapLocationListener,LocationSource,PoiSearch.OnPoiSearchListener {

    private MapView mapView;
    private OnLocationChangedListener mLocationListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Button button, btn_self, btn_start;
    private AMap aMap;
    private LatLng mCurLocation;
    private String address;
    private boolean isFirstLoc;
    private List<LatLng> pointList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private RouteSearch routeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);

        mapView = (MapView) findViewById(R.id.driver_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        aMap.moveCamera(CameraUpdateFactory.zoomTo(ConstValue.mapScale));
        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式


        setUpMap();

        button = (Button) findViewById(R.id.passenger_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_start.getText().toString().equals("寻找打车点")) {
                    if (mCurLocation == null) {
                        Toast.makeText(PassengerActivity.this, "当前信号不不佳，请稍候...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 如果获取到定位信息，就将地图视角动画移动到定位点
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mCurLocation, ConstValue.mapScale, 0, 0)), 500, null);
                }
                if (btn_start.getText().toString().equals("开始导航"))
                {

                }
            }
        });

        btn_self = findViewById(R.id.passenger_btn_info);
        btn_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassengerActivity.this, DriverInfoActivity.class);
                startActivity(intent);
            }
        });

        btn_start = findViewById(R.id.passenger_btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurLocation == null) {
                    Toast.makeText(PassengerActivity.this, "当前信号不不佳，请稍候...", Toast.LENGTH_SHORT).show();
                    return;
                }
                tryGetRecommendLocation(mCurLocation.latitude,mCurLocation.longitude);
            }
        });

        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mLocationListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                mCurLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                address = String.valueOf(aMapLocation.getLatitude()) + String.valueOf(aMapLocation.getLongitude());
                // 如果不设置isFirstLoc标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(ConstValue.mapScale));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mLocationListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    isFirstLoc = false;
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Toast.makeText(this, "定位失败，请看后台Log.e", Toast.LENGTH_LONG).show();
                Log.e("GGG", errText);
            }
        }
    }

    private void setUpMap() {
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位
        aMap.setMyLocationEnabled(true);

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //连续定位、蓝点不自动移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动（最常用的）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);

        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(this);
        // 设置定位监听
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(ConstValue.mapLocationIternal);
        //缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        isFirstLoc = true;
        mLocationClient.startLocation();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mLocationListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public void tryGetRecommendLocation(double latitude, double longitude) {
        String address = ConstValue.getCommendUrl(longitude, latitude);
        Log.e("GGG", address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseText);
                    deleteMark();
                    Marker marker;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i);
                        LatLng latLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                        /*query = new PoiSearch.Query("", "", null);
                        query.setPageSize(10);// 设置每页最多返回多少条poiitem
                        query.setPageNum(1);//设置查询页码
                        poiSearch = new PoiSearch(PassengerActivity.this, query);
                        poiSearch.setOnPoiSearchListener(PassengerActivity.this);
                        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 500));//设置周边搜索的中心点以及半径
                        poiSearch.searchPOIAsyn();*/
                        int m = i + 1;
                        marker = aMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("推荐打车点" + m ));
                        marker.setDraggable(false);
                        marker.setVisible(true);
                        markerList.add(marker);
                    }
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(PassengerActivity.this, "获取打车点成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(PassengerActivity.this, "获取打车点失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(PassengerActivity.this, "异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void deleteMark() {
        Marker marker;
        if (markerList==null) {
            markerList = new ArrayList<>();
        }
        while (markerList.size() != 0) {
            marker = markerList.get(0);
            if (marker.isRemoved() == false) {
                marker.remove();
            }
            markerList.remove(0);
            marker.destroy();
        }
        markerList.clear();
    }

    /**
     * 获取poiseach得到的点,并产生marker，放入marklist里面,并且显示到地图上
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        List<PoiItem> poiList=poiResult.getPois();
        if(poiList.size()>0){
            LatLng latLng = new LatLng(poiList.get(0).getLatLonPoint().getLatitude(),poiList.get(0).getLatLonPoint().getLongitude());
            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("推荐打车点"));
            marker.setDraggable(false);
            marker.setVisible(true);
            markerList.add(marker);
            Log.e("TAG", "经度"+poiList.get(0).getLatLonPoint().getLatitude()+"纬度"+poiList.get(0).getLatLonPoint().getLongitude());
            Toast.makeText(this, poiList.get(0).getTitle()+" "+poiList.get(0).getLatLonPoint().getLatitude()+" "+poiList.get(0).getLatLonPoint().getLongitude(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "wuwuwuuwuwuw", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            Log.e("GGG","点击了");
            btn_start.setText("开始导航");
            /*routeSearch = new RouteSearch(PassengerActivity.this);
            routeSearch.setRouteSearchListener(this);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, walkMode);*/
            return false;
        }
    };


}



