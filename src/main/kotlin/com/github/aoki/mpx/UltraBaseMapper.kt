package com.github.aoki.mpx

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Param
import java.io.Serializable

/**
 *  @author JDb
 *  @date  2020/7/23
 */
interface UltraBaseMapper<T> : BaseMapper<T> {

    /**
     * 插入一条数据，如果插入报错(比如唯一约束冲突) 则忽略
     */
    fun insertIgnore(entity: T): Int

    /**
     * 根据主键查询一条数据用户更新(行锁)
     */
    fun selectByIdForUpdate(id: Serializable): T?

    /**
     * 批量插入
     */
    fun insertBatch(@Param("list") entity: Collection<T>): Int

    /**
     * 批量插入数据，如果数据库此数据(唯一约束冲突) 则忽略
     */
    fun insertIgnoreBatch(@Param("list") entity: Collection<T>): Int

}