package com.bqy.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.*;

/**
 * <p>
 * 用户表情包
 * </p>
 *
 * @author ${author}
 * @since 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_emoji")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmoji implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户表ID
     */
    @TableField("uid")
    private Long uid;

    /**
     * 表情地址
     */
    @TableField("expression_url")
    private String expressionUrl;

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}
