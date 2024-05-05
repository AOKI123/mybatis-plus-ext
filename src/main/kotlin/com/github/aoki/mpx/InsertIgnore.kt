package com.github.aoki.mpx

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.core.injector.AbstractMethod
import com.baomidou.mybatisplus.core.metadata.TableInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator
import org.apache.ibatis.executor.keygen.KeyGenerator
import org.apache.ibatis.executor.keygen.NoKeyGenerator
import org.apache.ibatis.mapping.MappedStatement

/**
 *  @author AOKI123
 */
class InsertIgnore : AbstractMethod() {

    companion object {
        const val METHOD_NAME = "insertIgnore"
        const val SQL_TEMPLATE = """
            <script>
                INSERT IGNORE INTO %s %s VALUES %s
            </script>
        """
    }

    override fun injectMappedStatement(
        mapperClass: Class<*>?,
        modelClass: Class<*>?,
        tableInfo: TableInfo?
    ): MappedStatement {
        var keyGenerator: KeyGenerator = NoKeyGenerator()
        val columnScript = SqlScriptUtils.convertTrim(
            tableInfo!!.allInsertSqlColumnMaybeIf,
            StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET, null, StringPool.COMMA
        )
        val valuesScript = SqlScriptUtils.convertTrim(
            tableInfo.getAllInsertSqlPropertyMaybeIf(null),
            StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET, null, StringPool.COMMA
        )
        var keyProperty: String? = null
        var keyColumn: String? = null
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.keyProperty)) {
            if (tableInfo.idType == IdType.AUTO) {
                /** 自增主键  */
                keyGenerator = Jdbc3KeyGenerator()
                keyProperty = tableInfo.keyProperty
                keyColumn = tableInfo.keyColumn
            } else {
                if (null != tableInfo.keySequence) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(METHOD_NAME, tableInfo, builderAssistant)
                    keyProperty = tableInfo.keyProperty
                    keyColumn = tableInfo.keyColumn
                }
            }
        }
        val sql = String.format(SQL_TEMPLATE, tableInfo.tableName, columnScript, valuesScript)
        val sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass)
        return addInsertMappedStatement(
            mapperClass,
            modelClass,
            METHOD_NAME,
            sqlSource,
            keyGenerator,
            keyProperty,
            keyColumn
        )
    }
}

