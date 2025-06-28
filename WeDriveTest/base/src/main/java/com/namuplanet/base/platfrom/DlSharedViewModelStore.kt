package com.namuplanet.base.platfrom

import androidx.lifecycle.ViewModelStore

class DlSharedViewModelStore {

    private val modelStores: HashMap<DlStoreKey, ViewModelStore> = HashMap()

    fun get(key: DlStoreKey): ViewModelStore {
        return modelStores[key] ?: ViewModelStore().also { modelStores[key] = it }
    }

    fun clearLbSharedViewModelStore(key: DlStoreKey) {
        modelStores[key]?.clear()
        modelStores.remove(key)
    }

    fun clearLbSharedViewModelStore() {
        modelStores.values.forEach {
            it.clear()
        }
        modelStores.clear()
    }
}

interface DlStoreKey
