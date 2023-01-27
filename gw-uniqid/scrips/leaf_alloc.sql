DROP TABLE IF EXISTS `leaf_alloc`;

CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '' COMMENT '区分业务',
  `max_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '该biz_tag目前所被分配的ID号段的最大值',
  `step` int(11) NOT NULL COMMENT '每次分配的号段长度',
  `description` varchar(256)  DEFAULT NULL COMMENT '文字描述',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;