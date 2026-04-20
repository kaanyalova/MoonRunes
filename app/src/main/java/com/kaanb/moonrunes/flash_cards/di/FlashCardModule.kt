package com.kaanb.moonrunes.flash_cards.di

import android.content.Context
import androidx.room.Room
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDao
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDatabase
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
class FlashCardModule {
    @Provides
    @Singleton
    fun provideFlashCardDao(@ApplicationContext context: Context): FlashCardDao {
        val file = File(context.filesDir, "flash_cards.db")

        val db = Room.databaseBuilder(
            context, FlashCardDatabase::class.java, file.absolutePath
        ).openHelperFactory(RequerySQLiteOpenHelperFactory()).allowMainThreadQueries().build()

        return db.flashCardDao()
    }

    @Singleton
    @Provides
    fun provideJniFsrs(database: FlashCardDao): FsrsJni {
        val stateMaybe = database.getFSRSDump()
        return FsrsJni(stateMaybe?.data)
    }
}