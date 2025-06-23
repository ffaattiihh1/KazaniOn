package com.kazanion.ui.store

data class StoreProduct(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val stock: Long = 0,
    val imageUrl: String? = null
) 