package com.yellfun.demo.ui.navigate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.indoorun.mapapi.control.Idr;
import com.indoorun.mapapi.control.NaviOptions;
import com.indoorun.mapapi.control.map.MapLoader;
import com.indoorun.mapapi.control.navi.NaviFrom;
import com.indoorun.mapapi.control.navi.NaviResult;
import com.indoorun.mapapi.domain.IdrMapRegionFloorUnit;
import com.indoorun.mapapi.map.gl.IdrMapView;
import com.indoorun.mapapi.view.SpinnerView;
import com.yellfun.demo.R;
import com.yellfun.demo.ui.BaseActionbarActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class NaviOptionStaticActivity extends BaseActionbarActivity {

    @BindView(R.id.map_switcher_view)
    IdrMapView mapView;

    @BindView(R.id.map_switcher)
    SpinnerView spinnerView;

    Idr idr;

    IdrMapRegionFloorUnit currentUnit;

    @BindView(R.id.navi_layout)
    View naviLayout;

    /**
     * 地图加载器，用来添加marker
     */
    MapLoader mapLoader;

    @BindView(R.id.navi_unit_name)
    TextView unitName;

    /**
     * 导航结果
     */
    private NaviResult naviResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_option);
        setTitleTxt("OPTION导航（静态导航）");
        idr = Idr.with(mapView);
        mapLoader = idr.loadRegion("14428254382730015")// 加载region
                .onMapUnitClick((mapLoader, unit) -> {
                    naviLayout.setVisibility(View.VISIBLE);
                    currentUnit = unit;
                    unitName.setText(unit.getName());
                    return true;
                })
                .loadFloor(spinnerView);//加载楼层切换器
        idr.naviOptions().setType(NaviOptions.Type.STATIC_NAVI)//设置成静态导航
                .setSwitcher(mapLoader);//设置操作marker的对象
    }

    @OnClick(R.id.navi_stop_btn)
    public void stopNavi() {
        if (naviResult != null) {//停止导航
            naviResult.stopNavi();
            idr.naviOptions().setToPoint(null);
            idr.naviOptions().setFromPoint(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        idr.begin();
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

    @OnClick(R.id.navi_start)
    public void naviStart() {
        if (currentUnit != null) {
            mapLoader.removeMarkerByClass(NaviFrom.CLASS_MARKER_START);
            idr.naviOptions().setFromFloorId(currentUnit.getFloorId())
                    .setFromPoint(currentUnit.getPointF())
                    .setFromMarker(R.mipmap.start_position);
            if (naviResult != null) {
                naviResult.stopNavi(true, false);//第一个参数，是否清除起点marker，第二个参数，是否清除终点marker
            }
            navi();
        }
    }

    @OnClick(R.id.navi_stop)
    public void naviStop() {
        if (currentUnit != null) {
            mapLoader.removeMarkerByClass(NaviFrom.CLASS_MARKER_END);
            idr.naviOptions().setToFloorId(currentUnit.getFloorId())
                    .setToPoint(currentUnit.getPointF())
                    .setToMarker(R.mipmap.car_position);
            if (naviResult != null) {
                naviResult.stopNavi(false, true);//第一个参数，是否清除起点marker，第二个参数，是否清除终点marker
            }
            navi();
        }
    }

    private void navi() {
        if (!TextUtils.isEmpty(idr.naviOptions().getFromFloorId())
                && !TextUtils.isEmpty(idr.naviOptions().getToFloorId())
                && idr.naviOptions().getFromPoint() != null
                && idr.naviOptions().getToPoint() != null) {
            naviResult = idr.startNavi();
        }
    }
}
