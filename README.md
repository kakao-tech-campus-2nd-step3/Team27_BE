# Team27_BE

27조 백엔드

## 코드리뷰 가이드

```
코드리뷰란 주차 별 Master branch 에서 Review branch로 받아온 업데이트 내용을 담당 멘토님이 확인 후 리뷰하는 프로그램입니다.

코드리뷰는 Step3 기간 동안 총 6회 진행됩니다.
```

1️⃣ 업데이트 된 Review 브랜치에 특히 코드리뷰 받고 싶은 부분, 개발 질문 등을 포함하여 readme 파일 기재 후 PR

* Review branch는 **조 내 Admin(조장 or 테크리더)**이 **금요일 자정까지 pr**해야 함

2️⃣ pr을 한 뒤, **테크리더는 슬랙 채널에서 멘토님에게 pr을 했음을 @태깅하여 안내**

3️⃣ 해당 내용 바탕으로 코드 리뷰 서면으로 주말간 진행

### 1. KTC Step 3 Github 워크 플로우

![](https://quickest-asterisk-75d.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F3ef8dbd9-414c-4cf5-813d-32ecb943cc67%2F1c31cc8b-572d-489e-8d91-0f03256383b8%2F%25EB%25A0%2588%25ED%258F%25AC%25EC%25A7%2580%25ED%2586%25A0%25EB%25A6%25AC_%25EC%259A%25B4%25EC%2598%2581-001.png?table=block&id=1ff4373d-03f6-41a8-9f47-35c9f0ed5f4a&spaceId=3ef8dbd9-414c-4cf5-813d-32ecb943cc67&width=2000&userId=&cache=v2)

### 2. 27조 BE Github 워크플로우

![](https://file.notion.so/f/f/3ef8dbd9-414c-4cf5-813d-32ecb943cc67/822bb9d2-059f-40ab-b783-0b3583f0c051/image.png?table=block&id=17baad15-c64b-40d0-a8bf-8a4bd70be422&spaceId=3ef8dbd9-414c-4cf5-813d-32ecb943cc67&expirationTimestamp=1726826400000&signature=595SMdlc2zCb_Pe_6zog650S9eplq7DqAG82pG2P2S8&downloadName=image.png)

![](https://file.notion.so/f/f/3ef8dbd9-414c-4cf5-813d-32ecb943cc67/c8f314f1-8cfa-4e17-b88b-36942639e860/image.png?table=block&id=64755b80-5050-44cd-af56-5d5a480403db&spaceId=3ef8dbd9-414c-4cf5-813d-32ecb943cc67&expirationTimestamp=1726912800000&signature=KTt11_9vy5MHWI3X0-oMkE-tG0Of71m4hh31e63kYxI&downloadName=image.png)

## 프로젝트 구조

### API 명세서

| 기능                                                                                                   | 분류 키워드 | 설명                                                        | API PATH                             | HTTP Method |
| ------------------------------------------------------------------------------------------------------ | ----------- | ----------------------------------------------------------- | ------------------------------------ | ----------- |
| [OAuth 로그인](https://www.notion.so/OAuth-f34e1ff68c3942509ad4a8723f5f38df?pvs=4)                     | `로그인`    | OAuth를 통한 로그인을 한다.                                 | /api/login/{provider}                | `GET`       |
| [반려동물 정보 등록](https://www.notion.so/cf1cd4f7c1fa47b5b3828c6c04f14df1?pvs=4)                     | `로그인`    | 맨 처음 회원가입시,
반려동물의 정보를 등록한다.        | /api/pet                             | `POST`      |
| [사용자 정보 등록](https://www.notion.so/411271dbc3bc45548a50ae8fea9bace2?pvs=4)                       | `같이찾기`  | 맨 처음 회원가입시,
사용자의 정보를 등록한다.          | /api/member                          | `POST`      |
| [실종신고](https://www.notion.so/bdc11c8f48d44a02ac97a53fea21fa43?pvs=4)                               | `같이찾기`  | 애완동물을 실종 신고한다.                                   | /api/missing                         | `POST`      |
| [근처 실종 동물 나열](https://www.notion.so/0643c7dd9f544513a264cce25442f5ff?pvs=4)                    | `같이찾기`  | 사용자 근방에 있는 실종 신고를 가져온다.                    | /api/missing?region={region}         | `GET`       |
| [실종 동물 세부사항 확인](https://www.notion.so/158f788a4a4b4cc498181bfd02ab101e?pvs=4)                | `같이찾기`  | 실종 신고의 세부 사항을 가져온다.                           | /api/missing/{missing\_id}           | `GET`       |
| [제보 확인](https://www.notion.so/18a37b653f0e49aa942a541cd0fb2b9e?pvs=4)                              | `같이찾기`  | 자신에게 온 실종 제보를 가져온다.                           | /api/missing/report                  | `GET`       |
| [제보 세부사항 확인](https://www.notion.so/926553777f314203bc30023293e9462a?pvs=4)                     | `같이찾기`  | 자신에게 온 실종 제보의 세부사항을 가져온다.                | /api/missing/report/{report\_id}     | `GET`       |
| [실종 의심 동물 실종 신고](https://www.notion.so/e1f891f1f44c4bee82905a1239230316?pvs=4)               | `같이찾기`  | 실종 상태로 의심되는 동물을 실종 신고한다.                  | /api/missing/suspect                 | `POST`      |
| [실종 의심 동물 실종 신고 나열](https://www.notion.so/d27c0c4e2da64dc29a2f397bea40d87a?pvs=4)          | `같이찾기`  | 실종 상태로 의심되는 실종 신고를 가져온다.                  | /api/missing/suspect?region={region} | `GET`       |
| [실종 의심 동물 실종 신고 세부사항 확인](https://www.notion.so/a6c34c07fe9b45e6849f08662b7592b9?pvs=4) | `같이찾기`  | 실종 상태로 의심되는 실종 신고의
세부 사항을 가져온다. | /api/missing/suspect/{missing\_id}   | `GET`       |

### ERD

![](https://file.notion.so/f/f/3ef8dbd9-414c-4cf5-813d-32ecb943cc67/e66e695a-15a6-41a8-9441-9434f4546b59/image.png?table=block&id=7c5e68b9-fec5-48b6-a25c-fb89faf01ba2&spaceId=3ef8dbd9-414c-4cf5-813d-32ecb943cc67&expirationTimestamp=1726912800000&signature=XwQhO62kwISd_kH0hEUa5BhNfPndY21xYgB6IBUxs-w&downloadName=image.png)

[see on ERD Cloud](https://www.erdcloud.com/d/fMF9FFPdSnexqPGjN)

## Week3 진행사항

### Issue Template 작성([b529d2f](https://github.com/kakao-tech-campus-2nd-step3/Team27_BE/commit/b529d2f056b96e93491b53739f0a98e9cb70ff70))

### Entity 생성([5c2a6a7](https://github.com/kakao-tech-campus-2nd-step3/Team27_BE/commit/5c2a6a7a550c9bb44b606eeccc666598a30d2b82))

### 프로젝트 구조 결정([262347b](https://github.com/kakao-tech-campus-2nd-step3/Team27_BE/commit/262347b2e0a5892cc6dfac271a50cc084d721a8d))

# 해결해야할 문제 (멘토님께 질문사항)

* [ ] MySQL과 액추에이터의 의존성을 추가해도 되는가?
