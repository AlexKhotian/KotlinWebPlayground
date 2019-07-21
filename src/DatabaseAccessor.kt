package com.plktor

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users: Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50) // Column<String>
}

class DatabaseAccessor {
    init {
        Database.connect("jdbc:postgresql://localhost:5432/test", driver = "org.postgresql.Driver",
            user = "test_user")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users)
        }
    }

    fun insertUser(user: String) {
        transaction {
            Users.insert {
                it[name] = user
            }
        }
    }

    fun getUsers() : List<String> {
        var names = mutableListOf<String>()
        transaction {
            for (user in Users.selectAll()) {
                names.add(names.size, user[Users.name])
                println("${user[Users.id]};${user[Users.name]}")
            }
        }
        return names
    }
}
