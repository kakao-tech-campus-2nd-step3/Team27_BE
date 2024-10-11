package com.ktc.togetherPet.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorMessage {

    // 입력 및 인증 관련 문제 -10000 ~ -19999
    // request 입력 관련 문제 -10000 ~ -100099
    INVALID_HEADER(-10001, "유효하지 않은 헤더입니다."),

    // 토큰 관련 로직 문제 -10100 ~ -00199
    INVALID_TOKEN(-10101, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(-10102, "만료된 토큰입니다."),
    INVALID_PROVIDER(-10103, "유효하지 않은 Oauth입니다."),
    INVALID_API(-10104, "유효하지 않은 카카오 api입니다."),

    // 사용자 관련 문제 -10200 ~ -10299
    INVALID_USER(-10201, "유효하지 않은 사용자입니다."),

    // 엔티티 관련 문제 -20000 ~ -29999
    // Pet 관련 문제 -20100 ~ -20199
    INVALID_PET_MONTH(-20101, "애완동물의 개월수는 0이상이여야 합니다."),
    PET_NOT_FOUND(-20102, "해당 펫을 찾을 수 없습니다."),

    // 시간 관련 문제 -20200 ~ -20299
    INVALID_DATE(-20201, "현재 시간보다 이후의 시간은 선택할 수 없습니다."),

    // Location 관련 문제 -20300 ~ -20399
    INVALID_LOCATION(-20301, "올바르지 않은 위치 정보입니다."),

    // Breed 관련 문제 -20400 ~ -20499
    BREED_NOT_FOUND(-20401, "해당 종을 찾을 수 없습니다."),

    // Missing 관련 문제 -20500 ~ -20599
    MISSING_NOT_FOUND(-20501, "해당 실종 정보를 찾을 수 없습니다."),

    // Report 관련 문제 -20600 ~ -20699
    REPORT_NOT_FOUND(-20601, "해당 제보 정보를 찾을 수 없습니다."),

    // Image 관련 문제 -20700 ~ -20799
    IMAGE_NOT_FOUND(-20701, "해당 이미지를 찾을 수 없습니다."),

    // Walk 관련 문제 -20800 ~ -20899
    WALK_NOT_FOUND(-20801, "해당 산책 정보를 찾을 수 없습니다."),

    // 서버 내부 문제 -30000 ~ -39999
    // 파일처리 관련 문제 -30000 ~ -30099
    IO_EXCEPTION(-30001, "파일 처리 중 오류가 발생했습니다.");

    private final int code;
    private final String message;
}
