package com.aaronseaton.accounts.util

data class PageSize (val width: Int, val height: Int){
    companion object {
        val Letter = PageSize(612, 793 )
        val A4 = PageSize(595, 842)
    }
}