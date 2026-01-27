package com.example.replaysdk.replay

/**
 * Host app connects navigation once.
 * SDK uses it to drive UI during replay.
 */
interface ReplayNavigator {

    /** Navigate to a screen/route name (e.g. "Login", "Shop", "Product", "Checkout") */
    fun goTo(screen: String)

    /** Navigate back */
    fun back()

    /** Optional: trigger non-navigation actions by tag */
    fun performAction(tag: String) { /* no-op */ }
}
