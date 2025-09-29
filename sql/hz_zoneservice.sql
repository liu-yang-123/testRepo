/*
 Navicat Premium Data Transfer

 Source Server         : zone
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : hz_zoneservice

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 29/09/2025 09:33:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_update_setting
-- ----------------------------
DROP TABLE IF EXISTS `app_update_setting`;
CREATE TABLE `app_update_setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1 - 手机, 2 - PDA ',
  `app_update_time` bigint NOT NULL DEFAULT 0 COMMENT '版本升级日期',
  `version_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '版本名',
  `version_number` int NOT NULL DEFAULT 0 COMMENT '版本号',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '版本更新特性描述',
  `force_update` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否强制更新 0-不强制 1-强制',
  `package_size` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '安装包大小',
  `package_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '安装包下载地址',
  `status_t` tinyint NOT NULL DEFAULT 0 COMMENT '版本状态，',
  `create_user` bigint NOT NULL DEFAULT 0,
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '数据修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_addition_cash
-- ----------------------------
DROP TABLE IF EXISTS `atm_addition_cash`;
CREATE TABLE `atm_addition_cash`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `task_date` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务日期',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路（选填）',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属机构',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `cash_type` tinyint NULL DEFAULT 0 COMMENT '类型 0-备用金 1-其他',
  `denom_value` int NOT NULL DEFAULT 0 COMMENT '券别金额',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '备用金金额',
  `carry_route_id` bigint NOT NULL DEFAULT 0 COMMENT '携带线路',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '顶级部门ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `is_out` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否执行出库操作',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4323 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'ATM线路备用金\r\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_bank_check_record
-- ----------------------------
DROP TABLE IF EXISTS `atm_bank_check_record`;
CREATE TABLE `atm_bank_check_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `sub_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '设备网点',
  `check_mans` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '巡检人员',
  `check_time` bigint NOT NULL DEFAULT 0 COMMENT '检查时间',
  `room_check_result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '加钞间检查结果',
  `hall_check_result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '取款前厅检查结果（json,  字段：0/1  0 - 不正常，1正常）',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注说明',
  `revoke_alarm_time` bigint NOT NULL DEFAULT 0 COMMENT '撤防时间',
  `set_alarm_time` bigint NOT NULL DEFAULT 0 COMMENT '布放时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门id',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `check_normal` tinyint NOT NULL DEFAULT 0 COMMENT '网点巡检结果（0 - 正常，1 - 异常）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 148783 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '安全巡查子任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_clear_error
-- ----------------------------
DROP TABLE IF EXISTS `atm_clear_error`;
CREATE TABLE `atm_clear_error`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '出入库单号',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '对应设备',
  `detail_type` int NOT NULL DEFAULT 0 COMMENT '差错明细类型: 1 - 假币，2 - 残缺币，3 - 夹张',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `count` int NULL DEFAULT 1 COMMENT '张数',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `rmb_sn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '冠字号',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 238 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'atm清分差错明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_clear_task
-- ----------------------------
DROP TABLE IF EXISTS `atm_clear_task`;
CREATE TABLE `atm_clear_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `plan_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '计划清分金额/库存金额',
  `clear_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '实际清点金额',
  `clear_type` int NOT NULL DEFAULT 0 COMMENT '清分类型',
  `department_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '顶级部门ID',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路（选填）',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT 'atm设备（选填）',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行网点',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '关联清机任务',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '加钞券别',
  `task_date` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务日期',
  `import_batch` bigint NOT NULL DEFAULT 0 COMMENT '导入批次',
  `clear_man` bigint NOT NULL DEFAULT 0 COMMENT '清点员',
  `check_man` bigint NOT NULL DEFAULT 0 COMMENT '复核员',
  `clear_time` bigint NOT NULL DEFAULT 0 COMMENT '清点时间',
  `error_type` int NOT NULL DEFAULT 0 COMMENT '差错类型',
  `error_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '差错金额',
  `error_reason` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '差错备注',
  `error_confirm_man` bigint NOT NULL DEFAULT 0 COMMENT '差错确认主管',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 160147 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_clear_task_audit
-- ----------------------------
DROP TABLE IF EXISTS `atm_clear_task_audit`;
CREATE TABLE `atm_clear_task_audit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `clear_task_id` bigint NULL DEFAULT NULL COMMENT '清分任务ID',
  `plan_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '计划清分金额/库存金额',
  `clear_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '实际清点金额',
  `clear_type` int NOT NULL DEFAULT 0 COMMENT '清分类型',
  `department_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '顶级部门ID',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路（选填）',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT 'atm设备（选填）',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行网点',
  `task_date` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务日期',
  `clear_man` bigint NOT NULL DEFAULT 0 COMMENT '清点员',
  `check_man` bigint NOT NULL DEFAULT 0 COMMENT '复核员',
  `clear_time` bigint NOT NULL DEFAULT 0 COMMENT '清点时间',
  `error_type` int NOT NULL DEFAULT 0 COMMENT '差错类型',
  `error_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '差错金额',
  `error_reason` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '差错备注',
  `error_confirm_man` bigint NOT NULL DEFAULT 0 COMMENT '差错确认主管',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `error_list` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '差错明细',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态  0=未审核   1=审核中 2=审核通过 3=审核拒绝',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分任务审核记录数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_device
-- ----------------------------
DROP TABLE IF EXISTS `atm_device`;
CREATE TABLE `atm_device`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `ter_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '终端编号',
  `ter_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备类型',
  `ter_factory` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备品牌',
  `denom` int NOT NULL DEFAULT 0 COMMENT '加钞券别',
  `location_type` int NOT NULL DEFAULT 0 COMMENT '位置类型 1: 离行式 2：附行式 3：大堂式',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `install_info` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '装机地址',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属机构',
  `sub_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属网点',
  `gulp_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '取吞卡网点',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_ter_no`(`ter_no` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1190 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'ATM设备信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_maintain
-- ----------------------------
DROP TABLE IF EXISTS `atm_maintain`;
CREATE TABLE `atm_maintain`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `amt_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '终端ID',
  `task_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务编号',
  `task_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务类型',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务备注',
  `issue_list` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '故障列表',
  `status` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务状态',
  `line_id` bigint NOT NULL DEFAULT 0 COMMENT '分配线路',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `start_time` bigint NOT NULL DEFAULT 0 COMMENT '开始时间',
  `end_time` bigint NOT NULL DEFAULT 0 COMMENT '结束时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'ATM维修任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task
-- ----------------------------
DROP TABLE IF EXISTS `atm_task`;
CREATE TABLE `atm_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `task_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务编号',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '终端ID',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '设备所属银行',
  `sub_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '设备网点',
  `task_type` int NOT NULL DEFAULT 0 COMMENT '任务列表（1,2,3,4)  对应 维护，加钞，清机，巡检',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '分配线路',
  `carry_route_id` bigint NOT NULL DEFAULT 0 COMMENT '执行线路id',
  `import_batch` bigint NOT NULL DEFAULT 0 COMMENT '导入批次',
  `carry_type` int NOT NULL DEFAULT 0 COMMENT '回笼现金运送方式：0 - 钞袋， 1- 钞盒',
  `backup_flag` int NOT NULL DEFAULT 0 COMMENT '备用金排班标志 0 - 预排班， 1 - 备用金排班',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '加钞金额',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '加钞券别',
  `cashbox_list` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '加钞钞盒列表',
  `clear_site` int NOT NULL DEFAULT 0 COMMENT '现场清点标志 0-否 1-是',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '任务状态',
  `begin_time` bigint NOT NULL DEFAULT 0 COMMENT '开始时间',
  `end_time` bigint NOT NULL DEFAULT 0 COMMENT '结束时间',
  `atm_run_status` int NOT NULL DEFAULT 0 COMMENT 'ATM运行状态',
  `stuck_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '卡钞金额',
  `clean_op_man_id` bigint NOT NULL DEFAULT 0 COMMENT '清机密码员',
  `clean_key_man_id` bigint NOT NULL DEFAULT 0 COMMENT '清机钥匙员',
  `repair_plan_time` bigint NOT NULL DEFAULT 0 COMMENT '维修计划时间',
  `repair_content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维修内容',
  `repair_company` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维修公司名称',
  `offline` tinyint NOT NULL DEFAULT 0 COMMENT '离线处理标志（1 - 离线处理，0 - 联机处理）',
  `channel` tinyint NOT NULL DEFAULT 0 COMMENT '任务来源（0 - 后台，1 - 手机）',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  `is_out` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否执行出库',
  `check_result` tinyint NOT NULL DEFAULT 0 COMMENT '设备巡检结果（0 - 正常，1 - 异常）',
  `check_item_result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '检查项目结果',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `route_index`(`route_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 244501 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'ATM清机任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_card
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_card`;
CREATE TABLE `atm_task_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '取卡线路',
  `route_no` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '任务ID',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '设备id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门id',
  `card_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '吞卡卡号',
  `card_bank` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '发卡行',
  `category` tinyint NOT NULL DEFAULT 0 COMMENT '类型（0 - 实物卡，1 - 现场拿卡单)',
  `collect_man_a` bigint NOT NULL DEFAULT 0 COMMENT '交接人a',
  `collect_man_b` bigint NOT NULL DEFAULT 0 COMMENT '交接人b',
  `collect_time` bigint NOT NULL DEFAULT 0 COMMENT '交接时间',
  `dispatch_man_a` bigint NOT NULL DEFAULT 0 COMMENT '配卡出库人',
  `dispatch_man_b` bigint NOT NULL DEFAULT 0 COMMENT '配卡人2',
  `dispatch_time` bigint NOT NULL DEFAULT 0 COMMENT '配卡时间',
  `deliver_route_id` bigint NOT NULL DEFAULT 0 COMMENT '送卡线路',
  `deliver_route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '送卡线路编号',
  `deliver_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '交卡网点',
  `deliver_day` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '上交日期',
  `deliver_type` int NOT NULL DEFAULT 0 COMMENT '派送方式（1 - 上缴银行，2- 自取）',
  `receiver_idno` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '移交人证件号码',
  `receiver_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '移交人姓名',
  `receive_time` bigint NOT NULL DEFAULT 0 COMMENT '移交时间',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态（0 - 取回，1-入库，2-分配 3 - 派送, 4 - 领取',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5600 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '吐卡记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_check_record
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_check_record`;
CREATE TABLE `atm_task_check_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `atm_task_id` bigint NOT NULL DEFAULT 0 COMMENT '设备任务id',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '设备id',
  `check_item_result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '检查项目结果',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注说明',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '安全巡查记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_clean_record
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_clean_record`;
CREATE TABLE `atm_task_clean_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `atm_task_id` bigint NOT NULL DEFAULT 0 COMMENT '设备任务id',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '设备id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `sub_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '设备网点',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '加钞金额',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '加钞券别',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态（0 - 未执行，-1 - 撤销，1 - 完成',
  `cashbox_list` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '加钞钞盒列表',
  `clear_site` int NOT NULL DEFAULT 0 COMMENT '即时清点标志  0 - 否，1 - 是',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清机加钞操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_import_record
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_import_record`;
CREATE TABLE `atm_task_import_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门id',
  `import_type` int NOT NULL DEFAULT 0 COMMENT '导入类型 0-清机任务 1-清分任务',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '导入文件日期',
  `bank_type` int NOT NULL DEFAULT 0 COMMENT '银行类型',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `file_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `system_file_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '系统文件名',
  `create_user` bigint NOT NULL DEFAULT 0,
  `create_time` bigint NOT NULL DEFAULT 0,
  `update_user` bigint NOT NULL DEFAULT 0,
  `update_time` bigint NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17024 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_repair_record
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_repair_record`;
CREATE TABLE `atm_task_repair_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `atm_task_id` bigint NOT NULL DEFAULT 0 COMMENT '设备任务id',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '设备id',
  `arrive_time` bigint NOT NULL DEFAULT 0 COMMENT '业务员到达时间',
  `engineer_arrive_time` bigint NOT NULL DEFAULT 0 COMMENT '厂家到达时间',
  `engineer_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维修人',
  `fault_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '故障类型',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '故障描述',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注说明',
  `cashbox_replace` tinyint NOT NULL DEFAULT 0 COMMENT '是否更换钞箱',
  `cash_in_box` tinyint NOT NULL DEFAULT 0 COMMENT '钞箱是否有现金',
  `deal_result` int NOT NULL DEFAULT 0 COMMENT '处理结果：1 - 解决，0 - 未解决',
  `deal_comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '处理结果说明',
  `finish_time` bigint NOT NULL DEFAULT 0 COMMENT '完成时间',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47209 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '设备维护维修操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for atm_task_return
-- ----------------------------
DROP TABLE IF EXISTS `atm_task_return`;
CREATE TABLE `atm_task_return`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT 'atm任务id',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '对应atm',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '任务顶级银行id',
  `box_bar_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '包/盒条码',
  `carry_type` int NOT NULL DEFAULT 0 COMMENT '回笼运输类型',
  `task_date` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务日期',
  `clear_flag` int NOT NULL DEFAULT 0 COMMENT '清点标志（0 - 未清点，1 - 已清点）',
  `clear_time` bigint NOT NULL DEFAULT 0 COMMENT '清分时间',
  `clear_man_id` bigint NOT NULL DEFAULT 0 COMMENT '清点人',
  `check_man_id` bigint NOT NULL DEFAULT 0 COMMENT '复核人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 172113 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'ATM现金回笼记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attendance_machine
-- ----------------------------
DROP TABLE IF EXISTS `attendance_machine`;
CREATE TABLE `attendance_machine`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `address` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'ip地址',
  `port` int UNSIGNED NOT NULL COMMENT '端口号',
  `machine_num` int UNSIGNED NOT NULL COMMENT '机器号',
  `status` tinyint NULL DEFAULT 1 COMMENT '0：不可用，1：可用',
  `department_id` int NOT NULL,
  `create_user` bigint NULL DEFAULT 0 COMMENT '操作员id',
  `create_time` bigint NULL DEFAULT 0,
  `update_user` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bank
-- ----------------------------
DROP TABLE IF EXISTS `bank`;
CREATE TABLE `bank`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门（事业部id）',
  `parent_ids` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '父级机构ID: /0/aaa/bbb',
  `bank_no` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '银行编码',
  `full_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '机构名称',
  `short_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '机构简称',
  `province` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '城市',
  `district` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '区县',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '详细地址',
  `work_info` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '营业时间及应急联系人',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `carry_type` int NOT NULL DEFAULT 0 COMMENT '回笼现金运送方式：0 - 钞袋， 1- 钞盒',
  `locate_lat` decimal(10, 7) NOT NULL DEFAULT 0.0000000 COMMENT '位置经度',
  `locate_lng` decimal(10, 7) NOT NULL DEFAULT 0.0000000 COMMENT '位置纬度',
  `bank_type` int NOT NULL DEFAULT 0 COMMENT '网点类型（清机/尾箱)',
  `have_atm` tinyint NOT NULL DEFAULT 0 COMMENT '是否包含ATM业务',
  `have_box` tinyint NOT NULL DEFAULT 0 COMMENT '是否包含早送晚收业务',
  `have_clear` tinyint NOT NULL DEFAULT 0 COMMENT '是否包含商业清分业务',
  `have_store` tinyint NOT NULL DEFAULT 0 COMMENT '是否包含现金寄库业务',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '机构状态',
  `contact` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系人电话',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `bank_level` int NOT NULL DEFAULT 0 COMMENT '网点类型：1 - 总行，2 - 分行，3 - 支行，4 - 网点, 5 - 库房',
  `bank_category` int NOT NULL DEFAULT 0 COMMENT '机构种类：1 - 营业机构，2 - 非营业机构，3 - 库房',
  `delivery_time` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '早送合同时间',
  `collect_time` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '晚收合同时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '银行机构网点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bank_auth
-- ----------------------------
DROP TABLE IF EXISTS `bank_auth`;
CREATE TABLE `bank_auth`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '授权银行',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '被授权员工',
  `effect_date` bigint NOT NULL DEFAULT 0 COMMENT '生效日期',
  `expire_date` bigint NOT NULL DEFAULT 0 COMMENT '失效日期',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '银行授权登记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bank_teller
-- ----------------------------
DROP TABLE IF EXISTS `bank_teller`;
CREATE TABLE `bank_teller`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属机构',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '归属部门',
  `teller_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '柜员编号',
  `teller_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '柜员姓名',
  `mobile` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `password` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '柜员密码',
  `manager_flag` int NOT NULL DEFAULT 0 COMMENT '管理员标志（0 - 一般柜员，1 - 管理员）',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `emp_id` int NOT NULL DEFAULT 0 COMMENT '库房用户关联员工id',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '银行员工信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for boxpack
-- ----------------------------
DROP TABLE IF EXISTS `boxpack`;
CREATE TABLE `boxpack`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '归属部门',
  `box_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '箱包编号',
  `box_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '箱包名称',
  `box_type` int NOT NULL DEFAULT 0 COMMENT '箱包类型',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属网点',
  `share_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '共用机构',
  `rfid` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'RFID卡号',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '箱包状态: 1 - 网点，2 - 途中，3 - 库房',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '尾箱信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for boxpack_record
-- ----------------------------
DROP TABLE IF EXISTS `boxpack_record`;
CREATE TABLE `boxpack_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '任务编号',
  `bpk_id` int NOT NULL DEFAULT 0 COMMENT '箱包id',
  `warehouse_time` bigint NOT NULL DEFAULT 0 COMMENT '金库交接时间',
  `bank_time` bigint NOT NULL DEFAULT 0 COMMENT '银行交接时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '箱包记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for boxpack_task
-- ----------------------------
DROP TABLE IF EXISTS `boxpack_task`;
CREATE TABLE `boxpack_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `task_no` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务编号',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行网点',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '归属部门',
  `task_type` int NOT NULL DEFAULT 0 COMMENT '任务类型（ 1 - 固定下发，2 - 固定上缴，3 - 临时下发，4 - 临时上缴）',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务备注',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '分配线路',
  `start_time` bigint NOT NULL DEFAULT 0 COMMENT '开始时间',
  `end_time` bigint NOT NULL DEFAULT 0 COMMENT '结束时间',
  `box_list` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '尾箱列表',
  `hand_box_list` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '交接箱包',
  `cash_amount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '上、下介金额',
  `hand_esort_mans` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '交接押运员',
  `hand_esort_mans_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '交接押运员',
  `hand_op_mans` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '交接操作员',
  `hand_op_mans_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '交接操作员',
  `hand_time` bigint NOT NULL DEFAULT 0 COMMENT '交接时间',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '任务状态（ 1 - 已审核，2 - 已完成）',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  `is_overtime` int NOT NULL DEFAULT 0 COMMENT '0：未超时，1：超时',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8565 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '尾箱任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cashbox
-- ----------------------------
DROP TABLE IF EXISTS `cashbox`;
CREATE TABLE `cashbox`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `box_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '编号',
  `rfid` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '关联RFID',
  `box_type` int NOT NULL DEFAULT 0 COMMENT '钞盒钞袋类型（1 - 钞盒，2 - 钞袋）',
  `used` int NOT NULL DEFAULT 0 COMMENT '使用标志（0 -未使用，1 - 已使用）',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态(停用，使用）',
  `pack_id` bigint NOT NULL DEFAULT 0 COMMENT '包装记录id',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_box_no`(`box_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3343 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '钞盒' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cashbox_pack_record
-- ----------------------------
DROP TABLE IF EXISTS `cashbox_pack_record`;
CREATE TABLE `cashbox_pack_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `pack_time` bigint NOT NULL DEFAULT 0 COMMENT '包装日期',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期（入库日期）',
  `clear_man_id` bigint NOT NULL DEFAULT 0 COMMENT '清分员',
  `check_man_id` bigint NOT NULL DEFAULT 0 COMMENT '复核员',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `box_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '钞盒',
  `dev_id` bigint NOT NULL DEFAULT 0 COMMENT '清分机',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '装盒券别',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '包装金额',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态（封装，入库，出库，拆封）',
  `use_count` int NOT NULL DEFAULT 0 COMMENT '使用计数',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '领用线路',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '加钞设备',
  `second_atm_id` bigint NOT NULL DEFAULT 0 COMMENT '第二加钞设备',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `recv_clear_man_id` bigint NOT NULL DEFAULT 0,
  `recv_check_man_id` bigint NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 265823 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '钞盒包装记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cashbox_scan_record
-- ----------------------------
DROP TABLE IF EXISTS `cashbox_scan_record`;
CREATE TABLE `cashbox_scan_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `pack_id` bigint NOT NULL DEFAULT 0 COMMENT '包装id',
  `scan_node` int NOT NULL DEFAULT 0 COMMENT '扫描节点',
  `scan_user` bigint NOT NULL DEFAULT 0 COMMENT '扫描人',
  `scan_time` bigint NOT NULL DEFAULT 0 COMMENT '扫描时间',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 823941 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '钞盒扫描记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for currency
-- ----------------------------
DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `cur_code` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '货币编码，三位字符：CNY,USD',
  `cur_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '货币名称',
  `cur_country` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '流通地区',
  `cur_character` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '符号',
  `cur_index` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '币种信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for denom
-- ----------------------------
DROP TABLE IF EXISTS `denom`;
CREATE TABLE `denom`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `cur_code` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '货币代码',
  `attr` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '物理形态: C - 硬币，P - 纸币',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '券别名称',
  `value` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '面额值',
  `version` tinyint NOT NULL DEFAULT 0 COMMENT '是否包含版别 0 - 不带版别，1 - 带版本',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `wad_size` int NOT NULL DEFAULT 0 COMMENT '每把张数',
  `bundle_size` int NOT NULL DEFAULT 0 COMMENT '每捆张量/每盒枚数',
  `bag_size` int NOT NULL DEFAULT 0 COMMENT '每袋捆数/盒数',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 128 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '券别信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `description` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '部门描述',
  `parent_ids` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '上级部门',
  `linkman_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '负责人姓名',
  `linkman_mobile` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '部门状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '部门信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '归属部门',
  `device_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备编号',
  `model_id` bigint NOT NULL DEFAULT 0 COMMENT '设备型号',
  `factory_id` bigint NOT NULL DEFAULT 0 COMMENT '设备厂商',
  `device_sn` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备出厂SN',
  `location` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备所在位置',
  `ipaddr` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备ip地址',
  `status_t` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备状态 DS01- 正常使用，DS02 - 维修中，DS03 - 报废 DS04=保养',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `assigned` int NOT NULL DEFAULT 0 COMMENT '是否已分配 0 - 未分配，1 - 已分配',
  `enroll_date` bigint NOT NULL DEFAULT 0 COMMENT '购买日期',
  `last_maintain_date` bigint NOT NULL DEFAULT 0 COMMENT '上次维保日期',
  `next_maintain_date` bigint NOT NULL DEFAULT 0 COMMENT '下次维保日期',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '设备信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device_factory
-- ----------------------------
DROP TABLE IF EXISTS `device_factory`;
CREATE TABLE `device_factory`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
  `short_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '供应商简称',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '地址',
  `contact` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '联系人电话',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '设备供应商' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device_maintain
-- ----------------------------
DROP TABLE IF EXISTS `device_maintain`;
CREATE TABLE `device_maintain`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_id` bigint NOT NULL DEFAULT 0 COMMENT '设备编号',
  `mt_date` bigint NOT NULL DEFAULT 0 COMMENT '维保日期',
  `mt_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维保类型',
  `mt_reason` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '故障原因',
  `mt_content` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维保内容',
  `mt_cost` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '维保成本',
  `mt_result` int NOT NULL DEFAULT 0 COMMENT '维保结果: 1 - 维修成功，0- 维修失败',
  `mt_engineer` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维保工程师',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '设备维保记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device_model
-- ----------------------------
DROP TABLE IF EXISTS `device_model`;
CREATE TABLE `device_model`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `factory_id` bigint NOT NULL DEFAULT 0 COMMENT '厂商id',
  `model_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备型号',
  `device_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '设备分类',
  `size` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '规格',
  `speed` int NOT NULL DEFAULT 0 COMMENT '每分钟清点张数',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '设备品牌' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for district
-- ----------------------------
DROP TABLE IF EXISTS `district`;
CREATE TABLE `district`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `district_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '区域名称',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '区域信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '员工工号',
  `emp_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '员工姓名',
  `service_certificate` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务证',
  `manning_quotas` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '编制',
  `affiliated_company` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '所属公司',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `job_ids` bigint NOT NULL DEFAULT 0 COMMENT '岗位',
  `job_type` int NOT NULL DEFAULT 0 COMMENT '岗位类型',
  `title` int NOT NULL DEFAULT 0 COMMENT '职务 0-员工 1-主管',
  `sex` tinyint NOT NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `idno` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '身份证号',
  `nation` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '民族',
  `birthday` bigint NOT NULL DEFAULT 0 COMMENT '出生日期: yyyy-MM-dd 时间戳',
  `marriage` tinyint NOT NULL DEFAULT 0 COMMENT '婚姻: 0 未婚，1 - 已婚， 2 - 离异',
  `education` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '学历',
  `school` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '毕业院校',
  `politic` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '政治面貌',
  `military` tinyint NOT NULL DEFAULT 0 COMMENT '兵役情况: 0 - 未服兵役，1 - 退役',
  `idcard_addr` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '户口所在地详细地址',
  `idcard_district` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '户口所在地-区县',
  `idcard_city` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '户口所在地-城市',
  `idcard_privince` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '户口所在地-省',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '居住地址',
  `status_t` tinyint NOT NULL DEFAULT 0 COMMENT '在职状态：0 - 在职，1 - 离职',
  `entry_date` bigint NOT NULL DEFAULT 0 COMMENT '入职日期',
  `expiration_date` bigint NOT NULL DEFAULT 0 COMMENT '合同到期时间',
  `quit_date` bigint NOT NULL DEFAULT 0 COMMENT '离职日期',
  `mobile` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `wx_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '微信',
  `wx_openid` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '微信openid',
  `photo_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '照片',
  `contact_mobile` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '紧急联系人手机',
  `contact_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '紧急联系人姓名',
  `contact_relationship` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '紧急联系人关系',
  `guarantor_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '担保人姓名',
  `guarantor_mobile` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '担保人电话',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `next_visit_date` bigint NOT NULL DEFAULT 0 COMMENT '下次家访时间',
  `pda_enable` tinyint NOT NULL DEFAULT 0 COMMENT 'PDA登录权限 0:无 1:有',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `pda_admin` int NOT NULL DEFAULT 0 COMMENT '是否PDA系统管理员',
  `route_leader` int NOT NULL DEFAULT 0 COMMENT '车长资格（0 - 不具备车长资格，1 - 具备车长资格）',
  `role_id` bigint NOT NULL DEFAULT 0 COMMENT 'PDA角色编号',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建用户',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新用户',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 818 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '员工信息 ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee_awards
-- ----------------------------
DROP TABLE IF EXISTS `employee_awards`;
CREATE TABLE `employee_awards`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `awards_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '奖惩类型',
  `awards_date` bigint NOT NULL DEFAULT 0 COMMENT '奖惩时间',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '奖惩记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee_job
-- ----------------------------
DROP TABLE IF EXISTS `employee_job`;
CREATE TABLE `employee_job`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '岗位名称',
  `job_type` int NOT NULL DEFAULT 0 COMMENT '岗位类型  0: 其它 1: 司机  2: 护卫 3: 钥匙 4: 清机 5: 清分 6: 库房',
  `descript` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '岗位描述',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '员工岗位信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee_training_record
-- ----------------------------
DROP TABLE IF EXISTS `employee_training_record`;
CREATE TABLE `employee_training_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `train_id` bigint NOT NULL DEFAULT 0 COMMENT '培训主题id',
  `score` float NOT NULL DEFAULT 0 COMMENT '考核分数',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '培训记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee_training_subject
-- ----------------------------
DROP TABLE IF EXISTS `employee_training_subject`;
CREATE TABLE `employee_training_subject`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `train_date` bigint NOT NULL DEFAULT 0 COMMENT '培训日期',
  `train_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '培训类别',
  `train_title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '培训主题',
  `train_content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '培训内容',
  `trainer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '培训老师',
  `place` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '培训地点',
  `times` int NOT NULL DEFAULT 0 COMMENT '培训时长',
  `test` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '考核方式',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '修改时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '培训主题' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee_visit
-- ----------------------------
DROP TABLE IF EXISTS `employee_visit`;
CREATE TABLE `employee_visit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `visit_date` bigint NOT NULL DEFAULT 0 COMMENT '家访日期',
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '家访内容',
  `visiters` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '家访人员',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '家访记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_company
-- ----------------------------
DROP TABLE IF EXISTS `file_company`;
CREATE TABLE `file_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '单位号',
  `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '单位名称',
  `company_type` tinyint NOT NULL DEFAULT 0 COMMENT '单位类别 0-公司 1-银行',
  `status_t` tinyint NOT NULL DEFAULT 0 COMMENT '单位状态',
  `file_directory` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件目录',
  `user_ids` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '操作员',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '单位管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for file_record
-- ----------------------------
DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '发送人',
  `send_company_id` bigint NOT NULL DEFAULT 0 COMMENT '发送单位',
  `company_id` bigint NOT NULL DEFAULT 0 COMMENT '接收单位',
  `record_title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
  `file_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件类型',
  `file_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件地址',
  `read_sign` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1449 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件传输记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for fingerprint
-- ----------------------------
DROP TABLE IF EXISTS `fingerprint`;
CREATE TABLE `fingerprint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `user_type` int NOT NULL DEFAULT 0 COMMENT '人员类型 0：员工 1：柜员',
  `finger_idx` int NOT NULL DEFAULT 0 COMMENT '手指序号',
  `finger_print` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '指纹特征',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 492 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '指纹特征' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun
-- ----------------------------
DROP TABLE IF EXISTS `gun`;
CREATE TABLE `gun`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `gun_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '枪号/弹盒号',
  `gun_licence_id` bigint NOT NULL DEFAULT 0 COMMENT '持枪证号',
  `internal_num` bigint UNSIGNED NOT NULL COMMENT '内部编号',
  `buy_date` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '配发日期',
  `gun_category` bigint NOT NULL DEFAULT 0 COMMENT '枪械类型，枪盒默认为0',
  `gun_status` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '1：库存\r\n2：已发出\r\n3：禁用',
  `remark` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `department_id` int NOT NULL,
  `user_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '出勤次数',
  `is_clean` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '擦拭标志位   \r\n0：未擦拭\r\n1：已擦拭',
  `is_check` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '分解标志位  0：未分解1：已分解',
  `clean_date` bigint NULL DEFAULT 0 COMMENT '擦拭日期',
  `check_date` bigint NULL DEFAULT 0 COMMENT '分解日期',
  `create_user` bigint NULL DEFAULT 0 COMMENT '操作员id',
  `operator_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '操作员名',
  `create_time` bigint NOT NULL DEFAULT 0,
  `update_user` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_category
-- ----------------------------
DROP TABLE IF EXISTS `gun_category`;
CREATE TABLE `gun_category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `gun_type` tinyint NOT NULL DEFAULT 1 COMMENT '1:枪支，2：弹盒',
  `gun_category_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `department_id` int NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_user` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `update_user` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_licence
-- ----------------------------
DROP TABLE IF EXISTS `gun_licence`;
CREATE TABLE `gun_licence`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `gun_licence_num` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '持枪证号',
  `gun_licence_validity` bigint NOT NULL DEFAULT 0 COMMENT '持枪证有效期',
  `gun_licence_photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '证件图片地址',
  `create_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_time` bigint NULL DEFAULT 0,
  `update_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `update_time` bigint NULL DEFAULT 0,
  `licence_status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '证件管理状态',
  `department_id` int NOT NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_maintain_record
-- ----------------------------
DROP TABLE IF EXISTS `gun_maintain_record`;
CREATE TABLE `gun_maintain_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `operate_time` bigint NOT NULL,
  `operate_type` tinyint NOT NULL COMMENT ' 1：擦拭  2：分解',
  `gun_id` bigint NOT NULL,
  `operator` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `department_id` int NOT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `create_user` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  `update_user` bigint NULL DEFAULT NULL,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_security
-- ----------------------------
DROP TABLE IF EXISTS `gun_security`;
CREATE TABLE `gun_security`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `security_num` varchar(25) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '保安证号',
  `security_photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '证件图片地址',
  `authority` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '发证机构',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `status` int NOT NULL DEFAULT 1,
  `create_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_time` bigint NULL DEFAULT 0,
  `update_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `update_time` bigint NULL DEFAULT 0,
  `department_id` int NOT NULL,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_summary
-- ----------------------------
DROP TABLE IF EXISTS `gun_summary`;
CREATE TABLE `gun_summary`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `gun_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '枪弹名称',
  `gun_num` bigint UNSIGNED NOT NULL COMMENT '编号',
  `total` int NOT NULL DEFAULT 0 COMMENT '数量',
  `gun_type` int NOT NULL DEFAULT 1 COMMENT '1：枪支；2：弹盒',
  `remark` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `operater_name` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '操作员名',
  `create_user` bigint NULL DEFAULT 0,
  `create_time` bigint NULL DEFAULT 0,
  `update_user` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gun_task
-- ----------------------------
DROP TABLE IF EXISTS `gun_task`;
CREATE TABLE `gun_task`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `supercargo_id_a` bigint NULL DEFAULT NULL COMMENT '押运员Aid',
  `supercargo_name_a` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '押运员A姓名',
  `gun_code_a` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪支编号A',
  `gun_box_code_a` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪盒A',
  `gun_licence_num_a` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪证编号A',
  `gun_license_out_a` tinyint(1) NULL DEFAULT 1 COMMENT '枪证A（发枪）状态 1：未发  2：已发',
  `gun_license_in_a` tinyint(1) NULL DEFAULT 1 COMMENT '枪证A（收枪）状态 1：未收 2：已收',
  `supercargo_id_b` bigint NULL DEFAULT NULL COMMENT '押运员Aid',
  `supercargo_name_b` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '押运员B姓名',
  `gun_code_b` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪支编号B',
  `gun_box_code_b` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪盒A',
  `gun_licence_num_b` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪证编号B',
  `gun_license_out_b` tinyint(1) NULL DEFAULT 1 COMMENT '枪证B（发枪）状态 1：未发  2：已发',
  `gun_license_in_b` tinyint(1) NULL DEFAULT 1 COMMENT '枪证B（收枪）状态 1：未收 2：已收',
  `schd_id` bigint NOT NULL DEFAULT 0 COMMENT '调度结果id',
  `line_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `line_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '线路名称',
  `car_no` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '车辆号码',
  `plan_time` bigint NULL DEFAULT 0 COMMENT '计划发枪时间',
  `task_status` tinyint NOT NULL DEFAULT 1 COMMENT '任务状态 1：未发枪、2：已发枪、3：已完成、4：取消',
  `take_out_time` bigint NULL DEFAULT 0 COMMENT '实际发枪时间',
  `take_in_time` bigint NULL DEFAULT 0 COMMENT '收枪时间',
  `gun_li_not_return_remark` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '枪证未返还备注',
  `operator_name` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '操作员',
  `department_id` int NOT NULL,
  `line_auditor` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路审核员',
  `create_user` bigint NULL DEFAULT 0,
  `create_time` bigint NULL DEFAULT 0,
  `update_user` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for handover_log
-- ----------------------------
DROP TABLE IF EXISTS `handover_log`;
CREATE TABLE `handover_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '事业部ID',
  `title` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `contents` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `image_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '图片',
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '附件文件',
  `create_user` bigint NOT NULL DEFAULT 0,
  `create_time` bigint NOT NULL DEFAULT 0,
  `update_user` bigint NOT NULL DEFAULT 0,
  `update_time` bigint NOT NULL DEFAULT 0,
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for important_voucher
-- ----------------------------
DROP TABLE IF EXISTS `important_voucher`;
CREATE TABLE `important_voucher`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `bank_id` bigint NOT NULL COMMENT '所属银行id',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '重空名称',
  `type` tinyint NOT NULL DEFAULT 3 COMMENT '重空类型   1：卡类 2：贵金属 3：其他凭证类',
  `create_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_time` bigint NULL DEFAULT 0,
  `update_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `update_time` bigint NULL DEFAULT 0,
  `department_id` int NOT NULL DEFAULT 10,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for important_voucher_number_segment
-- ----------------------------
DROP TABLE IF EXISTS `important_voucher_number_segment`;
CREATE TABLE `important_voucher_number_segment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `voucher_id` bigint NOT NULL,
  `type` tinyint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `number_start` bigint NOT NULL COMMENT '号段起始',
  `number_end` bigint NOT NULL COMMENT '号段结束',
  `count` bigint NOT NULL COMMENT '当前号段数量',
  `create_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_time` bigint NULL DEFAULT 0,
  `update_user` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `update_time` bigint NULL DEFAULT 0,
  `department_id` int NOT NULL DEFAULT 10,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for important_voucher_warehouse_item
-- ----------------------------
DROP TABLE IF EXISTS `important_voucher_warehouse_item`;
CREATE TABLE `important_voucher_warehouse_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `voucher_id` bigint NOT NULL DEFAULT 0,
  `bank_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '凭证所属银行',
  `operate_type` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '操作类型',
  `voucher_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '凭证类型',
  `target_bank_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '出库网点',
  `voucher_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '凭证名称',
  `start` bigint NOT NULL DEFAULT 0 COMMENT '起始号码',
  `end` bigint NOT NULL COMMENT '结束号码',
  `count` bigint NOT NULL DEFAULT 0 COMMENT '数量',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0 COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT 0,
  `is_confirm` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1为已审核，0为未审核',
  `create_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `order_number` varchar(25) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `confirm_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '审核人',
  `confirm_time` bigint NULL DEFAULT 0 COMMENT '审核时间',
  `cancel_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `cancel_time` bigint NULL DEFAULT 0 COMMENT '反审核时间',
  `task_id` bigint NULL DEFAULT 0,
  `task_date` bigint NULL DEFAULT 0 COMMENT '任务时间',
  `task_line` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '任务路线',
  `task_cashbox` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '任务箱包',
  `department_id` int NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for important_voucher_warehouse_record
-- ----------------------------
DROP TABLE IF EXISTS `important_voucher_warehouse_record`;
CREATE TABLE `important_voucher_warehouse_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '申请记录类型：编辑、审批、反审、删除',
  `bank_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `target_bank_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `voucher_id` bigint NOT NULL,
  `operate_type` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `voucher_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `voucher_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `start` bigint NOT NULL DEFAULT 0,
  `end` bigint NOT NULL DEFAULT 0,
  `count` bigint NOT NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_time` bigint NULL DEFAULT 0,
  `update_time` bigint NULL DEFAULT 0,
  `update_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  `create_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0',
  `confirm_time` bigint NULL DEFAULT 0,
  `create_user` bigint NULL DEFAULT NULL,
  `is_confirm` tinyint(1) NOT NULL DEFAULT 0,
  `department_id` int NOT NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_push
-- ----------------------------
DROP TABLE IF EXISTS `message_push`;
CREATE TABLE `message_push`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_date` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '任务日期',
  `lpno` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车牌号',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '人员姓名',
  `route_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路',
  `change_man` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '人员变动信息',
  `driver` bigint NOT NULL DEFAULT 0 COMMENT '司机',
  `security_a` bigint NOT NULL DEFAULT 0 COMMENT '护卫A',
  `security_b` bigint NOT NULL DEFAULT 0 COMMENT '护卫B',
  `route_key_man` bigint NOT NULL DEFAULT 0 COMMENT '业务-钥匙员',
  `route_oper_man` bigint NOT NULL DEFAULT 0 COMMENT '业务-清机员',
  `type` int NULL DEFAULT 1 COMMENT '消息类型 1 排班线路 2人员调整',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `comments` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `change_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '变更类型',
  `bank` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '任务网点',
  `device` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '任务设备',
  `op_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '任务类型',
  `amount` decimal(15, 2) NULL DEFAULT 0.00 COMMENT '加钞金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1696010674544656387 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '消息推送信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_check_record
-- ----------------------------
DROP TABLE IF EXISTS `order_check_record`;
CREATE TABLE `order_check_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `order_id` bigint NOT NULL DEFAULT 0 COMMENT '订单id',
  `order_type` int NOT NULL DEFAULT 0 COMMENT '订单类型',
  `check_user` bigint NOT NULL DEFAULT 0 COMMENT '审核人',
  `check_role` bigint NOT NULL DEFAULT 0 COMMENT '审核角色id',
  `check_time` bigint NOT NULL DEFAULT 0 COMMENT '审核时间',
  `check_result` int NOT NULL DEFAULT 0 COMMENT '状态：0 - 不通过、1 - 通过',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '出入库单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pda
-- ----------------------------
DROP TABLE IF EXISTS `pda`;
CREATE TABLE `pda`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `tersn` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '终端编号',
  `mac` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '物理地址',
  `use_type` int NOT NULL DEFAULT 0 COMMENT '用途 0: 公司 1: 银行',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属机构',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'PDA终端信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pda_record
-- ----------------------------
DROP TABLE IF EXISTS `pda_record`;
CREATE TABLE `pda_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `ter_id` bigint NOT NULL DEFAULT 0 COMMENT '终端编号',
  `op_type` int NOT NULL DEFAULT 0 COMMENT '操作类型(借出/归还)',
  `use_man` int NOT NULL DEFAULT 0 COMMENT '使用人',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'PDA领用记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pda_user
-- ----------------------------
DROP TABLE IF EXISTS `pda_user`;
CREATE TABLE `pda_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `user_type` int NOT NULL DEFAULT 0 COMMENT '用户类型',
  `user_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '用户名称',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '关联员工id',
  `bank_teller_id` bigint NOT NULL DEFAULT 0 COMMENT '关联柜员id',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'PDA终端信息 pda用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route
-- ----------------------------
DROP TABLE IF EXISTS `route`;
CREATE TABLE `route`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `route_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路别名',
  `route_number` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路唯一编号',
  `route_type` int NOT NULL DEFAULT 0 COMMENT '线路类型 0: 固定线路 1: 临时线路',
  `template_type` int NOT NULL DEFAULT 0 COMMENT '模板类型',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `vehicle_id` int NOT NULL DEFAULT 0 COMMENT '分配车辆',
  `route_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `plan_begin_time` bigint NOT NULL DEFAULT 0 COMMENT '计划开始时间',
  `plan_finish_time` bigint NOT NULL DEFAULT 0 COMMENT '计划结束时间',
  `act_begin_time` bigint NOT NULL DEFAULT 0 COMMENT '实际开始时间',
  `act_finish_time` bigint NOT NULL DEFAULT 0 COMMENT '实际结束时间',
  `driver` bigint NOT NULL DEFAULT 0 COMMENT '司机',
  `security_a` bigint NOT NULL DEFAULT 0 COMMENT '护卫A',
  `security_b` bigint NOT NULL DEFAULT 0 COMMENT '护卫B',
  `route_key_man` bigint NOT NULL DEFAULT 0 COMMENT '业务-钥匙员',
  `route_oper_man` bigint NOT NULL DEFAULT 0 COMMENT '业务-清机员',
  `follower` bigint NOT NULL DEFAULT 0 COMMENT '跟车人员',
  `disp_oper_man` bigint NOT NULL DEFAULT 0 COMMENT '配钞-操作员',
  `disp_check_man` bigint NOT NULL DEFAULT 0 COMMENT '配钞-复核员',
  `disp_time` bigint NOT NULL DEFAULT 0 COMMENT '配钞时间',
  `disp_cfm_time` bigint NOT NULL DEFAULT 0 COMMENT '配钞复核时间',
  `disp_box_list` varchar(3072) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '钞盒列表',
  `disp_bag_count` int NOT NULL DEFAULT 0 COMMENT '钞袋数量',
  `hdover_oper_man` bigint NOT NULL DEFAULT 0 COMMENT '车间交接-操作员',
  `hdover_check_man` bigint NOT NULL DEFAULT 0 COMMENT '车间交接-复核员',
  `hdover_time` bigint NOT NULL DEFAULT 0 COMMENT '车间交接时间',
  `return_box_count` int NOT NULL DEFAULT 0 COMMENT '交接钞盒数量',
  `return_bag_count` int NOT NULL DEFAULT 0 COMMENT '交接钞袋数量',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '线路状态',
  `task_total` int NOT NULL DEFAULT 0 COMMENT '总任务数',
  `task_finish` int NOT NULL DEFAULT 0 COMMENT '完成任务数',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `check_user` bigint NOT NULL DEFAULT 0 COMMENT '审核人',
  `check_time` bigint NOT NULL DEFAULT 0 COMMENT '审核时间',
  `handover_change` tinyint NOT NULL DEFAULT 0 COMMENT '交接调整标志 0-无 1-有',
  `emp_change` tinyint NOT NULL DEFAULT 0 COMMENT '人员调整标志 0-无 1-有',
  `leader_log` tinyint NOT NULL DEFAULT 0 COMMENT '车长日志标志',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  `push_notice` tinyint NOT NULL DEFAULT 0 COMMENT '排班推送通知',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19438 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '线路任务记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_boxbag_change
-- ----------------------------
DROP TABLE IF EXISTS `route_boxbag_change`;
CREATE TABLE `route_boxbag_change`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `change_type` int NOT NULL DEFAULT 0 COMMENT '变更类型 0-钞盒 1-钞袋',
  `old_count` int NOT NULL DEFAULT 0 COMMENT '变更数目',
  `new_count` int NOT NULL DEFAULT 0 COMMENT '变更后数目',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '线路人员调整记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for route_emp_change
-- ----------------------------
DROP TABLE IF EXISTS `route_emp_change`;
CREATE TABLE `route_emp_change`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `job_type` int NOT NULL DEFAULT 0 COMMENT '岗位类型',
  `old_man_id` bigint NOT NULL DEFAULT 0 COMMENT '变更前人员',
  `new_man_id` bigint NOT NULL DEFAULT 0 COMMENT '变更后人员',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 825 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '线路人员调整记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_log
-- ----------------------------
DROP TABLE IF EXISTS `route_log`;
CREATE TABLE `route_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '线路id',
  `detail` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '记录详情（子项:结果:备注）',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '其他问题',
  `result` tinyint NOT NULL DEFAULT 0 COMMENT '日志是否正常（1 - 正常，0 - 异常）',
  `leader` bigint NOT NULL DEFAULT 0 COMMENT '当值车长',
  `create_time` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14157 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '线路车长日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_template
-- ----------------------------
DROP TABLE IF EXISTS `route_template`;
CREATE TABLE `route_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `route_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路名称',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `route_type` int NOT NULL DEFAULT 0 COMMENT '线路类型 0-其他线路 1-60/61号线 2-8号线',
  `rule` int NOT NULL DEFAULT 0 COMMENT '线路生成规则 0-每天 1-隔天',
  `sign` int NOT NULL DEFAULT 0 COMMENT '线路生成标志  0-生成  1-不生成',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `plan_begin_time` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '计划开始时间',
  `plan_finish_time` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '计划结束时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '线路规划模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_template_atm
-- ----------------------------
DROP TABLE IF EXISTS `route_template_atm`;
CREATE TABLE `route_template_atm`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `route_template_id` bigint NOT NULL DEFAULT 0 COMMENT '线路模板id',
  `atm_id` bigint NOT NULL DEFAULT 0 COMMENT '设备id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `sub_bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属网点',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_template_bank
-- ----------------------------
DROP TABLE IF EXISTS `route_template_bank`;
CREATE TABLE `route_template_bank`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `route_template_id` bigint NOT NULL DEFAULT 0 COMMENT '线路模板id',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路编号',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '途径网点',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '尾箱线路规划' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for route_template_record
-- ----------------------------
DROP TABLE IF EXISTS `route_template_record`;
CREATE TABLE `route_template_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_id` bigint NULL DEFAULT 0 COMMENT '部门ID',
  `route_date` bigint NULL DEFAULT 0 COMMENT '线路日期时间',
  `op_type` tinyint NULL DEFAULT 0 COMMENT '操作类型 0-创建 1-修改',
  `create_user` bigint UNSIGNED NULL DEFAULT 0 COMMENT '创建用户',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NULL DEFAULT 0 COMMENT '修改时间',
  `content` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '修改内容',
  `state` tinyint NULL DEFAULT 0 COMMENT '状态 0-未推送  1=已推送',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1499 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_alternate_assign
-- ----------------------------
DROP TABLE IF EXISTS `schd_alternate_assign`;
CREATE TABLE `schd_alternate_assign`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门id',
  `plan_id` bigint NOT NULL DEFAULT 0 COMMENT '计划ID',
  `plan_type` int NOT NULL DEFAULT 0 COMMENT '排班方式（0 - 清机司机， 1- 护卫）',
  `alternate_type` int NOT NULL DEFAULT 0 COMMENT '主备版类型（ 0 - 主备，1 - 替班）',
  `employee_id` bigint NOT NULL DEFAULT 0 COMMENT '员工ID',
  `route_ids` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '对应线路字符串，逗号分隔',
  `vehicle_nos` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车牌号码,逗号分隔',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 635 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '司机、护卫主替班配置数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_clean_pass_auth
-- ----------------------------
DROP TABLE IF EXISTS `schd_clean_pass_auth`;
CREATE TABLE `schd_clean_pass_auth`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构（一级机构）',
  `pass_type` int NOT NULL DEFAULT 0 COMMENT '授权类型 0 - 总行清机通行码， 1 - 网点出入授权',
  `pass_code` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '通行证编码',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 151 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清机通行证授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_clean_pass_auth1
-- ----------------------------
DROP TABLE IF EXISTS `schd_clean_pass_auth1`;
CREATE TABLE `schd_clean_pass_auth1`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构（一级机构）',
  `pass_type` int NOT NULL DEFAULT 0 COMMENT '授权类型 0 - 总行清机通行码， 1 - 网点出入授权',
  `pass_code` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '通行证编码',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清机通行证授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_clean_pass_auth_copy_copy
-- ----------------------------
DROP TABLE IF EXISTS `schd_clean_pass_auth_copy_copy`;
CREATE TABLE `schd_clean_pass_auth_copy_copy`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构（一级机构）',
  `pass_type` int NOT NULL DEFAULT 0 COMMENT '授权类型 0 - 总行清机通行码， 1 - 网点出入授权',
  `pass_code` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '通行证编码',
  `emp_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `city` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清机通行证授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_drive_restrict
-- ----------------------------
DROP TABLE IF EXISTS `schd_drive_restrict`;
CREATE TABLE `schd_drive_restrict`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `weekday` int NOT NULL COMMENT '星期几',
  `begin_time_am` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '早高峰开始时间',
  `end_time_am` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '早高峰结束时间',
  `begin_time_pm` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '晚高峰开始时间',
  `end_time_pm` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '晚高峰结束时间',
  `forbid_numbers` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '限行尾号',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '工作日汽车尾号限行规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_drive_restrict_xh
-- ----------------------------
DROP TABLE IF EXISTS `schd_drive_restrict_xh`;
CREATE TABLE `schd_drive_restrict_xh`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `day_type` int NOT NULL DEFAULT 0 COMMENT '单双日（0 - 双日，1 - 单日）',
  `begin_time` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '开始时间',
  `end_time` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '0' COMMENT '结束时间',
  `permit_numbers` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '允许尾号',
  `effect_routes` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '影响线路组',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '假期西湖限行规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_driver_assign
-- ----------------------------
DROP TABLE IF EXISTS `schd_driver_assign`;
CREATE TABLE `schd_driver_assign`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `driver` bigint NOT NULL DEFAULT 0 COMMENT '司机',
  `driver_type` int NOT NULL DEFAULT 0 COMMENT '主备版类型（ 0 - 主班，1 - 替班）',
  `route_id` bigint NOT NULL DEFAULT 0 COMMENT '对应线路',
  `vehicle_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车牌号码',
  `assign_type` int NOT NULL DEFAULT 0 COMMENT '排班方式（0 - 随机， 1- 固定）',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '部门id',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '司机主替班配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_holiday_plan
-- ----------------------------
DROP TABLE IF EXISTS `schd_holiday_plan`;
CREATE TABLE `schd_holiday_plan`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `plan_day` bigint NOT NULL DEFAULT 0 COMMENT '计划日期',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '说明',
  `holiday_type` int NOT NULL DEFAULT 0 COMMENT '假期类型（0 - 放假，1 - 补班）',
  `weekday` int NOT NULL COMMENT '星期几（如果是补班，对应补哪天的班）',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '放假，调休安排' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_result
-- ----------------------------
DROP TABLE IF EXISTS `schd_result`;
CREATE TABLE `schd_result`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 10 COMMENT '部门ID',
  `plan_day` bigint NOT NULL DEFAULT 0 COMMENT '计划日期',
  `route_type` int NOT NULL DEFAULT 0 COMMENT '线路类型 0-其他线路 1-60/61号线 2-8号线',
  `route_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '线路组别',
  `vehicle_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车牌号码',
  `driver` bigint NOT NULL DEFAULT 0 COMMENT '司机',
  `scurity_a` bigint NOT NULL DEFAULT 0 COMMENT '护卫1',
  `scurity_b` bigint NOT NULL DEFAULT 0 COMMENT '护卫2',
  `key_man` bigint NOT NULL DEFAULT 0 COMMENT '钥匙员',
  `op_man` bigint NOT NULL DEFAULT 0 COMMENT '密码操作员',
  `leader` bigint NOT NULL DEFAULT 0 COMMENT '车长',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22265 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '排班结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_result_record
-- ----------------------------
DROP TABLE IF EXISTS `schd_result_record`;
CREATE TABLE `schd_result_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_id` bigint NULL DEFAULT 0 COMMENT '部门ID',
  `plan_day` bigint NULL DEFAULT 0 COMMENT '排班时间',
  `category` tinyint NULL DEFAULT NULL COMMENT '类别 0-系统生成 1-手动修改',
  `op_type` tinyint NULL DEFAULT 0 COMMENT '操作类型 0-创建 1-修改',
  `create_user` bigint UNSIGNED NULL DEFAULT 0 COMMENT '创建用户',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint NULL DEFAULT 0 COMMENT '修改时间',
  `content` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '修改内容',
  `state` tinyint NULL DEFAULT NULL COMMENT '状态 0-未推送  1=已推送',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5133 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_subbank_auth
-- ----------------------------
DROP TABLE IF EXISTS `schd_subbank_auth`;
CREATE TABLE `schd_subbank_auth`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '姓名',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '登记网点',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '网点出入授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_vacate_adjust
-- ----------------------------
DROP TABLE IF EXISTS `schd_vacate_adjust`;
CREATE TABLE `schd_vacate_adjust`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `adjust_date` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '调整日期（yyyy-mm-dd)',
  `adjust_type` int NOT NULL DEFAULT 0 COMMENT '调整类别：0 - 休假，1 - 加班，2 - 替班',
  `plan_type` int NOT NULL DEFAULT 0 COMMENT '岗位类型 0=清机 1=护卫',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '休假、加班、替班员工',
  `rep_emp_id` bigint NOT NULL DEFAULT 0 COMMENT '替班者（adjust_type = 替班时存在）',
  `reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '调班原因',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '员工休息调整' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_vacate_plan
-- ----------------------------
DROP TABLE IF EXISTS `schd_vacate_plan`;
CREATE TABLE `schd_vacate_plan`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '计划名称',
  `plan_type` int NOT NULL DEFAULT 0 COMMENT '计划类型（0 -清机司机，1 - 护卫）',
  `begin_date` bigint NOT NULL DEFAULT 0 COMMENT '计划开始时间',
  `end_date` bigint NOT NULL DEFAULT 0 COMMENT '计划结束时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '员工休息计划设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schd_vacate_setting
-- ----------------------------
DROP TABLE IF EXISTS `schd_vacate_setting`;
CREATE TABLE `schd_vacate_setting`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `plan_id` bigint NOT NULL DEFAULT 0 COMMENT '计划id',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工id',
  `vacate_days` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '休息日',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1272 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '员工休息管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '编号',
  `groups` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '分类名称',
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `parent_code` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '父级编号',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '添加人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '添加时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '数据字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `user_id` bigint NULL DEFAULT 0 COMMENT '管理员',
  `ip` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '管理员地址',
  `type_t` int NULL DEFAULT 0 COMMENT '操作分类',
  `action` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '操作地址',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '操作内容',
  `result` varchar(127) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '操作结果',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 183715 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
  `pid` bigint NOT NULL DEFAULT 0 COMMENT '父级菜单',
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '地址',
  `level` int NOT NULL DEFAULT 0 COMMENT '层级',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `menu_type` int NOT NULL DEFAULT 0 COMMENT '菜单类型 0：管理系统  1：PDA',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 656 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '系统菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `role_id` bigint NOT NULL DEFAULT 0 COMMENT '角色ID',
  `permission` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '权限',
  `permission_type` int NOT NULL DEFAULT 0 COMMENT '权限类型 0：系统权限 1：PDA权限',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70014 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_qztask
-- ----------------------------
DROP TABLE IF EXISTS `sys_qztask`;
CREATE TABLE `sys_qztask`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `task_cron` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `task_class_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '类路径',
  `task_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `task_description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '描述',
  `task_enable` int NOT NULL DEFAULT 0 COMMENT '1/0   启动/关闭',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '定时任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `role_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `describes` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色描述',
  `enabled` tinyint NOT NULL DEFAULT 0 COMMENT '是否启用',
  `role_type` int NOT NULL DEFAULT 0 COMMENT '角色类型 0：系统角色 1：PDA角色',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_upload_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_upload_log`;
CREATE TABLE `sys_upload_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `data_date` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '数据日期',
  `data_type` int NOT NULL DEFAULT 0 COMMENT '数据表类型',
  `file_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内部文件名称',
  `uuid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '文件唯一序列号',
  `count` int NOT NULL DEFAULT 0 COMMENT '记录个数',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '上传结果：0 - 失败，1-成功',
  `upload_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `protect` int NOT NULL DEFAULT 0 COMMENT '是否密码保护',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21981 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '数据同步日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_upload_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_upload_setting`;
CREATE TABLE `sys_upload_setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `table_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '上传表名',
  `table_text` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '表中文名称',
  `table_type` int NOT NULL DEFAULT 0 COMMENT '表类型字段',
  `is_all` int NOT NULL DEFAULT 0 COMMENT '是否全量上传（0 - 增量，1 - 全量）',
  `data_date` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '数据的日期',
  `is_enable` int NOT NULL DEFAULT 0 COMMENT '是否上传',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '数据同步配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属部门',
  `top_department_id` bigint NOT NULL DEFAULT 0 COMMENT '顶级部门',
  `auth_departments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '数据权限部门',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '所属银行',
  `stock_bank` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '库存网点',
  `username` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `password` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `nick_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '用户姓名',
  `role_ids` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色列表',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '状态',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_whitelist
-- ----------------------------
DROP TABLE IF EXISTS `sys_whitelist`;
CREATE TABLE `sys_whitelist`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `ip_remarks` varchar(63) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '白名单ip备注',
  `ip_address` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '白名单IP',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `mac_address` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'mac地址白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '白名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for temp
-- ----------------------------
DROP TABLE IF EXISTS `temp`;
CREATE TABLE `temp`  (
  `1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `3` bigint NULL DEFAULT NULL,
  `5` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `6` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `7` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for temps
-- ----------------------------
DROP TABLE IF EXISTS `temps`;
CREATE TABLE `temps`  (
  `1` bigint NULL DEFAULT NULL,
  `2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `4` int NULL DEFAULT NULL,
  `5` bigint NULL DEFAULT NULL,
  `6` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_clear_charge_rule
-- ----------------------------
DROP TABLE IF EXISTS `trade_clear_charge_rule`;
CREATE TABLE `trade_clear_charge_rule`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属事业部',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '机构id',
  `gb_flag` int NOT NULL DEFAULT 0 COMMENT '券别类型：1 - 完整券，2 - 残损券',
  `attr` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '物理形态: C - 硬币，P - 纸币',
  `denom` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '票面面额',
  `price` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '收费规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_clear_error
-- ----------------------------
DROP TABLE IF EXISTS `trade_clear_error`;
CREATE TABLE `trade_clear_error`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属事业部',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行支行',
  `sub_bank` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '差错支行网点名称',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别id',
  `clear_date` bigint NOT NULL DEFAULT 0 COMMENT '发现日期/清分日期',
  `cash_over_count` int NOT NULL DEFAULT 0 COMMENT '长款笔数',
  `cash_over_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '长款金额',
  `cash_short_count` int NOT NULL DEFAULT 0 COMMENT '短款笔数',
  `cash_short_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '短款金额',
  `fake_count` int NOT NULL DEFAULT 0 COMMENT '假币笔数',
  `fake_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '假币金额',
  `carry_count` int NOT NULL DEFAULT 0 COMMENT '夹张笔数',
  `carry_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '夹张金额',
  `error_man` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '错款人',
  `clear_man` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '发现人',
  `check_man` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '复核人',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：-1 - 已取消，0 - 已创建，1 - 已确认',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `confirm_time` bigint NOT NULL DEFAULT 0 COMMENT '确认时间',
  `confirm_user` bigint NOT NULL DEFAULT 0 COMMENT '确认人',
  `check_comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `cancel_time` bigint NOT NULL DEFAULT 0 COMMENT '撤销时间',
  `cancel_user` bigint NOT NULL DEFAULT 0 COMMENT '撤销人',
  `seal_date` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '封签日期',
  `seal_man` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '封签人',
  `create_day` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '创建日期',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分差错' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_clear_result_record
-- ----------------------------
DROP TABLE IF EXISTS `trade_clear_result_record`;
CREATE TABLE `trade_clear_result_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '事业部ID',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '区域清分任务id',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `gb_flag` int NOT NULL DEFAULT 0 COMMENT '清分标志：0 - 可用券，1 - 残损券，2 - 五好券, 3 - 未清分, 4 - 尾零钞',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `count` int NOT NULL DEFAULT 1 COMMENT '张数',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_denom`(`denom_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分结果明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_clear_task
-- ----------------------------
DROP TABLE IF EXISTS `trade_clear_task`;
CREATE TABLE `trade_clear_task`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '事业部ID',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '机构id',
  `clear_type` tinyint NOT NULL DEFAULT 0 COMMENT '清分类型： 1 - 领现， 2 - 尾箱， 3 - 回笼',
  `have_detail` tinyint NOT NULL DEFAULT 0 COMMENT '有无明细： 0 - 无， 1 - 有',
  `total_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '任务金额',
  `reality_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '实际清分金额',
  `task_date` bigint NOT NULL DEFAULT 0 COMMENT '任务日期',
  `status` int NOT NULL DEFAULT 0 COMMENT '任务状态：-1 - 已取消， 0 - 已创建，1 - 已确认， 2 - 已完成',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `confirm_user` bigint NOT NULL DEFAULT 0 COMMENT '确认人',
  `confirm_time` bigint NOT NULL DEFAULT 0 COMMENT '确认时间',
  `cancel_user` bigint NOT NULL DEFAULT 0 COMMENT '撤销人',
  `cancel_time` bigint NOT NULL DEFAULT 0 COMMENT '撤销时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0,
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '修改人',
  `update_time` bigint NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_date_bank`(`task_date` ASC, `bank_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_clear_task_record
-- ----------------------------
DROP TABLE IF EXISTS `trade_clear_task_record`;
CREATE TABLE `trade_clear_task_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '事业部ID',
  `task_id` bigint NOT NULL DEFAULT 0 COMMENT '区域清分任务id',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `gb_flag` int NOT NULL DEFAULT 0 COMMENT '清分标志：0 - 可用券，1 - 残损券，2 - 五好券, 3 - 未清分, 4 - 尾零钞',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `count` int NOT NULL DEFAULT 1 COMMENT '张数',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_denom`(`denom_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '清分任务明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for traffic_rule
-- ----------------------------
DROP TABLE IF EXISTS `traffic_rule`;
CREATE TABLE `traffic_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增',
  `limit_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '限行尾号',
  `week_day` int NOT NULL DEFAULT 0 COMMENT '星期',
  `limit_type` int NOT NULL DEFAULT 0 COMMENT '规则类型（0普通,1-特殊)',
  `spec_day` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '特殊日期',
  `spec_action` int NOT NULL DEFAULT 0 COMMENT '特殊日期限行',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '交通限行规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vacation
-- ----------------------------
DROP TABLE IF EXISTS `vacation`;
CREATE TABLE `vacation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `emp_id` bigint NOT NULL DEFAULT 0 COMMENT '员工',
  `begin_time` bigint NOT NULL DEFAULT 0 COMMENT '休假开始时间',
  `end_time` bigint NOT NULL DEFAULT 0 COMMENT '休假结束时间',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '休假记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vault_check
-- ----------------------------
DROP TABLE IF EXISTS `vault_check`;
CREATE TABLE `vault_check`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '顶级部门ID',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构',
  `usable_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '可用券_余额',
  `bad_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '残损券_余额',
  `good_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '五好券_余额',
  `unclear_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '未清分_余额',
  `usable_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '可用券清点金额',
  `bad_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '残损券清点金额',
  `good_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '五号券清点金额',
  `unclear_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '未清分清点金额',
  `bad_stock_count` bigint NOT NULL DEFAULT 0 COMMENT '残损券库存张数',
  `bad_actual_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '残损券清点张数',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `wh_op_man` bigint NOT NULL DEFAULT 0 COMMENT '查库人1',
  `wh_check_man` bigint NOT NULL DEFAULT 0 COMMENT '查库人2',
  `wh_confirm_man` bigint NOT NULL DEFAULT 0 COMMENT '查库人3',
  `wh_op_time` bigint NOT NULL DEFAULT 0 COMMENT '盘点时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `remnant_amount` decimal(10, 0) NOT NULL DEFAULT 0 COMMENT '尾零券实际盘点金额',
  `remnant_balance` decimal(10, 0) NOT NULL DEFAULT 0 COMMENT '尾零券库存余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3181 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '金库盘点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vault_order
-- ----------------------------
DROP TABLE IF EXISTS `vault_order`;
CREATE TABLE `vault_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `department_id` bigint NULL DEFAULT 0 COMMENT '顶级部门ID',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构',
  `order_type` int NOT NULL DEFAULT 0 COMMENT '0-入库、1 - 出库',
  `sub_type` int NULL DEFAULT 0 COMMENT '0-ATM加钞 1-领缴款',
  `order_date` bigint NOT NULL DEFAULT 0 COMMENT '单据日期',
  `order_amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '单据金额',
  `voucher_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '凭证图片',
  `next_user_ids` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '下一个审核用户ID,逗号分隔',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '-1 - 已撤销，0 - 创建、1 - 审核中，2 - 审核拒绝，3 - 审核通过，4 - 已入库，5 - 撤销中 6=撤销拒绝',
  `finish` tinyint NOT NULL DEFAULT 0 COMMENT '出入库完成标志（0 - 未完成，1 - 已完成）',
  `comments` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `wh_op_man` bigint NOT NULL DEFAULT 0 COMMENT '操作员',
  `wh_check_man` bigint NOT NULL DEFAULT 0 COMMENT '操作员',
  `wh_confirm_man` bigint NOT NULL DEFAULT 0 COMMENT '操作员',
  `wh_op_time` bigint NOT NULL DEFAULT 0 COMMENT '出入库时间',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9482 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '出入库单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vault_order_record
-- ----------------------------
DROP TABLE IF EXISTS `vault_order_record`;
CREATE TABLE `vault_order_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `order_id` bigint NOT NULL DEFAULT 0 COMMENT '出入库单号',
  `denom_type` int NOT NULL DEFAULT 0 COMMENT '库存类型:可用券，残损券，五好券',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `count` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '张数',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22469 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '出入库明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vault_order_task
-- ----------------------------
DROP TABLE IF EXISTS `vault_order_task`;
CREATE TABLE `vault_order_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT 0 COMMENT '订单ID',
  `category` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '类别 0-ATM任务 2-备用金',
  `task_id` bigint NULL DEFAULT 0 COMMENT '任务ID',
  `atm_id` bigint NULL DEFAULT 0 COMMENT 'ATM设备ID',
  `route_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '线路ID',
  `bank_id` bigint NULL DEFAULT 0 COMMENT '机构ID',
  `denom_id` bigint NULL DEFAULT 0 COMMENT '券别ID',
  `amount` decimal(15, 2) NULL DEFAULT 0.00 COMMENT '加钞金额',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 169183 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '出库订单ATM加钞任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vault_volum
-- ----------------------------
DROP TABLE IF EXISTS `vault_volum`;
CREATE TABLE `vault_volum`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '顶级部门ID',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行机构',
  `denom_type` int NOT NULL DEFAULT 0 COMMENT '库存类型:可用券，残损券，五好券',
  `denom_id` bigint NOT NULL DEFAULT 0 COMMENT '券别',
  `amount` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `count` bigint NOT NULL DEFAULT 1 COMMENT '张数',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '金库库存' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `lpno` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车牌号码',
  `seqno` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车辆编号',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `vehicle_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车辆类型',
  `factory` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '制造商',
  `model` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '型号',
  `vh_type` int NOT NULL DEFAULT 0 COMMENT '车辆种类 1-押运车 2-小轿车',
  `frame_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '车架号',
  `engine_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '发动机号',
  `emission_standard` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '排放标准',
  `status_t` int NOT NULL DEFAULT 0 COMMENT '车辆状态 0：正常使用  1：维修中 2：报废',
  `production_date` bigint NOT NULL DEFAULT 0 COMMENT '出厂日期',
  `enroll_date` bigint NOT NULL DEFAULT 0 COMMENT '购买日期',
  `comments` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '车辆信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vehicle_maintain
-- ----------------------------
DROP TABLE IF EXISTS `vehicle_maintain`;
CREATE TABLE `vehicle_maintain`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `vehicle_id` bigint NOT NULL DEFAULT 0 COMMENT '设备编号',
  `department_id` bigint NOT NULL DEFAULT 0 COMMENT '所属顶级部门',
  `mt_date` bigint NOT NULL DEFAULT 0 COMMENT '维保日期',
  `mt_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维保类型',
  `mt_reason` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '故障原因',
  `mt_content` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '维保内容',
  `mt_cost` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '维保成本',
  `mt_result` int NOT NULL DEFAULT 0 COMMENT '维保结果: 0 - 维修成功，1- 维修失败',
  `mt_employee` int NOT NULL DEFAULT 0 COMMENT '经办人',
  `create_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `create_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_user` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` bigint NOT NULL DEFAULT 0 COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '车辆维保记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for workflow
-- ----------------------------
DROP TABLE IF EXISTS `workflow`;
CREATE TABLE `workflow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NULL DEFAULT 0,
  `node_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '审批节点名称',
  `user_ids` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户ID,逗号分隔',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 482 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '工作流设计数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for workflow_event
-- ----------------------------
DROP TABLE IF EXISTS `workflow_event`;
CREATE TABLE `workflow_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '所属部门ID',
  `event_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '事件名称',
  `event_code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '事件编码',
  `status` int NULL DEFAULT 0 COMMENT '事件状态 0=关闭 1=开启',
  `msg_status` int NULL DEFAULT 0 COMMENT '事件消息通知状态 0=关闭 1=开启',
  `update_user` bigint NULL DEFAULT 0 COMMENT '更新用户ID',
  `update_time` bigint NULL DEFAULT 0 COMMENT '更新时间戳',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '审批事件数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for workflow_record
-- ----------------------------
DROP TABLE IF EXISTS `workflow_record`;
CREATE TABLE `workflow_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NULL DEFAULT 0 COMMENT '事件ID',
  `identity_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '审批用户ID',
  `status` int NULL DEFAULT 0 COMMENT '审批结果 1=通过 2 =拒绝',
  `sort` int NULL DEFAULT 0 COMMENT '节点排序',
  `comments` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '审批批注',
  `create_user` bigint NULL DEFAULT 0 COMMENT '创建用户ID',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间戳',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13940 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '流程审批记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
