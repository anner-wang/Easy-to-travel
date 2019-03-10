package com.example.a12745.easytravel.navi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.a12745.easytravel.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMapNaviListener, View.OnClickListener, AMap.OnMapLoadedListener {
    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;
    private AMap mAMap;
    private NaviLatLng endLatlng = new NaviLatLng(39.90759, 116.392582);
    private NaviLatLng startLatlng = new NaviLatLng(39.993537, 116.472875);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    private RouteOverLay mRouteOverlay;
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /*
        * strategyFlag转换出来的值都对应PathPlanningStrategy常量，用户也可以直接传入PathPlanningStrategy常量进行算路。
        * 如:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
        */
    int strategyFlag = 0;
    private Button mStartNaviButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        mStartNaviButton = (Button) findViewById(R.id.calculate_route_start_navi);
        mStartNaviButton.setOnClickListener(this);
        setUpMapIfNeeded();
        initNavi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mAMap == null) {
            mAMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            UiSettings uiSettings = mAMap.getUiSettings();
            if (uiSettings != null) {
                uiSettings.setRotateGesturesEnabled(false);
            }
            mAMap.setOnMapLoadedListener(this);
        }
    }

    /**
     * 驾车路径规划计算
     */
    private void calculateDriveRoute() {
        try {
            strategyFlag = mAMapNavi.strategyConvert(true, false, false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
    }

    /**
     * 导航初始化
     */
    private void initNavi() {
        Intent intent = getIntent();
        ArrayList<String> start = intent.getStringArrayListExtra("start");
        ArrayList<String> wayListP = intent.getStringArrayListExtra("wayListP");
        ArrayList<String> end = intent.getStringArrayListExtra("end");
        startLatlng = new NaviLatLng(Double.valueOf(start.get(0)), Double.valueOf(start.get(1)));
        endLatlng = new NaviLatLng(Double.valueOf(end.get(0)), Double.valueOf(end.get(1)));
        wayList = new ArrayList<NaviLatLng>();
        for (int i = 0;i<(wayListP.size()/2);i++)
        {
            wayList.add(new NaviLatLng(Double.valueOf(wayListP.get(2*i)),Double.valueOf(wayListP.get(2*i+1))));
        }
        startList.add(startLatlng);
        endList.add(endLatlng);
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
    }

    private void cleanRouteOverlay() {
        if (mRouteOverlay != null) {
            mRouteOverlay.removeFromMap();
            mRouteOverlay.destroy();
        }
    }

    /**
     * 绘制路径规划结果
     *
     * @param path AMapNaviPath
     */
    private void drawRoutes(AMapNaviPath path) {
        mAMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        mRouteOverlay = new RouteOverLay(mAMap, path, this);
        mRouteOverlay.addToMap();
        mRouteOverlay.zoomToSpan();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.calculate_route_start_navi){
            startNavi();
        }

    }
    /**
     * 开始导航
     */
    private void startNavi() {
        Intent gpsintent = new Intent(getApplicationContext(), RouteNaviActivity.class);
        gpsintent.putExtra("gps", true); // gps 为true为真实导航，为false为模拟导航
        startActivity(gpsintent);
        finish();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        cleanRouteOverlay();
        AMapNaviPath path = mAMapNavi.getNaviPath();
        if (path != null) {
            drawRoutes(path);
        }
        mStartNaviButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onMapLoaded() {
        calculateDriveRoute();
    }
}
