package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class TransactionCategoryTest {
    @Test
    fun testToString() {
        Assert.assertEquals(TransactionCategory().toString(), "not_categorized")
        Assert.assertEquals(TransactionCategory("mortgage_interest").toString(),"housing / mortgage_interest")
    }

    @Test
    fun testToStringShort() {
        Assert.assertEquals(TransactionCategory().toStringShort(), "not_categorized")
        Assert.assertEquals(TransactionCategory("mortgage_interest").toStringShort(), "mortgage_interest")
        Assert.assertEquals(TransactionCategory("housing").toStringShort(), "housing")
    }

    @Test
    fun testGetParentCategory() {
        val category = TransactionCategory("mortgage_interest")
        val parentCategory = category.getParentCategory()

        Assert.assertEquals(parentCategory!!.idName, "housing")

        val parentParentCategory = parentCategory.getParentCategory()
        Assert.assertNull(parentParentCategory)
    }

    @Test
    fun testOptionals() {
        val catId: String? = null
        val catInId: Int? = null

        var category = TransactionCategory(catId)

        Assert.assertEquals(category.idName, TransactionCategory.UNCATEGORISED_IDNAME)

        category = TransactionCategory(catInId)
        Assert.assertEquals(category.idName, TransactionCategory.UNCATEGORISED_IDNAME)
    }

    @Test
    fun testTopSubLevel() {
        Assert.assertTrue(TransactionCategory("cash").isParent())
        Assert.assertTrue(TransactionCategory().isParent())
        Assert.assertTrue(TransactionCategory("health").isParent())
        Assert.assertFalse(TransactionCategory("canton_tax").isParent())

        Assert.assertTrue(TransactionCategory("garden").isSub())
        Assert.assertTrue(TransactionCategory("building_insurance").isSub())
        Assert.assertFalse(TransactionCategory().isSub())
    }
}