package com.example.util

import android.util.Log

object GeminiHelper {
    private const val TAG = "GeminiHelper"

    /**
     * Offline invitation builder that creates fully structured cards locally without any API key or internet required.
     */
    suspend fun generateInvitationContent(
        eventType: String,
        hostName: String,
        details: String,
        language: String // "en" or "bn"
    ): Map<String, String> {
        Log.d(TAG, "Generating invitation offline for type: $eventType, language: $language")
        return getLocalFallbackContent(eventType, hostName, details, language)
    }

    fun getLocalFallbackContent(
        eventType: String,
        hostName: String,
        details: String,
        language: String
    ): Map<String, String> {
        val displayHost = if (hostName.isBlank()) {
            if (language == "bn") "আমাদের পরিবার" else "Our Family"
        } else {
            hostName
        }

        return if (language == "bn") {
            val banglaEventName = when (eventType.lowercase()) {
                "wedding" -> "শুভ বিবাহ"
                "birthday" -> "শুভ জন্মদিন"
                "party" -> "গেট-টুগেদার উৎসব"
                "religious" -> "ধর্মীয় উৎসব"
                else -> eventType
            }
            mapOf(
                "title" to banglaEventName,
                "headline" to "আমাদের আনন্দের মুহূর্তে আপনাকে সাদর আমন্ত্রণ",
                "body" to "সবিনয় নিবেদন, অত্যন্ত আনন্দের সাথে জানাচ্ছি যে আগামীতে আমাদের পরিবারের $banglaEventName উদযাপিত হতে যাচ্ছে। এই শুভ ক্ষণে আপনি এবং আপনার সপরিবারে উপস্থিতি কামনা করি। $displayHost-এর পক্ষ থেকে আন্তরিক আমন্ত্রণ। ${if (details.isNotEmpty()) "\nবিশেষ দ্রষ্টব্য: $details" else ""}",
                "dressCode" to "ঐতিহ্যবাহী মার্জিত পোশাক",
                "rsvp" to "যোগাযোগ ও RSVP: $displayHost",
                "additionalNotes" to "আপনাদের উপস্থিতি আমাদের উৎসবের আনন্দকে দ্বিগুণ করে তুলবে।"
            )
        } else {
            val englishEventName = when (eventType.lowercase()) {
                "wedding" -> "Wedding Ceremony"
                "birthday" -> "Birthday Celebration"
                "party" -> "Grand Festive Party"
                "religious" -> "Auspicious Occasion"
                else -> eventType
            }
            mapOf(
                "title" to englishEventName,
                "headline" to "We request the honor of your presence",
                "body" to "We are delighted to invite you to celebrate the joyous occasion of our $englishEventName. Your esteemed presence and heartfelt blessings will make this day truly special for us. Warm regards from $displayHost. ${if (details.isNotEmpty()) "\nDetails: $details" else ""}",
                "dressCode" to "Elegant or Festive Attire",
                "rsvp" to "RSVP: Please contact $displayHost",
                "additionalNotes" to "We look forward to celebrating this beautiful milestone with you!"
            )
        }
    }
}
