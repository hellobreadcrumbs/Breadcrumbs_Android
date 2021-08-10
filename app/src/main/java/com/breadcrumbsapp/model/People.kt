package com.breadcrumbsapp.model

data class People(var name: String, private var lastName: String, var id: Int) {

    fun getlastName(): String {
        return lastName
    }

    fun setlastName(lastName: String) {
        this.lastName = lastName
    }
}
