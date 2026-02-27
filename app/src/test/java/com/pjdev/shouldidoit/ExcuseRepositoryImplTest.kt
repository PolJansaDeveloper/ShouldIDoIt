package com.pjdev.shouldidoit

import com.pjdev.shouldidoit.data.model.CategoryBlock
import com.pjdev.shouldidoit.data.model.ExcusesData
import com.pjdev.shouldidoit.data.model.GlobalContent
import com.pjdev.shouldidoit.data.model.GlobalTemplates
import com.pjdev.shouldidoit.data.model.ModeChoice
import com.pjdev.shouldidoit.data.model.Recipient
import com.pjdev.shouldidoit.data.model.Tone
import com.pjdev.shouldidoit.data.repository.ExcuseRepositoryImpl
import kotlin.random.Random
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExcuseRepositoryImplTest {

    @Test
    fun composeMessage_noCrashesWithEmptyLists() {
        val repository = ExcuseRepositoryImpl(Random(0))
        val data = ExcusesData(
            global = GlobalContent(),
            categories = listOf(CategoryBlock(id = "work", label = "Trabajo")),
            globalTemplates = GlobalTemplates()
        )

        val message = repository.composeMessage(
            data = data,
            selectedCategoryId = "work",
            modeChoice = ModeChoice.SERIO,
            recipient = Recipient.JEFE,
            tone = Tone.FORMAL,
            addEmojis = false
        )

        assertTrue(message.isNotBlank())
    }

    @Test
    fun categoriesForMode_absurdOnlyReturnsAbsurd() {
        val repository = ExcuseRepositoryImpl(Random(0))
        val data = ExcusesData(
            categories = listOf(
                CategoryBlock(id = "work", label = "Trabajo"),
                CategoryBlock(id = "absurd", label = "Absurdo")
            ),
            modeCategoryIds = mapOf("ABSURDO" to listOf("absurd"))
        )

        val absurdCategories = repository.categoriesForMode(data, ModeChoice.ABSURDO)
        val seriousCategories = repository.categoriesForMode(data, ModeChoice.SERIO)

        assertTrue(absurdCategories.all { it.id == "absurd" })
        assertFalse(seriousCategories.any { it.id == "absurd" })
    }
}
