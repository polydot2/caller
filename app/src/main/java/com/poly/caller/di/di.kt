package com.poly.caller

import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SpecificRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSpecificRepositoryFactory(): SpecificRepositoryFactory {
        return SpecificRepositoryFactory()
    }

    @Provides
    @Singleton
    fun providePersistanceRepository(): PersistanceRepository {
        return PersistanceRepository()
    }
}

@Singleton
class SpecificRepositoryFactory @Inject constructor() {
    fun create(moduleName: String): SpecificRepository {
        return SpecificRepository(moduleName)
    }
}
