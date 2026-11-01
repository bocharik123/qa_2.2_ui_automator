package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        // Press home
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)

        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(result, textToSet)
    }

    /**
     * Тест 1: попытка установки пустой строки (или только пробелов)
     * Проверяет, что текст в TextView не изменился
     */
    @Test
    fun testEmptyTextShouldNotChange() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        // Получаем исходный текст до изменения
        val textView = device.findObject(By.res(packageName, "textToBeChanged"))
        val originalText = textView.text

        // Устанавливаем пустой текст (только пробелы)
        device.findObject(By.res(packageName, "userInput")).text = "   "

        // Нажимаем кнопку изменения текста
        device.findObject(By.res(packageName, "buttonChange")).click()

        // Ждём обновления UI
        device.wait(Until.hasObject(By.res(packageName, "textToBeChanged")), TIMEOUT)

        // Проверяем, что текст не изменился
        val currentText = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals("Текст не должен измениться при пустом вводе", originalText, currentText)
    }

    /**
     * Тест 2: открытие текста в новой Activity
     * Проверяет, что текст из EditText передаётся в TextView на втором экране
     */
    @Test
    fun testOpenTextInAnotherActivity() {
        val packageName = MODEL_PACKAGE
        val testText = "Hello from UI Automator"

        waitForPackage(packageName)

        // Устанавливаем непустой текст в поле ввода
        device.findObject(By.res(packageName, "userInput")).text = testText

        // Нажимаем кнопку открытия новой Activity
        device.findObject(By.res(packageName, "buttonActivity")).click()

        // Ждём появления новой Activity и её TextView (id="text")
        device.wait(Until.hasObject(By.res(packageName, "text")), TIMEOUT)

        // Получаем текст из TextView на втором экране
        val textViewInNewActivity = device.findObject(By.res(packageName, "text"))
        val displayedText = textViewInNewActivity.text

        // Проверяем, что текст совпадает с введённым
        assertEquals("Текст в новой Activity должен совпадать с введённым", testText, displayedText)
    }
}



