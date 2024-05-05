package com.github.aoki.mpx

import com.baomidou.mybatisplus.core.injector.AbstractMethod
import com.baomidou.mybatisplus.core.metadata.TableInfo
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.scripting.defaults.RawSqlSource

/**
 *  @author AOKI123
 */
class SelectByIdForUpdate : AbstractMethod() {

    companion object {
        private const val SQL = "SELECT %s FROM %s WHERE %s = #{%s} %s FOR UPDATE"
        private const val METHOD_NAME = "selectByIdForUpdate"
    }

    override fun injectMappedStatement(
        mapperClass: Class<*>?,
        modelClass: Class<*>?,
        tableInfo: TableInfo
    ): MappedStatement {
        val sqlSource = RawSqlSource(
            configuration, String.format(
                SQL,
                sqlSelectColumns(tableInfo, false),
                tableInfo.tableName, tableInfo.keyColumn, tableInfo.keyProperty,
                tableInfo.getLogicDeleteSql(true, true)
            ), Any::class.java
        )
        return addSelectMappedStatementForTable(mapperClass, METHOD_NAME, sqlSource, tableInfo)
    }
}
