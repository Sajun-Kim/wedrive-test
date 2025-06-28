package com.namuplanet.base.view

abstract class DisplayableItem(private val layoutId: Int) {
    fun type() = layoutId
}

@DslMarker
annotation class DisplayableItemDsl

@DisplayableItemDsl
class DisplayableItemBuilder {
    private val displayableItems = ArrayList<DisplayableItem>()

    val size get() = displayableItems.size

    fun build() = displayableItems

    operator fun DisplayableItem.unaryPlus() {
        displayableItems += this
    }

    operator fun DisplayableItem.unaryMinus() {
        displayableItems -= this
    }

    operator fun List<DisplayableItem>.unaryPlus() {
        displayableItems.addAll(this)
    }
}

fun displayableItems(setup: DisplayableItemBuilder.() -> Unit): ArrayList<DisplayableItem> =
    DisplayableItemBuilder().apply(setup).build()

fun emptyDisplayableItems() = displayableItems {  }