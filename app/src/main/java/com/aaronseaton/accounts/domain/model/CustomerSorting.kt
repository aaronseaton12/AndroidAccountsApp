package com.aaronseaton.accounts.domain.model

sealed interface Sorting {
    enum class CustomerSorting {
        BY_FIRST_NAME,
        BY_LAST_NAME,
        BY_CUSTOMER_ID,
    }

    enum class TransactionSorting {
        BY_CUSTOMER_FIRSTNAME,
        BY_DATE,
        BY_VALUE
    }

    enum class TaskSorting {
        BY_ASSIGNED_TO,
        BY_DUE_DATE,
        BY_PRIORITY
    }

}

