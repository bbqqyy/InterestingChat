package com.bqy.common.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bqy.common.user.domain.entity.Black;
import com.bqy.common.user.mapper.BlackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-24
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IService<Black> {

}
