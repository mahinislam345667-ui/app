package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inline_data") val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @Json(name = "mime_type") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "response_mime_type") val responseMimeType: String? = "application/json",
    @Json(name = "temperature") val temperature: Float? = 0.4f
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class AiAnalysisJsonResponse(
    @Json(name = "estimatedAge") val estimatedAge: Int? = 25,
    @Json(name = "ageRangeMin") val ageRangeMin: Int? = 23,
    @Json(name = "ageRangeMax") val ageRangeMax: Int? = 27,
    @Json(name = "smileScore") val smileScore: Int? = 85,
    @Json(name = "confidenceScore") val confidenceScore: Int? = 88,
    @Json(name = "styleScore") val styleScore: Int? = 82,
    @Json(name = "symmetryScore") val symmetryScore: Int? = 89,
    @Json(name = "beardScore") val beardScore: Int? = 75,
    @Json(name = "hairScore") val hairScore: Int? = 86,
    @Json(name = "skinScore") val skinScore: Int? = 84,
    @Json(name = "eyeContactScore") val eyeContactScore: Int? = 90,
    @Json(name = "photoQualityScore") val photoQualityScore: Int? = 92,
    @Json(name = "profileScore") val profileScore: Int? = 88,
    @Json(name = "overallScore") val overallScore: Int? = 87,
    @Json(name = "firstImpression") val firstImpression: String? = "Warm, approachable, and highly charismatic presentation.",
    @Json(name = "aiRoastOrCompliment") val aiRoastOrCompliment: String? = "Looking like a CEO who accidentally walked into a fashion shoot!",
    @Json(name = "styleTip") val styleTip: String? = "Consider soft directional lighting to enhance jawline symmetry.",
    @Json(name = "photoTip") val photoTip: String? = "Great eye contact! A subtle fill light on the right side would maximize depth.",
    @Json(name = "groomingTip") val groomingTip: String? = "Hair is nicely textured. Keep up your current skincare routine."
)
