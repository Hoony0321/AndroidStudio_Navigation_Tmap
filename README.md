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




# 화면
<img src="https://user-images.githubusercontent.com/50730897/163183414-6b91f5c5-8df1-4fca-8df7-983621c883fd.jpg"  width="360" height="600"/>
<img src="https://user-images.githubusercontent.com/50730897/163183427-e0b82d2a-2b8b-4bb8-8f08-94d9d21c98c1.jpg"  width="360" height="600"/>
<img src="https://user-images.githubusercontent.com/50730897/163184229-3eff4b75-38be-4f97-a30d-62ad2833c908.jpg"  width="360" height="600"/>


# 프로젝트 시연 영상
[![프로젝트_시연영상](https://youtu.be/vkOmFKlFj4Q/0.jpg)](https://youtu.be/vkOmFKlFj4Q)
