package com.github.aoki.mpx

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.core.injector.AbstractMethod
import com.baomidou.mybatisplus.core.metadata.TableInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator
import org.apache.ibatis.executor.keygen.KeyGenerator
import org.apache.ibatis.executor.keygen.NoKeyGenerator
import org.apache.ibatis.mapping.MappedStatement

/**
 *  @author AOKI123
 */
class InsertBatch : AbstractMethod() {

    override fun injectMappedStatement(
        mapperClass: Class<*>?,
        modelClass: Class<*>?,
        tableInfo: TableInfo?
    ): MappedStatement {
        var keyGenerator: KeyGenerator = NoKeyGenerator()
        var keyProperty: String? = null
        var keyColumn: String? = null
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo != null) {
            if (StringUtils.isNotBlank(tableInfo.keyProperty)) {
                if (tableInfo.idType == IdType.AUTO) {
                    /** 自增主键  */
                    keyGenerator = Jdbc3KeyGenerator()
                    keyProperty = tableInfo.keyProperty
                    keyColumn = tableInfo.keyColumn
                } else {
                    if (null != tableInfo.keySequence) {
                        keyGenerator = TableInfoHelper.genKeyGenerator("insertBatch", tableInfo, builderAssistant)
                        keyProperty = tableInfo.keyProperty
                        keyColumn = tableInfo.keyColumn
                    }
                }
            }
        }
        val sql = "<script>insert into %s %s values %s</script>";
        val fieldSql = prepareFieldSql(tableInfo!!)
        val valueSql = prepareValuesSqlForMysqlBatch(tableInfo)
        val sqlResult = String.format(sql, tableInfo.tableName, fieldSql, valueSql)
        val sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass)

        return addInsertMappedStatement(
            mapperClass,
            modelClass,
            "insertBatch",
            sqlSource,
            keyGenerator,
            keyProperty,
            keyColumn
        )
    }

    private fun prepareFieldSql(tableInfo: TableInfo): String {
        val fieldSql = StringBuilder()
        fieldSql.append(tableInfo.keyColumn).append(",")
        tableInfo.fieldList.forEach { fieldSql.append(it.column).append(",") }
        fieldSql.delete(fieldSql.length - 1, fieldSql.length)
        fieldSql.insert(0, "(")
        fieldSql.append(")")
        return fieldSql.toString()
    }

    private fun prepareValuesSqlForMysqlBatch(tableInfo: TableInfo): String {
        val valueSql = StringBuilder()
        valueSql.append("""<foreach collection="list" item="item" index="index" open="(" separator="),(" close=")">""")
        valueSql.append("#{item.").append(tableInfo.keyProperty).append("},")
        tableInfo.fieldList.forEach {
            valueSql.append("#{item.").append(it.property).append("},")
        }
        valueSql.delete(valueSql.length - 1, valueSql.length)
        valueSql.append("</foreach>")
        return valueSql.toString()
    }
}


