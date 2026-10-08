package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = 0")
    suspend fun clearDefaultFlags()

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: Long)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddressById(id: Long)

    @Query("SELECT COUNT(*) FROM addresses")
    suspend fun getCount(): Int
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getBookingByIdFlow(id: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE status NOT IN ('SERVICE_COMPLETED', 'PAID', 'REVIEWED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveBookingFlow(): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: String)
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registeredAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0, authToken = NULL")
    suspend fun logoutAll()

    @Query("UPDATE users SET isLoggedIn = 1, authToken = :authToken WHERE id = :id")
    suspend fun setActiveUser(id: String, authToken: String)

    @Query("UPDATE users SET passwordHash = :passwordHash, passwordSalt = :passwordSalt WHERE id = :id")
    suspend fun updatePassword(id: String, passwordHash: String, passwordSalt: String)

    @Query("UPDATE users SET phoneVerified = :phoneVerified WHERE id = :id")
    suspend fun updatePhoneVerified(id: String, phoneVerified: Boolean)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

