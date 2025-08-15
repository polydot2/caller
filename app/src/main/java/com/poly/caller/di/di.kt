package com.poly.caller

import com.poly.caller.domain.GetConfigurationsUsecase
import com.poly.caller.domain.InitConfigurationUsecase
import com.poly.caller.domain.LoadConfigurationUsecase
import com.poly.caller.domain.ObserveConfigurationUsecase
import com.poly.caller.domain.SaveConfigurationUsecase
import com.poly.caller.domain.UpdateConfigurationUsecase
import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SpecificRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //region repositories
    @Provides
    @Singleton
    fun providePersistanceRepository(): PersistanceRepository {
        return PersistanceRepository()
    }

    @Provides
    @Singleton
    fun provideSpecificRepository(
        persistanceRepository: PersistanceRepository
    ): SpecificRepository {
        return SpecificRepository(persistanceRepository)
    }
    //endregion

    //region usecases
    @Provides
    @Singleton
    fun provideUpdateConfigurationUsecase(
        specificRepository: SpecificRepository
    ): UpdateConfigurationUsecase {
        return UpdateConfigurationUsecase(specificRepository)
    }

    @Provides
    @Singleton
    fun provideObserveConfigurationUsecase(
        specificRepository: SpecificRepository
    ): ObserveConfigurationUsecase {
        return ObserveConfigurationUsecase(specificRepository)
    }

    @Provides
    @Singleton
    fun provideLoadConfigurationUsecase(
        specificRepository: SpecificRepository
    ): LoadConfigurationUsecase {
        return LoadConfigurationUsecase(specificRepository)
    }

    @Provides
    @Singleton
    fun provideInitConfigurationUsecase(
        specificRepository: SpecificRepository
    ): InitConfigurationUsecase {
        return InitConfigurationUsecase(specificRepository)
    }

    @Provides
    @Singleton
    fun provideSaveConfigurationUsecase(
        persistanceRepository: PersistanceRepository
    ): SaveConfigurationUsecase {
        return SaveConfigurationUsecase(persistanceRepository)
    }

    @Provides
    @Singleton
    fun provideGetConfigurationsUsecase(
        persistanceRepository: PersistanceRepository
    ): GetConfigurationsUsecase {
        return GetConfigurationsUsecase(persistanceRepository)
    }
    //endregion
}
