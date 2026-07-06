package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.BitmapExporter
import com.example.util.GeminiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class TextLayerState(
    val id: String,
    val text: String,
    val x: Float, // percentage 0f..1f of editor canvas
    val y: Float, // percentage 0f..1f of editor canvas
    val size: Float, // text size unit in editor
    val color: Long,
    val fontFamily: String,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val opacity: Float = 1.0f,
    val textAlignment: String = "Center" // Left, Center, Right
)

data class StickerLayerState(
    val id: String,
    val type: String, // "Heart", "Star", "Balloons", "Cake", "Rings", "Flowers", "Lantern"
    val x: Float,
    val y: Float,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f
)

class InvitationViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "InvitationViewModel"
    private val database = AppDatabase.getDatabase(application)
    private val repository = InvitationRepository(database)

    // Flow listings
    val allInvitations = repository.allInvitations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteInvitations = repository.favoriteInvitations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // App Preferences / Settings
    var language by mutableStateOf("en") // "en" or "bn"
    var isDarkMode by mutableStateOf(true)
    var autoSave by mutableStateOf(true)

    // Active invitation editor state
    var currentInvitation by mutableStateOf<InvitationEntity?>(null)
        private set
    
    val textLayers = mutableStateListOf<TextLayerState>()
    val stickerLayers = mutableStateListOf<StickerLayerState>()
    
    var selectedTextLayerId by mutableStateOf<String?>(null)
    var selectedStickerLayerId by mutableStateOf<String?>(null)

    // Background configurations
    var bgPreset by mutableStateOf<String?>("None")
    var bgGradientStart by mutableStateOf<Long?>(null)
    var bgGradientEnd by mutableStateOf<Long?>(null)
    var qrCodeData by mutableStateOf<String?>(null)

    // Event Management active streams
    private val _guests = MutableStateFlow<List<GuestEntity>>(emptyList())
    val guests: StateFlow<List<GuestEntity>> = _guests.asStateFlow()

    private val _budgetItems = MutableStateFlow<List<BudgetItemEntity>>(emptyList())
    val budgetItems: StateFlow<List<BudgetItemEntity>> = _budgetItems.asStateFlow()

    private val _checklistItems = MutableStateFlow<List<ChecklistItemEntity>>(emptyList())
    val checklistItems: StateFlow<List<ChecklistItemEntity>> = _checklistItems.asStateFlow()

    // AI Generation states
    var aiLoading by mutableStateOf(false)
    var aiResult by mutableStateOf<Map<String, String>?>(null)
    var aiError by mutableStateOf<String?>(null)

    init {
        // Load settings from SharedPreferences
        val prefs = application.getSharedPreferences("infinity_invitation_prefs", Context.MODE_PRIVATE)
        language = prefs.getString("language", "en") ?: "en"
        isDarkMode = prefs.getBoolean("dark_mode", true)
        autoSave = prefs.getBoolean("auto_save", true)
    }

    fun toggleLanguage() {
        language = if (language == "en") "bn" else "en"
        getApplication<Application>()
            .getSharedPreferences("infinity_invitation_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("language", language)
            .apply()
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        getApplication<Application>()
            .getSharedPreferences("infinity_invitation_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("dark_mode", isDarkMode)
            .apply()
    }

    // --- Editor Control and Layers ---
    
    fun loadInvitationForEditing(invitation: InvitationEntity) {
        currentInvitation = invitation
        bgPreset = invitation.bgImagePreset
        bgGradientStart = invitation.bgGradientStart
        bgGradientEnd = invitation.bgGradientEnd
        qrCodeData = invitation.qrCodeData
        
        selectedTextLayerId = null
        selectedStickerLayerId = null

        // Parse Layers
        textLayers.clear()
        try {
            val array = JSONArray(invitation.textsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                textLayers.add(
                    TextLayerState(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        text = obj.optString("text", ""),
                        x = obj.optDouble("x", 0.5).toFloat(),
                        y = obj.optDouble("y", 0.5).toFloat(),
                        size = obj.optDouble("size", 20.0).toFloat(),
                        color = obj.optLong("color", 0xFFFFFFFF),
                        fontFamily = obj.optString("fontFamily", "serif"),
                        isBold = obj.optBoolean("isBold", false),
                        isItalic = obj.optBoolean("isItalic", false),
                        isUnderline = obj.optBoolean("isUnderline", false),
                        opacity = obj.optDouble("opacity", 1.0).toFloat(),
                        textAlignment = obj.optString("textAlignment", "Center")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading text layers: ${e.message}")
        }

        stickerLayers.clear()
        try {
            val array = JSONArray(invitation.stickersJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                stickerLayers.add(
                    StickerLayerState(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        type = obj.optString("type", "Heart"),
                        x = obj.optDouble("x", 0.5).toFloat(),
                        y = obj.optDouble("y", 0.5).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        rotation = obj.optDouble("rotation", 0.0).toFloat(),
                        opacity = obj.optDouble("opacity", 1.0).toFloat()
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sticker layers: ${e.message}")
        }

        // Load Event lists
        observeEventManagement(invitation.id)
    }

    private fun observeEventManagement(eventId: Int) {
        viewModelScope.launch {
            repository.getGuestsForEvent(eventId).collect {
                _guests.value = it
            }
        }
        viewModelScope.launch {
            repository.getBudgetItemsForEvent(eventId).collect {
                _budgetItems.value = it
            }
        }
        viewModelScope.launch {
            repository.getChecklistForEvent(eventId).collect {
                _checklistItems.value = it
            }
        }
    }

    fun startNewFromTemplate(template: InvitationTemplate) {
        viewModelScope.launch {
            val newEntity = Templates.createEntityFromTemplate(template, language)
            val id = repository.insertInvitation(newEntity)
            val savedEntity = repository.getInvitationById(id.toInt())
            if (savedEntity != null) {
                loadInvitationForEditing(savedEntity)
            }
        }
    }

    fun startNewBlank() {
        viewModelScope.launch {
            val dateText = if (language == "bn") "১লা জানুয়ারি, ২০২৭" else "January 1, 2027"
            val textsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "title")
                    put("text", if (language == "bn") "আমন্ত্রণ লিপি" else "Celebration Title")
                    put("x", 0.5)
                    put("y", 0.3)
                    put("size", 32.0)
                    put("color", 0xFFFFFFFF)
                    put("fontFamily", "serif")
                    put("isBold", true)
                })
                put(JSONObject().apply {
                    put("id", "body")
                    put("text", if (language == "bn") "আপনাকে আমাদের এই বিশেষ ক্ষণে সাদর আমন্ত্রণ জানাচ্ছি।" else "We request the pleasure of your company on this joyful occasion.")
                    put("x", 0.5)
                    put("y", 0.5)
                    put("size", 16.0)
                    put("color", 0xFFECEFF1)
                    put("fontFamily", "serif")
                })
            }
            val newEntity = InvitationEntity(
                title = if (language == "bn") "খালি ক্যানভাস" else "Custom Invitation",
                category = "Custom",
                eventName = if (language == "bn") "উৎসবের নাম" else "My Event",
                hostName = "The Host",
                guestName = "",
                date = dateText,
                time = "6:00 PM",
                venue = "My Venue",
                address = "123 Event Street",
                mapsLink = "",
                phoneNumber = "",
                dressCode = "",
                rsvp = "",
                notes = "",
                templateId = "blank",
                bgGradientStart = 0xFF1E293B, // dark slate gradient
                bgGradientEnd = 0xFF0F172A,
                textsJson = textsArray.toString(),
                stickersJson = "[]"
            )
            val id = repository.insertInvitation(newEntity)
            val savedEntity = repository.getInvitationById(id.toInt())
            if (savedEntity != null) {
                loadInvitationForEditing(savedEntity)
            }
        }
    }

    fun duplicateInvitation(invitation: InvitationEntity) {
        viewModelScope.launch {
            val dup = invitation.copy(
                id = 0,
                title = "${invitation.title} (Copy)",
                lastModified = System.currentTimeMillis()
            )
            repository.insertInvitation(dup)
        }
    }

    fun deleteInvitation(invitation: InvitationEntity) {
        viewModelScope.launch {
            repository.deleteInvitation(invitation)
        }
    }

    fun toggleFavorite(invitation: InvitationEntity) {
        viewModelScope.launch {
            val updated = invitation.copy(isFavorite = !invitation.isFavorite)
            repository.updateInvitation(updated)
            if (currentInvitation?.id == invitation.id) {
                currentInvitation = updated
            }
        }
    }

    fun updateInvitationTitle(newTitle: String) {
        val current = currentInvitation ?: return
        currentInvitation = current.copy(title = newTitle)
        saveCurrentEditorStateToDb()
    }

    // --- Background/Gradient/QR Updates ---
    
    fun setBackgroundPreset(preset: String) {
        bgPreset = preset
        bgGradientStart = null
        bgGradientEnd = null
        saveCurrentEditorStateToDb()
    }

    fun setBackgroundGradient(start: Long, end: Long) {
        bgPreset = "None"
        bgGradientStart = start
        bgGradientEnd = end
        saveCurrentEditorStateToDb()
    }

    fun updateQrCode(data: String?) {
        qrCodeData = if (data.isNullOrBlank()) null else data
        saveCurrentEditorStateToDb()
    }

    // --- Layer Custom Operations ---

    fun addCustomText(textString: String) {
        val id = UUID.randomUUID().toString()
        val defaultText = TextLayerState(
            id = id,
            text = textString,
            x = 0.5f,
            y = 0.4f,
            size = 20.0f,
            color = 0xFFFFFFFF,
            fontFamily = "serif"
        )
        textLayers.add(defaultText)
        selectedTextLayerId = id
        selectedStickerLayerId = null
        saveCurrentEditorStateToDb()
    }

    fun addSticker(type: String) {
        val id = UUID.randomUUID().toString()
        val defaultSticker = StickerLayerState(
            id = id,
            type = type,
            x = 0.5f,
            y = 0.2f,
            scale = 1.0f,
            rotation = 0f
        )
        stickerLayers.add(defaultSticker)
        selectedStickerLayerId = id
        selectedTextLayerId = null
        saveCurrentEditorStateToDb()
    }

    fun selectTextLayer(id: String?) {
        selectedTextLayerId = id
        if (id != null) selectedStickerLayerId = null
    }

    fun selectStickerLayer(id: String?) {
        selectedStickerLayerId = id
        if (id != null) selectedTextLayerId = null
    }

    fun updateSelectedTextLayer(transform: (TextLayerState) -> TextLayerState) {
        val selId = selectedTextLayerId ?: return
        val index = textLayers.indexOfFirst { it.id == selId }
        if (index != -1) {
            textLayers[index] = transform(textLayers[index])
            saveCurrentEditorStateToDb()
        }
    }

    fun updateSelectedStickerLayer(transform: (StickerLayerState) -> StickerLayerState) {
        val selId = selectedStickerLayerId ?: return
        val index = stickerLayers.indexOfFirst { it.id == selId }
        if (index != -1) {
            stickerLayers[index] = transform(stickerLayers[index])
            saveCurrentEditorStateToDb()
        }
    }

    fun deleteSelectedLayer() {
        val textId = selectedTextLayerId
        val stickerId = selectedStickerLayerId
        
        if (textId != null) {
            textLayers.removeAll { it.id == textId }
            selectedTextLayerId = null
        } else if (stickerId != null) {
            stickerLayers.removeAll { it.id == stickerId }
            selectedStickerLayerId = null
        }
        saveCurrentEditorStateToDb()
    }

    fun bringSelectedToFront() {
        // Move to end of list so it draws last (on top)
        val textId = selectedTextLayerId
        val stickerId = selectedStickerLayerId

        if (textId != null) {
            val idx = textLayers.indexOfFirst { it.id == textId }
            if (idx != -1 && idx != textLayers.lastIndex) {
                val item = textLayers.removeAt(idx)
                textLayers.add(item)
                saveCurrentEditorStateToDb()
            }
        } else if (stickerId != null) {
            val idx = stickerLayers.indexOfFirst { it.id == stickerId }
            if (idx != -1 && idx != stickerLayers.lastIndex) {
                val item = stickerLayers.removeAt(idx)
                stickerLayers.add(item)
                saveCurrentEditorStateToDb()
            }
        }
    }

    /**
     * Serializes textLayers and stickerLayers back to the current database entity.
     */
    fun saveCurrentEditorStateToDb() {
        val current = currentInvitation ?: return
        
        // Build JSON representations
        val textsArr = JSONArray()
        for (t in textLayers) {
            textsArr.put(JSONObject().apply {
                put("id", t.id)
                put("text", t.text)
                put("x", t.x)
                put("y", t.y)
                put("size", t.size)
                put("color", t.color)
                put("fontFamily", t.fontFamily)
                put("isBold", t.isBold)
                put("isItalic", t.isItalic)
                put("isUnderline", t.isUnderline)
                put("opacity", t.opacity)
                put("textAlignment", t.textAlignment)
            })
        }

        val stickersArr = JSONArray()
        for (s in stickerLayers) {
            stickersArr.put(JSONObject().apply {
                put("id", s.id)
                put("type", s.type)
                put("x", s.x)
                put("y", s.y)
                put("scale", s.scale)
                put("rotation", s.rotation)
                put("opacity", s.opacity)
            })
        }

        val updated = current.copy(
            bgImagePreset = bgPreset,
            bgGradientStart = bgGradientStart,
            bgGradientEnd = bgGradientEnd,
            qrCodeData = qrCodeData,
            textsJson = textsArr.toString(),
            stickersJson = stickersArr.toString(),
            lastModified = System.currentTimeMillis()
        )

        currentInvitation = updated
        
        if (autoSave) {
            viewModelScope.launch {
                repository.updateInvitation(updated)
            }
        }
    }

    // Force save editor to database
    fun saveDraftForce() {
        val current = currentInvitation ?: return
        viewModelScope.launch {
            repository.updateInvitation(current)
        }
    }

    // --- Guest List Operations ---
    
    fun addGuest(name: String, phone: String, email: String, rsvp: String, notes: String = "") {
        val eventId = currentInvitation?.id ?: return
        viewModelScope.launch {
            repository.insertGuest(
                GuestEntity(
                    eventId = eventId,
                    name = name,
                    phone = phone,
                    email = email,
                    rsvpStatus = rsvp,
                    notes = notes
                )
            )
        }
    }

    fun updateGuestRsvp(guest: GuestEntity, newRsvp: String) {
        viewModelScope.launch {
            repository.updateGuest(guest.copy(rsvpStatus = newRsvp))
        }
    }

    fun deleteGuest(guest: GuestEntity) {
        viewModelScope.launch {
            repository.deleteGuest(guest)
        }
    }

    // --- Budget Operations ---

    fun addBudgetItem(itemName: String, category: String, estimated: Double, actual: Double) {
        val eventId = currentInvitation?.id ?: return
        viewModelScope.launch {
            repository.insertBudgetItem(
                BudgetItemEntity(
                    eventId = eventId,
                    itemName = itemName,
                    category = category,
                    estimatedCost = estimated,
                    actualCost = actual,
                    isPaid = false
                )
            )
        }
    }

    fun toggleBudgetItemPaid(item: BudgetItemEntity) {
        viewModelScope.launch {
            repository.updateBudgetItem(item.copy(isPaid = !item.isPaid))
        }
    }

    fun updateBudgetItemCosts(item: BudgetItemEntity, est: Double, act: Double) {
        viewModelScope.launch {
            repository.updateBudgetItem(item.copy(estimatedCost = est, actualCost = act))
        }
    }

    fun deleteBudgetItem(item: BudgetItemEntity) {
        viewModelScope.launch {
            repository.deleteBudgetItem(item)
        }
    }

    // --- Checklist Operations ---

    fun addChecklistItem(taskName: String, priority: String, dueDate: String) {
        val eventId = currentInvitation?.id ?: return
        viewModelScope.launch {
            repository.insertChecklistItem(
                ChecklistItemEntity(
                    eventId = eventId,
                    taskName = taskName,
                    priority = priority,
                    dueDate = dueDate,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.updateChecklistItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }

    // --- Export / Share ---

    fun renderBitmap(context: Context, quality: BitmapExporter.ExportQuality): Bitmap {
        val current = currentInvitation ?: return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        return BitmapExporter.renderInvitationToBitmap(context, current, quality)
    }

    fun getShareUri(context: Context, bitmap: Bitmap): Uri? {
        return BitmapExporter.saveBitmapToShareUri(context, bitmap)
    }

    fun getPdfShareUri(context: Context): Uri? {
        val current = currentInvitation ?: return null
        return BitmapExporter.exportToPdfSimulation(context, current)
    }

    // --- Gemini Assisted Content Writer ---

    fun generateWithGemini(eventType: String, hostName: String, details: String) {
        aiLoading = true
        aiResult = null
        aiError = null
        
        viewModelScope.launch {
            try {
                val map = GeminiHelper.generateInvitationContent(eventType, hostName, details, language)
                aiResult = map
                aiLoading = false
            } catch (e: Exception) {
                Log.e(TAG, "Gemini call failure in VM: ${e.message}")
                aiError = e.message
                aiLoading = false
            }
        }
    }

    fun applyGeminiResultToEditor() {
        val result = aiResult ?: return
        val current = currentInvitation ?: return

        val title = result["title"] ?: ""
        val headline = result["headline"] ?: ""
        val body = result["body"] ?: ""
        val dressCode = result["dressCode"] ?: ""
        val rsvpText = result["rsvp"] ?: ""
        val notesText = result["additionalNotes"] ?: ""

        // Find and replace text matching existing titles/headlines, or replace the whole layer texts
        for (i in 0 until textLayers.size) {
            val layer = textLayers[i]
            when (layer.id) {
                "title" -> if (title.isNotEmpty()) textLayers[i] = layer.copy(text = title)
                "headline" -> if (headline.isNotEmpty()) textLayers[i] = layer.copy(text = headline)
                "body" -> if (body.isNotEmpty()) textLayers[i] = layer.copy(text = body)
                "rsvp" -> if (rsvpText.isNotEmpty()) textLayers[i] = layer.copy(text = rsvpText)
            }
        }

        // Add additional fields if they were non-empty and didn't have existing slots
        if (dressCode.isNotEmpty() && textLayers.none { it.id == "dressCode" }) {
            textLayers.add(TextLayerState(
                id = "dressCode",
                text = if (language == "bn") "পোশাকের ধরণ: $dressCode" else "Dress Code: $dressCode",
                x = 0.5f,
                y = 0.83f,
                size = 14f,
                color = 0xFFFFFFFF,
                fontFamily = "serif"
            ))
        }

        if (notesText.isNotEmpty() && textLayers.none { it.id == "notes" }) {
            textLayers.add(TextLayerState(
                id = "notes",
                text = notesText,
                x = 0.5f,
                y = 0.92f,
                size = 12f,
                color = 0xFFECEFF1,
                fontFamily = "serif",
                isItalic = true
            ))
        }

        currentInvitation = current.copy(
            eventName = title.ifEmpty { current.eventName },
            dressCode = dressCode.ifEmpty { current.dressCode },
            rsvp = rsvpText.ifEmpty { current.rsvp },
            notes = notesText.ifEmpty { current.notes }
        )

        saveCurrentEditorStateToDb()
        aiResult = null // clear result once applied
    }
}
