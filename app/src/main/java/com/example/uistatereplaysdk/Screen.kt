package com.example.uistatereplaysdk

enum class Screen {
    Login, Shop, Product, Checkout;

    companion object {
        fun from(value: String, fallback: Screen = Login): Screen {
            return entries.firstOrNull { it.name == value } ?: fallback
        }
    }
}
