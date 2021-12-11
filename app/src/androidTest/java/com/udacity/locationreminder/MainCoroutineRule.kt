package com.udacity.locationreminder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainCoroutineRule (
    private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(testCoroutineDispatcher) {
    /** Analog to @Before annotation */
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher = testCoroutineDispatcher)
    }
    /** Analog to @After annotation */
    override fun finished(description: Description?) {
        super.finished(description)
        /**
         * Resets state of the [Dispatchers.Main] to the original main dispatcher.
         * For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
         * Used to clean up all possible dependencies, should be used in tear down (`@After`) methods.
         *
         * It is unsafe to call this method if alive coroutines launched in [Dispatchers.Main] exist.
         */
        Dispatchers.resetMain()
        /**
         * Call after test code completes to ensure that the dispatcher is properly cleaned up.
         * @throws 'UncompletedCoroutinesError' if any pending tasks are active, however it will not
         * throw for suspended coroutines.
         */
        cleanupTestCoroutines()
    }
}

