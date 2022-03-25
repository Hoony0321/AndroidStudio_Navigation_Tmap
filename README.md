# Introduction
Tmap 지도 API를 이용한 Navigation Application입니다.
TTS & STT 기능이 들어가 음성 인식 및 텍스트 듣기가 가능합니다.
주요 기능은 크게 2가지로 중심지 찾기랑 시각장애인을 위한 Navigation 기능입니다.


# 주요 기능

## 1. 중심지 찾기
입력된 Location들을 기반으로 중심지를 찾는 기능입니다.
1. 비교하고 싶은 Location들의 위치를 입력합니다.
2. 입력된 Location의 위도와 경도의 평균 값을 구합니다.
3. 해당 평균 값과 가장 인접한 지하철 역을 찾아 이용자에게 보여줍니다.


## 2. 시각장애인을 위한 Navigation
시각장애인을 위한 Navigation 기능입니다.
기본적으로 TTS & STT 기능이 들어가 있어 음성 인식 및 텍스트 듣기가 가능합니다.
1. 음성 인식 버튼을 클릭합니다.
2. 출발지 - 도착지 순서로 마이크를 통해 Location을 입력합니다.
3. 입력된 음성 데이터를 통해 출발지 - 도착지 Navigation 경로를 맵에 그립니다.
4. detail 버튼을 통해 해당 Navigation 경로를 음성으로 들을 수 있습니다.


## 한계점
해당 Application은 시각장애인을 위해 만들었다고 하지만 많은 한계점이 존재합니다.
일단 TTS(Text To Speak) 음성인식 과 STT(Speak To Text) 텍스트 듣기 기능이 있기는 하지만 
해당 기능을 사용하기 위해선 버튼을 눌러야 한다는 명확한 한계점이 있습니다.
차후에 해당 버튼 클릭을 TTS 기능으로 대체하여 해결해보겠습니다.




# 발생문제 및 해결방법

## 1. Marker 다중 생성
지도 API를 통해 길찾기 기능, 중심지 찾기 기능을 구현하기 위해선 여러 위치에 Marker 표시를 해야했습니다. 

그래서 TMapMarkerItem 객체를 만들어 이를  TMapView 객체에 추가해줬습니다. 


하지만 이상하게 Marker를 추가하면 기존에 있던 Marker가 사라졌습니다. 

처음에는 동일한 Marker 객체가 중복되어 사용되어서 기존의 Marker가 사라졌다고 생각했습니다.

그래서 아예 Array를 만들어 매번 new TMapMarkerItem() 객체를 넣어주었습니다. 

하지만 서로 다른 MarkerItem임에도 불구하고 기존의 Marker들이 사라졌습니다.

이에 저는 동일한 Marker 객체의 중복 사용 문제가 아닌 Marker 객체를 지도에 추가될 때가 문제라고 생각했습니다.

그래서 Marker를 MapView에 추가해주는 addMarkerItem Method를 자세히 살펴봤습니다.

알고 보니 첫 번째 parameter가 Marker의 ‘name’이 아닌 ‘id’였습니다. 

동일한 객체를 사용한 게 아닌 동일한 id를 계속 사용하여 기존의 Marker들이 사라졌던 것입니다.

이에 Marker마다 다른 id를 부여해주고 이 문제를 해결했습니다.

이런 사소한 착각 하나로 인해 저는 많은 시간을 소비하였습니다. 이로 인해 사용하는 Method에 대한 정확한 파악이 필수라는 교훈을 얻었습니다.


# 프로젝트 시연 영상
[![프로젝트_시연영상](https://youtu.be/vkOmFKlFj4Q/0.jpg)](https://youtu.be/vkOmFKlFj4Q)
