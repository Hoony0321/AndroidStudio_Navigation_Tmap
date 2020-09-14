package com.example.googlemap_navigation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;

public class MainActivity extends AppCompatActivity {

    //지도 변수 선언
    private LinearLayout linearLayoutTmap;
    private Context context;
    private TMapView tMapView;

    //검색창 관련 변수 선언
    private EditText inputStart_editText;
    private EditText inputEnd_editText;
    private ListView searchResult_listView;
    private List<String> list_data;
    private ArrayAdapter<String> adapter;
    private boolean display_listView = false;
    private boolean startLocation_finish = false;

    private Button findPath_btn;
    private Button change_btn;



    //이동수단 경로 선택 변수 선언
    private Button select_car_btn;
    private Button select_bus_btn;
    private Button select_pedestrian_btn;
    private int findPath_case = 2; // default => 보행자



    //TMap API 변수 선언
    private TMapData tMapData;


    //Marker 관련 변수 선언
    private Bitmap bitmap;
    private TMapPoint tMapPoint;
    private Marker startMarker;
    private Marker endMarker;

    //경로 안내 detail info 관련 변수 선언
    private Button detailInfo_path_btn;
    private Element root;

    //현위치 표시 관련 변수 선언
    private Button showCurPosition_btn;
    Bitmap bitmap2;
    TMapGpsManager tMapGpsManager;
    TMapPoint curPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //----------------------------------------------------------//
        //------------------------ 변수 선언 ------------------------//
        //----------------------------------------------------------//


