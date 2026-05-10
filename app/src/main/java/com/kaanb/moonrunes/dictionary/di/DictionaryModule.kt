package com.kaanb.moonrunes.dictionary.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kaanb.moonrunes.dictionary.dao.DictionaryDao
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabase
import com.kaanb.moonrunes.dictionary.service.AudioService
import com.kaanb.moonrunes.dictionary.service.WotdService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
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

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl("http://10.0.2.2:3000").build()
    }

    @Singleton
    @Provides
    fun provideAudioService(retrofit: Retrofit) : AudioService {
        return retrofit.create(AudioService::class.java)
    }

    @Singleton
    @Provides
    fun provideWotdService(retrofit: Retrofit): WotdService {
        return retrofit.create(WotdService::class.java)
    }


}