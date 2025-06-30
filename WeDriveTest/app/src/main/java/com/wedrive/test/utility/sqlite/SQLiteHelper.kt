package com.wedrive.test.utility.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(
    context: Context,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "wedrive.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 최근 검색어 저장 테이블 생성
        val createRecentSearchTableQuery = """
            CREATE TABLE RecentSearch(
                keyword TEXT PRIMARY KEY,
                regDt TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%s', 'now'))
            );
        """.trimIndent()
        db.execSQL(createRecentSearchTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 스키마 업그레이드
        db.execSQL("DROP TABLE IF EXISTS RecentSearch")
        onCreate(db)
    }
}