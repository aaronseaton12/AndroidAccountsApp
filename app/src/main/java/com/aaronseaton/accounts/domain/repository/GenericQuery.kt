package com.aaronseaton.accounts.domain.repository

sealed interface GenericQuery {
    data class WhereQuery(
        val field:String,
        val value: Any
    ): GenericQuery

    data class TimeQuery(
        val field: String,
        val operator: Comparator,
        val value: Any
    )

    data class OrderQuery(
        val field: String,
        val direction: DirectionOrder = DirectionOrder.DESC
    ): GenericQuery

    data class LimitQuery(
        val number: Int
    ): GenericQuery

    object NoQuery : GenericQuery

    enum class DirectionOrder{
        ASC, DESC
    }
    enum class Comparator{
        EQUAL,
        MORETHAN,
        LESSTHAN,
        MORETHANOREQUAL,
        LESSTHANOREQUAL,
    }
}



