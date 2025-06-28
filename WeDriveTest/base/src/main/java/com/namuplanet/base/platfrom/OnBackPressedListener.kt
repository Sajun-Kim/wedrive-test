package com.namuplanet.base.platfrom

/**
 * Fragment can be notified back button press by implementing this interface.
 */
interface OnBackPressedListener {
    /**
     * Called when back pressed.
     * @return true if handled, otherwise false
     */
    fun onBackPressed(): Boolean
}
