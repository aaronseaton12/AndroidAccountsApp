package com.aaronseaton.accounts.domain.model

import java.util.*

class TestInfo {
    companion object {

        val Damian = Customer(
            customerID = "y8De23MjWowp",
            firstName = "Damian",
            middleName = "Roy",
            lastName = "Mani",
            emailAddress = "realmani@yahoo.com",
            address = Address(
                "#3 Lovely Lane",
                "Righteous Ave",
                "Port of Spain",
                "Trinidad and Tobago"
            ),
            phoneNumber = PhoneNumber(
                "888-8888",
                "712-6345",
                "485-6712"
            ),
            documentID = "y8De23MjWowp"
        )
        val Khadija = Customer(
            customerID = "customer343",
            firstName = "Khadija",
            middleName = "Aya",
            lastName = "Sinanan",
            emailAddress = "ksins@yahoo.com",
            address = Address(
                "#4 St. George Street",
                "Grande Avenue",
                "Sangre Grande",
                "Trinidad and Tobago"
            ),
            phoneNumber = PhoneNumber(
                "777-7777",
                "612-9345",
                "285-6712"
            ),
            documentID = "customer343"
        )
        val Roger = Customer(
            customerID = "customer457",
            firstName = "Roger",
            middleName = "Vax",
            lastName = "Harri",
            emailAddress = "harilal@yahoo.com",
            address = Address(
                "#Honerman Lane",
                "La Puerta Avenue",
                "Deigo Martin",
                "Trinidad and Tobago"
            ),
            phoneNumber = PhoneNumber(
                "444-4444",
                "612-9345",
                "285-6712"
            ),
            documentID = "customer457"
        )

        val firstPayment = Payment(
            "first",
            Date(122, 1, 11),
            1545.00,
            "customer457",
            "customer457",
            "Payment for Service of Claim Form",
            "Cash"
        )
        val secondPayment = Payment(
            "second",
            Date(122, 2, 21),
            3433.00,
            "customer343",
            "customer343",
            "Payment for Help with Submissions",
            "Bank"
        )
        val thirdPayment = secondPayment.copy(documentID = "customer457", amount = 25434.45)
        val fourthPayment = firstPayment.copy(documentID = "customer457", amount = 6987.45)

        val firstReceipt = Receipt(
            "primero",
            Date(122, 3, 17),
            11545.00,
            "customer123",
            "customer123",
            "Fees in Probate of Estate",
            "Cheque"
        )
        val secondReceipt = Receipt(
            "segundo",
            Date(122, 4, 15),
            343.00,
            "customer343",
            "customer343",
            "Fees for Advocacy",
            "Cash"

        )
        val thirdReceipt = secondReceipt.copy(documentID = "customer457", amount = 5434.45)
        val fourthReceipt = firstReceipt.copy(documentID = "customer457", amount = 98987.45)

        val listOfTestReceipts = listOf(firstReceipt, secondReceipt, thirdReceipt, fourthReceipt)
        val listOfTestPayments = listOf(firstPayment, secondPayment, thirdPayment, fourthPayment)
        val listOfCustomers = listOf(Khadija, Damian, Roger)
    }

}