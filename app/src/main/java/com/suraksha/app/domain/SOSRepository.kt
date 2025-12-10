package com.suraksha.app.domain

import com.suraksha.app.domain.model.Contact

interface SOSRepository {
    suspend fun getContact(): List<Contact>
    suspend fun saveContacts(contacts: List<Contact>)
    suspend fun selectedContacts(): List<Contact>
    suspend fun sendSms(phoneNumber: String, message: String): Result<Unit>
}