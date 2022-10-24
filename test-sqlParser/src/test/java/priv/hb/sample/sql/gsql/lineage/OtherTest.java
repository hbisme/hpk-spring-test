package priv.hb.sample.sql.gsql.lineage;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import io.vavr.Tuple2;
import priv.hb.sample.sql.gsql.utils.Common;


import static io.vavr.API.Tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author hubin
 * @date 2022年10月09日 14:38
 */
public class OtherTest {
    @Test
    public void test1() {

        //@formatter:off
        String sql =
                "use ytdw; SELECT supply_info.supplierId AS `supplierId`\n" +
                        "  , supply_info.supplierName AS `supplierName`\n" +
                        "  , supply_info.provinceId AS `provinceId`\n" +
                        "  , supply_area_data.provinceName AS `provinceName`\n" +
                        "  , supply_info.purchaserNames AS `purchaserNames`\n" +
                        "  , coalesce(supply_data.gmv, 0) AS `gmv`\n" +
                        "  , coalesce(before_supply_data.gmv, 0) AS `beforeGmv`\n" +
                        "  , coalesce(supply_data.couponAmount, 0) AS `couponAmount`\n" +
                        "  , coalesce(supply_data.supplierCouponAmount, 0) AS `supplierCouponAmount`\n" +
                        "  , coalesce(supply_data.orderNum, 0) AS `orderNum`\n" +
                        "  , coalesce(before_supply_data.orderNum, 0) AS `beforeOrderNum`\n" +
                        "  , coalesce(supply_data.itemNum, 0) AS `itemNum`\n" +
                        "  , coalesce(supply_data.shopNum, 0) AS `shopNum`\n" +
                        "  , coalesce(before_supply_data.itemNum, 0) AS `beforeItemNum`\n" +
                        "  , coalesce(supply_data.marketingActivityOrderNum, 0) AS `marketingActivityOrderNum`\n" +
                        "  , coalesce(supply_data.marketingActivityGmv, 0) AS `marketingActivityGmv`\n" +
                        "  , coalesce(before_supply_data.marketingActivityGmv, 0) AS `beforeMarketingActivityGmv`\n" +
                        "  , coalesce(supply_data.dayGmv, 0) AS `dayGmv`\n" +
                        "  , coalesce(supply_data.newItemGmv, 0) AS `newItemGmv`\n" +
                        "  , coalesce(supply_data.hiGmv, 0) AS `hiGmv`\n" +
                        "  , coalesce(supply_data.cashierGmv, 0) AS `cashierGmv`\n" +
                        "  , coalesce(supply_data.flushBuyGmv, 0) AS `flushBuyGmv`\n" +
                        "  , coalesce(supply_data.existOrderItemNum, 0) AS `existOrderItemNum`\n" +
                        "  , supply_info.customerNames AS `customerNames`\n" +
                        "  , coalesce(supply_data.deliverOrderNum, 0) AS `deliverOrderNum`   , coalesce(before_supply_data.deliverOrderNum, 0) AS `beforeDeliverOrderNum`   , coalesce(supply_data.fwlyRefundOrderNum, 0) AS `fwlyRefundOrderNum`   , coalesce(before_supply_data.fwlyRefundOrderNum, 0) AS `beforeFwlyRefundOrderNum`   , coalesce(supply_data.fwlyRefundNum, 0) AS `fwlyRefundNum`   , coalesce(before_supply_data.fwlyRefundNum, 0) AS `beforeFwlyRefundNum`   , coalesce(supply_data.onlyRefundNum, 0) AS `onlyRefundNum`   , coalesce(supply_data.fwlyOnlyRefundNum, 0) AS `fwlyOnlyRefundNum`   , coalesce(supply_data.finishedFwlyOnlyRefundNum, 0) AS `finishedFwlyOnlyRefundNum`   , coalesce(supply_data.supplyOnlyRefundNum, 0) AS `supplyOnlyRefundNum`   , coalesce(supply_refund_time_data.supplyOnlyRefundTime, 0) AS `supplyOnlyRefundTime`   , coalesce(supply_data.itemRefundNum, 0) AS `itemRefundNum`   , coalesce(supply_data.fwlyItemRefundNum, 0) AS `fwlyItemRefundNum`   , coalesce(supply_data.finishedFwlyItemRefundNum, 0) AS `finishedFwlyItemRefundNum`   , coalesce(supply_data.supplyItemRefundNum, 0) AS `supplyItemRefundNum`   , coalesce(supply_refund_time_data.supplyItemRefundTime, 0) AS `supplyItemRefundTime`   , supply_info.refundDealType AS `refundDealType`\n" +
                        "  , coalesce(supply_data.disputeOrderNum, 0) AS `disputeOrderNum`\n" +
                        "  , coalesce(supply_data.customerOrderNum, 0) AS `customerOrderNum`\n" +
                        "  , coalesce(supply_data.logisticsWarnOrderNum, 0) AS `logisticsWarnOrderNum`\n" +
                        "  , coalesce(supply_data.logisticsPunishOrderNum, 0) AS `logisticsPunishOrderNum`\n" +
                        "  , supply_info.firstAuditTime AS `firstAuditTime`\n" +
                        "  , supply_info.matchScore AS `matchScore`\n" +
                        "  , supply_info.deliveryScore AS `deliveryScore`\n" +
                        "  , supply_info.responseScore AS `responseScore`\n" +
                        "  , supply_info.openIm AS `openIm`\n" +
                        "  , supply_info.depositStatus AS `depositStatus`\n" +
                        "  , supply_info.firstItemApplyTime AS `firstItemApplyTime`\n" +
                        "  , supply_info.firstItemBatchTime AS `firstItemBatchTime`\n" +
                        "  , coalesce(supplier_im.imResponseNum, 0) AS `imResponseNum`   , coalesce(supplier_im.totalImResponseNum, 0) AS `totalImResponseNum`   , supply_refund_category_data.mostRefundCategory AS `mostRefundCategory`   , coalesce(supply_refund_category_data.mostRefundCategoryOrderNum, 0) AS `mostRefundCategoryOrderNum`   , supply_refund_cause_data.mostRefundCause AS `mostRefundCause`   , coalesce(supply_refund_cause_data.mostRefundCauseOrderNum, 0) AS `mostRefundCauseOrderNum` FROM (\n" +
                        "  SELECT supply_id AS `supplierId`\n" +
                        "    , supply_name AS `supplierName`\n" +
                        "    , provincearea_id AS `provinceId`\n" +
                        "    , concat_ws(',', js_auditor_user_names) AS `purchaserNames`\n" +
                        "    , refund_process_flag AS `refundDealType`\n" +
                        "    , concat_ws(',', docking_user_names) AS `customerNames`\n" +
                        "    , supply_enter_days AS `firstAuditTime`\n" +
                        "    , match_score AS `matchScore`\n" +
                        "    , delivery_score AS `deliveryScore`\n" +
                        "    , response_score AS `responseScore`\n" +
                        "    , CASE WHEN im_status = 1 THEN '是' \n" +
                        "           ELSE '否' END AS `openIm`\n" +
                        "    , CASE WHEN deposit_status = 1 THEN '未缴纳'\n" +
                        "           WHEN deposit_status = 2 THEN '未缴齐'\n" +
                        "           WHEN deposit_status = 3 THEN '已缴纳待审核'\n" +
                        "           WHEN deposit_status = 4 THEN '已缴纳'\n" +
                        "           WHEN deposit_status = 5 THEN '退回'\n" +
                        "           ELSE '其他' END AS `depositStatus`\n" +
                        "    , first_item_apply_day_cost AS `firstItemApplyTime`\n" +
                        "    , first_item_batch_day_cost AS `firstItemBatchTime`\n" +
                        "  FROM dw_supply_info_detail_d\n" +
                        "WHERE (status = 1 OR (apply_flag = 2 AND status NOT IN (9, 10))) AND inuse = 1\n" +
                        "AND dayid = '20220521'\n" +
                        "\n" +
                        ") supply_info\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supply_id AS `supplierId`\n" +
                        "    , coalesce(SUM(total_pay_amount), 0) AS `gmv`\n" +
                        "    , coalesce(SUM(coupon_amount), 0) AS `couponAmount`\n" +
                        "    , coalesce(SUM(supply_support_coupon_amount), 0) AS `supplierCouponAmount`\n" +
                        "    , coalesce(COUNT(DISTINCT trade_id), 0) AS `orderNum`\n" +
                        "    , coalesce(SUM(item_count), 0) AS `itemNum`\n" +
                        "    , coalesce(COUNT(DISTINCT shop_id), 0) AS `shopNum`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_yxhd_order = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `marketingActivityOrderNum`\n" +
                        "    , coalesce(SUM(yxhd_total_pay_amount), 0) AS `marketingActivityGmv`\n" +
                        "    , coalesce(SUM(mrbq_total_pay_amount), 0) AS `dayGmv`\n" +
                        "    , coalesce(SUM(xpbq_total_pay_amount), 0) AS `newItemGmv`\n" +
                        "    , coalesce(SUM(hqc_total_pay_amount), 0) AS `hiGmv`\n" +
                        "    , coalesce(SUM(syt_total_pay_amount), 0) AS `cashierGmv`\n" +
                        "    , coalesce(SUM(jhs_total_pay_amount), 0) AS `flushBuyGmv`\n" +
                        "    , coalesce(COUNT(DISTINCT item_id), 0) AS `existOrderItemNum`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_delivery_order = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `deliverOrderNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyRefundOrderNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_jtk_refund_order = 1 or is_bctk_refund_order = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `onlyRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_jtk_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyOnlyRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_jtk_refund_worker = 1 and is_refund_order_over = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `finishedFwlyOnlyRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_supply_jtk_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `supplyOnlyRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_thtk_refund_order = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `itemRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_thtk_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyItemRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_thtk_refund_worker = 1 and is_refund_order_over = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `finishedFwlyItemRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_supply_thtk_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `supplyItemRefundNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_dispute_order = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `disputeOrderNum`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_supply_complaints_order = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `customerOrderNum`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_logistic_warn = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `logisticsWarnOrderNum`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_logistic_punish = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `logisticsPunishOrderNum`\n" +
                        "  FROM ads_sup_supply_data_analyse_d\n" +
                        "WHERE business_unit not in ('其他')\n" +
                        "AND dayid = '20220521'\n" +
                        "AND order_pay_day >= '20220421'\n" +
                        "AND order_pay_day <= '20220522'\n" +
                        "\n" +
                        "  GROUP BY supply_id\n" +
                        ") supply_data \n" +
                        "ON supply_data.supplierId = supply_info.supplierId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supply_id AS `supplierId`\n" +
                        "    , coalesce(SUM(total_pay_amount), 0) AS `gmv`\n" +
                        "    , coalesce(COUNT(DISTINCT trade_id), 0) AS `orderNum`\n" +
                        "    , coalesce(SUM(item_count), 0) AS `itemNum`\n" +
                        "    , coalesce(SUM(yxhd_total_pay_amount), 0) AS `marketingActivityGmv`\n" +
                        "    , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_delivery_order = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `deliverOrderNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyRefundOrderNum`     , coalesce(COUNT(DISTINCT CASE \n" +
                        "      WHEN is_fwly_refund_worker = 1 THEN refund_id\n" +
                        "      ELSE NULL END), 0) AS `fwlyRefundNum`   FROM ads_sup_supply_data_analyse_d\n" +
                        "WHERE business_unit not in ('其他')\n" +
                        "AND dayid = '20220521'\n" +
                        "AND order_pay_day >= '20220321'\n" +
                        "AND order_pay_day <= '20220422'\n" +
                        "\n" +
                        "  GROUP BY supply_id\n" +
                        ") before_supply_data\n" +
                        "ON before_supply_data.supplierId = supply_info.supplierId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supply_refund_time_data.supplierId AS `supplierId`\n" +
                        "    , coalesce(SUM(supply_refund_time_data.supplyOnlyRefundTime), 0) AS `supplyOnlyRefundTime`     , coalesce(SUM(supply_refund_time_data.supplyItemRefundTime), 0) AS `supplyItemRefundTime`   FROM (\n" +
                        "    SELECT supply_id AS `supplierId`\n" +
                        "      , trade_id AS `tradeId`\n" +
                        "      , refund_id AS `refundId`\n" +
                        "      , coalesce(max(supply_jtk_refund_worker_cost), 0) AS `supplyOnlyRefundTime`       , coalesce(max(supply_thtk_refund_worker_cost), 0) AS `supplyItemRefundTime`     FROM ads_sup_supply_data_analyse_d\n" +
                        "WHERE business_unit not in ('其他')\n" +
                        "AND dayid = '20220521'\n" +
                        "AND order_pay_day >= '20220421'\n" +
                        "AND order_pay_day <= '20220522'\n" +
                        "\n" +
                        "    GROUP BY supply_id, trade_id, refund_id\n" +
                        "  ) supply_refund_time_data\n" +
                        "  GROUP BY supply_refund_time_data.supplierId\n" +
                        ") supply_refund_time_data\n" +
                        "ON supply_refund_time_data.supplierId = supply_info.supplierId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supply_refund_category_data.supplierId AS `supplierId`\n" +
                        "    , coalesce(CASE \n" +
                        "      WHEN coalesce(first(supply_refund_category_data.refundCategoryOrderNum), 0) > 0 THEN first(supply_refund_category_data.refundCategory)\n" +
                        "      ELSE '' END, '') AS `mostRefundCategory`     , coalesce(first(supply_refund_category_data.refundCategoryOrderNum), 0) AS `mostRefundCategoryOrderNum`   FROM (\n" +
                        "    SELECT supply_id AS `supplierId`\n" +
                        "      , category_id_first_name AS `refundCategory`       , coalesce(COUNT(DISTINCT CASE \n" +
                        "          WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "          ELSE NULL END), 0) AS `refundCategoryOrderNum`       , row_number() over (PARTITION BY supply_id ORDER BY\n" +
                        "          coalesce(COUNT(DISTINCT CASE \n" +
                        "            WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "            ELSE NULL END), 0) DESC) AS `rank`     FROM ads_sup_supply_data_analyse_d\n" +
                        "WHERE business_unit not in ('其他')\n" +
                        "AND dayid = '20220521'\n" +
                        "AND order_pay_day >= '20220421'\n" +
                        "AND order_pay_day <= '20220522'\n" +
                        "\n" +
                        "    GROUP BY supply_id, category_id_first_name\n" +
                        "  ) supply_refund_category_data\n" +
                        "  WHERE supply_refund_category_data.rank = 1\n" +
                        "  GROUP BY supply_refund_category_data.supplierId\n" +
                        ") supply_refund_category_data\n" +
                        "ON supply_refund_category_data.supplierId = supply_info.supplierId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supply_refund_cause_data.supplierId AS `supplierId`\n" +
                        "    , coalesce(CASE \n" +
                        "      WHEN coalesce(first(supply_refund_cause_data.refundCauseOrderNum), 0) > 0 THEN first(supply_refund_cause_data.refundCause)\n" +
                        "      ELSE '' END, '') AS `mostRefundCause`     , coalesce(first(supply_refund_cause_data.refundCauseOrderNum), 0) AS `mostRefundCauseOrderNum`   FROM (\n" +
                        "    SELECT supply_id AS `supplierId`\n" +
                        "      , refund_cause_name AS `refundCause`       , coalesce(COUNT(DISTINCT CASE \n" +
                        "          WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "          ELSE NULL END), 0) AS `refundCauseOrderNum`       , row_number() over (PARTITION BY supply_id ORDER BY\n" +
                        "          coalesce(COUNT(DISTINCT CASE \n" +
                        "            WHEN is_fwly_refund_worker = 1 THEN trade_id\n" +
                        "            ELSE NULL END), 0) DESC) AS `rank`     FROM ads_sup_supply_data_analyse_d\n" +
                        "WHERE business_unit not in ('其他')\n" +
                        "AND dayid = '20220521'\n" +
                        "AND order_pay_day >= '20220421'\n" +
                        "AND order_pay_day <= '20220522'\n" +
                        "\n" +
                        "    GROUP BY supplierId, refundCause\n" +
                        "  ) supply_refund_cause_data\n" +
                        "  WHERE supply_refund_cause_data.rank = 1\n" +
                        "  GROUP BY supply_refund_cause_data.supplierId\n" +
                        ") supply_refund_cause_data\n" +
                        "ON supply_refund_cause_data.supplierId = supply_info.supplierId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT DISTINCT pro_area_id AS `provinceId`\n" +
                        "    , pro_area_name AS `provinceName`\n" +
                        "  FROM dwd_area_transform_d\n" +
                        "WHERE dayid = '20220521'\n" +
                        "\n" +
                        ") supply_area_data\n" +
                        "ON supply_area_data.provinceId = supply_info.provinceId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT supplier_id AS `supplierId`\n" +
                        "    , sum(response_in_40s_count) AS `imResponseNum`\n" +
                        "    , sum(response_total_count) AS `totalImResponseNum`\n" +
                        "  FROM dwd_cc_moor_chat_info_d\n" +
                        "WHERE dayid = '20220521'\n" +
                        "AND substr(create_time, 1, 8) >= '20220421'\n" +
                        "AND substr(create_time, 1, 8) <= '20220522'\n" +
                        "\n" +
                        "  GROUP BY supplier_id\n" +
                        ") supplier_im \n" +
                        "on supplier_im.supplierId = supply_info.supplierId\n" +
                        "WHERE 1 = 1\n" +
                        "ORDER BY coalesce(supply_data.gmv, 0) DESC limit 5000\n" ;
        //@formatter:on


        List<Tuple2<Set<String>, String>> lineage = Common.getLineage(sql);

        System.out.println(lineage);
    }




}
