package com.example.madk7_8

import android.Manifest
import android.os.Environment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun whenUrlIsEmpty_showToastMessage() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            activity.editTextUrl.setText("")
            activity.buttonDownload.performClick()

            val toastText = ShadowToast.getTextOfLatestToast()
            assertThat(
                toastText,
                `is`("Enter the image URL")
            )
        }
    }

    @Test
    fun whenValidUrlEntered_imageIsDisplayed() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val validUrl = "https://via.placeholder.com/150"
            activity.editTextUrl.setText(validUrl)
            activity.buttonDownload.performClick()

            // Подождем, чтобы изображение успело загрузиться
            Thread.sleep(3000)

            assertThat(activity.imageView.drawable, `is`(notNullValue()))
        }
    }

    @Test
    fun whenInvalidUrlEntered_showErrorToast() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val invalidUrl = "https://invalid.url/image.jpg"
            activity.editTextUrl.setText(invalidUrl)
            activity.buttonDownload.performClick()

            // Подождем для обработки ошибки
            Thread.sleep(3000)

            val toastText = ShadowToast.getTextOfLatestToast()
            assertThat(
                toastText,
                `is`("Error loading image")
            )
        }
    }

    @Test
    fun whenImageDownloaded_imageIsSavedToStorage() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val validUrl = "https://via.placeholder.com/150"
            activity.editTextUrl.setText(validUrl)
            activity.buttonDownload.performClick()

            // Подождем для завершения сохранения
            Thread.sleep(5000)

            val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val files = storageDir?.listFiles()
            val isFileSaved = files?.any { it.name.startsWith("downloaded_image_") } ?: false

            assertThat(isFileSaved, `is`(true))
        }
    }

    @Test
    fun onStart_permissionIsRequested() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val shadowApplication = shadowOf(activity.application)
            val requestedPermissions = shadowApplication.getRequestedPermissions()


            assertThat(
                requestedPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                `is`(true)
            )
        }
    }
}
