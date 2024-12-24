use mallchat;
CREATE TABLE `item_config`  (
                                `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
                                `type` int(11) NOT NULL COMMENT '物品类型 1改名卡 2徽章',
                                `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
                                `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品功能描述',
                                `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_create_time`(`create_time`) USING BTREE,
                                INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '功能物品配置表' ROW_FORMAT = Dynamic;
CREATE TABLE `user_backpack`  (
                                  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                                  `uid` bigint(20) NOT NULL COMMENT 'uid',
                                  `item_id` bigint(20) NOT NULL COMMENT '物品id',
                                  `status` int(11) NOT NULL COMMENT '使用状态 0.待使用 1已使用',
                                  `idempotent` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等号',
                                  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uniq_idempotent`(`idempotent`) USING BTREE,
                                  INDEX `idx_uid`(`uid`) USING BTREE,
                                  INDEX `idx_create_time`(`create_time`) USING BTREE,
                                  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = Dynamic;
INSERT INTO `item_config` VALUES (1, 1, NULL, '用户可以使用改名卡，更改自己的名字。mallchat名称全局唯一，快抢订你的专属昵称吧', '2023-03-25 22:27:30.511', '2023-03-25 22:27:30.511');
INSERT INTO `item_config` VALUES (2, 2, 'https://cdn-icons-png.flaticon.com/128/1533/1533913.png', '爆赞徽章，单条消息被点赞超过10次，即可获得', '2023-05-07 17:50:31.090', '2023-05-07 18:12:05.824');
INSERT INTO `item_config` VALUES (3, 2, 'https://cdn-icons-png.flaticon.com/512/6198/6198527.png ', '抹茶聊天前10名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.100', '2023-05-07 18:12:01.448');
INSERT INTO `item_config` VALUES (4, 2, 'https://cdn-icons-png.flaticon.com/512/10232/10232583.png', '抹茶聊天前100名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.109', '2023-05-07 17:56:36.059');
INSERT INTO `item_config` VALUES (5, 2, 'https://cdn-icons-png.flaticon.com/128/2909/2909937.png', '抹茶知识星球成员的专属徽章', '2023-05-07 17:50:31.109', '2023-05-07 17:56:36.059');
INSERT INTO `item_config` VALUES (6, 2, 'https://minio.mallchat.cn/mallchat/%E8%B4%A1%E7%8C%AE%E8%80%85.png', '抹茶项目contributor专属徽章', '2023-05-07 17:50:31.109', '2023-05-07 17:56:36.059');