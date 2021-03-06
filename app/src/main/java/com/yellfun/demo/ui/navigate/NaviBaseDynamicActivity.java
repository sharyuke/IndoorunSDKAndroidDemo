package com.yellfun.demo.ui.navigate;

import android.os.Bundle;

import com.indoorun.mapapi.control.Idr;
import com.indoorun.mapapi.control.navi.NaviResult;
import com.indoorun.mapapi.map.gl.IdrMapView;
import com.indoorun.mapapi.view.SpinnerView;
import com.yellfun.demo.R;
import com.yellfun.demo.ui.BaseActionbarActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class NaviBaseDynamicActivity extends BaseActionbarActivity {

    @BindView(R.id.map_switcher_view)
    IdrMapView mapView;

    @BindView(R.id.map_switcher)
    SpinnerView spinnerView;

    Idr idr;

    NaviResult naviResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_base_dynamic);
        setTitleTxt("基本方法（动态导航）");
        idr = Idr.with(mapView);
        idr.locateWithSwitcher().bindStartAndStopLocateToMapHelper();// 开启定位
        idr.loadRegion("14428254382730015")// 加载region
                .onMapUnitClick((mapLoader, unit) -> {
                    if (naviResult != null) {// 如果上一个导航没结束，那么结束上一次导航
                        naviResult.stopNavi();
                    }
                    naviResult = idr.naviFrom().to(unit, R.mipmap.car_position).startNavi();//开启动态导航
                    return true;
                })
                .loadFloor(spinnerView);//加载楼层切换器
    }

    @OnClick(R.id.navi_stop_btn)
    public void stopNavi() {
        if (naviResult != null) {//停止导航
            naviResult.stopNavi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        idr.begin();// 调用此方法，用来resume MapView，以此来初始化地磁传感器
    }

    @Override
    protected void onPause() {
        idr.end();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        idr.destory();
        super.onDestroy();
    }
}
