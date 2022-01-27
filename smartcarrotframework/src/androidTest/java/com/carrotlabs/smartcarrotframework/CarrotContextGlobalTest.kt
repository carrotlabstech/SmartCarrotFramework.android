package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CarrotContextGlobalTest {
    @Test
    fun versionNotEmpty() {
        Assert.assertTrue(CarrotContextGlobal.versionString != "")
    }
}