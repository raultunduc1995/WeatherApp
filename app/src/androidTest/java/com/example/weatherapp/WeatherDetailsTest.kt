package com.example.weatherapp

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.weatherapp.ui.activities.MainActivity
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class ViewVisibilityIdlingResource(
    private val activity: AppCompatActivity,
    private val viewId: Int
) : IdlingResource {
    companion object {
        val TAG by lazy { ViewVisibilityIdlingResource::class.java.simpleName }
    }

    private lateinit var callback: IdlingResource.ResourceCallback

    override fun getName(): String {
        return TAG
    }

    override fun isIdleNow(): Boolean {
        val view = activity.findViewById<View>(viewId)
        if (view != null) {
            callback.onTransitionToIdle()
        }
        return view != null
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        callback?.let {
            this.callback = it
        }
    }
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class WeatherDetailsTest {
    private var idlingResource: IdlingResource? = null

    @Before
    fun setup() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            idlingResource = ViewVisibilityIdlingResource(activity, R.id.progressView)
        }
    }

    @After
    fun teardown() {
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun testWeimarWeatherDetailsFromRecyclerView() {
        Espresso.onView(ViewMatchers.withId(R.id.countryTextInput))
            .perform(ViewActions.typeText("Germany"))
        Espresso.onView(ViewMatchers.withId(R.id.cityTextInput))
            .perform(ViewActions.typeText("Weimar"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.getLocationWeatherButton))
            .perform(ViewActions.click())

        // Wait until loading indicator is shown on Weather Details screen
        idlingResource?.let { IdlingRegistry.getInstance().register(it) }

        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        // Check if the Weimar text is present in one of the ViewHolder's items
        Espresso.onView(ViewMatchers.withText("Weimar"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}
