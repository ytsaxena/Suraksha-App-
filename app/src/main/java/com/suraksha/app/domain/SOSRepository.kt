package com.suraksha.app.domain

import com.suraksha.app.domain.model.Contact

interface SOSRepository {
    suspend fun getContact(): List<Contact>
}