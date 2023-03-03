package com.example.shoppinglist

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingListViewModel: ViewModel() {
    private var _items = MutableLiveData<MutableList<String>>(mutableListOf())

    val items: LiveData<MutableList<String>>
        get() = _items

    fun addItem(item: String, sharedPreferences: SharedPreferences) {
        _items.value = (_items.value!! + item).toMutableList()
        saveList(sharedPreferences)
    }

    fun removeAll(sharedPreferences: SharedPreferences) {
        _items.value = mutableListOf()
        saveList(sharedPreferences)
    }

    fun setItems(items: List<String>) {
        _items.value = items.toMutableList()
    }

    private fun saveList(sharedPreferences: SharedPreferences) {
        with(sharedPreferences.edit()) {
            putStringSet(ITEMS, items.value?.toSet())
            apply()
        }
    }

    fun deleteAt(index: Int, sharedPreferences: SharedPreferences) {
        val itemsCopy = _items.value?.toMutableList()
        itemsCopy?.removeAt(index)
        _items.value = itemsCopy
        saveList(sharedPreferences)
    }

    companion object {
        const val ITEMS = "ITEMS"
    }
}