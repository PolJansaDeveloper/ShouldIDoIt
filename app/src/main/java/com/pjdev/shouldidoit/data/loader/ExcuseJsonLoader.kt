package com.pjdev.shouldidoit.data.loader

import android.content.Context
import com.pjdev.shouldidoit.data.model.CategoryBlock
import com.pjdev.shouldidoit.data.model.ExcusesData
import com.pjdev.shouldidoit.data.model.GlobalContent
import com.pjdev.shouldidoit.data.model.GlobalTemplates
import org.json.JSONArray
import org.json.JSONObject

class ExcuseJsonLoader(private val context: Context) {

    private val files = listOf(
        "excuses_v2_base.json",
        "excuses_v2_work.json",
        "excuses_v2_social.json",
        "excuses_v2_late.json",
        "excuses_v2_study.json",
        "excuses_v2_family.json",
        "excuses_v2_absurd.json"
    )

    fun load(): ExcusesData {
        val parsed = files.mapNotNull(::readAsset).map(::parseData)
        if (parsed.isEmpty()) return ExcusesData()

        val base = parsed.first()
        val mergedCategories = parsed.flatMap { it.categories }
        val mergedModeIds = parsed.fold(base.modeCategoryIds.toMutableMap()) { acc, item ->
            item.modeCategoryIds.forEach { (key, ids) ->
                val current = acc[key].orEmpty()
                acc[key] = (current + ids).distinct()
            }
            acc
        }

        return base.copy(
            categories = mergedCategories,
            modeCategoryIds = mergedModeIds
        )
    }

    private fun readAsset(name: String): String? = runCatching {
        context.assets.open(name).bufferedReader().use { it.readText() }
    }.getOrNull()

    private fun parseData(raw: String): ExcusesData {
        val json = JSONObject(raw)
        return ExcusesData(
            version = json.optInt("version", 2),
            global = parseGlobal(json.optJSONObject("global")),
            categories = parseCategories(json.optJSONArray("categories")),
            modeCategoryIds = parseModeCategoryIds(json.optJSONObject("modeCategoryIds")),
            globalTemplates = parseTemplates(json.optJSONObject("globalTemplates")),
            synonymBuckets = parseStringMap(json.optJSONObject("synonymBuckets"))
        )
    }

    private fun parseGlobal(json: JSONObject?): GlobalContent {
        if (json == null) return GlobalContent()
        return GlobalContent(
            greetings = json.optStringList("greetings"),
            apologies = json.optStringList("apologies"),
            timeframes = json.optStringList("timeframes"),
            constraints = json.optStringList("constraints"),
            followups = json.optStringList("followups"),
            softeners = json.optStringList("softeners"),
            connectors = json.optStringList("connectors"),
            emojiSets = json.optStringList("emojiSets"),
            detonators = json.optStringList("detonators"),
            problems = json.optStringList("problems"),
            details = json.optStringList("details"),
            actions = json.optStringList("actions"),
            closures = json.optStringList("closures")
        )
    }

    private fun parseTemplates(json: JSONObject?): GlobalTemplates {
        if (json == null) return GlobalTemplates()
        return GlobalTemplates(
            templatesShort = json.optStringList("templatesShort"),
            templatesMedium = json.optStringList("templatesMedium"),
            templatesLong = json.optStringList("templatesLong")
        )
    }

    private fun parseCategories(array: JSONArray?): List<CategoryBlock> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    CategoryBlock(
                        id = item.optString("id"),
                        label = item.optString("label"),
                        detonators = item.optStringList("detonators"),
                        problems = item.optStringList("problems"),
                        details = item.optStringList("details"),
                        actions = item.optStringList("actions"),
                        closures = item.optStringList("closures"),
                        templatesShort = item.optStringList("templatesShort"),
                        templatesMedium = item.optStringList("templatesMedium"),
                        templatesLong = item.optStringList("templatesLong"),
                        absurdProblems = item.optStringList("absurdProblems"),
                        absurdDetails = item.optStringList("absurdDetails"),
                        absurdClosures = item.optStringList("absurdClosures"),
                        recipientStyle = parseStringMap(item.optJSONObject("recipientStyle"))
                    )
                )
            }
        }.filter { it.id.isNotBlank() }
    }

    private fun parseModeCategoryIds(json: JSONObject?): Map<String, List<String>> {
        if (json == null) return emptyMap()
        return json.keys().asSequence().associateWith { key -> json.optStringList(key) }
    }

    private fun parseStringMap(json: JSONObject?): Map<String, List<String>> {
        if (json == null) return emptyMap()
        return json.keys().asSequence().associateWith { key -> json.optStringList(key) }
    }

    private fun JSONObject.optStringList(key: String): List<String> {
        val arr = optJSONArray(key) ?: return emptyList()
        return arr.toStringList()
    }

    private fun JSONArray.toStringList(): List<String> = buildList {
        repeat(length()) { idx ->
            val value = optString(idx).trim()
            if (value.isNotBlank()) add(value)
        }
    }
}
