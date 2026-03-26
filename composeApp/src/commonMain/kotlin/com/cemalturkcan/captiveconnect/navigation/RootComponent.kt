package com.cemalturkcan.captiveconnect.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<com.arkivanov.decompose.router.stack.ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Connect,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    fun onOpenSettings() {
        navigation.push(Config.Settings)
    }

    fun onBack() {
        navigation.pop()
    }

    private fun createChild(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Connect -> Child.Connect
            is Config.Settings -> Child.Settings
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Connect : Config

        @Serializable
        data object Settings : Config
    }

    sealed class Child {
        data object Connect : Child()
        data object Settings : Child()
    }
}
