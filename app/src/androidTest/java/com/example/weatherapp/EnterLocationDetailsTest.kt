package com.example.weatherapp

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.weatherapp.ui.activities.MainActivity
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class EnterLocationDetailsTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private fun hasErrorText(matchText: String?): Matcher<View> =
        object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
            override fun describeTo(description: Description?) {
            }

            override fun matchesSafely(item: TextInputLayout?): Boolean {
                return matchText == item?.error?.toString()
            }
        }

    @Ignore("Keep it only as an example")
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.weatherapp", appContext.packageName)
    }

    @Test
    fun testFieldsErrorWarnings() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        Espresso.onView(ViewMatchers.withId(R.id.getLocationWeatherButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.cityTextInputWrapper))
            .check(
                ViewAssertions.matches(
                    hasErrorText(appContext.getString(R.string.fill_in_with_correct_data))
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.countryTextInputWrapper))
            .check(
                ViewAssertions.matches(
                    hasErrorText(appContext.getString(R.string.fill_in_with_correct_data))
                )
            )
    }

    @Test
    fun testEnterRandomFields() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        Espresso.onView(ViewMatchers.withId(R.id.countryTextInput))
            .perform(ViewActions.typeText("asfasf"))
        Espresso.onView(ViewMatchers.withId(R.id.cityTextInput))
            .perform(ViewActions.typeText("ASD"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.getLocationWeatherButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.circularProgress))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.cityTextInputWrapper))
            .check(
                ViewAssertions.matches(
                    hasErrorText(
                        appContext.getString(R.string.fill_in_with_correct_data)
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.countryTextInputWrapper))
            .check(
                ViewAssertions.matches(
                    hasErrorText(
                        appContext.getString(R.string.fill_in_with_correct_data)
                    )
                )
            )
    }

    @Test
    fun enterLocationDetailsAndCheckProgress() {
        Espresso.onView(ViewMatchers.withId(R.id.countryTextInput))
            .perform(ViewActions.typeText("Germany"))
        Espresso.onView(ViewMatchers.withId(R.id.cityTextInput))
            .perform(ViewActions.typeText("Weimar"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.getLocationWeatherButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.circularProgress))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.countryTextInputWrapper))
            .check(ViewAssertions.matches(hasErrorText(null)))
        Espresso.onView(ViewMatchers.withId(R.id.cityTextInputWrapper))
            .check(ViewAssertions.matches(hasErrorText(null)))
    }
}
