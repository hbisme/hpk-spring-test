package priv.hb.sample;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * @author hubin
 * @date 2023年04月05日 21:40
 */

public class Test2 {
    @Test
    public void test1() {

        SmOrder smOrder = new SmOrder();
        String string = JSON.toJSONString(smOrder).toString();
        System.out.println(string);
    }


}


class SmOrder{
    public String prompt = "sm_order的建表语句";

    public String completion = "-- auto-generated definition\n" +
            "create table sm_order\n" +
            "(\n" +
            "    Id                       bigint                                   not null,\n" +
            "    gmt_create               timestamp default CURRENT_TIMESTAMP      not null,\n" +
            "    creator                  varchar(32) charset utf8                 not null,\n" +
            "    gmt_modified             timestamp default CURRENT_TIMESTAMP      not null on update CURRENT_TIMESTAMP,\n" +
            "    modifier                 varchar(32) charset utf8                 not null,\n" +
            "    is_deleted               char charset utf8                        not null,\n" +
            "    order_id                 varchar(64) charset utf8                 null,\n" +
            "    ae_gmt_create            timestamp default '0000-00-00 00:00:00'  not null comment 'ae订单创建时间',\n" +
            "    ae_gmt_modified          timestamp default '0000-00-00 00:00:00'  not null comment 'ae订单更新时间-订单详情接口取得',\n" +
            "    buyer_login_id           varchar(64) charset utf8                 null comment '买家loginId',\n" +
            "    buyer_email              varchar(64) charset utf8                 null comment '买家email-详情接口',\n" +
            "    pay_amount               decimal(14, 2)                           not null comment '订单应付款总金额',\n" +
            "    order_status             varchar(128) charset utf8                not null comment '订单状态',\n" +
            "    isv_memo                 varchar(1024) collate utf8mb4_general_ci null,\n" +
            "    buyer_signer_full_name   varchar(256)                             not null comment '买家全名',\n" +
            "    receipt_address_country  varchar(12) charset utf8                 null,\n" +
            "    gmt_pay_time             timestamp                                null comment '订单付款时间',\n" +
            "    logistics_amount         decimal(14, 2)                           not null comment '物流运费总价',\n" +
            "    country                  varchar(12) charset utf8                 null,\n" +
            "    zip                      varchar(128) charset utf8                null,\n" +
            "    address2                 varchar(512) charset utf8                null,\n" +
            "    detail_address           varchar(512)                             null,\n" +
            "    city                     varchar(128) charset utf8                null,\n" +
            "    phone_number             varchar(64) collate utf8mb4_general_ci   null,\n" +
            "    province                 varchar(128) charset utf8                null,\n" +
            "    phone_area               varchar(20) charset utf8                 null,\n" +
            "    phone_country            varchar(20) charset utf8                 null,\n" +
            "    contact_person           varchar(512)                             null,\n" +
            "    mobile_no                varchar(20) charset utf8                 null,\n" +
            "    related_order_id         varchar(2000) charset utf8               null,\n" +
            "    is_main_order            varchar(2) charset utf8                  null,\n" +
            "    is_printed               varchar(2) charset utf8                  null,\n" +
            "    memo_gmt_modified        timestamp                                null,\n" +
            "    isv_memo_modified        varchar(256) charset utf8                null,\n" +
            "    gmt_printed              timestamp                                null,\n" +
            "    ae_gmt_create_timezone   varchar(10) charset utf8                 null,\n" +
            "    ae_gmt_modified_timezone varchar(10) charset utf8                 null,\n" +
            "    gmt_pay_time_timezone    varchar(10) charset utf8                 null,\n" +
            "    is_combine               varchar(2) charset utf8                  null comment '是否已合并',\n" +
            "    issue_status             varchar(40) charset utf8                 null,\n" +
            "    refund_status            varchar(40) charset utf8                 null,\n" +
            "    loan_status              varchar(40) charset utf8                 null,\n" +
            "    send_good_expired_date   datetime                                 null,\n" +
            "    biz_type                 varchar(40) charset utf8                 null,\n" +
            "    local_remark             varchar(256) charset utf8                null comment '本地备注',\n" +
            "    timeout_left_time        bigint                                   null comment 'bigint',\n" +
            "    local_order_status       varchar(40) charset utf8                 null comment '本地状态',\n" +
            "    exception_order          char charset utf8                        null comment '是否异常订单',\n" +
            "    is_user_set              int                                      null comment '是否为用户设置：0为系统设置，1为用户设置',\n" +
            "    item_count               int                                      null comment '订单包含的产品数量',\n" +
            "    user_id                  bigint                                   not null,\n" +
            "    auth_id                  bigint                                   not null,\n" +
            "    order_gmt_create         datetime                                 null comment '订单创建时间（在对应网站上时间的北京时间）',\n" +
            "    order_gmt_modified       datetime                                 null comment '订单更新时间（在对应网站上时间的北京时间）',\n" +
            "    gmt_send                 datetime                                 null comment '发货时间',\n" +
            "    country_full_name        varchar(128) charset utf8                null comment '收货地址国家全称',\n" +
            "    site                     varchar(10) charset utf8                 not null comment '所属网站（指AE、敦煌网、ebay等）',\n" +
            "    ali_id                   bigint                                   not null,\n" +
            "    transaction_id           varchar(32) charset utf8                 null comment '订单交易ID',\n" +
            "    sku_code                 varchar(128) charset utf8                null comment 'sku，多个用逗号隔开',\n" +
            "    parent_id                bigint                                   null comment '父订单号，合并订单需要',\n" +
            "    logistics_service_name   varchar(64) charset utf8                 null comment '买家所选物流，填写其中一个产品上的物流',\n" +
            "    lack_declaration         char charset utf8                        null comment '是否填写报关',\n" +
            "    has_msg                  char charset utf8                        null comment '是否有站内信',\n" +
            "    has_order_msg            char charset utf8                        null comment '是否有订单留言',\n" +
            "    name_not_full            char charset utf8                        null comment '俄罗斯名称是否不全',\n" +
            "    currency_code            char(4) charset utf8                     null comment '货币类型',\n" +
            "    fulfillment_channel      char(4) charset utf8                     null comment '配送方式',\n" +
            "    shipment_service_level   char(16) charset utf8                    null comment '配送服务级别',\n" +
            "    order_number             varchar(32) charset utf8                 null comment '订单号',\n" +
            "    payment_method           varchar(128) charset utf8                null comment '付款方式',\n" +
            "    gmt_trade_end            datetime                                 null comment '交易结束日期',\n" +
            "    sku_amount               decimal(14, 2)                           null comment '商品金额',\n" +
            "    sku_not_match            char collate utf8mb4_general_ci          null,\n" +
            "    order_product_type       int(4)                                   null,\n" +
            "    district                 varchar(128) collate utf8mb4_general_ci  null,\n" +
            "    gmt_cancel               datetime                                 null comment '订单取消时间',\n" +
            "    order_type               int(1)    default 1                      not null comment '订单类型 1:线上订单,2代发单,默认1',\n" +
            "    abnormal_profit          char                                     null comment '异常利润(n:否,y是)',\n" +
            "    err_msg                  varchar(512)                             null comment '异常利润信息',\n" +
            "    is_sync                  tinyint(1)                               null comment '是否同步历史库（0/null否，1是）',\n" +
            "    primary key (Id, buyer_signer_full_name),\n" +
            "    constraint ae_order_id\n" +
            "        unique (order_id, site)\n" +
            ")\n" +
            "    comment '订单主表 订单主表核心信息' collate = utf8mb4_unicode_ci;\n" +
            "\n" +
            "create index IDX_local2\n" +
            "    on sm_order (user_id, is_deleted, exception_order, local_order_status, auth_id, parent_id);\n" +
            "\n" +
            "create index IDX_order_number\n" +
            "    on sm_order (order_number);\n" +
            "\n" +
            "create index IDX_orderid_localorderstatus\n" +
            "    on sm_order (order_id, local_order_status, auth_id, user_id);\n" +
            "\n" +
            "create index IDX_uid_authid_exception\n" +
            "    on sm_order (user_id, auth_id, is_deleted, exception_order);\n" +
            "\n" +
            "create index IDX_userid_order_id_auth_id\n" +
            "    on sm_order (user_id, is_deleted, order_id, auth_id);\n" +
            "\n" +
            "create index ali_id\n" +
            "    on sm_order (ali_id);\n" +
            "\n" +
            "create index auth_id\n" +
            "    on sm_order (auth_id, local_order_status);\n" +
            "\n" +
            "create index buyer_login_id\n" +
            "    on sm_order (buyer_login_id);\n" +
            "\n" +
            "create index finance_query\n" +
            "    on sm_order (site, country, order_status);\n" +
            "\n" +
            "create index idx_ae_gmt_create\n" +
            "    on sm_order (ae_gmt_create);\n" +
            "\n" +
            "create index index_exception_order\n" +
            "    on sm_order (user_id, auth_id, exception_order, local_order_status);\n" +
            "\n" +
            "create index `order_id-pay_time`\n" +
            "    on sm_order (order_id, gmt_pay_time);\n" +
            "\n" +
            "create index user_id\n" +
            "    on sm_order (user_id, parent_id);\n" +
            "\n";

}
