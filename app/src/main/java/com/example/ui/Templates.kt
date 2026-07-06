package com.example.ui

import com.example.data.InvitationEntity
import org.json.JSONArray
import org.json.JSONObject

data class InvitationTemplate(
    val id: String,
    val category: String,
    val nameEn: String,
    val nameBn: String,
    val presetBg: String,
    val bgGradientStart: Long? = null,
    val bgGradientEnd: Long? = null,
    val titleEn: String,
    val titleBn: String,
    val headlineEn: String,
    val headlineBn: String,
    val bodyEn: String,
    val bodyBn: String,
    val defaultStickers: List<TemplateSticker> = emptyList()
)

data class TemplateSticker(
    val type: String,
    val x: Float,
    val y: Float,
    val scale: Float = 1.0f,
    val rotation: Float = 0f
)

object Templates {
    val list = listOf(
        InvitationTemplate(
            id = "wedding_royal",
            category = "Wedding",
            nameEn = "Royal Wedding Golden",
            nameBn = "রয়্যাল ওয়েডিং গোল্ডেন",
            presetBg = "Luxury",
            titleEn = "Wedding Celebration",
            titleBn = "শুভ বিবাহ উৎসব",
            headlineEn = "Together with their families, we invite you",
            headlineBn = "পরিবারের পক্ষ থেকে আপনাকে সাদর আমন্ত্রণ",
            bodyEn = "To celebrate the marriage of our beloved children. Your presence will bless their new journey.",
            bodyBn = "আমাদের পরম স্নেহময়ী সন্তানদের শুভ পরিণয় উৎসবে আপনার উপস্থিতি ও আশীর্বাদ কামনা করি।",
            defaultStickers = listOf(
                TemplateSticker("Rings", 0.5f, 0.15f, 1.2f),
                TemplateSticker("Flowers", 0.15f, 0.15f, 0.8f, -20f),
                TemplateSticker("Flowers", 0.85f, 0.15f, 0.8f, 20f)
            )
        ),
        InvitationTemplate(
            id = "birthday_cake",
            category = "Birthday",
            nameEn = "Classic Birthday",
            nameBn = "ক্লাসিক জন্মদিন",
            presetBg = "Floral",
            titleEn = "Happy Birthday",
            titleBn = "শুভ জন্মদিন",
            headlineEn = "You're invited to celebrate with us!",
            headlineBn = "আমাদের সাথে আনন্দ উদযাপনে যোগ দিন!",
            bodyEn = "A year older, a year wiser! Come eat cake, laugh, and celebrate another wonderful year.",
            bodyBn = "আরও একটি বছর পেরিয়ে জীবনের নতুন ধাপে পা রাখা। কেক কাটা, গল্প এবং আনন্দে মেতে ওঠার আমন্ত্রণ।",
            defaultStickers = listOf(
                TemplateSticker("Cake", 0.5f, 0.15f, 1.3f),
                TemplateSticker("Balloons", 0.2f, 0.25f, 1.0f),
                TemplateSticker("Balloons", 0.8f, 0.25f, 1.0f)
            )
        ),
        InvitationTemplate(
            id = "anniversary_silver",
            category = "Anniversary",
            nameEn = "Silver Anniversary",
            nameBn = "রজত জয়ন্তী উৎসব",
            presetBg = "Golden",
            titleEn = "Anniversary Gala",
            titleBn = "বিবাহ বার্ষিকী উৎসব",
            headlineEn = "Celebrating 25 years of love and togetherness",
            headlineBn = "ভালোবাসা ও বন্ধনের ২৫টি বছর উদযাপন",
            bodyEn = "We request your presence as we renew our vows and toast to a silver milestone of love.",
            bodyBn = "আমাদের দীর্ঘ পথচলার ২৫ বছর পূর্তিতে সুহৃদদের সাথে আনন্দ ভাগ করে নিতে আপনাদের সান্নিধ্য চাই।",
            defaultStickers = listOf(
                TemplateSticker("Heart", 0.5f, 0.15f, 1.3f),
                TemplateSticker("Star", 0.3f, 0.25f, 0.8f),
                TemplateSticker("Star", 0.7f, 0.25f, 0.8f)
            )
        ),
        InvitationTemplate(
            id = "baby_shower_sweet",
            category = "Baby Shower",
            nameEn = "Sweet Baby Shower",
            nameBn = "মিষ্টি বেবি শাওয়ার",
            presetBg = "Watercolor",
            titleEn = "Baby Shower",
            titleBn = "সাধ ভক্ষণ ও বেবি শাওয়ার",
            headlineEn = "A little one is on the way!",
            headlineBn = "নতুন অতিথির আগমন বার্তা!",
            bodyEn = "Help us shower the parents-to-be with love, laughter, and warm wishes for their little bundle of joy.",
            bodyBn = "আমাদের অনাগত সন্তানের মঙ্গল কামনায় ও হবু বাবা-মাকে শুভকামনা জানাতে আয়োজিত ঘরোয়া অনুষ্ঠানে আমন্ত্রিত।",
            defaultStickers = listOf(
                TemplateSticker("Balloons", 0.5f, 0.15f, 1.2f),
                TemplateSticker("Star", 0.2f, 0.15f, 0.8f)
            )
        ),
        InvitationTemplate(
            id = "eid_mubarak",
            category = "Eid",
            nameEn = "Eid Mubarak Greetings",
            nameBn = "ঈদ মোবারক শুভেচ্ছা",
            presetBg = "Luxury",
            titleEn = "Eid Milan Festival",
            titleBn = "ঈদ পুনর্মিলনী উৎসব",
            headlineEn = "May your Eid be blessed and full of joy",
            headlineBn = "আপনার ঈদ হোক আনন্দময় ও বরকতময়",
            bodyEn = "We warmly invite you and your family to join us for a delightful Eid Milan feast at our residence.",
            bodyBn = "ঈদের আনন্দকে আরও বহুগুণ বাড়িয়ে তুলতে আমাদের গৃহে আয়োজিত ঈদ আড্ডায় সপরিবারে আমন্ত্রিত।",
            defaultStickers = listOf(
                TemplateSticker("Lantern", 0.5f, 0.15f, 1.4f),
                TemplateSticker("Star", 0.15f, 0.15f, 0.7f),
                TemplateSticker("Star", 0.85f, 0.15f, 0.7f)
            )
        ),
        InvitationTemplate(
            id = "diwali_lights",
            category = "Diwali",
            nameEn = "Festival of Lights",
            nameBn = "দীপাবলি উৎসব কার্ড",
            presetBg = "Golden",
            titleEn = "Diwali Celebration",
            titleBn = "শুভ দীপাবলি ও দেওয়ালি",
            headlineEn = "Let's share sweet treats and sparklers",
            headlineBn = "মিষ্টি ও আলোর রোশনাই ছড়িয়ে দিন একসাথে",
            bodyEn = "We invite you to light lamps, share prayers, and celebrate Diwali with sweets and light at our home.",
            bodyBn = "আসুন প্রদীপ জ্বালিয়ে, শুভেচ্ছা বিনিময় ও মিষ্টি মুখ করে এবারের দীপাবলি আমাদের সাথে উদযাপন করুন।",
            defaultStickers = listOf(
                TemplateSticker("Lantern", 0.5f, 0.15f, 1.3f),
                TemplateSticker("Star", 0.2f, 0.2f, 0.9f)
            )
        ),
        InvitationTemplate(
            id = "ramadan_iftar",
            category = "Ramadan Iftar",
            nameEn = "Ramadan Iftar Gathering",
            nameBn = "রমজান ইফতার মাহফিল",
            presetBg = "Luxury",
            titleEn = "Iftar Gathering",
            titleBn = "ইফতার মাহফিল",
            headlineEn = "Sharing blessings of Ramadan",
            headlineBn = "পবিত্র রমজানের রহমত ও বরকত ভাগ করে নেওয়া",
            bodyEn = "We request your presence to join us in breaking our fast together at this holy Ramadan Iftar feast.",
            bodyBn = "পবিত্র মাহে রমজানের সংযম ও সৌহার্দ্যের অংশ হিসেবে আমাদের সাথে ইফতার আয়োজনে অংশগ্রহণের সাদর আমন্ত্রণ।",
            defaultStickers = listOf(
                TemplateSticker("Lantern", 0.5f, 0.15f, 1.4f)
            )
        )
    )

