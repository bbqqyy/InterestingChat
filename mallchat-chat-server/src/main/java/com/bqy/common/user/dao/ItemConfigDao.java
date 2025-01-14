package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.mapper.ItemConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-19
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig>{

    public List<ItemConfig> getVaildItemByType(Integer itemType) {
        return lambdaQuery()
                .eq(ItemConfig::getType,itemType)
                .list();
    }
}
