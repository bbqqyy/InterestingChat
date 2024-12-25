use mallchat;
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
                                        `status` tinyint(8) NOT NULL COMMENT '状态 1待执行 2已失败',
                                        `next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
                                        `retry_times` int(11) NOT NULL COMMENT '已经重试的次数',
                                        `max_retry_times` int(11) NOT NULL COMMENT '最大重试次数',
                                        `fail_reason` text COMMENT '执行失败的堆栈',
                                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                        `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `idx_next_retry_time` (`next_retry_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';
DROP TABLE IF EXISTS `user_emoji`;
CREATE TABLE `user_emoji` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `uid` bigint(20) NOT NULL COMMENT '用户表ID',
                              `expression_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情地址',
                              `delete_status` int(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0-正常,1-删除)',
                              `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                              `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `IDX_USER_EMOJIS_UID` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表情包';
CREATE TABLE `sensitive_word` (
    `word` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `message_mark`;
CREATE TABLE `message_mark`  (
                                 `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `msg_id` bigint(20) NOT NULL COMMENT '消息表id',
                                 `uid` bigint(20) NOT NULL COMMENT '标记人uid',
                                 `type` int(11) NOT NULL COMMENT '标记类型 1点赞 2举报',
                                 `status` int(11) NOT NULL COMMENT '消息状态 0正常 1取消',
                                 `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                 `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_msg_id`(`msg_id`) USING BTREE,
                                 INDEX `idx_uid`(`uid`) USING BTREE,
                                 INDEX `idx_create_time`(`create_time`) USING BTREE,
                                 INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = Dynamic;
