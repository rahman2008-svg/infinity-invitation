package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invitations")
data class InvitationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val eventName: String,
    val hostName: String,
    val guestName: String,
    val date: String,
    val time: String,
    val venue: String,
    val address: String,
    val mapsLink: String,
    val phoneNumber: String,
    val dressCode: String,
    val rsvp: String,
    val notes: String,
    val templateId: String,
    val isFavorite: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    
    // JSON-serialized components for layer editor
    val textsJson: String = "[]",
    val stickersJson: String = "[]",
    val bgGradientStart: Long? = null,
    val bgGradientEnd: Long? = null,
    val bgImagePreset: String? = null,
    val qrCodeData: String? = null
)

@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int, // relates to InvitationEntity.id or a standalone event
    val name: String,
    val phone: String,
    val email: String,
    val rsvpStatus: String, // "Confirmed", "Declined", "Pending"
    val notes: String = ""
)

@Entity(tableName = "budget_items")
data class BudgetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val itemName: String,
    val category: String,
    val estimatedCost: Double,
    val actualCost: Double,
    val isPaid: Boolean = false
)

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val taskName: String,
    val isCompleted: Boolean = false,
    val dueDate: String = "",
    val priority: String = "Medium" // "High", "Medium", "Low"
)
