package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.RouteOverlay;


import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.WeightedLatLng;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.api.services.weather.LocalWeatherLive;
import com.example.a12745.easytravel.R;
import com.example.a12745.easytravel.navi.MainActivity;
import com.google.gson.JsonArray;
import com.yw.game.floatmenu.FloatItem;

import com.yw.game.floatmenu.FloatMenuView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Util.ActivityCollector;
import Util.DrivingRouteOverlay;
import Util.FloatLogoMenu;
import Util.HttpUtil;
import common.ConstValue;
import common.HotPoint;
import fragment.ConfirmFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 展示给给司机的界面
 * @author XuCong
 */
public class DriverActivity extends AppCompatActivity implements AMapLocationListener,LocationSource,INaviInfoCallback,PoiSearch.OnPoiSearchListener,RouteSearch.OnRouteSearchListener,WeatherSearch.OnWeatherSearchListener {

    public static boolean isShowConfirmFragment = false;
    private MapView mapView;
    private OnLocationChangedListener mLocationListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Button button,btn_self,btn_start;
    private AMap aMap;
    private LatLng mCurLocation;
    private String address;
    private boolean canMove = true;
    private List<HotPoint>pointList;


    private double curentLatitude,curentLongitude;
    private RouteSearch routeSearch;
    private  List<LatLonPoint> wayList;
    private boolean hasShowRoute=false;
    private int colorNumber=0;
    private int hasChoseRoute=-1;
    private long firstTime;//再按一次退出程序
    private TextView weather_cityName,weather_temp,weather_now;
    private  Timer timer;
    private TimerTask timerTask;
    private FloatLogoMenu mFloatMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        weather_cityName=findViewById(R.id.weather_cityName);
        weather_now=findViewById(R.id.weather_now);
        weather_temp=findViewById(R.id.weather_temp);



