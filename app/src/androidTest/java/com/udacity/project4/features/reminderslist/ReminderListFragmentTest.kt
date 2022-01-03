package com.udacity.project4.features.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.core.di.MainModule
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.stubs.reminderData1
import com.udacity.project4.stubs.reminderData2
import com.udacity.project4.util.AppViewAssertion.isTextDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewGone
import com.udacity.project4.util.shouldNavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderListFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val navGraphOfMain = R.navigation.nav_graph_main
    private val repository: RemindersLocalRepository = mock()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test
     * our code. at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        runBlocking {
            stopKoin()

            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().context)
                MainModule().initMainModules()
            }

            val module = module {
                viewModel {
                    RemindersListViewModel(
                        remindersLocalRepository = repository
                    )
                }
            }
            loadKoinModules(module)
        }
    }

    @Test
    fun when_screenOpened_should_display_no_reminders_found_content() =
        mainCoroutineRule.runBlockingTest {
            whenever(repository.getReminders()).thenReturn(ResultData.Success(emptyList()))
            launchFragmentInContainer<ReminderListFragment>(
                themeResId = R.style.LocationReminderAppTheme
            )
            isViewDisplayed(R.id.noDataAnimation)
            isViewDisplayed(R.id.noDataTextView)
        }

    @Test
    fun when_screenOpened_should_display_snack_error() = mainCoroutineRule.runBlockingTest {
        whenever(repository.getReminders()).thenReturn(ResultData.Error("Ooops!"))
        launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.LocationReminderAppTheme)
        isViewDisplayed(R.id.noDataAnimation)
        isTextDisplayed("Ooops! Error loading reminders!")
        isTextDisplayed("Dismiss")
    }

    @Test
    fun when_screenOpened_should_display_reminders_content_list() = mainCoroutineRule.runBlockingTest {
            whenever(repository.getReminders()).thenReturn(
                ResultData.Success(
                    listOf(
                        reminderData1,
                        reminderData2
                    )
                )
            )

            launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.LocationReminderAppTheme)

            isTextDisplayed("Reminders")
            isTextDisplayed("LOGOUT")
            isViewDisplayed(R.id.remindersRecyclerView)
            isViewDisplayed(R.id.actionButtonAddReminder)

            isTextDisplayed("Grow tomatoes")
            isTextDisplayed("Do not forget the fertilizer")
            isTextDisplayed("Mars, South Pole")

            isTextDisplayed("Grow potatoes")
            isTextDisplayed("Do not forget the fertilizer and water")
            isTextDisplayed("Mars, North Pole")

            isViewGone(R.id.noDataAnimation)
            isViewGone(R.id.noDataTextView)
            isViewGone(R.id.progressBar)
        }

    @Test
    fun add_reminderClick_should_navigate_to_add_reminder() = mainCoroutineRule.runBlockingTest {
        whenever(repository.getReminders()).thenReturn(ResultData.Success(emptyList()))

        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.LocationReminderAppTheme
        )

        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.actionButtonAddReminder,
            destinationResId = R.id.saveReminderFragment,
            navGraph = navGraphOfMain
        )
    }
}