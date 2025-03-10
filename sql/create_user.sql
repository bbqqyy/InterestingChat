
use mallchat;

CREATE TABLE `user` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
                        `name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
                        `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
                        `sex` int(11) DEFAULT NULL COMMENT '性别 1为男性，2为女性',
                        `open_id` char(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid用户标识',
                        `active_status` int(11) DEFAULT '2' COMMENT '在线状态 1在线 2离线',
                        `last_opt_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '最后上下线时间',
                        `ip_info` json DEFAULT NULL COMMENT 'ip信息',
                        `item_id` bigint(20) DEFAULT NULL COMMENT '佩戴的徽章id',
                        `status` int(11) DEFAULT '0' COMMENT '使用状态 0.正常 1拉黑',
                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                        `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uniq_open_id` (`open_id`) USING BTREE,
                        UNIQUE KEY `uniq_name` (`name`) USING BTREE,
                        KEY `idx_create_time` (`create_time`) USING BTREE,
                        KEY `idx_update_time` (`update_time`) USING BTREE,
                        KEY `idx_active_status_last_opt_time` (`active_status`,`last_opt_time`)
) ENGINE=InnoDB AUTO_INCREMENT=11000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';