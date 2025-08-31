package org.example

import java.sql.ResultSet

fun fetchData(query: String): List<Map<String, Any>> {
    val results = mutableListOf<Map<String, Any>>()
    DatabaseConnection.getConnection().use { conn ->
        conn.createStatement().use { stmt ->
            val rs: ResultSet = stmt.executeQuery(query)
            val meta = rs.metaData
            while (rs.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..meta.columnCount) {
                    row[meta.getColumnName(i)] = rs.getObject(i)
                }
                results.add(row)
            }
        }
    }
    return results
}

fun resultSetToList(rs: ResultSet): List<Map<String, Any>> {
    val md = rs.metaData
    val columns = md.columnCount
    val list = mutableListOf<Map<String, Any>>()

    while (rs.next()) {
        val row = mutableMapOf<String, Any>()
        for (i in 1..columns) {
            row[md.getColumnName(i)] = rs.getObject(i)
        }
        list.add(row)
    }
    return list
}
