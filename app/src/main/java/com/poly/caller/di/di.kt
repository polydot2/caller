package com.poly.caller

import com.poly.caller.model.GetConfigurationUsecase
import com.poly.caller.model.LoadConfigurationUsecase
import com.poly.caller.model.ObserveConfigurationUsecase
import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SaveConfigurationUsecase
import com.poly.caller.model.SpecificRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @Named("module1")
    fun provideSpecificRepositoryModule1(): SpecificRepository {
        return SpecificRepository("module1")
    }

    @Provides
    @Named("module1")
    fun provideGetConfigurationUsecaseModule1(
        @Named("module1") repository: SpecificRepository
    ): GetConfigurationUsecase {
        return GetConfigurationUsecase(repository)
    }

    @Provides
    @Named("module1")
    fun provideSaveConfigurationUsecaseModule1(
        @Named("module1") repository: SpecificRepository
    ): SaveConfigurationUsecase {
        return SaveConfigurationUsecase(repository)
    }

    @Provides
    @Named("module1")
    fun provideObserveConfigurationUsecaseModule1(
        @Named("module1") repository: SpecificRepository
    ): ObserveConfigurationUsecase {
        return ObserveConfigurationUsecase(repository)
    }

    @Provides
    @Named("module1")
    fun provideLoadConfigurationUsecaseUsecaseModule1(
        @Named("module1") repository: SpecificRepository
    ): LoadConfigurationUsecase {
        return LoadConfigurationUsecase(repository)
    }

    @Provides
    @Named("module1")
    fun provideNameModule1(): String {
        return "module1"
    }

    //
    @Provides
    @Singleton
    fun providePersistanceRepository(): PersistanceRepository {
        return PersistanceRepository()
    }
}