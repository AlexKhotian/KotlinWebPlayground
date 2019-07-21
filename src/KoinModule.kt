package com.plktor

import org.koin.dsl.module

val DatabaseModule = module {
    single { DatabaseAccessor() }
}