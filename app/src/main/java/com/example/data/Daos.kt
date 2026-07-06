package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvitationDao {
    @Query("SELECT * FROM invitations ORDER BY lastModified DESC")
    fun getAllInvitations(): Flow<List<InvitationEntity>>

    @Query("SELECT * FROM invitations WHERE isFavorite = 1 ORDER BY lastModified DESC")
    fun getFavoriteInvitations(): Flow<List<InvitationEntity>>

    @Query("SELECT * FROM invitations WHERE id = :id")
    suspend fun getInvitationById(id: Int): InvitationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: InvitationEntity): Long

    @Update
    suspend fun updateInvitation(invitation: InvitationEntity)

    @Delete
    suspend fun deleteInvitation(invitation: InvitationEntity)

    @Query("DELETE FROM invitations WHERE id = :id")
    suspend fun deleteInvitationById(id: Int)
}

@Dao
interface GuestDao {
    @Query("SELECT * FROM guests WHERE eventId = :eventId ORDER BY name ASC")
    fun getGuestsForEvent(eventId: Int): Flow<List<GuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: GuestEntity): Long

    @Update
    suspend fun updateGuest(guest: GuestEntity)

    @Delete
    suspend fun deleteGuest(guest: GuestEntity)

    @Query("DELETE FROM guests WHERE id = :id")
    suspend fun deleteGuestById(id: Int)
}

@Dao
interface BudgetItemDao {
    @Query("SELECT * FROM budget_items WHERE eventId = :eventId ORDER BY itemName ASC")
    fun getBudgetItemsForEvent(eventId: Int): Flow<List<BudgetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(item: BudgetItemEntity): Long

    @Update
    suspend fun updateBudgetItem(item: BudgetItemEntity)

    @Delete
    suspend fun deleteBudgetItem(item: BudgetItemEntity)

    @Query("DELETE FROM budget_items WHERE id = :id")
    suspend fun deleteBudgetItemById(id: Int)
}

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_items WHERE eventId = :eventId ORDER BY isCompleted ASC, priority DESC")
    fun getChecklistForEvent(eventId: Int): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteChecklistItemById(id: Int)
}