    fun createEntityFromTemplate(template: InvitationTemplate, language: String): InvitationEntity {
        val title = if (language == "bn") template.titleBn else template.titleEn
        val headline = if (language == "bn") template.headlineBn else template.headlineEn
        val body = if (language == "bn") template.bodyBn else template.bodyEn

        val dateText = if (language == "bn") "১৫ই আগস্ট, ২০২৬" else "August 15, 2026"
        val timeText = if (language == "bn") "সন্ধ্যা ৭:০০ ঘটিকায়" else "7:00 PM onwards"
        val venueText = if (language == "bn") "সোনারগাঁও গ্র্যান্ড হল" else "Sonargaon Grand Hall"
        val addressText = if (language == "bn") "১২, কাজী নজরুল ইসলাম এভিনিউ, ঢাকা" else "12, Kazi Nazrul Islam Avenue, Dhaka"
        val rsvpText = if (language == "bn") "মোবাইল: ০১৭০০-০০০০০০" else "Contact: +8801700-000000"

        // Generate JSON configs for Layer Editor
        val textsArray = JSONArray().apply {
            put(JSONObject().apply {
                put("id", "title")
                put("text", title)
                put("x", 0.5)
                put("y", 0.3)
                put("size", 34.0)
                put("color", 0xFFD4AF37) // Gold Color
                put("fontFamily", "serif")
                put("isBold", true)
                put("textAlignment", "Center")
            })
            put(JSONObject().apply {
                put("id", "headline")
                put("text", headline)
                put("x", 0.5)
                put("y", 0.42)
                put("size", 18.0)
                put("color", 0xFFFFFFFF)
                put("fontFamily", "serif")
                put("isItalic", true)
                put("textAlignment", "Center")
            })
            put(JSONObject().apply {
                put("id", "body")
                put("text", body)
                put("x", 0.5)
                put("y", 0.54)
                put("size", 15.0)
                put("color", 0xFFECEFF1)
                put("fontFamily", "serif")
                put("textAlignment", "Center")
            })
            put(JSONObject().apply {
                put("id", "dateTime")
                put("text", "$dateText | $timeText")
                put("x", 0.5)
                put("y", 0.68)
                put("size", 16.0)
                put("color", 0xFFD4AF37)
                put("fontFamily", "serif")
                put("isBold", true)
                put("textAlignment", "Center")
            })
            put(JSONObject().apply {
                put("id", "venue")
                put("text", "At: $venueText\n$addressText")
                put("x", 0.5)
                put("y", 0.77)
                put("size", 14.0)
                put("color", 0xFFB0BEC5)
                put("fontFamily", "serif")
                put("textAlignment", "Center")
            })
            put(JSONObject().apply {
                put("id", "rsvp")
                put("text", "RSVP: $rsvpText")
                put("x", 0.5)
                put("y", 0.88)
                put("size", 14.0)
                put("color", 0xFFFFFFFF)
                put("fontFamily", "serif")
                put("textAlignment", "Center")
            })
        }

        val stickersArray = JSONArray().apply {
            for (st in template.defaultStickers) {
                put(JSONObject().apply {
                    put("id", java.util.UUID.randomUUID().toString())
                    put("type", st.type)
                    put("x", st.x)
                    put("y", st.y)
                    put("scale", st.scale)
                    put("rotation", st.rotation)
                })
            }
        }

        return InvitationEntity(
            title = if (language == "bn") template.nameBn else template.nameEn,
            category = template.category,
            eventName = title,
            hostName = if (language == "bn") "আমন্ত্রণকারী পরিবার" else "The Family",
            guestName = "",
            date = dateText,
            time = timeText,
            venue = venueText,
            address = addressText,
            mapsLink = "https://maps.google.com",
            phoneNumber = rsvpText,
            dressCode = if (language == "bn") "উৎসবের সাজ" else "Formal / Festive",
            rsvp = rsvpText,
            notes = "",
            templateId = template.id,
            bgImagePreset = template.presetBg,
            bgGradientStart = template.bgGradientStart,
            bgGradientEnd = template.bgGradientEnd,
            textsJson = textsArray.toString(),
            stickersJson = stickersArray.toString()
        )
    }
}
