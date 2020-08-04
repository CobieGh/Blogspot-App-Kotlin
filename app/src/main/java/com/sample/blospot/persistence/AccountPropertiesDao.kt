package com.sample.blospot.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.blospot.models.AccountProperties

@Dao
interface AccountPropertiesDao {

    // When the object you are trying to insert already has a row in the table, replace it with this one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserAndReplace(accountProperties: AccountProperties): Long

    // Opposite of the function above. Ignores the insert when there is the same object in out table
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk: Int): AccountProperties?

    @Query("SELECT * FROM account_properties WHERE email = :email")
    fun searchByEmail(email: String): AccountProperties?
}