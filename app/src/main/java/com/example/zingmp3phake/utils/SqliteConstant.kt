package com.example.zingmp3phake.utils

const val INDEX_0 = 0
const val INDEX_1 = 1
const val INDEX_2 = 2
const val INDEX_3 = 3
const val INDEX_4 = 4
const val INDEX_5 = 5
const val INDEX_6 = 6
const val INDEX_7 = 7
const val INDEX_8 = 8
const val DB_NAME = "song.db"
const val DB_VERSION = 3
const val TABLE_SONG = "tblsong"
const val DROP_TABLE = "DROP TABLE IF EXISTS tblsong"
const val CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS tblsong (songid varchar(255) primary key, songname varchar(255), " +
            "songartist varchar(255),songduration int, songurl varchar(255), songimg varchar(255), " +
            "songlocal int, songfavorite int,songlyric varchar(255) )"
