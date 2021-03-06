/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.common.db.types

import slatekit.common.Types
import slatekit.common.db.*
import slatekit.common.db.DbUtils.ensureField
import slatekit.common.newline
import java.rmi.UnexpectedException

/**
 * Builds up database tables, indexes and other database components
 */
open class DbSourceMySql : DbSource {

    /**
     * Mapping of normalized types ot postgres type names
     */
    val dataToColumnTypes = mapOf(
        DbFieldType.DbString to  "NVARCHAR",
        DbFieldType.DbBool to  "BIT",
        DbFieldType.DbShort to  "TINYINT",
        DbFieldType.DbNumber to  "INTEGER",
        DbFieldType.DbLong to  "BIGINT",
        DbFieldType.DbFloat to  "FLOAT",
        DbFieldType.DbDouble to  "DOUBLE",
        DbFieldType.DbDecimal to  "DECIMAL",
        DbFieldType.DbLocalDate to  "DATE",
        DbFieldType.DbLocalTime to  "TIME",
        DbFieldType.DbLocalDateTime to  "DATETIME",
        DbFieldType.DbZonedDateTime to  "DATETIME",
        DbFieldType.DbInstant to  "INSTANT",
        DbFieldType.DbDateTime to  "DATETIME"
    )


    val langToDataTypes = mapOf(
        Types.JBoolClass to  DbFieldType.DbBool,
        Types.JStringClass to  DbFieldType.DbString,
        Types.JShortClass to  DbFieldType.DbShort,
        Types.JIntClass to  DbFieldType.DbNumber,
        Types.JLongClass to  DbFieldType.DbLong,
        Types.JFloatClass to  DbFieldType.DbFloat,
        Types.JDoubleClass to  DbFieldType.DbDouble,
        Types.JDecimalClass to  DbFieldType.DbDecimal,
        Types.JLocalDateClass to  DbFieldType.DbLocalDate,
        Types.JLocalTimeClass to  DbFieldType.DbLocalTime,
        Types.JLocalDateTimeClass to  DbFieldType.DbLocalDateTime,
        Types.JZonedDateTimeClass to  DbFieldType.DbZonedDateTime,
        Types.JInstantClass to  DbFieldType.DbInstant,
        Types.JDateTimeClass to  DbFieldType.DbDateTime
    )

    /**
     * Builds the drop table DDL for the name supplied.
     */
    override fun buildDropTable(name: String): String = build(name,"DROP TABLE IF EXISTS")

    /**
     * Builds a delete statement to delete all rows
     */
    override fun buildDeleteAll(name: String): String = build(name,"DELETE FROM")

    /**
     * Builds an add column DDL sql statement
     */
    override fun buildAddCol(name: String, dataType: DbFieldType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = buildColType(dataType, maxLen)
        val colName = buildColName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }

    /**
     * Builds a valid column name
     */
    override fun buildColName(name: String): String = "`" + ensureField(name) + "`"

    /**
     * Builds a valid column type
     */
    override fun buildColType(colType: DbFieldType, maxLen: Int): String {
        return if (colType == DbFieldType.DbString && maxLen == -1)
            "longtext"
        else if (colType == DbFieldType.DbString)
            "VARCHAR($maxLen)"
        else
            getColTypeName(colType)
    }


    private fun build(name:String, prefix:String): String {
        val tableName = ensureField(name)
        val sql = "$prefix `$tableName`;"
        return sql
    }

    private fun getColTypeName(sqlType: DbFieldType): String {
        return if(dataToColumnTypes.containsKey(sqlType))
            dataToColumnTypes[sqlType] ?: ""
        else
            throw UnexpectedException("Unexpected db type : $sqlType")
    }
}
