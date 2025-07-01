package com.wedrive.test.utility

import android.text.InputFilter
import android.text.Spanned

class AlphaNumericInputFilter : InputFilter {
    override fun filter(
        source : CharSequence, // 새로 입력된 문자열
        start  : Int,          // 새로 입력된 문자열의 시작 인덱스
        end    : Int,          // 새로 입력된 문자열의 끝 인덱스
        dest   : Spanned,      // 현재 EditText에 입력되어 있는 전체 문자열
        dstart : Int,          // 변경될 부분의 시작 인덱스(커서 위치 또는 선택 영역 시작)
        dend   : Int           // 변경될 부분의 끝 인덱스(커서 위치 또는 선택 영역 끝)
    ): CharSequence? {
        // 정규식: 영어 대소문자 및 숫자만 허용
        val regex = "^[a-zA-Z0-9]*$"
        if (source.matches(regex.toRegex())) {
            return null // 입력 허용
        }
        return "" // 입력 차단
    }
}