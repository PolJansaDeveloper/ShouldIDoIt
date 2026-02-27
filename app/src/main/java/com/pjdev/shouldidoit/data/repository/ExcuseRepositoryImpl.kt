package com.pjdev.shouldidoit.data.repository

import com.pjdev.shouldidoit.data.model.CategoryBlock
import com.pjdev.shouldidoit.data.model.ExcusesData
import com.pjdev.shouldidoit.data.model.ModeChoice
import com.pjdev.shouldidoit.data.model.Recipient
import com.pjdev.shouldidoit.data.model.Tone
import kotlin.random.Random

class ExcuseRepositoryImpl(
    private val random: Random = Random.Default
) {

    fun composeMessage(
        data: ExcusesData,
        selectedCategoryId: String,
        modeChoice: ModeChoice,
        recipient: Recipient,
        tone: Tone,
        addEmojis: Boolean
    ): String {
        val category = resolveCategory(data, selectedCategoryId, modeChoice)

        val greetBase = styleGreeting(recipient = recipient, tone = tone)
        val greet = if (greetBase.isNotBlank()) greetBase else safePick(data.global.greetings, "Hola")

        val trigger = when (modeChoice) {
            ModeChoice.ABSURDO -> safePick(category?.detonators, safePick(data.global.detonators, "plot twist del día:"))
            ModeChoice.SERIO -> safePick(category?.detonators, safePick(data.global.detonators, "te aviso:"))
        }

        val problem = when (modeChoice) {
            ModeChoice.ABSURDO -> safePick(
                category?.absurdProblems?.ifEmpty { category.problems },
                "se desacomodó todo y no llego a tiempo"
            )

            ModeChoice.SERIO -> safePick(category?.problems, "me surgió un imprevisto y no alcanzo")
        }

        val close = when (modeChoice) {
            ModeChoice.ABSURDO -> safePick(
                category?.absurdClosures?.ifEmpty { category.closures },
                "Prometo compensarlo en cuanto aterrice de nuevo en la realidad."
            )

            ModeChoice.SERIO -> safePick(category?.closures, "Gracias por la comprensión.")
        }

        val emoji = if (addEmojis) safePick(data.global.emojiSets, "") else ""

        val templates = (category?.templatesMedium.orEmpty() + data.globalTemplates.templatesMedium)
            .ifEmpty {
                listOf(
                    "{greet}, {trigger} {problem}. {close} {emoji}",
                    "{greet}: {trigger} {problem}. {close} {emoji}",
                    "{trigger} {problem}. {close} {emoji}"
                )
            }

        val template = safePick(templates, "{greet}, {trigger} {problem}. {close} {emoji}")
        return template
            .replace("{greet}", greet)
            .replace("{trigger}", trigger)
            .replace("{problem}", problem)
            .replace("{close}", close)
            .replace("{emoji}", emoji)
            .cleanupText()
    }

    fun categoriesForMode(data: ExcusesData, modeChoice: ModeChoice): List<CategoryBlock> {
        val configuredAbsurd = data.modeCategoryIds[ModeChoice.ABSURDO.name].orEmpty().toSet()
        return when (modeChoice) {
            ModeChoice.ABSURDO -> {
                val absurd = data.categories.filter {
                    it.id == "absurd" || configuredAbsurd.contains(it.id)
                }
                if (absurd.isNotEmpty()) absurd else data.categories.filter { it.id == "absurd" }
            }

            ModeChoice.SERIO -> data.categories.filter { it.id != "absurd" }
        }
    }

    private fun resolveCategory(data: ExcusesData, selectedId: String, modeChoice: ModeChoice): CategoryBlock? {
        val available = categoriesForMode(data, modeChoice)
        return available.firstOrNull { it.id == selectedId } ?: available.firstOrNull()
    }

    private fun styleGreeting(recipient: Recipient, tone: Tone): String {
        return when (recipient) {
            Recipient.PAREJA -> "Cariño"
            Recipient.AMIGO -> "Ey"
            Recipient.FAMILIA -> "Hola familia"
            Recipient.JEFE -> if (tone == Tone.FORMAL) "Hola" else "Buenos días"
            Recipient.CLIENTE -> if (tone == Tone.FORMAL) "Estimado" else "Hola"
        }
    }

    private fun <T> safePick(options: List<T>?, fallback: T): T {
        val clean = options.orEmpty()
        if (clean.isEmpty()) return fallback
        return clean[random.nextInt(clean.size)]
    }

    private fun String.cleanupText(): String {
        return replace(Regex("\\s+"), " ")
            .replace("..", ".")
            .replace(" .", ".")
            .replace(" ,", ",")
            .trim()
    }
}