        //검색창 변수 설정
        inputStart_editText = (EditText) findViewById(R.id.inputStart_editText);
        inputEnd_editText = (EditText) findViewById(R.id.inputEnd_editText);
        searchResult_listView = (ListView)findViewById(R.id.searchResultList);
        list_data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_data);
        searchResult_listView.setAdapter(adapter);

        findPath_btn = (Button)findViewById(R.id.findPath_btn);
        change_btn = (Button)findViewById(R.id.change_btn);

        //지도 변수 설정
        linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        context = this;
        tMapView = new TMapView(context);

        //TMap API 변수 설정
        tMapData = new TMapData();

        //Marker 관련 변수 설정
        startMarker = new Marker();
        endMarker = new Marker();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_star);
        tMapPoint = new TMapPoint(0,0);

        //이동수단 경로 선택 변수 선언
        select_car_btn = (Button)findViewById(R.id.select_car_btn);
        select_bus_btn = (Button)findViewById(R.id.select_bus_btn);
        select_pedestrian_btn = (Button)findViewById(R.id.select_pedestrian_btn);
        select_pedestrian_btn.setSelected(true);

        //경로 안내 detail info 관련 변수 설정
        detailInfo_path_btn = (Button)findViewById(R.id.showDetailInfo_Path);
        detailInfo_path_btn.setVisibility(View.INVISIBLE);

        //현위치 표시 관련 변수 설정
        showCurPosition_btn = (Button)findViewById(R.id.showCurPosition_Btn);
        tMapGpsManager = new TMapGpsManager(context);
        tMapGpsManager.setMinTime(1000);
        tMapGpsManager.setMinDistance(5);
        tMapGpsManager.setProvider(tMapGpsManager.NETWORK_PROVIDER);


        //----------------------------------------------------------//
        //------------------------ 기능 구현 ------------------------//
        //----------------------------------------------------------//

        //지도 설정
        context = this;
        tMapView = new TMapView(context);
        tMapView.setHttpsMode(true);
        tMapView.setSKTMapApiKey( "l7xx71d0f56b9dd94407a77a7a6ca24e4eef" );
        linearLayoutTmap.addView( tMapView );


        //검색창 입력 이벤트 설정
        inputStart_editText.addTextChangedListener(new TextWatcher() {
            String input_locationPOI;

            //입력하기 전 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //입력할 때  이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) return;



                if(!display_listView){
                    startLocation_finish = false;
                    linearLayoutTmap.setVisibility(View.INVISIBLE);
                    searchResult_listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }


                //ListView 내용 초기화
                list_data.clear();

                if(s.toString().length() < 2){ //2개 미만은 검색 X
                    adapter.notifyDataSetChanged();
                    return;
                }


                input_locationPOI = s.toString();

                tMapData.findAllPOI(input_locationPOI, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if(!arrayList.isEmpty()){
                            for(int i = 0; i < arrayList.size(); i++) {
                                TMapPOIItem item = arrayList.get(i);
                                list_data.add(item.getPOIName());

                            }
                            //새로운 Thread 생성
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
                    }

                });


            }

            //입력 후 이벤트
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputEnd_editText.addTextChangedListener(new TextWatcher() {
            String input_locationPOI;

            //입력하기 전 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //입력할 때  이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) return;

                //ListView 내용 초기화
                list_data.clear();
                if(!display_listView){
                    startLocation_finish = true;
                    linearLayoutTmap.setVisibility(View.INVISIBLE);
                    searchResult_listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }




                if(s.toString().length() < 2){ //2개 미만은 검색 X
                    adapter.notifyDataSetChanged();
                    return;
                }


                input_locationPOI = s.toString();

                tMapData.findAllPOI(input_locationPOI, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if(!arrayList.isEmpty()){
                            for(int i = 0; i < arrayList.size(); i++) {
                                TMapPOIItem item = arrayList.get(i);
                                list_data.add(item.getPOIName());

                            }
                            //새로운 Thread 생성
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
                    }

                });


            }

            //입력 후 이벤트
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //검색창 클릭 이벤트 설정
        searchResult_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int index = 0;
            Marker marker;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tMapData.findAllPOI(list_data.get(position), 1, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        TMapPOIItem item;
                        item = arrayList.get(0);
                        marker = new Marker(item.getPOIName(), item.getPOIAddress(), "None",item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());


                        if (!startLocation_finish) {
                            inputStart_editText.setText(marker.getName());
                            startMarker = marker;
                        } else {
                            inputEnd_editText.setText(marker.getName());
                            endMarker = marker;
                        }


                        //카메라 이동
                        tMapView.setCenterPoint(marker.getLongitude(),marker.getLatitude());

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

                        if(!startLocation_finish){
                            startMarker.setMarker_id("markerItem_1");
                            tMapView.addMarkerItem(startMarker.getMarker_id(), tMapMarkerItem); // 지도에 마커 추가
                        }
                        else{
                            endMarker.setMarker_id("markerItem_2");
                            tMapView.addMarkerItem(endMarker.getMarker_id(), tMapMarkerItem); // 지도에 마커 추가
                        }


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        // 해당 작업을 처리함
                                        linearLayoutTmap.setVisibility(View.VISIBLE);
                                        searchResult_listView.setVisibility(View.INVISIBLE);
                                        display_listView = false;
                                    }
                                });
                            }
                        }).start();


                    }
                });



            }

        });

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //endMaker & startMarker 정보 swap
                Marker marker_instance = startMarker;
                startMarker = endMarker;
                endMarker = marker_instance;

                //editText text 내용 swap
                inputStart_editText.setText(startMarker.getName());
                inputEnd_editText.setText(endMarker.getName());

                //listView 안 보이게 설정
                startLocation_finish = true;
                linearLayoutTmap.setVisibility(View.VISIBLE);
                searchResult_listView.setVisibility(View.INVISIBLE);
                display_listView = false;


            }
        });

        findPath_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //detail Info 버튼 활성화
                detailInfo_path_btn.setVisibility(View.VISIBLE);


                //카메라 중심정 이동
                TMapPoint tMapPoint_instance = new TMapPoint((startMarker.getLatitude() + endMarker.getLatitude())/2 ,
                        (startMarker.getLongitude() + endMarker.getLongitude())/2);
                tMapView.setCenterPoint(tMapPoint_instance.getLongitude(),tMapPoint_instance.getLatitude(),true);
                tMapView.setZoomLevel(14);


                //경로 안내
                switch (findPath_case){
                    case 0:     //자동차
                        FindPath_Car();
                        break;
                    case 1:     //대중교통
                        FindPath_Bus();
                        break;
                    case 2:     //보행자
                        FindPath_Pedestrian();
                        break;


                }
            }
        });

        //이동수단 버튼 클릭 이벤트 설정
        //자동차
        select_car_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_car_btn.setSelected(true); //선택한 버튼 클릭 상태로 유지
                SelectTransPort(0); //그 전에 선택한 버튼 클릭 상태 해제 및 findPath_case 값 변경
            }
        });
        //대중교통
        select_bus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_bus_btn.setSelected(true); //선택한 버튼 클릭 상태로 유지
                SelectTransPort(1); //그 전에 선택한 버튼 클릭 상태 해제 및 findPath_case 값 변경
            }
        });
        //보행자
        select_pedestrian_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_pedestrian_btn.setSelected(true); //선택한 버튼 클릭 상태로 유지
                SelectTransPort(2); //그 전에 선택한 버튼 클릭 상태 해제 및 findPath_case 값 변경
            }
        });


        //경로 안내 자세한 정보 버튼 클릭
        detailInfo_path_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalDistance = 0; //총거리
                int crosswalk_number = 0; //횡단보도 개수
                int overpass_number = 0; // 육교 개수
                int undergroundWalkway_number = 0; //지하보도 개수
                int tunnel_number = 0; //터널 개수
                int number = 0; //임시 숫자
                String message_simple = ""; //실제 경로 안내 간단한 형태
                String message_detail = "<===== 자세한 정보 =====>\n"; //실제 경로 안내 자세한 형태


                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();

                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        String str = "";
                        int index = 0;
                        System.out.println(nodeListPlacemarkItem.item(j).getNodeName());
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                            str = nodeListPlacemarkItem.item(j).getTextContent().trim();
                            index = str.indexOf(",");
                            if(index == -1){
                                message_detail += str + "\n";
                            }
                        }
                        else if(nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:facilityType")){
                            System.out.println(nodeListPlacemarkItem.item(j).getTextContent().trim());
                            if(nodeListPlacemarkItem.item(j).getTextContent().trim() != ""){
                                number = Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim());
                                switch (number){
                                    case 1: //교량
                                        break;
                                    case 2: //터널
                                        tunnel_number++;
                                        break;
                                    case 3: //고가도로
                                        break;
                                    case 11: //일반보행자도로
                                        break;
                                    case 12: //육교
                                        overpass_number++;
                                        break;
                                    case 14: //지하보도
                                        undergroundWalkway_number++;
                                        break;
                                    case 15: //횡단보도
                                        crosswalk_number++;
                                        break;
                                    case 16: //대형시설물이동통로
                                        break;
                                    case 17: //계단
                                        break;
                                }
                            }



                        }
                        else if(nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:distance")){
                            totalDistance += Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim());
                        }
                    }
                }
                message_simple = "총 거리 : " + totalDistance/2 + "m" + "\n" +
                        "횡단보도 개수 : " + crosswalk_number/2 + "\n" +
                        "육교 개수 : " + overpass_number/2 + "\n" +
                        "지하보도 개수 : " + undergroundWalkway_number/2 + "\n" +
                        "터널 개수 : " + tunnel_number/2 + "\n";

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("출발 : " + startMarker.getName() + "\n" + "도착 : " + endMarker.getName());
                builder.setMessage(message_simple + message_detail);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

        //현위치 표시 버튼 클릭 이벤트
        showCurPosition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gps.setProvider(gps.GPS_PROVIDER); // 핸드폰일때?? 잘 모르겠음

                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
                    }
                    return;
                }

                tMapGpsManager.OpenGps();

                //현위치 잡기
                curPosition = new TMapPoint(tMapGpsManager.getLocation().getLatitude(), tMapGpsManager.getLocation().getLongitude());

                //현위치 마커 설정
                TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();


                tMapMarkerItem.setIcon(bitmap2); // 마커 아이콘 지정
                tMapMarkerItem.setCanShowCallout(true);
                tMapMarkerItem.setAutoCalloutVisible(true);
                tMapMarkerItem.setTMapPoint(curPosition); //마커 좌표 설정

                //마커 Title & SubTitle 지정
                tMapMarkerItem.setCalloutTitle("현위치");

                //현위치 마커 표시
                tMapView.addMarkerItem("marker_curPosition",tMapMarkerItem);


                //현위치로 카메라 이동
                tMapView.setCenterPoint(curPosition.getLongitude(),curPosition.getLatitude(),false);


                System.out.println(curPosition.getLatitude() + " " + curPosition.getLongitude());


            }
        });





   }


   //보행자 경로 찾는 함수
    public void FindPath_Pedestrian(){
        TMapPoint startpoint = new TMapPoint(startMarker.getLatitude(),startMarker.getLongitude());
        TMapPoint endpoint = new TMapPoint(endMarker.getLatitude(),endMarker.getLongitude());


        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapView.addTMapPath(tMapPolyLine);
            }
        });

        tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        root = document.getDocumentElement();
                    }
                }
        );
    }
    public void FindPath_Car(){
        TMapPoint startpoint = new TMapPoint(startMarker.getLatitude(),startMarker.getLongitude());
        TMapPoint endpoint = new TMapPoint(endMarker.getLatitude(),endMarker.getLongitude());


        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapView.addTMapPath(tMapPolyLine);
            }
        });

        tMapData.findPathDataAllType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        root = document.getDocumentElement();
                    }
                }
        );
    }
    public void FindPath_Bus(){
        TMapPoint startpoint = new TMapPoint(startMarker.getLatitude(),startMarker.getLongitude());
        TMapPoint endpoint = new TMapPoint(endMarker.getLatitude(),endMarker.getLongitude());


        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapView.addTMapPath(tMapPolyLine);
            }
        });

        tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        Element root = document.getDocumentElement();
                        NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                        for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                            NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();

                            for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                                if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                                    Log.d("debug", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                                }
                            }
                        }
                    }
                }
        );
    }

    //이동수단 버튼 클릭 이벤트 관련 함수
    public void SelectTransPort(int value){
        if(findPath_case == value) return;
        switch (findPath_case){
            case 0:
                select_car_btn.setSelected(false);
                break;
            case 1:
                select_bus_btn.setSelected(false);
                break;
            case 2:
                select_pedestrian_btn.setSelected(false);
                break;
        }

        findPath_case = value;
    }
}