        mapView = (MapView) findViewById(R.id.driver_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        aMap.moveCamera(CameraUpdateFactory.zoomTo(ConstValue.mapScale));
        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式


        setUpMap();

        button = (Button) findViewById(R.id.driver_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurLocation == null) {
                    Toast.makeText(DriverActivity.this, "当前信号不佳，请稍候...", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 如果获取到定位信息，就将地图视角动画移动到定位点
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mCurLocation, ConstValue.mapScale, 0, 0)), 500, null);

            }
        });

        btn_self=findViewById(R.id.driver_btn_info);
        btn_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverActivity.this,DriverInfoActivity.class);
                if (ActivityCollector.isContains("driverActivity")==false)
                    ActivityCollector.addActivity("driverActivity",DriverActivity.this);
                startActivity(intent);
            }
        });


        btn_start=findViewById(R.id.driver_btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasShowRoute){
                    hasShowRoute=true;
                    tryGetDriverRoute(curentLatitude,curentLongitude);
                    colorNumber=0;
                }else if(hasChoseRoute>0){
                    /*Poi start = new Poi("起点", new LatLng(curentLatitude,curentLongitude), "");
                    List<Poi> wayListP = new ArrayList();//途径点目前最多支持3个。
                    int i;
                    for(i=hasChoseRoute-3;i<hasChoseRoute;i++){
                        wayListP.add(new Poi("点"+i+1, new LatLng(wayList.get(i).getLatitude(),wayList.get(i).getLongitude()), ""));
                    }
                    Poi end = new Poi("终点", new LatLng(wayList.get(i).getLatitude(),wayList.get(i).getLongitude()), "");
                    //第一个沿途点需要最后添加
                    AmapNaviPage.getInstance().showRouteActivity(DriverActivity.this, new AmapNaviParams(start, wayListP, end, AmapNaviType.DRIVER),DriverActivity.this);*/
                    ArrayList<String> start = new ArrayList<String>();
                    ArrayList<String> wayListP = new ArrayList<String>();
                    ArrayList<String> end = new ArrayList<String>();
                    start.add(String.valueOf(curentLatitude));
                    start.add(String.valueOf(curentLongitude));
                    int i;
                    for(i=hasChoseRoute-3;i<hasChoseRoute;i++){
                        wayListP.add(String.valueOf(wayList.get(i).getLatitude()));
                        wayListP.add(String.valueOf(wayList.get(i).getLongitude()));
                    }
                    end.add(String.valueOf(wayList.get(i).getLatitude()));
                    end.add(String.valueOf(wayList.get(i).getLongitude()));
                    Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                    intent.putStringArrayListExtra("start",start);
                    intent.putStringArrayListExtra("wayListP",wayListP);
                    intent.putStringArrayListExtra("end",end);
                    startActivity(intent);
                }else{
                    Toast.makeText(DriverActivity.this, "请选择终点", Toast.LENGTH_SHORT).show();
                }
            }
        });

        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                canMove = false;
            }
        });




        List<FloatItem>floatItemList=new ArrayList<>();
        floatItemList.add(new FloatItem("",R.color.colorAccent,R.color.colorTrancent, BitmapFactory.decodeResource(getResources(),R.drawable.gaode)));
        floatItemList.add(new FloatItem("",R.color.colorAccent,R.color.colorTrancent,BitmapFactory.decodeResource(getResources(),R.drawable.amap_car)));
        mFloatMenu = new FloatLogoMenu.Builder()
                .withContext(this.getApplication())//这个在7.0（包括7.0）以上以及大部分7.0以下的国产手机上需要用户授权，需要搭配<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
                .logo(BitmapFactory.decodeResource(getResources(),R.drawable.gaode))
                .drawCicleMenuBg(true)
                .backMenuColor(R.color.colorTrancent)
                .setBgDrawable(this.getResources().getDrawable(R.drawable.btn_float))
                //这个背景色需要和logo的背景色一致
                .setFloatItems(floatItemList)
                .defaultLocation(FloatLogoMenu.RIGHT)
                .drawRedPointNum(false)
                .showWithListener(new FloatMenuView.OnMenuClickListener() {
                    @Override
                    public void onItemClick(int position, String title) {
                        //Toast.makeText(LoginActivity.this, "position " + position + " title:" + title + " is clicked.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void dismiss() {

                    }
                });

    }

    private void initFloatBall(){

    }

    @Override
    protected void onStart() {
        if(timer==null){
            timer=new Timer();
            timerTask= new TimerTask(){
                public void run(){
                    Message message=new Message();
                    message.what= 5;
                    handler.sendMessage(message);
                }
            };
            timer.schedule( timerTask,ConstValue.time_refreshHotMap, ConstValue.time_refreshHotMap);
        }else{
            timer.schedule( timerTask,ConstValue.time_refreshHotMap, ConstValue.time_refreshHotMap);
        }
        Log.e("GGG","定时器启动");
        if (isShowConfirmFragment)
        {
            isShowConfirmFragment = false;
            ConfirmFragment confirmFragment = new ConfirmFragment();
            confirmFragment.show(getFragmentManager(),null);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(timer!=null){
            timer.cancel();
            timer = null;
            timerTask.cancel();
            timerTask = null;
            Log.e("GGG","定时器停止更新");
        }
        super.onStop();
    }

    public void showRoute(List<LatLonPoint> wayList){
        for(int i=0;i<wayList.size()/4;i++){
            //有wayList.size()/4 条路线
            LatLonPoint finalPoint=wayList.get(i*4+3);
            routeSearch=new RouteSearch(DriverActivity.this);
            routeSearch.setRouteSearchListener(DriverActivity.this);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(curentLatitude,curentLongitude),finalPoint);
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,0, wayList.subList(i*4,i*4+2), null, "");
            routeSearch.calculateDriveRouteAsyn(query);
        }

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
                // 如果不设置isFirstLoc标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (canMove) {
                    //设置缩放级别
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mCurLocation, ConstValue.mapScale, 0, 0)), 500, null);
                    //点击定位按钮 能够将地图的中心移动到定位点
                }
                mLocationListener.onLocationChanged(aMapLocation);
                //获取定位信息
                if(pointList==null||pointList.size()<1){
                    tryGetHotMap(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                }
                curentLatitude=aMapLocation.getLatitude();
                curentLongitude=aMapLocation.getLongitude();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                //Toast.makeText(this, "定位失败，请看后台Log.e", Toast.LENGTH_LONG).show();
                Log.e("GGG",errText);
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
        canMove = true;
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

    public void tryGetHotMap(double latitude,double longitude){
        if(Math.abs(latitude-0)<0.00001&&Math.abs(longitude-0)<0.00001)return;
        String address=ConstValue.getHotMapUrl(longitude,latitude);
        Log.e("GGG",address);
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
                    if(pointList==null){
                        pointList=new ArrayList<>();
                    }else{
                        pointList.clear();
                    }
                    for (int i=0;i < jsonArray.length();i++)
                    {
                        HotPoint n = new HotPoint();
                        JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i);
                        n.latitude=jsonObject.getDouble("latitude");
                        n.longitude=jsonObject.getDouble("longitude");
                        n.predictNumber=jsonObject.getInt("predictNumber");
                        pointList.add(n);
                    }
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }


            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(DriverActivity.this, "获取热力图数据成功！", Toast.LENGTH_SHORT).show();
                    drawHotMap();
                    searchOPI();
                    break;
                case 0:
                    Toast.makeText(DriverActivity.this, "无法获取热力图数据", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(DriverActivity.this, "获取热力图数据异常", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showRoute(wayList);
                    if(wayList.size()>0){
                        Toast.makeText(DriverActivity.this, "获取规划路线数据成功！", Toast.LENGTH_SHORT).show();
                        btn_start.setText("开始巡航");
                        for(int i=0;i<wayList.size()/4;i++){
                            Log.e("GGG","第"+(i+1)+"条路径:");
                            Log.e("GGG","第1个点:"+wayList.get(i*4).getLatitude()+"|"+wayList.get(i*4).getLongitude());
                            Log.e("GGG","第2个点:"+wayList.get(i*4+1).getLatitude()+"|"+wayList.get(i*4+1).getLongitude());
                            Log.e("GGG","第3个点:"+wayList.get(i*4+2).getLatitude()+"|"+wayList.get(i*4+2).getLongitude());
                            Log.e("GGG","终点:"+wayList.get(i*4+3).getLatitude()+"|"+wayList.get(i*4+3).getLongitude());
                        }
                    }else{
                        Toast.makeText(DriverActivity.this, "规划路线数据为空！", Toast.LENGTH_SHORT).show();
                        hasShowRoute=false;
                    }
                    break;
                case 3:
                    Toast.makeText(DriverActivity.this, "无法获取规划路线数据", Toast.LENGTH_SHORT).show();
                    hasShowRoute=false;
                    break;
                case 4:
                    Toast.makeText(DriverActivity.this, "获取规划路线数据异常", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Log.e("GGG","刷新一次");
                    aMap.clear();
                    btn_start.setText("查询路径");
                    hasShowRoute=false;
                    hasChoseRoute=-1;
                    tryGetHotMap(curentLatitude,curentLongitude);
                    break;
            }
        }
    };
    public void drawHotMap(){
        //生成热力点坐标列表
        WeightedLatLng[] latlngs = new WeightedLatLng[pointList.size()];
        for (int i = 0; i < pointList.size(); i++) {
            latlngs[i] = new WeightedLatLng(new LatLng(pointList.get(i).latitude,pointList.get(i).longitude),pointList.get(i).predictNumber);
        }

        // 构建热力图 HeatmapTileProvider
        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
        builder.weightedData(Arrays.asList(latlngs)); // 设置热力图绘制的数据

        // Gradient 的设置可见参考手册
        // 构造热力图对象
        HeatmapTileProvider heatmapTileProvider = builder.build();

        // 初始化 TileOverlayOptions
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
        // 向地图上添加 TileOverlayOptions 类对象
        aMap.addTileOverlay(tileOverlayOptions);


    }




    /**
     * 以下几个函数为导航的回调函数
     */



    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "初始化导航失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    /**
     * Poi检索
     * @param poiResult
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        List<PoiItem> poiList=poiResult.getPois();
        if(poiList.size()>0){
           PoiItem poiItem = poiList.get(0);
           String city=poiItem.getCityName();
            WeatherSearchQuery mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
            WeatherSearch mweathersearch=new WeatherSearch(this);
            mweathersearch.setOnWeatherSearchListener(this);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        }


    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 驾车线路
     */
    private void setDrivingRoute(DrivePath drivePath, LatLonPoint start, LatLonPoint end,String Color) {
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, start, end);
        drivingRouteOverlay.ColorStrin=Color;
        drivingRouteOverlay.setNodeIconVisibility(true);//设置节点（转弯）marker是否显示
        drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap();//去掉DriveLineOverlay上的线段和标记。
        drivingRouteOverlay.addToMap(markerClickListener); //添加驾车路线添加到地图上显示。
        aMap.addMarker((new MarkerOptions()).position(new LatLng(end.getLatitude(),end.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)).title("\u7EC8\u70B9"));
        drivingRouteOverlay.zoomToSpan();//移动镜头到当前的视角。
        drivingRouteOverlay.setRouteWidth(1);//设置路线的宽度
    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i == 1000) {
            DrivePath drivePath = driveRouteResult.getPaths().get(0);
            String colors[]={"#94e6ff00","#940dff00","#9400ffc4","#94003cff"};
            setDrivingRoute(drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos(),colors[colorNumber++]);
            //策略
            String strategy = drivePath.getStrategy();
            //总的交通信号灯数
            int clights = drivePath.getTotalTrafficlights();
            //距离 米：/1000转公里 1公里=1km
            float distance = drivePath.getDistance() / 1000;
            //时间 秒：、60转分
            long duration = drivePath.getDuration() / 60;

            Log.e("GGG", "onDriveRouteSearched: 路线规划成功");
        } else {
            Toast.makeText(DriverActivity.this, "onDriveRouteSearched: 路线规划失败", Toast.LENGTH_SHORT).show();
            Log.e("GGG", "onDriveRouteSearched: 路线规划失败");
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    public void tryGetDriverRoute(double latitude,double longitude) {
        String address = ConstValue.getDriverRouteUrl(longitude, latitude);
        Log.e("GGG", address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseText);
                    if (wayList == null) {
                        wayList = new ArrayList<>();
                    } else {
                        wayList.clear();
                    }
                    for(int j=0;j<jsonArray.length();j++){
                        JSONArray jsonArray1=jsonArray.getJSONArray(j);
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                            LatLonPoint latLonPoint = new LatLonPoint(jsonObject1.getDouble("latitude"),jsonObject1.getDouble("longitude"));
                            wayList.add(latLonPoint);
                        }
                    }
                    Log.e("GGG","请求了"+wayList.size()/4+"条路线的数据");
                    handler.sendEmptyMessage(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(4);
                }


            }
        });

    }


    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng latLng=marker.getPosition();
            Log.e("GGG","点击了");
            for(int i=0;i<wayList.size();i++){
                if(Math.abs(latLng.latitude-wayList.get(i).getLatitude())<=0.000001&&Math.abs(latLng.longitude-wayList.get(i).getLongitude())<=0.000001){
                    Log.e("GGG","点击的是第"+(i+1)/4+"条路线的终点:"+latLng.latitude+" "+latLng.longitude);
                    //Log.e("GGG",wayList.get(i).getLatitude()+" "+wayList.get(i).getLongitude());
                    hasChoseRoute=i;
                    break;
                }

            }
            return false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){// 点击了返回按键
            exitApp(3000);// 退出应用
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出应用
     * @param timeInterval 设置第二次点击退出的时间间隔
     */
    private void exitApp(long timeInterval) {
        if(System.currentTimeMillis() - firstTime >= timeInterval){
            Toast.makeText(DriverActivity.this, "再按一次退出程序",Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        }else {
            finish();// 销毁当前activity
            System.exit(0);// 完全退出应用
        }
    }


    public void searchOPI(){
        PoiSearch.Query query = new PoiSearch.Query("", "", "");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(1);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(curentLatitude,curentLongitude), ConstValue.SEARCHRADIUS));//设置周边搜索的中心点以及半径
        Log.e("GGG",curentLatitude+" "+curentLongitude);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }


    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null&&localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherlive = localWeatherLiveResult.getLiveResult();
                weather_temp.setText(weatherlive.getTemperature()+"°C");
                weather_cityName.setText(weatherlive.getCity());
                weather_now.setText(weatherlive.getWeather());
            }else {

            }
        }else {
            Log.e("GGG","无法获取天气信息");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}



