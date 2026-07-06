package com.example.data

import kotlinx.coroutines.flow.Flow

class InvitationRepository(private val database: AppDatabase) {
    val allInvitations: Flow<List<InvitationEntity>> = database.invitationDao().getAllInvitations()
    val favoriteInvitations: Flow<List<InvitationEntity>> = database.invitationDao().getFavoriteInvitations()

    suspend fun getInvitationById(id: Int): InvitationEntity? {
        return database.invitationDao().getInvitationById(id)
    }

    suspend fun insertInvitation(invitation: InvitationEntity): Long {
        return database.invitationDao().insertInvitation(invitation)
    }

    suspend fun updateInvitation(invitation: InvitationEntity) {
        database.invitationDao().updateInvitation(invitation)
    }

    suspend fun deleteInvitation(invitation: InvitationEntity) {
        database.invitationDao().deleteInvitation(invitation)
    }

    suspend fun deleteInvitationById(id: Int) {
        database.invitationDao().deleteInvitationById(id)
    }

    // Guest operations
    fun getGuestsForEvent(eventId: Int): Flow<List<GuestEntity>> {
        return database.guestDao().getGuestsForEvent(eventId)
    }

    suspend fun insertGuest(guest: GuestEntity): Long {
        return database.guestDao().insertGuest(guest)
    }

    suspend fun updateGuest(guest: GuestEntity) {
        database.guestDao().updateGuest(guest)
    }

    suspend fun deleteGuest(guest: GuestEntity) {
        database.guestDao().deleteGuest(guest)
    }

    suspend fun deleteGuestById(id: Int) {
        database.guestDao().deleteGuestById(id)
    }

    // Budget operations
    fun getBudgetItemsForEvent(eventId: Int): Flow<List<BudgetItemEntity>> {
        return database.budgetItemDao().getBudgetItemsForEvent(eventId)
    }

    suspend fun insertBudgetItem(item: BudgetItemEntity): Long {
        return database.budgetItemDao().insertBudgetItem(item)
    }

    suspend fun updateBudgetItem(item: BudgetItemEntity) {
        database.budgetItemDao().updateBudgetItem(item)
    }

    suspend fun deleteBudgetItem(item: BudgetItemEntity) {
        database.budgetItemDao().deleteBudgetItem(item)
    }

    suspend fun deleteBudgetItemById(id: Int) {
        database.budgetItemDao().deleteBudgetItemById(id)
    }

    // Checklist operations
    fun getChecklistForEvent(eventId: Int): Flow<List<ChecklistItemEntity>> {
        return database.checklistItemDao().getChecklistForEvent(eventId)
    }

    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long {
        return database.checklistItemDao().insertChecklistItem(item)
    }

    suspend fun updateChecklistItem(item: ChecklistItemEntity) {
        database.checklistItemDao().updateChecklistItem(item)
    }

    suspend fun deleteChecklistItem(item: ChecklistItemEntity) {
        database.checklistItemDao().deleteChecklistItem(item)
    }

    suspend fun deleteChecklistItemById(id: Int) {
        database.checklistItemDao().deleteChecklistItemById(id)
    }
}
