package com.kaanb.moonrunes.dictionary.di

import android.content.Context
import androidx.room.Room
import com.kaanb.moonrunes.dictionary.dao.DictionaryDao
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabase
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.usecase.SearchAndFormatDictionaryEntriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DictionaryModule {

    @Singleton
    @Provides
    fun provideDictionaryDao(@ApplicationContext context: Context): DictionaryDao {
        val file = File(context.filesDir, "dict.db")

        val db = Room.databaseBuilder(
            context, DictionaryDatabase::class.java, file.absolutePath
        ).openHelperFactory(RequerySQLiteOpenHelperFactory()).allowMainThreadQueries().build()

        return db.dictionaryDao()
    }
}