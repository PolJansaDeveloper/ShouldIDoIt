package com.pjdev.shouldidoit.data.model

data class ExcusesData(
    val version: Int = 2,
    val global: GlobalContent = GlobalContent(),
    val categories: List<CategoryBlock> = emptyList(),
    val modeCategoryIds: Map<String, List<String>> = emptyMap(),
    val globalTemplates: GlobalTemplates = GlobalTemplates(),
    val synonymBuckets: Map<String, List<String>> = emptyMap()
)

data class GlobalContent(
    val greetings: List<String> = emptyList(),
    val apologies: List<String> = emptyList(),
    val timeframes: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val followups: List<String> = emptyList(),
    val softeners: List<String> = emptyList(),
    val connectors: List<String> = emptyList(),
    val emojiSets: List<String> = emptyList(),
    val detonators: List<String> = emptyList(),
    val problems: List<String> = emptyList(),
    val details: List<String> = emptyList(),
    val actions: List<String> = emptyList(),
    val closures: List<String> = emptyList()
)

data class GlobalTemplates(
    val templatesShort: List<String> = emptyList(),
    val templatesMedium: List<String> = emptyList(),
    val templatesLong: List<String> = emptyList()
)

data class CategoryBlock(
    val id: String,
    val label: String,
    val detonators: List<String> = emptyList(),
    val problems: List<String> = emptyList(),
    val details: List<String> = emptyList(),
    val actions: List<String> = emptyList(),
    val closures: List<String> = emptyList(),
    val templatesShort: List<String> = emptyList(),
    val templatesMedium: List<String> = emptyList(),
    val templatesLong: List<String> = emptyList(),
    val absurdProblems: List<String> = emptyList(),
    val absurdDetails: List<String> = emptyList(),
    val absurdClosures: List<String> = emptyList(),
    val recipientStyle: Map<String, List<String>> = emptyMap()
)

enum class Intensity { BAJA, MEDIA, ALTA }

enum class Tone { FORMAL, CERCANO, NEUTRO }

enum class Mode { REALISTA, DIVERTIDO, ABSURDO }

enum class ModeChoice { SERIO, ABSURDO }

enum class Recipient { JEFE, CLIENTE, AMIGO, PAREJA, FAMILIA }
