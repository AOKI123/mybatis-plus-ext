package com.github.aoki.mpx

import com.baomidou.mybatisplus.core.injector.AbstractMethod
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector

/**
 *  @author JDb
 *  @date  2020/7/23
 */
class UltraSqlInjector : DefaultSqlInjector() {

    override fun getMethodList(mapperClass: Class<*>?): MutableList<AbstractMethod> {
        val methodList = super.getMethodList(mapperClass)
        methodList.add(InsertIgnore())
        methodList.add(SelectByIdForUpdate())
        methodList.add(InsertBatch())
        methodList.add(InsertIgnoreBatch())
        return methodList
    }
}