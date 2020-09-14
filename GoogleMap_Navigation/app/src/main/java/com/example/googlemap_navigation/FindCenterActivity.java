package com.example.googlemap_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FindCenterActivity extends AppCompatActivity {


    //지도 변수 선언
    private LinearLayout linearLayoutTmap;
    private Context context;
    private TMapView tMapView;

    //장소 검색 관련 변수 선언
    private Button searchBtn;
    private EditText inputLocation_editText;
    private ListView listView;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private TMapData tMapData;
    private boolean list_display = false;

    private ArrayList<Marker> markerList;

    //마커 관렵 변수 선언
    Bitmap bitmap;
    Bitmap bitmap2;
    TMapPoint tMapPoint;

    //중심지 찾기 이벤트 관련 변수 선언
    private Button findCenter_btn;
    private TMapPoint centerPoint;
    private Marker centerMarker;

    //줌 관련 변수 선언
    private TMapPoint leftTop;
    private TMapPoint rightBottom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_center);

        //지도 변수 설정
        linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        context = this;
        tMapView = new TMapView(context);

        //장소 검색 관련 변수 설정
        searchBtn = (Button)findViewById(R.id.search_location_btn);
        inputLocation_editText = (EditText) findViewById(R.id.inputLocation_editText);
        listView = (ListView)findViewById(R.id.locationListView);
        tMapData = new TMapData();
        markerList = new ArrayList<Marker>();
        listData = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        listView.setVisibility(View.INVISIBLE);

        //마커 관련 변수 설정
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_star);
        tMapPoint = new TMapPoint(0,0);

        //중심지 찾기 이벤트 관련 변수 설정
        findCenter_btn = (Button)findViewById(R.id.findCenter_btn);
        centerMarker = new Marker();
        centerPoint = new TMapPoint(0.0,0.0);

        //줌 관련 변수 선언
        leftTop = new TMapPoint(32,133);
        rightBottom = new TMapPoint(44, 123);

        //----------------------------------------------------------//
        //------------------------ 기능 구현 ------------------------//
        //----------------------------------------------------------//

        //지도 설정
        context = this;
        tMapView = new TMapView(context);
        tMapView.setHttpsMode(true);
        tMapView.setSKTMapApiKey( "l7xx71d0f56b9dd94407a77a7a6ca24e4eef" );
        linearLayoutTmap.addView( tMapView );


        //장소 검색 버튼 클릭 이벤트
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ListView 활성화 & 지도 비활성화
                linearLayoutTmap.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                list_display = true;

                //listData 초기화
                listData.clear();

                //입력받은 장소 이름으로 POI검색 & ListView에 반영
                String inputLocation = inputLocation_editText.getText().toString();
                tMapData.findAllPOI(inputLocation, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if(!arrayList.isEmpty()){
                            for(int i = 0; i < arrayList.size(); i++) {
                                TMapPOIItem item = arrayList.get(i);
                                listData.add(item.getPOIName());
                            }
                        }
                        //새로운 Thread 생성 - ListView에 반영
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();
                        }


                });
            }
        });

        //listView 아이템 클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            Marker marker;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tMapData.findAllPOI(listData.get(position), 1, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        TMapPOIItem item;
                        item = arrayList.get(0);
                        marker = new Marker(item.getPOIName(), item.getPOIAddress(), "marker_" + markerList.size(),item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                        //마커 위치로 카메라 이동
                        tMapView.setCenterPoint(marker.getLongitude(),marker.getLatitude(),true);

                        //Point 좌표 설정
                        tMapPoint.setLongitude(marker.getLongitude());
                        tMapPoint.setLatitude(marker.getLatitude());

                        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();


                        tMapMarkerItem.setIcon(bitmap); // 마커 아이콘 지정
                        tMapMarkerItem.setCanShowCallout(true);
                        tMapMarkerItem.setAutoCalloutVisible(true);
                        tMapMarkerItem.setTMapPoint(tMapPoint); //마커 좌표 설정

                        //마커 Title & SubTitle 지정
                        tMapMarkerItem.setCalloutTitle(marker.getName());
                        tMapMarkerItem.setCalloutSubTitle(marker.getAddress().trim());

                        //지도에 마커 추가
                        tMapView.addMarkerItem(marker.getMarker_id(),tMapMarkerItem);

                        //마커 위치 비교 - 후에 Zoom 크기에 이용
                        ComparePoint(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());

                        //markerList에 추가
                        markerList.add(marker);

                        //지도 활성화 & listView 비활성화
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        // 해당 작업을 처리함
                                        linearLayoutTmap.setVisibility(View.VISIBLE);
                                        listView.setVisibility(View.INVISIBLE);
                                        list_display = false;
                                    }
                                });
                            }
                        }).start();


                    }
                });



            }

        });


        //중심지 찾기 버튼 클릭 이벤트
        findCenter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double center_Latitude = 0.0;
                double center_Longitude = 0.0;
                int list_size = markerList.size();

                for(int i = 0; i < list_size; i++){
                    Marker marker = markerList.get(i);
                    center_Latitude += marker.getLatitude();
                    center_Longitude += marker.getLongitude();
                }


                //center Point 설정
                centerPoint = new TMapPoint(center_Latitude/list_size, center_Longitude/list_size);

                //가장 가까운 지하철 역 검색 -> 중심지
                tMapData.findAroundNamePOI(centerPoint, "지하철", new TMapData.FindAroundNamePOIListenerCallback() {
                    @Override
                    public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {
                        if(arrayList == null) return;

                        TMapPOIItem item = arrayList.get(0);
                        Marker marker_instance = new Marker(item.getPOIName(),item.getPOIAddress(),"marker_center",
                                item.getPOIPoint().getLatitude(),item.getPOIPoint().getLongitude());

                        centerMarker = marker_instance;
                        System.out.println(item.getPOIName());

                        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();


                        tMapMarkerItem.setIcon(bitmap2); // 마커 아이콘 지정
                        tMapMarkerItem.setCanShowCallout(true);
                        tMapMarkerItem.setAutoCalloutVisible(true);
                        centerPoint = new TMapPoint(centerMarker.getLatitude(),centerMarker.getLongitude());
                        tMapMarkerItem.setTMapPoint(centerPoint); //마커 좌표 설정

                        //마커 Title & SubTitle 지정
                        tMapMarkerItem.setCalloutTitle(centerMarker.getName());
                        tMapMarkerItem.setCalloutSubTitle(centerMarker.getAddress().trim());

                        //지도에 마커 추가
                        tMapView.addMarkerItem(centerMarker.getMarker_id(),tMapMarkerItem);

                        //마커 위치 비교 - 후에 Zoom 크기에 이용
                        ComparePoint(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());

                        //카메라 이동
                        System.out.println(centerPoint.getLatitude() + " " + centerPoint.getLongitude());
                        System.out.println(leftTop.getLatitude() + " " + leftTop.getLongitude() );
                        System.out.println(rightBottom.getLatitude() + " " + rightBottom.getLongitude() );
                        tMapView.zoomToTMapPoint(leftTop,rightBottom);
                        tMapView.setCenterPoint(centerPoint.getLongitude(),centerPoint.getLatitude(),true);

                    }
                });



            }
        });

    }

    public void ComparePoint(double latitude, double longitude){
        //왼쪽 비교
        if(leftTop.getLatitude() < latitude) leftTop.setLatitude(latitude);
        //오른쪽 비교
        else if(rightBottom.getLatitude() > latitude) rightBottom.setLatitude(latitude);
        //아래 비교
        if(rightBottom.getLongitude() < longitude) rightBottom.setLongitude(longitude);
        //위 비교
        else if(leftTop.getLongitude() > longitude) leftTop.setLongitude(longitude);
    }


}
