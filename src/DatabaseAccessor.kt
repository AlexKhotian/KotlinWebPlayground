package com.plktor

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users: Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50) // Column<String>
    val pet = varchar("pet", length = 256)
}

class DatabaseAccessor {
    init {
        Database.connect("jdbc:postgresql://localhost:5432/test", driver = "org.postgresql.Driver",
            user = "test_user")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users)
        }
    }

    fun insertUser(user: AddUserRequest) {
        transaction {
            Users.insert {
                it[name] = user.name
                it[pet] = user.pet
            }
        }
    }

    fun getUsersNames() : List<String> {
        var names = mutableListOf<String>()
        transaction {
            for (user in Users.selectAll()) {
                names.add(names.size, user[Users.name])
            }
        }
        return names
    }

    fun getUserPet(name: String): String? {
        var res: String? = null
        transaction {
            Users.select{Users.name.eq(name)}.forEach{
                if (it[Users.name] != null){
                    res = it[Users.pet]
                }
            }
        }
        return res
    }
}
