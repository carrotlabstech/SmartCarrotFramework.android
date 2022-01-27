package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.utils.StringSanitizer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class StringSanitizerTest {
    @Test
    fun testSanitization() {
        Assert.assertEquals(StringSanitizer.cleanseString(""), "")
        Assert.assertEquals(StringSanitizer.cleanseString("привет"), "")
        Assert.assertEquals(StringSanitizer.cleanseString("/hɛlˈviːʃə/"), "hlvi")
        Assert.assertEquals(StringSanitizer.cleanseString("アニメ"), "")
        Assert.assertEquals(StringSanitizer.cleanseString("💖worldа"), "world")
        Assert.assertEquals(StringSanitizer.cleanseString("grüezi"), "grueezi")
        Assert.assertEquals(StringSanitizer.cleanseString("grüe|zi"), "grueezi")
        Assert.assertEquals(StringSanitizer.cleanseString("grüe|zi"), "grueezi")
        Assert.assertEquals(StringSanitizer.cleanseString("    (The) Quiçk45 & brown föx-juMPed_over  456 a b c99    \t\n the lazy à dogs.?  "), "the quickxqzptxbrown foex jumped over the lazy dogs")
    }
}