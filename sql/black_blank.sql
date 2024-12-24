use mallchat;
CREATE TABLE `black`  (
                          `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                          `type` int(11) NOT NULL COMMENT '拉黑目标类型 1.ip 2uid',
                          `target` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '拉黑目标',
                          `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                          `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE INDEX `idx_type_target`(`type`, `target`) USING BTREE
) COMMENT = '黑名单' ROW_FORMAT = Dynamic;
CREATE TABLE `role` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                        `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `idx_create_time` (`create_time`) USING BTREE,
                        KEY `idx_update_time` (`update_time`) USING BTREE
) COMMENT='角色表';
CREATE TABLE `user_role` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `uid` bigint(20) NOT NULL COMMENT 'uid',
                             `role_id` bigint(20) NOT NULL COMMENT '角色id',
                             `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                             `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_uid` (`uid`) USING BTREE,
                             KEY `idx_role_id` (`role_id`) USING BTREE,
                             KEY `idx_create_time` (`create_time`) USING BTREE,
                             KEY `idx_update_time` (`update_time`) USING BTREE
) COMMENT='用户角色关系表';
insert into role(id,`name`) values(1,'超级管理员');
insert into role(id,`name`) values(2,'抹茶群聊管理员');