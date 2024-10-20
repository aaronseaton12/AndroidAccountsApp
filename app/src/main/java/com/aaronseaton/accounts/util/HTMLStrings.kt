package com.aaronseaton.accounts.util

import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat

val htmlString: (String, Customer, Business, User, FinancialTransaction) -> String = { styleString, customer, business, accountUser, transaction ->
    """<!DOCTYPE html>
<html>
<head>
    <style>$styleString</style>
    <title>${transaction.transactionName()} ${transaction.documentID.take(4)} for ${customer.fullName()}</title>
</head>
<body>
    <header>
        <div class="logo">
            <img src = ${business.logo} >
        </div>
        <div class="business-info">
            <div>${business.name}</div>
            <div>${business.address.addressLine1}</div>
            <div>${business.address.addressLine2}</div>
            <div>${business.address.city}</div>
            <div>${business.address.country}</div>
            <div>${business.emailAddress}</div>
            <div>${business.phoneNumber.cellNumber}</div>
            <div>${business.phoneNumber.workNumber}</div>
        </div>
    </header>
    <p class="information-line">${transaction.transactionName()}  ‧  ${transaction.documentID.take(4)}</p>
    <table>
        <tr>
            <td class ="label">Client: </td>
            <td width="20pt"></td>
            <td>${customer.fullName()} </td>
        </tr>
        <tr>
            <td class ="label">Paid: </td>
            <td width="20pt"></td>
            <td>$${decimalFormat.format(transaction.amount)}</td>
        </tr>
        <tr>
            <td class ="label">Date: </td>
            <td width="20pt"></td>
            <td>${dateFormatter.format(transaction.date)}</td>
        </tr>
        <tr>
            <td class ="label">Reason: </td>
            <td width="20pt"></td>
            <td> ${transaction.reason}</td>
        </tr>
        <tr>
            <td class ="label"></td>
            <td width="20pt"></td>
            <td style ="font-family: 'Brush Script MT', cursive; color:blue;"> ${accountUser.fullName}</td>
        </tr>
    <table>

<footer>
    <div>
        <div>${business.name}</div>
        <div>${business.address}</div>
    </div>
</footer>
</body>
</html>""".trim()
}
val htmlStringTwo: (String, Customer, Business, User, FinancialTransaction) -> String = {styleString, customer, business, accountUser, transaction ->
    """<!DOCTYPE html>
<html>
<head>
    <style>${styleString}</style>
    <title>${transaction.transactionName()} ${transaction.documentID.take(4)} for ${customer.fullName()}</title>
</head>
<body>
    <header>
        <div class="logo">
            <img src = ${business.logo} >
        </div>
        <div class="business-info">
            <div>${business.name}</div>
            <div>${business.address.addressLine1}</div>
            <div>${business.address.addressLine2}</div>
            <div>${business.address.city}</div>
            <div>${business.address.country}</div>
            <div>${business.emailAddress}</div>
            <div>${business.phoneNumber.cellNumber}</div>
            <div>${business.phoneNumber.workNumber}</div>
        </div>
    </header>
    <p class="information-line">${transaction.transactionName()}</p>
    <table>
        <tr>
            <td class ="label">Client: </td>
            <td width="20pt"></td>
            <td>${customer.fullName()} </td>
        </tr>
        <tr>
            <td class ="label">Paid: </td>
            <td width="20pt"></td>
            <td>$${decimalFormat.format(transaction.amount)}</td>
        </tr>
        <tr>
            <td class ="label">Date: </td>
            <td width="20pt"></td>
            <td>${dateFormatter.format(transaction.date)}</td>
        </tr>
        <tr>
            <td class ="label">Reason: </td>
            <td width="20pt"></td>
            <td> ${transaction.reason}</td>
        </tr>
        <tr>
            <td class ="label"></td>
            <td width="20pt"></td>
            <td> ${accountUser.fullName} <img src = ${accountUser.signature}></td>
        </tr>
    <table>
<footer>
    <div>
        <div>${business.name}</div>
        <div>${business.address}</div>
    </div>
</footer>
</body>
</html>""".trim()
}
val htmlStringThree: (String, Customer, Business, User, FinancialTransaction) -> String = {styleString, contact, business, accountUser, income ->
"""<!DOCTYPE html>
<html>
  <head>
    <style>${styleString}</style>
    <title>
      Receipt ${income.documentID.take(4)} for
      ${contact.fullName()}
    </title>
  </head>
  <body>
    <div class="flex flex-col justify-between p-12 bg-white text-black  min-w-[800px] max-w-[850px] aspect-[8.5/11] text-lg">
        <div class="flex flex-col text-lg">
          <div class = "flex flex-col gap-6 ">
            <div class = "flex flex-row gap-8">
              <div class = "flex flex-col flex-1 gap-4 pt-2">
                <div class = "flex flex-row align-end">
                  <div class = "text-black font-[Helvetica-Bold] text-3xl uppercase leading-5">
                    ${income.transactionName()}
                  </div>
                </div>
                <div class = "text-neutral-500 text-lg">
                  <div class = "flex flex-row">
                    <div>${income.transactionName()} No: </div>
                    <div>${income.documentID}</div>
                  </div>
                  <div class = "flex flex-row text-lg">
                    <div>${income.transactionName()} Date: </div>
                    <div> ${dateFormatter.format(income.date)}</div>
                  </div>
                </div>
              </div>
              <div class = "flex-1">
                <img src=${business.logo} class = "w-24" />
              </div>
            </div>
            <div class = "flex flex-row gap-8 leading-5 mt-8">
              <div class = "flex-1 text-neutral-500 text-lg leading-5">
                <div class = "font-[Helvetica-Bold] text-black">
                  Received By:
                </div>
                <div>${business.name}</div>
                <div>${business.address}</div>
                <div>${business.phoneNumber.cellNumber}</div>
                <div>${business.emailAddress}</div>
              </div>
              <div class = "flex-1 text-neutral-500 text-lg leading-5">
                <div class = "font-[Helvetica-Bold] text-black">
                  Received From:
                </div>
                <div>${contact.firstName} ${contact.lastName}</div>
                <div>${contact.address}</div>
                <div>${contact.phoneNumber.cellNumber}</div>
                <div>${contact.emailAddress}</div>
              </div>
            </div>
            <div class = "flex flex-col border border-neutral-100 text-lg mt-8">
              <div class = "flex flex-row bg-blue-50 font-[Helvetica-Bold] p-2">
                <div class = "w-[20%] text-neutral-600">Date</div>
                <div class = "w-[20%] text-center ">
                  Amount
                </div>
                <div class = "w-[60%] text-center text-neutral-600">Reason</div>
              </div>
              <div class = "flex flex-row p-2 gap-4">
                <div class = "w-[20%] text-neutral-600">${dateFormatter.format(income.date)}</div>
                <div class = "w-[20%] text-center font-[Helvetica-Bold] text-lg">
                  $${decimalFormat.format(income.amount)}
                </div>
                <div class = "w-[60%] text-neutral-600 text-center">${income.reason}</div>
              </div>
            </div>
          <div>
            <div class = "text-4xl text-right mr-8" style ="font-family: 'Brush Script MT', cursive; font-size:2rem; color:darkblue;">
             ${accountUser.fullName}
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

""".trim()}
const val receipt = "https://play.tailwindcss.com/5kL1jUxsOG"
const val updatedDec27 = "https://play.tailwindcss.com/xTyGo2wxQ5"