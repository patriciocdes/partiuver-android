package com.partiuver.data.di

import com.partiuver.data.repository.MovieRepositoryImpl
import com.partiuver.domain.repository.MovieRepository
import com.partiuver.domain.usecase.GetMovieDetailUseCase
import com.partiuver.domain.usecase.SearchMoviesUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    companion object {

        @Provides
        @Singleton
        fun provideSearchMoviesUseCase(repo: MovieRepository) = SearchMoviesUseCase(repo)

        @Provides
        @Singleton
        fun provideGetMovieDetailUseCase(repo: MovieRepository) = GetMovieDetailUseCase(repo)
    }
}