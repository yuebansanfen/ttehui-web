SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `act_r_activity_goods`;
CREATE TABLE `act_r_activity_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL COMMENT '活动id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `goods_img` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `tagline` varchar(255) DEFAULT NULL COMMENT '宣传语',
  `sell_price` varchar(255) NOT NULL COMMENT '商品售价(可能是区间)',
  `old_price` varchar(255) DEFAULT NULL COMMENT '商品原价(可能是区间)',
  `sell_low_price` int(11) DEFAULT NULL COMMENT '售价最低价',
  `old_low_price` int(11) DEFAULT NULL COMMENT '原价最低价',
  `activity_type` varchar(255) NOT NULL COMMENT '活动类型',
  `groupon_num` int(11) DEFAULT NULL COMMENT '拼团活动可参与人数',
  `actual_url` varchar(255) DEFAULT NULL COMMENT '真实跳转链接url',
  `short_url` varchar(255) DEFAULT NULL COMMENT '对外短链接url',
  `begin_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `hold_standard` text NOT NULL COMMENT '拥有的库存规格json串',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COMMENT='活动商品票';

DROP TABLE IF EXISTS  `act_t_activity`;
CREATE TABLE `act_t_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `is_show` tinyint(4) DEFAULT NULL COMMENT '是否展示',
  `intro` varchar(256) DEFAULT NULL COMMENT '简介',
  `sorting` int(11) DEFAULT NULL COMMENT '排序',
  `type` varchar(255) DEFAULT NULL COMMENT '所属类别(seckill秒杀，groupon团购)',
  `show_local` varchar(255) DEFAULT NULL COMMENT '展示位置(mall:商城 abc:农行)',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='活动表';

DROP TABLE IF EXISTS  `act_t_groupon`;
CREATE TABLE `act_t_groupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `act_goods_id` bigint(20) NOT NULL COMMENT '活动商品id',
  `open_user_id` bigint(20) DEFAULT NULL COMMENT '开团用户id',
  `open_profile` varchar(255) DEFAULT NULL COMMENT '开团用户头像',
  `open_time` datetime DEFAULT NULL COMMENT '建团时间',
  `close_time` datetime DEFAULT NULL COMMENT '关团时间',
  `begin_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `take_num` int(11) DEFAULT NULL COMMENT '实际参团人数',
  `groupon_num` int(11) DEFAULT NULL COMMENT '参团的总人数',
  `is_pay` tinyint(1) DEFAULT '0' COMMENT '是否付款',
  `group_status` varchar(10) DEFAULT '0' COMMENT '是否成团(ungroup:未成团 grouped:已成团 grouping:成团中)',
  `group_time` datetime DEFAULT NULL COMMENT '成团时间',
  `is_deal` tinyint(1) DEFAULT '0' COMMENT '是否处理',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `can_take` (`act_goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `act_t_groupon_detail`;
CREATE TABLE `act_t_groupon_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `groupon_id` bigint(20) DEFAULT NULL COMMENT '参团id',
  `act_goods_id` bigint(20) DEFAULT NULL COMMENT '活动商品id',
  `take_time` datetime DEFAULT NULL COMMENT '参团时间',
  `take_user_id` bigint(20) DEFAULT NULL COMMENT '参团用户id',
  `take_profile` varchar(255) DEFAULT NULL COMMENT '参团用户头像',
  `is_head` tinyint(1) DEFAULT '0' COMMENT '是否团长 1：是 0：不是',
  `is_pay` tinyint(1) DEFAULT '0' COMMENT '是否付款',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='已开团队详情表';

DROP TABLE IF EXISTS  `bak_t_mall_home`;
CREATE TABLE `bak_t_mall_home` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_chain` char(1) DEFAULT '0' COMMENT '是否外链（0:否 1:是）',
  `goods_type` varchar(255) DEFAULT NULL COMMENT '类型（seckill:秒杀  groupon:团购 common:普通）',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `activity_id` bigint(20) DEFAULT NULL COMMENT '活动id',
  `act_goods_id` bigint(20) DEFAULT NULL COMMENT '活动商品id',
  `show_local` varchar(255) DEFAULT NULL COMMENT '展示位置（act:活动精选 special:爆品特卖）',
  `show_name` varchar(255) DEFAULT NULL COMMENT '展示名称',
  `show_img` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `show_price` varchar(255) DEFAULT NULL COMMENT '展示价格',
  `old_price` varchar(255) DEFAULT NULL COMMENT '商品原价',
  `show_des` varchar(255) DEFAULT NULL COMMENT '描述',
  `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `sorting` int(11) DEFAULT '0' COMMENT '排序',
  `tag_img` varchar(255) DEFAULT NULL COMMENT '活动精选展示图标(秒杀、团购、0元购)',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `buy_t_logistics`;
CREATE TABLE `buy_t_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '编码',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `buy_t_order`;
CREATE TABLE `buy_t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺id',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号(唯一)',
  `is_invoice` tinyint(4) DEFAULT '0' COMMENT '是否开发票(1.是 0.否)',
  `total_price` bigint(20) NOT NULL COMMENT '商品总价',
  `trans_fee` bigint(20) DEFAULT '0' COMMENT '配送费',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `order_status` varchar(20) NOT NULL COMMENT '订单状态（wait_pay:等待买家付款  wait_send:等待卖家发货 wait_accept:等待确认收货  deal_suc:交易成功  deal_close:交易关闭）',
  `refund_status` varchar(20) DEFAULT '' COMMENT '退货状态(unrefund:无需退款，refunding:退款中，refunded:已退款)',
  `deal_status` varchar(10) DEFAULT NULL COMMENT '处理状态',
  `remark` varchar(511) DEFAULT NULL COMMENT '用户备注',
  `address_id` bigint(20) NOT NULL COMMENT '收货地址id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `recipient` varchar(255) DEFAULT NULL COMMENT '收件人',
  `telephone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `coupon_sn` text COMMENT '使用的优惠码',
  `coupon_money` bigint(20) DEFAULT '0' COMMENT '使用优惠券总金额',
  `order_type` varchar(20) DEFAULT NULL COMMENT '订单类型(seckill:秒杀  groupon:团购 other:其他)',
  `type_id` bigint(20) DEFAULT NULL COMMENT '不同类型的id',
  `payment_num` varchar(255) NOT NULL COMMENT '支付编码',
  `source_type` varchar(255) DEFAULT NULL COMMENT '来源类型（tehui:商城 abc:农行客户端）',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_num` (`order_num`) USING BTREE,
  KEY `idx_num_cumid` (`order_num`,`customer_id`,`is_deleted`) USING BTREE,
  KEY `idx_status` (`order_status`,`is_deleted`) USING BTREE,
  KEY `idx_pnum_cum` (`payment_num`,`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1039 DEFAULT CHARSET=utf8 COMMENT='订单表';

