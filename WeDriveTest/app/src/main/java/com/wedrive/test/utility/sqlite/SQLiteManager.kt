package com.wedrive.test.utility.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import java.util.*

class SQLiteManager(context: Context) {

    private val dbHelper: SQLiteHelper = SQLiteHelper(context)
    private val keywordTableName = "RecentSearch"
    private val columnKeyword = "keyword"
    private val columnRegDt = "regDt"

    fun insertOrUpdateKeyword(keyword: String) {
        val db = dbHelper.writableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateTime = sdf.format(Date())

        // 키워드 존재 여부 확인
        val cursor = db.query(
            keywordTableName,
            arrayOf(columnKeyword), // 확인할 컬럼 (존재 여부만 파악하므로 키워드 컬럼만)
            "$columnKeyword = ?", // WHERE 절
            arrayOf(keyword), // WHERE 절의 인자
            null,
            null,
            null
        )

        val values = ContentValues()
        values.put(columnKeyword, keyword)
        values.put(columnRegDt, currentDateTime)

        // 키워드 존재
        if (cursor.moveToFirst()) {
            db.update(
                keywordTableName,
                values, // 업데이트할 값(keyword도 포함되나, WHERE 절 때문에 해당 row의 keyword는 어차피 동일)
                "$columnKeyword = ?",
                arrayOf(keyword)
            )
        }
        // 키워드 없음
        else {
            db.insert(keywordTableName, null, values)
        }

        cursor.close() // 커서 사용 후 닫기
        db.close()
    }

    fun deleteKeyword(keyword: String) {
        val db = dbHelper.writableDatabase
        val selection = "keyword = ?"
        val selectionArgs = arrayOf(keyword)
        db.delete(keywordTableName, selection, selectionArgs)
        db.close()
    }

    fun getAllKeyword(): Cursor {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            keywordTableName,
            null,
            null,
            null,
            null,
            null,
            "regDt DESC"
        )
        return cursor
    }
}