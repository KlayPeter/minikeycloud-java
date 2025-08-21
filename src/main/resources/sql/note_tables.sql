-- 云笔记模块数据库表结构

-- 笔记信息表
DROP TABLE IF EXISTS `note_info`;
CREATE TABLE `note_info` (
  `note_id` varchar(10) NOT NULL COMMENT '笔记ID',
  `user_id` varchar(10) NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '笔记标题',
  `content` longtext COMMENT '笔记内容',
  `content_type` tinyint(1) DEFAULT 1 COMMENT '内容类型 1:markdown 2:富文本',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0:删除 1:正常',
  `is_public` tinyint(1) DEFAULT 0 COMMENT '是否公开 0:私有 1:公开',
  `view_count` int(11) DEFAULT 0 COMMENT '查看次数',
  PRIMARY KEY (`note_id`,`user_id`),
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_is_public` (`is_public`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记信息表';

-- 笔记分享表
DROP TABLE IF EXISTS `note_share`;
CREATE TABLE `note_share` (
  `share_id` varchar(20) NOT NULL COMMENT '分享ID',
  `note_id` varchar(10) NOT NULL COMMENT '笔记ID',
  `user_id` varchar(10) NOT NULL COMMENT '用户ID',
  `share_title` varchar(200) DEFAULT NULL COMMENT '分享标题',
  `valid_type` tinyint(4) DEFAULT 3 COMMENT '有效期类型 0:1天 1:7天 2:30天 3:永久有效',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `share_time` datetime DEFAULT NULL COMMENT '分享时间',
  `code` varchar(5) DEFAULT NULL COMMENT '提取码(可为空)',
  `show_count` int(11) DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0:取消分享 1:正常分享',
  PRIMARY KEY (`share_id`),
  KEY `idx_note_id` (`note_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记分享信息表';

-- 笔记标签表
DROP TABLE IF EXISTS `note_tag`;
CREATE TABLE `note_tag` (
  `tag_id` varchar(10) NOT NULL COMMENT '标签ID',
  `user_id` varchar(10) NOT NULL COMMENT '用户ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(7) DEFAULT '#409EFF' COMMENT '标签颜色',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0:删除 1:正常',
  PRIMARY KEY (`tag_id`,`user_id`),
  KEY `idx_user_id` (`user_id`) USING BTREE,
  UNIQUE KEY `uk_user_tag` (`user_id`,`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记标签表';

-- 笔记标签关联表
DROP TABLE IF EXISTS `note_tag_relation`;
CREATE TABLE `note_tag_relation` (
  `note_id` varchar(10) NOT NULL COMMENT '笔记ID',
  `tag_id` varchar(10) NOT NULL COMMENT '标签ID',
  `user_id` varchar(10) NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`note_id`,`tag_id`,`user_id`),
  KEY `idx_note_id` (`note_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记标签关联表';