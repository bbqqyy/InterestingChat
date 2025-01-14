package com.bqy.common.user.service.cache;

import com.bqy.common.user.dao.ItemConfigDao;
import com.bqy.common.user.domain.entity.ItemConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getVaildItemByType(itemType);
    }

    @Cacheable(cacheNames = "item",key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId){
        return itemConfigDao.getById(itemId);
    }

    @CacheEvict(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public void evictByType(Integer itemType) {

    }
}
