package com.zaze.apps.feature.message.di

import com.zaze.apps.core.router.MessageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageModule {

    @Binds
    abstract fun bindMessageService(messageService: MessageServiceImpl): MessageService
}