package com.example.a12745.easytravel.navi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.a12745.easytravel.R;
import com.example.a12745.easytravel.activity.DriverActivity;

import fragment.ConfirmFragment;

/**
 * 按照选定策略导航
 */
public class RouteNaviActivity extends FragmentActivity implements AMapNaviListener {

	AMapNavi mAMapNavi;

	Fragment mNaviFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_basic_navi);

		//到达目的地时需要再设置为false
        DriverActivity.isShowConfirmFragment = true;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}

		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(this);
		mAMapNavi.setUseInnerVoice(true);

		mAMapNavi.setEmulatorNaviSpeed(60);
		boolean gps=getIntent().getBooleanExtra("gps", false);
		if(gps){
			mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
		}else{
			mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
		}
		setUpMapIfNeeded();

	}

	private void setUpMapIfNeeded() {
		if (mNaviFragment == null) {
			mNaviFragment = getSupportFragmentManager().findFragmentById(R.id.navi_fragment);
		}
	}

	@Override
	protected void onResume() {
		setUpMapIfNeeded();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		//
		//        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
		//        mAMapNavi.stopNavi();
		mAMapNavi.pauseNavi();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNavi.stopNavi();
	}

	//back按键返回时也传递信息
	@Override
	public void onBackPressed()
	{
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
        DriverActivity.isShowConfirmFragment = false;
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

}
