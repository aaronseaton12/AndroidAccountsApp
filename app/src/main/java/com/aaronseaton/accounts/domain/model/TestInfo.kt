package com.aaronseaton.accounts.domain.model

import java.util.Date

class TestInfo {
    companion object {

        val Damian = Customer(
            customerID = "Customer-1",
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
            documentID = "Customer-1"
        )
        val Khadija = Customer(
            customerID = "Customer-2",
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
            documentID = "Customer-2"
        )
        val Roger = Customer(
            customerID = "Customer-3",
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
            documentID = "Customer-3"
        )

        val firstPayment = Payment(
            "Payment-1",
            Date(122, 1, 11),
            1545.00,
            Roger.documentID,
            "Payment-2",
            "Payment for Service of Claim Form",
            "Cash"
        )
        val secondPayment = Payment(
            "Payment-2",
            Date(122, 2, 21),
            3433.00,
            Khadija.documentID,
            "Payment-2",
            "Payment for Help with Submissions",
            "Bank"
        )
        val thirdPayment = secondPayment.copy(documentID = "Payment-3", amount = 25434.45)
        val fourthPayment = firstPayment.copy(documentID = "Payment-4", amount = 6987.45)

        val firstReceipt = Receipt(
            "Receipt-1",
            Date(122, 3, 17),
            11545.00,
            Damian.documentID,
            "Receipt-2",
            "Fees in Probate of Estate",
            "Cheque"
        )
        val secondReceipt = Receipt(
            "Receipt-2",
            Date(122, 4, 15),
            343.00,
            Roger.documentID,
            "Receipt-2",
            "Fees for Advocacy",
            "Cash"

        )
        val thirdReceipt = secondReceipt.copy(documentID = "Receipt-3", amount = 5434.45)
        val fourthReceipt = firstReceipt.copy(documentID = "Receipt-4", amount = 98987.45)

        val listOfTestReceipts = listOf(firstReceipt, secondReceipt, thirdReceipt, fourthReceipt)
        val listOfTestPayments = listOf(firstPayment, secondPayment, thirdPayment, fourthPayment)
        val listOfCustomers = listOf(Khadija, Damian, Roger)

        val matters = listOf(
            Matter(
                documentID = "DOC-1",
                customerID = Khadija.documentID,
                description = "Legal matter 1",
                title = "Case 1",
                type = "Civil",
                responsibleAttorney = "Attorney 1",
                createdBy = "User 1",
                number = 101.0,
                open = true
            ),
            Matter(
                documentID = "DOC-2",
                customerID = Damian.documentID,
                description = "Legal matter 2",
                title = "Case 2",
                type = "Civil",
                responsibleAttorney = "Attorney 2",
                createdBy = "User 2",
                number = 102.0,
                open = true
            ),
            Matter(
                documentID = "DOC-3",
                customerID = Roger.documentID,
                description = "Legal matter 3",
                title = "Case 3",
                type = "Civil",
                responsibleAttorney = "Attorney 3",
                createdBy = "User 3",
                number = 103.0,
                open = true
            ),
            Matter(
                documentID = "DOC-4",
                customerID = Khadija.documentID,
                description = "Legal matter 4",
                title = "Case 4",
                type = "Civil",
                responsibleAttorney = "Attorney 4",
                createdBy = "User 4",
                number = 104.0,
                open = true
            ),
            Matter(
                documentID = "DOC-5",
                customerID = Roger.documentID,
                description = "Legal matter 5",
                title = "Case 5",
                type = "Civil",
                responsibleAttorney = "Attorney 5",
                createdBy = "User 5",
                number = 105.0,
                open = true
            ),
            Matter(
                documentID = "DOC-6",
                customerID = Damian.documentID,
                description = "Legal matter 6",
                title = "Case 6",
                type = "Civil",
                responsibleAttorney = "Attorney 6",
                createdBy = "User 6",
                number = 106.0,
                open = true
            ),
            Matter(
                documentID = "DOC-7",
                customerID = Khadija.documentID,
                description = "Legal matter 7",
                title = "Case 7",
                type = "Civil",
                responsibleAttorney = "Attorney 7",
                createdBy = "User 7",
                number = 107.0,
                open = true
            ),
            Matter(
                documentID = "DOC-8",
                customerID = Roger.documentID,
                description = "Legal matter 8",
                title = "Case 8",
                type = "Civil",
                responsibleAttorney = "Attorney 8",
                createdBy = "User 8",
                number = 108.0,
                open = true
            ),
            Matter(
                documentID = "DOC-9",
                customerID = Damian.documentID,
                description = "Legal matter 9",
                title = "Case 9",
                type = "Civil",
                responsibleAttorney = "Attorney 9",
                createdBy = "User 9",
                number = 109.0,
                open = true
            ),
            Matter(
                documentID = "DOC-10",
                customerID = Damian.documentID,
                description = "Legal matter 10",
                title = "Case 10",
                type = "Civil",
                responsibleAttorney = "Attorney 10",
                createdBy = "User 10",
                number = 110.0,
                open = true
            ),
        )
    }

}