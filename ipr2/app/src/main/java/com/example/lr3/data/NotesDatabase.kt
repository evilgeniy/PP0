package com.example.lr3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



@Database(
    entities = [Note::class, Tag::class, NoteTagJoin::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun noteDataDao(): NoteDataDao
    abstract fun noteTagDao(): NoteTagDao

    private class NotesDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.noteDataDao(),
                        database.noteTagDao())
                }
            }
        }

        suspend fun populateDatabase(noteDataDao: NoteDataDao, noteTagDao: NoteTagDao) {
            noteTagDao.deleteAllNoteTagJoins()
            noteDataDao.deleteAllNotes()
            noteDataDao.deleteAllTags()

            var note = Note("SSS", "bzzz")
            noteDataDao.insertNote(note)
            note = Note("bzzzz")
            noteDataDao.insertNote(note)

        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                )
                    //.addCallback(NotesDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}