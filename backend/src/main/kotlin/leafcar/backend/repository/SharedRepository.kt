package org.example.leafcar.backend.repository

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.FloatColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateTimeColumnType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import  org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.time.LocalDate
import java.time.LocalDateTime

abstract class SharedRepository<T> (
    private val table: Table,
    private val mapper: (ResultRow) -> T // wat is dit?
) {
    fun findWithFilters(
        params: Map<String, String>): List<T> = transaction {
        val conditions = params.mapNotNull { (key, value) ->
            table.columns.find { it.name == key }?.let { column ->
                when (column.columnType) {
                    is VarCharColumnType, is TextColumnType ->
                        (column as Column<String>) like "%$value%"
                    is IntegerColumnType ->
                        value.toIntOrNull()?.let { (column as Column<Int>) eq it }
                    is LongColumnType ->
                        value.toLongOrNull()?.let { (column as Column<Long>) eq it }
                    is DecimalColumnType ->
                        value.toBigDecimalOrNull()?.let { (column as Column<java.math.BigDecimal>) eq it }
                    is DoubleColumnType ->
                        value.toDoubleOrNull()?.let { (column as Column<Double>) eq it }
                    is FloatColumnType ->
                        value.toFloatOrNull()?.let { (column as Column<Float>) eq it }
                    is BooleanColumnType ->
                        value.toBooleanStrictOrNull()?.let { (column as Column<Boolean>) eq it }
                    is JavaLocalDateColumnType ->
                        runCatching { (column as Column<LocalDate>) eq LocalDate.parse(value) }.getOrNull()
                    is JavaLocalDateTimeColumnType ->
                        runCatching { (column as Column<LocalDateTime>) eq LocalDateTime.parse(value) }.getOrNull()
                    else -> null
                }
            }
        }
        table
            .select { conditions.reduceOrNull { acc, expr -> acc and expr } ?: Op.TRUE }
            .map(mapper)
    }
}