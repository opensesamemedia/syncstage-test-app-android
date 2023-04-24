package media.opensesame.syncstagetestappandroid.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.lang.ref.WeakReference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {
    @Singleton
    @Provides
    fun provideContext(application: Application): WeakReference<Context> =
        WeakReference(application.applicationContext)

    @Singleton
    @Provides
    fun providePreferencesRepo(@ApplicationContext context: Context) =
        PreferencesRepo(context = context)

    @Singleton
    @Provides
    fun provideSyncStage(@ApplicationContext context: Context) = SyncStage(context)
}