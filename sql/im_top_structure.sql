use mallchat;
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `type` int(11) NOT NULL COMMENT '房间类型 1群聊 2单聊',
                        `hot_flag` int(11) DEFAULT '0' COMMENT '是否全员展示 0否 1是',
                        `active_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '群最后消息的更新时间（热点群不需要写扩散，只更新这里）',
                        `last_msg_id` bigint(20) DEFAULT NULL COMMENT '会话中的最后一条消息id',
                        `ext_json` json DEFAULT NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                        `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `idx_create_time` (`create_time`) USING BTREE,
                        KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';
DROP TABLE IF EXISTS `room_friend`;
CREATE TABLE `room_friend` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `room_id` bigint(20) NOT NULL COMMENT '房间id',
                               `uid1` bigint(20) NOT NULL COMMENT 'uid1（更小的uid）',
                               `uid2` bigint(20) NOT NULL COMMENT 'uid2（更大的uid）',
                               `room_key` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间key由两个uid拼接，先做排序uid1_uid2',
                               `status` int(11) NOT NULL COMMENT '房间状态 0正常 1禁用(删好友了禁用)',
                               `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                               `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `room_key` (`room_key`) USING BTREE,
                               KEY `idx_room_id` (`room_id`) USING BTREE,
                               KEY `idx_create_time` (`create_time`) USING BTREE,
                               KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单聊房间表';
DROP TABLE IF EXISTS `room_group`;
CREATE TABLE `room_group` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `room_id` bigint(20) NOT NULL COMMENT '房间id',
                              `name` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群名称',
                              `avatar` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群头像',
                              `ext_json` json DEFAULT NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
                              `delete_status` int(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0-正常,1-删除)',
                              `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                              `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `idx_room_id` (`room_id`) USING BTREE,
                              KEY `idx_create_time` (`create_time`) USING BTREE,
                              KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群聊房间表';
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `group_id` bigint(20) NOT NULL COMMENT '群主id',
                                `uid` bigint(20) NOT NULL COMMENT '成员uid',
                                `role` int(11) NOT NULL COMMENT '成员角色 1群主 2管理员 3普通成员',
                                `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_group_id_role` (`group_id`,`role`) USING BTREE,
                                KEY `idx_create_time` (`create_time`) USING BTREE,
                                KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群成员表';
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `uid` bigint(20) NOT NULL COMMENT 'uid',
                           `room_id` bigint(20) NOT NULL COMMENT '房间id',
                           `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
                           `active_time` datetime(3) DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
                           `last_msg_id` bigint(20) DEFAULT NULL COMMENT '会话最新消息id',
                           `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                           `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `uniq_uid_room_id` (`uid`,`room_id`) USING BTREE,
                           KEY `idx_room_id_read_time` (`room_id`,`read_time`) USING BTREE,
                           KEY `idx_create_time` (`create_time`) USING BTREE,
                           KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话列表';
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `room_id` bigint(20) NOT NULL COMMENT '会话表id',
                            `from_uid` bigint(20) NOT NULL COMMENT '消息发送者uid',
                            `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息内容',
                            `reply_msg_id` bigint(20) NULL DEFAULT NULL COMMENT '回复的消息内容',
                            `status` int(11) NOT NULL COMMENT '消息状态 0正常 1删除',
                            `gap_count` int(11) NULL DEFAULT NULL COMMENT '与回复的消息间隔多少条',
                            `type` int(11) NULL DEFAULT 1 COMMENT '消息类型 1正常文本 2.撤回消息',
                            `extra` json DEFAULT NULL COMMENT '扩展信息',
                            `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                            `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `idx_room_id`(`room_id`) USING BTREE,
                            INDEX `idx_from_uid`(`from_uid`) USING BTREE,
                            INDEX `idx_create_time`(`create_time`) USING BTREE,
                            INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;
INSERT INTO `user` (`id`, `name`, `avatar`, `sex`, `open_id`, `last_opt_time`, `ip_info`, `item_id`, `status`, `create_time`, `update_time`) VALUES (1, '系统消息', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '0', '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
insert INTO `room`(`id`,`type`,`hot_flag`) values (1,1,1);
insert INTO `room_group`(`id`,`room_id`,`name`,`avatar`) values (1,1,'抹茶全员群','https://mallchat.cn/assets/logo-e81cd252.jpeg');