DROP TABLE IF EXISTS  `buy_t_order_bill`;
CREATE TABLE `buy_t_order_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `order_num` varchar(255) DEFAULT NULL COMMENT '订单编码（唯一）',
  `bill_header` varchar(255) DEFAULT NULL COMMENT '发票抬头',
  `bill_remark` varchar(255) DEFAULT NULL COMMENT '发票备注',
  `bill_type` varchar(10) DEFAULT NULL COMMENT '发票类型（personal:个人  compay:公司）',
  `bill_status` varchar(10) DEFAULT NULL COMMENT '发票状态（have_open：已开发票  no_open：未开发票）',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_num` (`order_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='订单发票表';

DROP TABLE IF EXISTS  `buy_t_order_detail`;
CREATE TABLE `buy_t_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_detail_num` varchar(255) DEFAULT NULL COMMENT '订单详情编号',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `goods_img` varchar(255) DEFAULT NULL COMMENT '商品缩略图',
  `goods_standard` varchar(255) NOT NULL COMMENT '商品规格',
  `goods_standard_des` varchar(255) NOT NULL COMMENT '商品规格描述',
  `goods_act_goods_id` bigint(20) DEFAULT NULL COMMENT '活动商品关联id',
  `goods_category` bigint(20) DEFAULT NULL COMMENT '商品分类',
  `goods_price` bigint(20) DEFAULT NULL COMMENT '商品原价',
  `goods_real_price` bigint(20) NOT NULL COMMENT '商品实际价格',
  `goods_amount` int(11) NOT NULL COMMENT '商品购买数量',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商id',
  `supplier_title` varchar(255) DEFAULT NULL COMMENT '供应商名称',
  `apply_time` datetime DEFAULT NULL COMMENT '申请退款时间',
  `refund_status` varchar(10) DEFAULT NULL COMMENT '退货状态(退货中，已退货)',
  `refund_time` datetime DEFAULT NULL COMMENT '退货时间',
  `refund_money` bigint(20) DEFAULT NULL COMMENT '退货金额',
  `refund_reason` varchar(10) DEFAULT NULL COMMENT '退款理由',
  `refund_des` varchar(255) DEFAULT NULL COMMENT '退款说明',
  `coupon_sn` text COMMENT '使用的优惠码（多个用英文逗号隔开）',
  `coupon_money` bigint(20) DEFAULT '0' COMMENT '使用优惠券总金额',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_ordernum` (`is_deleted`,`order_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1076 DEFAULT CHARSET=utf8 COMMENT='订单详情表';

DROP TABLE IF EXISTS  `buy_t_order_logistics`;
CREATE TABLE `buy_t_order_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `company` varchar(255) DEFAULT NULL COMMENT '物流公司',
  `exp_num` varchar(255) DEFAULT NULL COMMENT '物流单号',
  `is_sms` varchar(10) DEFAULT NULL COMMENT '短信提醒',
  `status` varchar(10) DEFAULT NULL COMMENT '物流状态',
  `logistics_code` varchar(255) NOT NULL COMMENT '物流公司code',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8 COMMENT='物流表';

DROP TABLE IF EXISTS  `buy_t_order_pay`;
CREATE TABLE `buy_t_order_pay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `payment_num` varchar(255) NOT NULL COMMENT '支付编号',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号(唯一)',
  `order_amount` bigint(20) DEFAULT NULL COMMENT '订单金额',
  `pay_date` datetime DEFAULT NULL COMMENT '付款时间',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式（kcode：K码支付   wxpay：微信支付  alipay：支付宝支付）',
  `pay_status` varchar(20) DEFAULT NULL COMMENT '支付状态(wait:待支付 success:支付成功 fail:支付失败)',
  `batch_no` varchar(255) DEFAULT NULL COMMENT '交易批次号',
  `batch_status` varchar(10) DEFAULT NULL COMMENT '交易状态',
  `amount` bigint(20) DEFAULT NULL COMMENT '交易金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '商户备注',
  `notify_type` varchar(4) DEFAULT NULL COMMENT '通知类型(0:页面通知   1:服务器通知)',
  `irsp_ref` varchar(255) DEFAULT NULL COMMENT '支付流水号',
  `voucher_no` varchar(255) DEFAULT NULL COMMENT '交易凭证号',
  `host_date` varchar(10) DEFAULT NULL COMMENT '银行交易日期',
  `host_time` varchar(8) DEFAULT NULL COMMENT '银行交易时间',
  `mch_id` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_nums` (`payment_num`,`order_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1054 DEFAULT CHARSET=utf8 COMMENT='订单支付表';

DROP TABLE IF EXISTS  `buy_t_order_refund`;
CREATE TABLE `buy_t_order_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `refund_num` varchar(255) NOT NULL COMMENT '退款单号',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `order_detail_num` varchar(255) DEFAULT NULL,
  `order_detail_id` bigint(20) NOT NULL COMMENT '订单详情id',
  `refund_status` varchar(20) DEFAULT NULL COMMENT '退款状态（success:退款成功 fail:退款失败）',
  `irsp_ref` varchar(50) DEFAULT NULL COMMENT '交易流水号',
  `batch_status` varchar(10) DEFAULT NULL COMMENT '交易状态',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '交易批次号',
  `refund_fee` int(11) DEFAULT NULL COMMENT '退款金额',
  `total_fee` int(11) DEFAULT NULL COMMENT '订单金额',
  `voucher_no` varchar(50) DEFAULT NULL COMMENT '交易凭证号',
  `host_date` varchar(10) DEFAULT NULL COMMENT '银行交易日期',
  `host_time` varchar(8) DEFAULT NULL COMMENT '银行交易时间',
  `mch_id` varchar(255) DEFAULT NULL COMMENT '商户号',
  `error_code` varchar(255) DEFAULT NULL COMMENT '错误码',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='退款表';

DROP TABLE IF EXISTS  `buy_t_pay_config`;
CREATE TABLE `buy_t_pay_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `partner` varchar(45) DEFAULT '' COMMENT '商家商户号（mch_id）',
  `partner_name` varchar(255) DEFAULT '' COMMENT '商户名称',
  `seller_id` varchar(45) DEFAULT '' COMMENT '收款账号',
  `private_key` varchar(255) DEFAULT '' COMMENT '私钥路径(支付宝)',
  `public_key` varchar(255) DEFAULT '' COMMENT '公钥路径(支付宝)',
  `sign_type` varchar(45) DEFAULT '' COMMENT '签名方式(支付宝默认RSA)',
  `charset` varchar(45) DEFAULT 'utf-8' COMMENT '编码方式，默认utf-8',
  `cacert_path` varchar(255) DEFAULT '' COMMENT 'ca证书路径',
  `transport` varchar(45) DEFAULT 'http' COMMENT '访问方式(http)',
  `pay_type` varchar(45) DEFAULT '' COMMENT '支付类型(alipay,wxpay,kcode)',
  `notify_type` varchar(45) DEFAULT '' COMMENT '通知方式',
  `appid` varchar(255) DEFAULT '' COMMENT 'appid',
  `appsecret` varchar(45) DEFAULT '' COMMENT 'appid对应的接口密钥',
  `open` tinyint(1) DEFAULT '0' COMMENT '是否开启(0,不开启)',
  `sign_key` varchar(255) DEFAULT '' COMMENT '签名密钥(微信)',
  `ext_key` text COMMENT '扩展配置项(json存储)',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00',
  `is_deleted` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_pay_list` (`open`,`is_deleted`),
  KEY `idx_t` (`pay_type`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `buy_t_prize_order`;
CREATE TABLE `buy_t_prize_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `prize_img` varchar(255) DEFAULT NULL COMMENT '奖品图片',
  `prize_name` varchar(255) NOT NULL COMMENT '奖品名称',
  `start_time` date DEFAULT NULL COMMENT '开始时间',
  `end_time` date DEFAULT NULL COMMENT '截止时间',
  `send_status` varchar(255) NOT NULL COMMENT '发货状态(wait_send:未发货   have_send:已发货)',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `address_id` bigint(20) DEFAULT NULL COMMENT '收货地址id',
  `recipient` varchar(255) DEFAULT NULL COMMENT '收件人',
  `telephone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `logistics_code` varchar(255) DEFAULT NULL COMMENT '物流公司code',
  `company` varchar(255) DEFAULT NULL COMMENT '物流公司',
  `logistics_num` varchar(255) DEFAULT NULL COMMENT '物流单号',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='实物礼品表';

DROP TABLE IF EXISTS  `buy_t_shopping_cart`;
CREATE TABLE `buy_t_shopping_cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺id',
  `shop_name` varchar(255) DEFAULT NULL COMMENT '店铺名称',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `show_logo` varchar(255) DEFAULT NULL COMMENT '商品logo地址',
  `old_price` bigint(20) DEFAULT NULL COMMENT '商品原价',
  `sell_price` bigint(20) NOT NULL COMMENT '商品售价',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `goods_sku` varchar(255) NOT NULL COMMENT '商品规格(指定格式)',
  `goods_sku_des` varchar(255) DEFAULT NULL COMMENT '商品规格描述',
  `act_goods_id` bigint(20) DEFAULT '0',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_cid_goods` (`customer_id`,`goods_id`,`goods_sku`,`act_goods_id`,`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=743 DEFAULT CHARSET=utf8 COMMENT='购物车表';

DROP TABLE IF EXISTS  `fee_t_carry_mode`;
CREATE TABLE `fee_t_carry_mode` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `freight_id` bigint(20) DEFAULT NULL COMMENT '运费模板id',
  `send_way` varchar(255) DEFAULT NULL COMMENT '运送方式(express.快递 ems.EMS mail.平邮)',
  `arrive_area` varchar(255) DEFAULT NULL COMMENT '运送地区（省）',
  `first_num` varchar(255) DEFAULT NULL COMMENT '首单位数量（依据运费模板表计价方式来选择）',
  `first_free` bigint(20) DEFAULT NULL COMMENT '首费用(单位分)',
  `next_num` varchar(255) DEFAULT NULL COMMENT '续单位数量（依据运费模板表计价方式来选择）',
  `next_free` bigint(20) DEFAULT NULL COMMENT '续费用(单位分)',
  `is_default` tinyint(4) DEFAULT NULL COMMENT '是否默认',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='运送方式';

DROP TABLE IF EXISTS  `fee_t_freight_mould`;
CREATE TABLE `fee_t_freight_mould` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `name` varchar(255) DEFAULT NULL COMMENT '模板名称',
  `send_addr` varchar(512) DEFAULT NULL COMMENT '发货地址',
  `send_date` varchar(20) DEFAULT NULL COMMENT '发货时间（1.付款后12小时内；2.付款后24小时内...）',
  `is_free` varchar(20) DEFAULT NULL COMMENT '是否包邮（free.卖家承担运费 nofree.自定义运费）',
  `calc_way` varchar(20) DEFAULT NULL COMMENT '计价方式（num.按件数 weight.按重量 ）',
  `post_require` tinyint(4) DEFAULT NULL COMMENT '是否指定包邮条件(1.指定，0.不指定)',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='运费模板表';

DROP TABLE IF EXISTS  `fee_t_postage_condition`;
CREATE TABLE `fee_t_postage_condition` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `freight_id` bigint(20) DEFAULT NULL COMMENT '运费模板id',
  `post_area` varchar(255) DEFAULT NULL COMMENT '包邮地区',
  `post_way` tinyint(4) DEFAULT NULL COMMENT '包邮方式（money.金额 num.件数 weight.重量）',
  `need_con` tinyint(4) DEFAULT NULL COMMENT '条件（gt:大于 lt:小于）',
  `con_value` varchar(255) DEFAULT NULL COMMENT '条件值',
  `send_way` varchar(20) DEFAULT NULL COMMENT '运送方式(express.快递 ems.EMS mail.平邮)',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='包邮条件表';

DROP TABLE IF EXISTS  `gd_r_goods_channel`;
CREATE TABLE `gd_r_goods_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `channel_id` bigint(20) DEFAULT NULL COMMENT '频道id',
  `channel_sorting` int(11) DEFAULT NULL COMMENT '频道排序',
  `is_top` tinyint(4) DEFAULT NULL COMMENT '是否置顶',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品频道表';

DROP TABLE IF EXISTS  `gd_t_attribute`;
CREATE TABLE `gd_t_attribute` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '属性名称',
  `code` varchar(255) DEFAULT NULL COMMENT '属性编码',
  `is_img` tinyint(4) DEFAULT NULL COMMENT '是否是图片',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='商品属性表';

DROP TABLE IF EXISTS  `gd_t_category`;
CREATE TABLE `gd_t_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `banner` varchar(255) DEFAULT NULL COMMENT '分类banner',
  `sorted` int(11) DEFAULT NULL COMMENT '排序',
  `is_show` tinyint(4) DEFAULT NULL COMMENT '是否展示（1 展示， 0 不展示）',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='商品库存表';

DROP TABLE IF EXISTS  `gd_t_category_goods`;
CREATE TABLE `gd_t_category_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL COMMENT '分类id',
  `is_chain` char(1) NOT NULL DEFAULT '0' COMMENT '是否外链',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `show_name` varchar(255) DEFAULT NULL COMMENT '展示名称',
  `show_img` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `sorting` int(11) DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_cid` (`category_id`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `gd_t_channel`;
CREATE TABLE `gd_t_channel` (
  `id` bigint(20) NOT NULL COMMENT '自增id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父频道id(默认0无)',
  `name` varchar(255) DEFAULT NULL COMMENT '频道名称',
  `banner` varchar(255) DEFAULT NULL COMMENT '频道banner',
  `be_city` varchar(255) DEFAULT NULL COMMENT '频道所属城市',
  `be_longitude` double(10,6) DEFAULT NULL COMMENT '频道投放经度',
  `be_latitude` double(10,6) DEFAULT NULL COMMENT '频道投放纬度',
  `is_show` tinyint(4) DEFAULT NULL COMMENT '是否展示',
  `intro` varchar(256) DEFAULT NULL COMMENT '频道简介',
  `sorted` int(11) DEFAULT NULL COMMENT '频道排序',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='频道表';

DROP TABLE IF EXISTS  `gd_t_coupon`;
CREATE TABLE `gd_t_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_name` varchar(20) DEFAULT NULL COMMENT '优惠券名称',
  `coupon_money` bigint(20) NOT NULL COMMENT '优惠券面额',
  `is_limit` tinyint(4) DEFAULT NULL COMMENT '是否限制数量（1.是 0.否）',
  `is_outer` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为外部领取优惠券（1：是；0：否）',
  `coupon_num` int(11) DEFAULT NULL COMMENT '优惠券数量',
  `first_num` bigint(20) DEFAULT NULL COMMENT '优惠券起始编号',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `begin_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `coupon_sn` varchar(255) DEFAULT NULL COMMENT '优惠券sn',
  `coupon_des` varchar(255) DEFAULT NULL COMMENT '优惠券说明',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `relate_type` varchar(10) NOT NULL COMMENT '关联类型（no:全场通用 goods:指定商品 category:指定分类 shop:指定店铺）',
  `type_ids` varchar(255) DEFAULT NULL COMMENT '商品id或分类id或店铺id',
  `coupon_type` tinyint(1) DEFAULT '0' COMMENT '优惠券/码 0:优惠券 1:优惠码',
  `full_cut` bigint(20) NOT NULL COMMENT '满多少可用',
  `sn_type` tinyint(1) DEFAULT NULL COMMENT '编码类型（1.随机 2.指定）',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='优惠券';

DROP TABLE IF EXISTS  `gd_t_coupon_detail`;
CREATE TABLE `gd_t_coupon_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券id',
  `coupon_sn` varchar(255) NOT NULL COMMENT '优惠券编码',
  `status` varchar(255) DEFAULT NULL COMMENT '优惠券状态(unused:未使用 used:已使用)',
  `relate_type` varchar(10) DEFAULT NULL COMMENT '关联类型（no:全场通用 goods:指定商品 category:指定分类）',
  `type_ids` varchar(255) DEFAULT NULL COMMENT '商品id或分类id',
  `full_cut` bigint(20) NOT NULL COMMENT '满多少可用',
  `begin_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `coupon_des` varchar(255) DEFAULT NULL COMMENT '优惠券说明',
  `coupon_money` float NOT NULL COMMENT '优惠券面额',
  `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_coupon_sn` (`coupon_sn`) USING BTREE,
  KEY `idx_customer` (`customer_id`,`status`,`coupon_sn`,`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=361006 DEFAULT CHARSET=utf8 COMMENT='优惠券详情';

DROP TABLE IF EXISTS  `gd_t_discover`;
CREATE TABLE `gd_t_discover` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `is_goods` tinyint(4) DEFAULT NULL COMMENT '是否关联商品',
  `goods_channel` bigint(20) DEFAULT NULL COMMENT '商品分类',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `act_name` varchar(255) DEFAULT NULL COMMENT '名称',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `show_img` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `is_show` tinyint(4) DEFAULT NULL COMMENT '是否显示',
  `sorting` int(11) DEFAULT NULL COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='发现页';

DROP TABLE IF EXISTS  `gd_t_goods`;
CREATE TABLE `gd_t_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `title` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类id',
  `old_price` varchar(255) DEFAULT NULL COMMENT '原价区间',
  `sell_price` varchar(255) DEFAULT NULL COMMENT '售价区间',
  `old_low_price` int(11) DEFAULT NULL COMMENT '最低原价',
  `sell_low_price` int(11) DEFAULT NULL COMMENT '最低售价',
  `is_limit_buy` tinyint(4) DEFAULT '0' COMMENT '是否限制购买',
  `limit_nums` int(11) DEFAULT '0' COMMENT '限制购买数量',
  `score` int(11) DEFAULT NULL COMMENT '单件商品获得的积分',
  `is_checked` varchar(10) DEFAULT '0' COMMENT '是否通过审核（待审核:unCheck，已通过:checkPass，未通过:checkFail）',
  `is_show` varchar(10) DEFAULT NULL COMMENT '上下架状态（待上架:notShelf，已上架:onshelf，已下架:offShelf）',
  `on_shelf_time` datetime DEFAULT NULL COMMENT '上架时间',
  `jd_price` varchar(255) DEFAULT NULL COMMENT '京东价',
  `tm_price` varchar(255) DEFAULT NULL COMMENT '天猫价',
  `describe` varchar(255) DEFAULT NULL COMMENT '商品描述',
  `shop_sorting` int(11) DEFAULT NULL COMMENT '店铺排序',
  `img_listPage` varchar(255) DEFAULT NULL COMMENT '列表页展示图片',
  `img_banner` varchar(1000) DEFAULT NULL COMMENT '详细页顶部banner',
  `img_cart` varchar(255) DEFAULT NULL COMMENT '购物车展示图片',
  `details` text COMMENT '图文详细页',
  `store_total` int(11) DEFAULT NULL COMMENT '总库存',
  `sale_total` int(11) DEFAULT NULL COMMENT '总销量',
  `standard` text COMMENT '商品规格（住：属性和属性值的json串）',
  `hold_standard` text COMMENT '拥有的库存规格json串',
  `is_coupon` tinyint(4) DEFAULT '0' COMMENT '能否使用优惠券（1.能  0.不能）',
  `coupon_num` int(11) DEFAULT NULL COMMENT '允许使用优惠券数量',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商id',
  `supplier_title` varchar(255) DEFAULT '' COMMENT '供应商名称',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8 COMMENT='商品表';

DROP TABLE IF EXISTS  `gd_t_goods_launch`;
CREATE TABLE `gd_t_goods_launch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `be_province` varchar(255) DEFAULT NULL COMMENT '投放省',
  `be_city` varchar(255) DEFAULT NULL COMMENT '投放市',
  `be_area` varchar(255) DEFAULT NULL COMMENT '投放区县',
  `be_longitude` double(10,6) DEFAULT NULL COMMENT '经度',
  `be_latitude` double(10,6) DEFAULT NULL COMMENT '纬度',
  `areas_code` varchar(255) DEFAULT NULL COMMENT '投放区域编码',
  `areas_name` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品投放表';

DROP TABLE IF EXISTS  `gd_t_goods_param`;
CREATE TABLE `gd_t_goods_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `goods_key` varchar(255) DEFAULT NULL COMMENT '商品key',
  `goods_val` varchar(255) DEFAULT NULL COMMENT '商品value',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `is_top` tinyint(4) DEFAULT NULL COMMENT '是否置顶',
  PRIMARY KEY (`id`),
  KEY `shop_goods` (`shop_id`,`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8 COMMENT='商品参数';

DROP TABLE IF EXISTS  `gd_t_goods_share`;
CREATE TABLE `gd_t_goods_share` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `title` varchar(255) DEFAULT NULL COMMENT '分享标题',
  `share_pic` varchar(255) DEFAULT NULL COMMENT '分享图片',
  `description` varchar(255) DEFAULT NULL COMMENT '分享描述',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_gid` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='商品分享表';

DROP TABLE IF EXISTS  `gd_t_goods_storage`;
CREATE TABLE `gd_t_goods_storage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `standard_code` varchar(255) NOT NULL COMMENT '规格编码',
  `stock_num` int(11) unsigned DEFAULT NULL COMMENT '库存量',
  `subGoods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '活动商品关联id',
  `describe` varchar(255) DEFAULT NULL COMMENT '描述',
  `img` char(255) DEFAULT NULL COMMENT '图片',
  `sale_price` int(11) DEFAULT NULL COMMENT '单价(分为单位)',
  `old_price` int(11) DEFAULT NULL COMMENT '原价(分为单位)',
  `version` bigint(20) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `gid_scode` (`goods_id`,`subGoods_id`,`standard_code`,`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=635 DEFAULT CHARSET=utf8 COMMENT='商品库存表';

DROP TABLE IF EXISTS  `gd_t_navigate`;
CREATE TABLE `gd_t_navigate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` bigint(20) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `show_img` varchar(255) DEFAULT NULL,
  `sorting` int(11) DEFAULT NULL COMMENT '排序',
  `is_show` tinyint(1) DEFAULT NULL COMMENT '是否显示',
  `type` varchar(10) DEFAULT NULL COMMENT '类型（activity:活动 subject:专题）',
  `type_id` bigint(20) DEFAULT NULL COMMENT '类型id（活动id或专题id）',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `gd_t_shop`;
CREATE TABLE `gd_t_shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '店铺名称',
  `img_banner` varchar(255) DEFAULT NULL COMMENT '店铺宣传图',
  `img_logo` varchar(255) DEFAULT NULL COMMENT '店铺logo',
  `level` int(11) DEFAULT NULL COMMENT '店铺等级',
  `leader` varchar(255) DEFAULT NULL COMMENT '店铺负责人',
  `open_date` date DEFAULT NULL COMMENT '开店日期',
  `address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `buss_status` varchar(20) DEFAULT NULL COMMENT '店铺状态（opening:营业中 stop:关闭 pause:暂停）',
  `audit_status` varchar(255) DEFAULT NULL COMMENT '审核状态（wait:审核中 pass:审核通过 unpass:审核不通过）',
  `show_url` varchar(255) DEFAULT NULL COMMENT '店铺url',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='店铺表';

DROP TABLE IF EXISTS  `gd_t_store`;
CREATE TABLE `gd_t_store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `shop_name` varchar(255) DEFAULT NULL COMMENT '店铺名称',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `show_logo` varchar(255) DEFAULT NULL COMMENT '商品logo地址',
  `old_price` varchar(255) DEFAULT NULL COMMENT '商品原价',
  `sell_price` varchar(255) DEFAULT NULL COMMENT '商品售价',
  `store_time` datetime DEFAULT NULL COMMENT '收藏时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_cum_goods` (`is_deleted`,`customer_id`,`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8 COMMENT='商品收藏';

DROP TABLE IF EXISTS  `gd_t_supplier`;
CREATE TABLE `gd_t_supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(20) NOT NULL COMMENT '供货商名称',
  `name` varchar(20) DEFAULT NULL COMMENT '负责人',
  `goods_count` tinyint(4) DEFAULT '0' COMMENT '产品数量',
  `period` varchar(20) DEFAULT NULL COMMENT '账期（预付，月结，周结）',
  `telephone` varchar(20) DEFAULT NULL COMMENT '客服电话',
  `mail` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='供货商表';

DROP TABLE IF EXISTS  `gift_buy_t_gift_sheet`;
CREATE TABLE `gift_buy_t_gift_sheet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT '' COMMENT '商品名称',
  `goods_img` varchar(255) DEFAULT '' COMMENT '商品logo地址',
  `price` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品售价',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `limit_num` int(11) DEFAULT '0',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8 COMMENT='购物车表';

DROP TABLE IF EXISTS  `gift_buy_t_order`;
CREATE TABLE `gift_buy_t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `total_price` bigint(20) DEFAULT '0' COMMENT '商品总价',
  `order_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '下单时间',
  `order_status` varchar(20) NOT NULL DEFAULT '' COMMENT '订单状态（commit:已提交  wait_check:等待审核 cancel:已取消  send:已发货  success:已完成）',
  `address_id` bigint(20) DEFAULT '0' COMMENT '收货地址id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `bill_status` varchar(10) DEFAULT '' COMMENT '发票状态（have_open：已开发票  no_open：未开发票）',
  `goods_num` int(11) DEFAULT '0' COMMENT '商品数',
  `type_num` int(11) DEFAULT '0' COMMENT '商品种类数',
  `recipient` varchar(255) DEFAULT '' COMMENT '收件人',
  `telephone` varchar(20) DEFAULT '' COMMENT '联系电话',
  `address` varchar(255) DEFAULT '' COMMENT '收货地址',
  `exp_company` varchar(255) DEFAULT '' COMMENT '快递公司',
  `exp_num` varchar(255) DEFAULT '' COMMENT '快递单号',
  `note` varchar(255) DEFAULT '' COMMENT '订单备注',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8 COMMENT='订单表';

DROP TABLE IF EXISTS  `gift_buy_t_order_bill`;
CREATE TABLE `gift_buy_t_order_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `order_id` bigint(20) DEFAULT NULL,
  `order_num` varchar(255) NOT NULL DEFAULT '' COMMENT '订单编号',
  `is_paper` varchar(10) DEFAULT '' COMMENT '是否是纸质发票（paper：纸质发票  electronic ：电子发票）',
  `is_VAT` varchar(10) DEFAULT '' COMMENT '是否是增值税发票（VAT：增值税发票  general：普通发票）',
  `tax_id` varchar(255) DEFAULT '' COMMENT '纳税人识别号',
  `header` varchar(255) DEFAULT '' COMMENT '名称',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `telephone` varchar(12) DEFAULT '' COMMENT '联系电话',
  `bank` varchar(50) DEFAULT '' COMMENT '开户行',
  `account_num` varchar(20) DEFAULT '' COMMENT '银行卡卡号',
  `is_compay` varchar(10) DEFAULT '' COMMENT '发票类型（personal:个人  compay:公司）',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否为默认',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COMMENT='订单发票表';

DROP TABLE IF EXISTS  `gift_buy_t_order_detail`;
CREATE TABLE `gift_buy_t_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_detail_num` varchar(255) DEFAULT '' COMMENT '订单详情编号',
  `order_num` varchar(255) NOT NULL DEFAULT '' COMMENT '订单编号',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单id',
  `goods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT '' COMMENT '商品名称',
  `goods_img` varchar(255) DEFAULT '' COMMENT '商品缩略图',
  `goods_price` bigint(20) DEFAULT '0' COMMENT '商品单价',
  `goods_amount` int(11) NOT NULL DEFAULT '0' COMMENT '商品购买数量',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8 COMMENT='订单详情表';

DROP TABLE IF EXISTS  `gift_dma_t_demand`;
CREATE TABLE `gift_dma_t_demand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户id',
  `scene` varchar(10) DEFAULT '' COMMENT '应用场景',
  `gift_feature` varchar(10) DEFAULT '' COMMENT '礼品特征',
  `budget` varchar(10) DEFAULT '' COMMENT '预算',
  `gift_num` varchar(10) DEFAULT '' COMMENT '礼品总量',
  `telephone` varchar(12) DEFAULT '' COMMENT '联系电话',
  `status` varchar(12) DEFAULT '' COMMENT '是否已联系（1:已联系；2:未联系）',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='轮播banner';

DROP TABLE IF EXISTS  `gift_gd_t_category`;
CREATE TABLE `gift_gd_t_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `pid` bigint(20) DEFAULT '0' COMMENT '父类id',
  `name` varchar(255) DEFAULT '' COMMENT '分类名称',
  `is_show` tinyint(4) DEFAULT '0' COMMENT '是否展示（1 展示， 0 不展示）',
  `c_type` varchar(10) DEFAULT '' COMMENT '分类类型（virtual 虚拟， material 实物）',
  `sorting` bigint(20) DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8 COMMENT='分类表';

DROP TABLE IF EXISTS  `gift_gd_t_goods`;
CREATE TABLE `gift_gd_t_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `category_id` bigint(20) NOT NULL COMMENT '分类id',
  `price` int(11) DEFAULT '0' COMMENT '单价(分为单位)',
  `title` varchar(255) DEFAULT '' COMMENT '商品名称',
  `is_limit_buy` tinyint(4) DEFAULT '0' COMMENT '是否限制购买',
  `limit_nums` int(11) DEFAULT '0' COMMENT '限制购买数量',
  `is_checked` varchar(10) DEFAULT '' COMMENT '是否通过审核（待审核:unCheck，已通过:checkPass，未通过:checkFail）',
  `is_show` varchar(10) DEFAULT '' COMMENT '上下架状态（待上架:notShelf，已上架:onshelf，已下架:offShelf）',
  `describe` varchar(255) DEFAULT '' COMMENT '商品描述',
  `delivery_type` varchar(10) DEFAULT '' COMMENT '发货方式(直充:dr,物流快递:exp电子兑换码:eec)',
  `sorting` bigint(20) DEFAULT '0',
  `img_listPage_pc` varchar(255) DEFAULT '' COMMENT '列表页pc展示图片',
  `img_listPage_mobile` varchar(255) DEFAULT '' COMMENT '列表页礼品图移动端',
  `img_banner` varchar(1000) DEFAULT '' COMMENT '详细页顶部banner',
  `details` text COMMENT '图文详细页',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COMMENT='礼品表';

DROP TABLE IF EXISTS  `gift_mall_t_banner`;
CREATE TABLE `gift_mall_t_banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(30) DEFAULT '' COMMENT '名称',
  `pc_img` varchar(255) DEFAULT '' COMMENT '移动端图片',
  `mob_img` varchar(255) DEFAULT '' COMMENT 'PC端图片',
  `status` varchar(20) NOT NULL DEFAULT 'link' COMMENT '链接类型(goods:商品,link:链接,demand:极速获取)',
  `link_url` varchar(255) DEFAULT '' COMMENT '跳转链接',
  `goods_id` bigint(20) DEFAULT '0',
  `sort` tinyint(4) DEFAULT '0' COMMENT '排序',
  `is_show` tinyint(4) DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='轮播banner';

DROP TABLE IF EXISTS  `gift_ps_t_address`;
CREATE TABLE `gift_ps_t_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) DEFAULT '0' COMMENT '用户id',
  `recipient` varchar(50) DEFAULT '' COMMENT '收件人',
  `telephone` varchar(12) DEFAULT '' COMMENT '联系电话',
  `province` varchar(5) DEFAULT '' COMMENT '省编码',
  `city` varchar(5) DEFAULT '' COMMENT '市编码',
  `area` varchar(5) DEFAULT '' COMMENT '区县编码',
  `address` varchar(500) DEFAULT '' COMMENT '详细地址',
  `post_code` varchar(50) DEFAULT '' COMMENT '邮政编码',
  `period` tinyint(4) DEFAULT '0' COMMENT '收货时间',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `cid` (`customer_id`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8 COMMENT='收货地址表';

DROP TABLE IF EXISTS  `gift_ps_t_customer`;
CREATE TABLE `gift_ps_t_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `telephone` varchar(12) DEFAULT '' COMMENT '手机号',
  `customer_name` varchar(12) DEFAULT '' COMMENT '用户名',
  `user_name` varchar(20) DEFAULT '' COMMENT '登录名',
  `organization` varchar(30) DEFAULT '' COMMENT '机构名称',
  `operator` varchar(30) DEFAULT '' COMMENT '运营人员',
  `opt_telephone` varchar(12) DEFAULT '' COMMENT '运营人员电话',
  `password` varchar(255) DEFAULT '' COMMENT '注册密码',
  `random_num` varchar(11) DEFAULT '' COMMENT '密码随机数',
  `register_time` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '注册时间',
  `last_login_time` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '最近一次登入时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8 COMMENT='用户表';

DROP TABLE IF EXISTS  `ntc_t_mail`;
CREATE TABLE `ntc_t_mail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `notice_type` varchar(255) NOT NULL COMMENT 'refund:退款通知，buy:下单通知',
  `mail` varchar(255) NOT NULL COMMENT '邮件地址',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='通知邮件表';

DROP TABLE IF EXISTS  `ps_t_customer_address`;
CREATE TABLE `ps_t_customer_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` bigint(20) DEFAULT '0' COMMENT '用户id',
  `recipient` varchar(50) DEFAULT '' COMMENT '收件人',
  `telephone` varchar(12) DEFAULT '' COMMENT '联系电话',
  `province` varchar(5) DEFAULT '' COMMENT '省编码',
  `city` varchar(5) DEFAULT '' COMMENT '市编码',
  `area` varchar(5) DEFAULT '' COMMENT '区县编码',
  `address` varchar(500) DEFAULT '' COMMENT '详细地址',
  `post_code` varchar(50) DEFAULT '' COMMENT '邮政编码',
  `period` tinyint(4) DEFAULT '0' COMMENT '收货时间',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `cid` (`customer_id`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=33478 DEFAULT CHARSET=utf8 COMMENT='收货地址表';

DROP TABLE IF EXISTS  `ps_t_customer_info`;
CREATE TABLE `ps_t_customer_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `telephone` varchar(12) DEFAULT NULL COMMENT '手机号',
  `openid` varchar(255) DEFAULT '' COMMENT '用户绑定微信后的openid',
  `abcaid` varchar(255) DEFAULT '' COMMENT '农行app用户id',
  `password` varchar(255) DEFAULT '' COMMENT '注册密码',
  `random_num` varchar(11) DEFAULT '' COMMENT '密码随机数',
  `register_time` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '注册时间',
  `last_login_time` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '最近一次登入时间',
  `user_name` varchar(20) DEFAULT '' COMMENT '用户名',
  `sex` tinyint(4) DEFAULT '0' COMMENT '性别',
  `birthday` date DEFAULT '1970-01-01' COMMENT '出生年月',
  `email` varchar(20) DEFAULT '' COMMENT '邮箱',
  `profile` varchar(255) DEFAULT '' COMMENT '用户头像',
  `integral` int(11) DEFAULT '0' COMMENT '积分',
  `level` int(11) DEFAULT '0' COMMENT '等级',
  `growth` bigint(20) DEFAULT '0' COMMENT '成长值',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_tel` (`telephone`,`is_deleted`) USING BTREE,
  KEY `idx_aid` (`abcaid`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=332281 DEFAULT CHARSET=utf8 COMMENT='用户表';

DROP TABLE IF EXISTS  `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(200) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sub_r_subject_goods`;
CREATE TABLE `sub_r_subject_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `subject_id` bigint(20) NOT NULL COMMENT '专题id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `goods_img` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `tagline` varchar(255) DEFAULT NULL COMMENT '宣传语',
  `sell_price` varchar(255) NOT NULL COMMENT '商品售价(可能是区间)',
  `old_price` varchar(255) NOT NULL COMMENT '商品原价(可能是区间)',
  `sell_low_price` int(11) NOT NULL COMMENT '最低售价',
  `old_low_price` int(11) NOT NULL COMMENT '最低原价',
  `sorting` int(11) DEFAULT NULL COMMENT '专题商品排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8 COMMENT='专题商品表';

DROP TABLE IF EXISTS  `sub_t_subject`;
CREATE TABLE `sub_t_subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `title` varchar(255) DEFAULT NULL COMMENT '专题名称',
  `sub_tmp_code` varchar(255) DEFAULT NULL COMMENT '专题模板code',
  `top_banner` varchar(255) DEFAULT NULL COMMENT '顶部banner',
  `be_city` varchar(255) DEFAULT NULL COMMENT '投放城市',
  `be_longitude` double(10,6) DEFAULT NULL COMMENT '投放经度',
  `be_latitude` double(10,6) DEFAULT NULL COMMENT '投放纬度',
  `is_show` tinyint(4) DEFAULT NULL COMMENT '是否展示',
  `intro` varchar(256) DEFAULT NULL COMMENT '专题简介',
  `sorting` int(11) DEFAULT NULL COMMENT '专题排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='专题表';

DROP TABLE IF EXISTS  `sub_t_subject_templet`;
CREATE TABLE `sub_t_subject_templet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '模板名称',
  `code` varchar(255) DEFAULT NULL COMMENT '唯一编码（区分是那个画面）',
  `img_width` varchar(10) DEFAULT NULL COMMENT '图片宽',
  `img_height` varchar(10) DEFAULT NULL COMMENT '图片高',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='专题模板';

DROP TABLE IF EXISTS  `sys_r_role_rule`;
CREATE TABLE `sys_r_role_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `rule_id` bigint(20) DEFAULT NULL COMMENT '操作id',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_r_user_role`;
CREATE TABLE `sys_r_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_t_areas`;
CREATE TABLE `sys_t_areas` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `code` varchar(255) DEFAULT NULL COMMENT '编码(不可重复)',
  `name` varchar(255) DEFAULT '' COMMENT '地区名称',
  `p_code` varchar(255) DEFAULT '0' COMMENT '父级编码',
  `type` varchar(20) DEFAULT NULL COMMENT '类型(province:省  city:市 area:区)',
  `longitude` double(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` double(10,6) DEFAULT NULL COMMENT '纬度',
  `bd_citycode` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`p_code`),
  KEY `region_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3434 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='地区表';

DROP TABLE IF EXISTS  `sys_t_areas_bak`;
CREATE TABLE `sys_t_areas_bak` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `code` varchar(255) DEFAULT NULL COMMENT '编码(不可重复)',
  `name` varchar(255) DEFAULT '' COMMENT '地区名称',
  `p_code` varchar(255) DEFAULT '0' COMMENT '父级编码',
  `type` varchar(20) DEFAULT NULL COMMENT '类型(province:省  city:市 area:区)',
  `longitude` double(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` double(10,6) DEFAULT NULL COMMENT '纬度',
  `bd_citycode` varchar(255) DEFAULT NULL COMMENT '百度城市code码',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`p_code`),
  KEY `region_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3434 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_t_image`;
CREATE TABLE `sys_t_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(20) DEFAULT NULL COMMENT '图片类型（logo:logo thumbnail:缩略图 banner:banner other:其他）',
  `source_type` varchar(255) DEFAULT NULL COMMENT '来源类型（seckill:秒杀  groupon:团购 common:普通）',
  `seat` varchar(20) DEFAULT NULL COMMENT '图片位置（goods:商品 discover:发现页 mallhome:农行客户端首页）',
  `seat_id` bigint(20) DEFAULT NULL COMMENT '对应id',
  `location` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `link_url` varchar(255) DEFAULT NULL COMMENT '链接地址',
  `sorting` int(11) DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='图片表';

DROP TABLE IF EXISTS  `sys_t_operate_log`;
CREATE TABLE `sys_t_operate_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `operator` varchar(20) DEFAULT NULL COMMENT '操作者',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `type` tinyint(1) DEFAULT '0' COMMENT '类型',
  `content` mediumtext COMMENT '操作内容',
  `ip` varchar(20) DEFAULT NULL COMMENT 'ip',
  `ip_city` varchar(100) DEFAULT NULL COMMENT '所在城市',
  `gmt_created` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=324 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_t_role`;
CREATE TABLE `sys_t_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_name` varchar(100) NOT NULL DEFAULT '' COMMENT '角色名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '角色状态',
  `gmt_created` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_t_rule`;
CREATE TABLE `sys_t_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `pid` int(11) NOT NULL COMMENT '父id',
  `url` varchar(80) NOT NULL DEFAULT '' COMMENT '连接url',
  `title` varchar(20) DEFAULT '' COMMENT '菜单名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `type` tinyint(1) DEFAULT '1' COMMENT '类型',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `islink` tinyint(1) DEFAULT '1',
  `orderby` int(11) DEFAULT NULL COMMENT '排序',
  `tips` text COMMENT '提示',
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=266 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `sys_t_user`;
CREATE TABLE `sys_t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(225) NOT NULL COMMENT '用户名',
  `real_name` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(32) DEFAULT NULL,
  `head` varchar(255) DEFAULT NULL COMMENT '头像',
  `sex` tinyint(1) DEFAULT '0' COMMENT '0保密1男，2女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `qq` varchar(20) DEFAULT NULL COMMENT 'QQ',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `reg_date` datetime DEFAULT NULL COMMENT '注册时间',
  `tokenDES` varchar(255) DEFAULT NULL,
  `login_token` varchar(255) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00',
  `is_deleted` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `td_t_member_account`;
CREATE TABLE `td_t_member_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_key` varchar(255) NOT NULL COMMENT '公钥',
  `app_secret` varchar(255) NOT NULL COMMENT '秘钥',
  `is_deny` tinyint(1) DEFAULT '0' COMMENT '是否启用（0，是；1，否）',
  `remark` varchar(255) NOT NULL COMMENT '备注',
  `keymark` varchar(255) NOT NULL COMMENT '活动标识',
  `abc_appid` varchar(255) NOT NULL COMMENT '农行分配的appid',
  `abc_appsecret` varchar(255) NOT NULL COMMENT '农行分配的appsecret',
  `page_address` varchar(255) NOT NULL COMMENT '页面地址',
  `return_url` varchar(255) DEFAULT NULL COMMENT '支付完成后，跳转地址',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00',
  `is_deleted` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_km` (`keymark`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `td_t_third_goods`;
CREATE TABLE `td_t_third_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `old_price` varchar(255) DEFAULT NULL COMMENT '原价区间',
  `sell_price` varchar(255) DEFAULT NULL COMMENT '售价区间',
  `show_img` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `describe` varchar(255) DEFAULT NULL COMMENT '商品描述',
  `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `show_local` varchar(10) DEFAULT NULL COMMENT '显示位置（）',
  `sorting` int(11) DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方商品表';

DROP TABLE IF EXISTS  `td_t_third_goods_areas`;
CREATE TABLE `td_t_third_goods_areas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` varchar(255) DEFAULT NULL COMMENT '商品id',
  `city_code` varchar(255) DEFAULT NULL COMMENT '投放城市code',
  `city_name` varchar(255) DEFAULT NULL COMMENT '投放城市名称',
  `bd_citycode` varchar(255) DEFAULT NULL COMMENT '百度城市中心码',
  `longitude` double(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` double(10,6) DEFAULT NULL COMMENT '纬度',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方商品表投放地区表';

DROP TABLE IF EXISTS  `td_t_third_order`;
CREATE TABLE `td_t_third_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `abcaid` varchar(255) DEFAULT NULL COMMENT '用户id',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `order_amount` bigint(20) NOT NULL COMMENT '订单金额（分）',
  `order_times` datetime NOT NULL COMMENT '订单日期',
  `order_date` varchar(10) NOT NULL COMMENT '订单日期（yyyy/MM/DD）',
  `order_time` varchar(8) NOT NULL COMMENT '订单时间（HH:mm:ss）',
  `order_source` varchar(255) DEFAULT NULL COMMENT '订单来源(fresh：每日优鲜)',
  `products` tinytext COMMENT '商品json格式',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '通知地址',
  `order_status` varchar(255) DEFAULT NULL COMMENT '订单状态(wait:待支付 success:支付成功 fail:支付失败 refund:已退款)',
  `payment_num` varchar(255) NOT NULL COMMENT '支付编号',
  `app_key` varchar(255) DEFAULT NULL COMMENT '公钥',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pnum` (`payment_num`) USING BTREE,
  UNIQUE KEY `idx_numkey` (`order_num`,`app_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `td_t_third_order_pay`;
CREATE TABLE `td_t_third_order_pay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `payment_num` varchar(255) NOT NULL COMMENT '支付编号',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `order_amount` bigint(20) DEFAULT NULL COMMENT '订单金额',
  `pay_date` datetime DEFAULT NULL COMMENT '付款时间',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式（kcode：K码支付   wxpay：微信支付  alipay：支付宝支付）',
  `pay_status` varchar(20) DEFAULT NULL COMMENT '支付状态(wait:待支付 success:支付成功 fail:支付失败)',
  `batch_no` varchar(255) DEFAULT NULL COMMENT '交易批次号',
  `batch_status` varchar(10) DEFAULT NULL COMMENT '交易状态',
  `amount` bigint(20) DEFAULT NULL COMMENT '交易金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '商户备注',
  `notify_type` varchar(4) DEFAULT NULL COMMENT '通知类型(0:页面通知   1:服务器通知)',
  `irsp_ref` varchar(255) DEFAULT NULL COMMENT '支付流水号',
  `voucher_no` varchar(255) DEFAULT NULL COMMENT '交易凭证号',
  `host_date` varchar(10) DEFAULT NULL COMMENT '银行交易日期',
  `host_time` varchar(8) DEFAULT NULL COMMENT '银行交易时间',
  `mch_id` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_num` (`order_num`),
  KEY `nums` (`payment_num`,`order_num`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `td_t_third_order_refund`;
CREATE TABLE `td_t_third_order_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `refund_num` varchar(255) NOT NULL COMMENT '退款单号',
  `order_num` varchar(255) NOT NULL COMMENT '订单编号',
  `app_key` varchar(255) DEFAULT NULL COMMENT '订单详情编号',
  `refund_status` varchar(20) DEFAULT NULL COMMENT '退款状态（success:退款成功 fail:退款失败）',
  `irsp_ref` varchar(50) DEFAULT NULL COMMENT '交易流水号',
  `batch_status` varchar(10) DEFAULT NULL COMMENT '交易状态',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '交易批次号',
  `refund_fee` int(11) NOT NULL COMMENT '退款金额',
  `total_fee` int(11) NOT NULL COMMENT '订单金额',
  `voucher_no` varchar(50) DEFAULT NULL COMMENT '交易凭证号',
  `host_date` varchar(10) DEFAULT NULL COMMENT '银行交易日期',
  `host_time` varchar(8) DEFAULT NULL COMMENT '银行交易时间',
  `mch_id` varchar(255) DEFAULT NULL COMMENT '商户号',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `tp_customer_basic_info`;
CREATE TABLE `tp_customer_basic_info` (
  `id` bigint(20) unsigned NOT NULL,
  `token` varchar(255) NOT NULL COMMENT '商户辨识码',
  `mobile_phone` char(11) NOT NULL COMMENT '用户注册手机号',
  `password` char(32) NOT NULL COMMENT '用户注册密码',
  `register_time` bigint(20) NOT NULL COMMENT '用户注册时间',
  `last_login_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户最近一次登录的时间',
  `login_identifier` varchar(500) NOT NULL DEFAULT '0' COMMENT '用户登录标识符',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据是否被删除（0表示否，1表示是）',
  `ip` varchar(50) NOT NULL COMMENT '注册ip',
  `userfrom_id` int(10) DEFAULT '0' COMMENT '用户来源ID',
  `area` varchar(255) NOT NULL DEFAULT '区域',
  `from_act` varchar(255) DEFAULT '' COMMENT '注册来源活动',
  `user_name` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别（0，男；1，女）',
  `birthday` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '出生日期',
  `email` varchar(255) DEFAULT '' COMMENT '邮箱',
  `profile` varchar(255) DEFAULT '' COMMENT '用户头像',
  `integral` int(45) DEFAULT '0' COMMENT '积分',
  `rank_id` int(11) NOT NULL DEFAULT '0' COMMENT '等级id',
  `growth_value` bigint(20) NOT NULL DEFAULT '0' COMMENT '成长值',
  `is_add` tinyint(1) DEFAULT '0' COMMENT '判断是否判断过资料完善（0,否;1,是)',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile_phone`,`is_deleted`),
  KEY `idx_ident` (`login_identifier`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商城注册用户基础信息表';

DROP TABLE IF EXISTS  `wx_t_config`;
CREATE TABLE `wx_t_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(10) DEFAULT NULL COMMENT '项目code',
  `appid` varchar(255) DEFAULT NULL COMMENT '小程序appid',
  `secret` varchar(255) DEFAULT NULL COMMENT '小程序secret',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '删除',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='微信配置表';

DROP TABLE IF EXISTS  `wx_t_share_config`;
CREATE TABLE `wx_t_share_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `link_url` varchar(255) NOT NULL COMMENT '分享url',
  `description` varchar(255) NOT NULL COMMENT '分享描述',
  `img_url` varchar(255) NOT NULL COMMENT '分享图片地址',
  `type` varchar(10) DEFAULT 'link' COMMENT '分享类型  music、video或link',
  `data_url` varchar(255) DEFAULT NULL COMMENT '如果type是music或video，则要提供数据链接，默认为空',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认 0:是 1:否',
  `gmt_created` datetime DEFAULT '1970-01-01 00:00:00',
  `gmt_modified` datetime DEFAULT '1970-01-01 00:00:00',
  `is_deleted` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

