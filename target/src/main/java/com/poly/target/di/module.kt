package com.poly.target.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
//    @Provides
//    fun provideExtrasConfig(): ExtrasConfigInterface {
//        return ExtrasConfig() // Assurez-vous que cette classe est accessible
//    }
//    @Binds
//    @Singleton
//    fun bindExtrasConfig(extrasConfig: ExtrasConfig): ExtrasConfigInterface
}