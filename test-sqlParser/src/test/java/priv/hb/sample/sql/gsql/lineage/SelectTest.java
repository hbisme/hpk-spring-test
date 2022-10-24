package priv.hb.sample.sql.gsql.lineage;

import priv.hb.sample.sql.gsql.lineage.visitor.TSelectSqlStatementVisitor;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import io.vavr.API;
import io.vavr.Tuple2;
import priv.hb.sample.sql.gsql.utils.Common;


import static io.vavr.API.Tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SELECT语句,类型就是 TSelectSqlStatement
 * @author hubin
 * @date 2022年09月30日 17:28
 */
public class SelectTest {
    @Test
    public void test1() {
        //@formatter:off
        String sql =
                "SELECT page_views.c1\n" +
                        "FROM page_views JOIN dim_users\n" +
                        "  ON (page_views.user_id = dim_users.id " +

                        "      AND page_views.date >= '2008-03-01' " +
                        "      AND page_views.date <= '2008-03-31')";
        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = Common.getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("page_views", "dim_users").toJavaSet(), ""));

        System.out.println(lineage);
    }

    @Test
    public void test2() {
        String sql = "SELECT col1 FROM (SELECT col11, SUM(col2) AS col2sum FROM t1 GROUP BY col1) t2 WHERE t2.col2sum > 10";


        List<Tuple2<Set<String>, String>> lineage = Common.getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("t1").toJavaSet(), ""));

        System.out.println(lineage);
    }

    @Test
    public void test3() {
        //@formatter:off
        String sql =
                "with item_base as\n" +
                        "(\n" +
                        "select * from ytdw.dim_hpc_itm_item_d where dayid='20220929' and is_platform_valid = 1 and business_unit not in ('卡券票','其他')\n" +
                        "),\n" +
                        "is_shop_coupon as\n" +
                        "(\n" +
                        "--店铺券\n" +
                        "select\n" +
                        "item_id,\n" +
                        "1 as is_shop_coupon\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "store_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "supply_id\n" +
                        "from ytdw.dwd_item_extra_d\n" +
                        "lateral view explode(split(supply_ids, ',')) t as supply_id\n" +
                        "where dayid = '20220929' and is_deleted = 0\n" +
                        ") item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as store_id,\n" +
                        "supplier_id\n" +
                        "from ytdw.dwd_supplier_store_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        ") supplier on item.supply_id = supplier.supplier_id\n" +
                        "where supplier.store_id is not null\n" +
                        ") item_store\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as coupon_id,\n" +
                        "scope_type_value,\n" +
                        "initiator_value -- 发起方id\n" +
                        "from\n" +
                        "(\n" +
                        "select id,\n" +
                        "initiator_value\n" +
                        "from ytdw.dwd_coupon_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "and initiator_type = 1 -- 发放类型：小二发放 用户领取 任务发放\n" +
                        "and status = 1\n" +
                        "and (stop_date>=regexp_replace(current_date(),'-','') or stop_date is null)\n" +
                        ") coupon\n" +
                        "left join\n" +
                        "(\n" +
                        "select out_biz_id,\n" +
                        "scope_type_value2 as scope_type_value\n" +
                        "from ytdw.dwd_smc_promotion_scope_d\n" +
                        "lateral view explode(split(scope_type_value, ',')) t as scope_type_value2\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "and scope_type = 1 --1.包含前台类目2.包含后台类目3.包含品牌4.包含商品id5.不包含商品id6.商品包含大类7.商品包含标识\n" +
                        ") scope on coupon.id = scope.out_biz_id\n" +
                        ") coupon on item_store.store_id = coupon.initiator_value\n" +
                        "where coupon.initiator_value is not null\n" +
                        "and (\n" +
                        "coupon.scope_type_value = 'all'\n" +
                        "or coupon.scope_type_value = item_store.item_id\n" +
                        ")\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "is_activity as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(distinct act_style_code) as activity_cnt,\n" +
                        "sum(case when act_name LIKE '%拿样%' then 1 else 0 end) as is_ny\n" +
                        "from ytdw.dw_hpc_prm_activity_item_d where dayid='20220929' and act_end_time>=regexp_replace(current_date(),'-','')\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "is_one_piece as\n" +
                        "(\n" +
                        "select id as item_id,case when start_purchase_num=1 then 1 else 0 end as is_one_piece,\n" +
                        "sale_type,\n" +
                        "nvl(size(split(picture,';')),0) as pic_cnt,\n" +
                        "nvl(if(video_id is null,0,1),0) as is_video\n" +
                        "from ytdw.dwd_item_d where dayid='20220929'\n" +
                        "),\n" +
                        "tit_is_contain_brand as (\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "case when item.item_name like concat('%',brand.cn_name,'%') or item.item_name like concat('%',brand.en_name,'%') then 1 else 0 end as tit_is_brand,\n" +
                        "case when item.item_name like '%品牌直供%' then 1\n" +
                        "when item.item_name like '%厂家直销%' then 2\n" +
                        "when item.item_name like '%每日必抢%' then 3\n" +
                        "when item.item_name like '%巨划算%' then 4\n" +
                        "when item.item_name like '%秒杀%' then 5\n" +
                        "else 0 end as tit_is_spe\n" +
                        "from\n" +
                        "item_base item\n" +
                        "join\n" +
                        "ytdw.dwd_brand_d brand on brand.id=item.brand_id where brand.dayid='20220929'\n" +
                        "),\n" +
                        "tit_is_contain_cate as (\n" +
                        "select\n" +
                        "item_id,sum(is_contain_cate) as is_contain_cate\n" +
                        "from\n" +
                        "(\n" +
                        "select item_id,case when item_name like concat('%',real_category_name,'%') then 1 else 0 end as is_contain_cate\n" +
                        "from item_base lateral view explode(split(category_leaf_name,'\\/')) t as real_category_name\n" +
                        ") t\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "tit_is_contain_pro as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_name,\n" +
                        "sum(is_contain_pro_rep) as is_contain_pro_rep\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,item_name,\n" +
                        "case when item_name like concat('%',property_rep,'%') then 1 else 0 end as is_contain_pro_rep\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,item_name,property_set,required_property_set\n" +
                        "from\n" +
                        "(select item_id,item_name from item_base) item\n" +
                        "left join\n" +
                        "(\n" +
                        "select item_property.item_id,\n" +
                        "collect_set(regexp_replace(property_value,'\"','')) as property_set,\n" +
                        "collect_set(case when category_property.is_required=1 then regexp_replace(property_value,'\"','') else null end) as required_property_set\n" +
                        "from\n" +
                        "(\n" +
                        "select * from ytdw.dwd_item_property_d where dayid='20220929' and is_deleted=0 and property_value is not null and property_value != ''\n" +
                        "--部分商品没有属性，如305097，是因为类目没有属性需要填\n" +
                        ") item_property\n" +
                        "left join\n" +
                        "(\n" +
                        "select * from ytdw.dwd_category_property_d where dayid='20220929' and is_deleted=0\n" +
                        "--部分类目没有属性，如8011，8006，8007\n" +
                        ") category_property on category_property.id=item_property.category_property_id\n" +
                        "group by item_property.item_id\n" +
                        ") pro on pro.item_id=item.item_id\n" +
                        ") t\n" +
                        "lateral view explode(required_property_set) t3 as property_rep\n" +
                        ") tmp\n" +
                        "group by item_id,item_name\n" +
                        "),\n" +
                        "is_material as (\n" +
                        "select biz_value as item_id,count(distinct material_id) as cnt_material --卖货素材数\n" +
                        "from ytdw.dwd_material_relations_d\n" +
                        "where dayid='20220929' and biz_type=2 and is_deleted = 0\n" +
                        "group by biz_value\n" +
                        "),\n" +
                        "sale_service as (\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(0) as cnt\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "item.item_name,\n" +
                        "item.after_sale_tag,\n" +
                        "after_sale_tag.tag_name as after_sale_tag_name\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_id,\n" +
                        "item_name,\n" +
                        "after_sale_tag\n" +
                        "from ytdw.ods_t_item_d\n" +
                        "lateral view explode(split(after_sale_tags,',')) after_sale_tags as after_sale_tag\n" +
                        "where dayid = '20220929'\n" +
                        "--and bu_id = 0\n" +
                        "--and after_sale_tags is not null\n" +
                        "--and length(after_sale_tag) > 0\n" +
                        ") as item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id,\n" +
                        "tag_name\n" +
                        "from ytdw.dwd_item_after_sale_tag_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        ") as after_sale_tag\n" +
                        "on item.after_sale_tag = after_sale_tag.id\n" +
                        "--where item.item_name like '%惠氏启赋4段%'\n" +
                        ") t group by item_id\n" +
                        "),\n" +
                        "logistic_service as (\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(0) as cnt,\n" +
                        "concat_ws(',',collect_set(logistic_type_name)) as logistic_type_names\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "item.item_name,\n" +
                        "item.logistic_template_id,\n" +
                        "logistic_carry.logistic_type,\n" +
                        "case when logistic_carry.logistic_type = 1 then '快递'\n" +
                        "when logistic_carry.logistic_type = 2 then '物流到店'\n" +
                        "when logistic_carry.logistic_type = 3 then '物流自提'\n" +
                        "when logistic_carry.logistic_type = 4 then '供应商配送'\n" +
                        "when logistic_carry.logistic_type = 5 then '落地配'\n" +
                        "when logistic_carry.logistic_type = 6 then '品骏快递'\n" +
                        "when logistic_carry.logistic_type = 7 then '京东配送'\n" +
                        "when logistic_carry.logistic_type = 8 then '顺丰速递'\n" +
                        "when logistic_carry.logistic_type = 9 then '极兔速递'\n" +
                        "else null end as logistic_type_name\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_id,\n" +
                        "item_name,\n" +
                        "logistic_template_id\n" +
                        "from ytdw.dwd_item_full_d\n" +
                        "where dayid = '20220929'\n" +
                        "and logistic_template_id is not null\n" +
                        ") as item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "logistic_template_id,\n" +
                        "logistic_type\n" +
                        "from ytdw.dwd_logistic_carry_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "group by logistic_template_id, logistic_type\n" +
                        ") as logistic_carry on logistic_carry.logistic_template_id = item.logistic_template_id\n" +
                        ") t group by item_id\n" +
                        "),\n" +
                        "stage1 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "collect_set(property_value)[0] as milk_stage1,\n" +
                        "case\n" +
                        "when collect_set(property_value)[0] rlike '1段|一段|pre段' then '1段'\n" +
                        "when collect_set(property_value)[0] rlike '2段|二段' then '2段'\n" +
                        "when collect_set(property_value)[0] rlike '3段|三段|1\\\\+段|2\\\\+段' then '3段'\n" +
                        "when collect_set(property_value)[0] rlike '4段|四段|5段|五段|6段|六段|儿童' then '儿童'\n" +
                        "when collect_set(property_value)[0] rlike '成人' then '成人'\n" +
                        "else '其他' end as milk_stage2\n" +
                        "from ytdw.dwd_item_property_d\n" +
                        "where dayid='20220929'\n" +
                        "and property_name in ('适用段数','适用阶段')\n" +
                        "and is_deleted=0\n" +
                        "and property_value is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "weight1 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(collect_set(property_value)[0] as int) as weight\n" +
                        "from ytdw.dwd_item_property_d\n" +
                        "where dayid='20220929'\n" +
                        "and property_name in ('净重')\n" +
                        "and is_deleted=0\n" +
                        "and property_value is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "stage2 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_name,\n" +
                        "case\n" +
                        "when lower(item_name) rlike '1段|一段|pre段' then '1段'\n" +
                        "when item_name rlike '2段|二段' then '2段'\n" +
                        "when item_name rlike '3段|三段|1\\\\+段|2\\\\+段' then '3段'\n" +
                        "when item_name rlike '4段|四段|5段|五段|6段|六段|儿童' then '儿童'\n" +
                        "when item_name rlike '成人' then '成人'\n" +
                        "else '其他'\n" +
                        "end as milk_stage2,\n" +
                        "case when item_name like '%新包装%' then '新包装' else null end as pack,\n" +
                        "case when item_name like '%瘪罐%' or item_name like '%破盖%' then '瘪罐/破盖' else null end as damage,\n" +
                        "regexp_extract(item_name,'（(.{1,})）[^）]',1) as brackets1,\n" +
                        "regexp_extract(item_name,'【(.*)】[^】]',1) as brackets2,\n" +
                        "cast(case when regexp_extract(item_name,'(\\\\d{1,})[g|克|G]',1)!='' then\n" +
                        "regexp_extract(item_name,'(\\\\d{1,})[g|克|G]',1)\n" +
                        "else\n" +
                        "cast(regexp_extract(item_name,'(\\\\d{1,})[kg|千克|KG]',1) as int)*1000 end as int) as gram\n" +
                        "from item_base\n" +
                        "),\n" +
                        "loca as\n" +
                        "(\n" +
                        "select\n" +
                        "t1.item_id,\n" +
                        "concat_ws(',',collect_set(locality)) as locality,\n" +
                        "concat_ws(',',collect_set(t2.series_name)) as series_name\n" +
                        "from\n" +
                        "(\n" +
                        "select * from ytdw.dw_item_d WHERE dayid ='20220929' --and category_id_first_name='奶粉'\n" +
                        ") t1\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id,\n" +
                        "brand_id,\n" +
                        "series_name\n" +
                        "from ytdw.dwd_brand_series_d where dayid='20220929'\n" +
                        ") t2 on t2.id=t1.brand_series_id\n" +
                        "group by t1.item_id\n" +
                        "),\n" +
                        "item_pool as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(collect_set(item_pool_id)[0] as int) as item_pool_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_pool_id,\n" +
                        "item_id,\n" +
                        "pool_name as item_pool_name,\n" +
                        "pool_type\n" +
                        "from ytdw.dwd_item_pool_d\n" +
                        "lateral view explode(split(item_ids,',')) tmp as item_id\n" +
                        "where dayid='20220929' and is_deleted=0\n" +
                        ") t1 group by item_id\n" +
                        "),\n" +
                        "pay_price as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "pay_time,\n" +
                        "pay_price,\n" +
                        "avg_pay_price_7ds,\n" +
                        "avg_pay_price_15ds,\n" +
                        "avg_pay_price_30ds,\n" +
                        "avg_pay_price_90ds\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_actual_amount/item_count as pay_price,\n" +
                        "pay_time,\n" +
                        "row_number() over(partition by item_id order by pay_time desc,item_actual_amount/item_count) as rn,\n" +
                        "avg(case when pay_day>='20220922' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_7ds,\n" +
                        "avg(case when pay_day>='20220914' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_15ds,\n" +
                        "avg(case when pay_day>='20220830' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_30ds,\n" +
                        "avg(case when pay_day>='20220701' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_90ds\n" +
                        "from ytdw.dw_order_d\n" +
                        "where dayid='20220929' and pay_day>='20220701'\n" +
                        "and pay_time is not null\n" +
                        "--and category_1st_name in ('奶粉') and business_unit in ('大贸奶纸')\n" +
                        ") t1 where rn=1\n" +
                        "),\n" +
                        "mai_tbl as\n" +
                        "(\n" +
                        "select\n" +
                        "*\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(split(code_extend_fields['price'],'-')[0] as decimal(18,2)) as price,\n" +
                        "code_extend_fields['active_info_tag'] as active_info_tag,\n" +
                        "cast(regexp_extract(code_extend_fields['return_rate'],'(\\\\d{1,}\\\\.\\\\d{1,})',1) as decimal(18,2)) as return_rate,\n" +
                        "code_extend_fields['item_tags_info'] as item_tags_info,\n" +
                        "code_extend_fields['top_name'] as top_name,\n" +
                        "request_time,\n" +
                        "row_number() over(partition by item_id order by request_time desc) as rn\n" +
                        "from ytdw.dw_hpc_flw_search_area_exposure_di where dayid>=''\n" +
                        ") t where rn=1\n" +
                        "),\n" +
                        "is_store as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "collect_set(store_id)[0] as store_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "supply_id\n" +
                        "from ytdw.dwd_item_extra_d\n" +
                        "lateral view explode(split(supply_ids, ',')) t as supply_id\n" +
                        "where dayid = '20220929' and is_deleted = 0\n" +
                        ") item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as store_id,\n" +
                        "supplier_id\n" +
                        "from ytdw.dwd_supplier_store_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0 and store_type=2\n" +
                        ") supplier on item.supply_id = supplier.supplier_id\n" +
                        "where supplier.store_id is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "trade_shop as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "floor(pow(trade_count,0.9)*10) as trade_count\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "sum(popularity) as popularity,\n" +
                        "sum(sales_volume) as sales_volume,\n" +
                        "sum(trade_count) as trade_count\n" +
                        "from reco.st_item_popularity_d\n" +
                        "where dayid='20220929'\n" +
                        "group by item_id\n" +
                        ") t1\n" +
                        ")\n" +
                        "insert overwrite table reco.dws_item_base_info_d partition (dayid='20220929')\n" +
                        "select\n" +
                        "base.item_id,\n" +
                        "base.brand_id,\n" +
                        "base.category_1st_id,\n" +
                        "base.category_2nd_id,\n" +
                        "base.category_3rd_id,\n" +
                        "base.category_leaf_id,\n" +
                        "ticb.tit_is_brand as tit_is_contain_brand,\n" +
                        "ticc.is_contain_cate as tit_is_contain_cate,\n" +
                        "case when ticp.is_contain_pro_rep>0 then 1 else 0 end as tit_is_contain_pro,\n" +
                        "nvl(ticp.is_contain_pro_rep,0) as tit_is_contain_pro_cnt,\n" +
                        "ticb.tit_is_spe as tit_type,\n" +
                        "case when i_act.is_ny>0 then 1 else 0 end as is_ny,\n" +
                        "nvl(i_one_p.is_one_piece,0) as is_one_piece,\n" +
                        "i_one_p.is_video as is_video,\n" +
                        "i_one_p.pic_cnt as pic_cnt,\n" +
                        "case when im.cnt_material>0 then 1 else 0 end as is_material,\n" +
                        "case when i_act.item_id is not null then 1 else 0 end as is_activity,\n" +
                        "nvl(i_act.activity_cnt,0) as activity_cnt,\n" +
                        "case when ss.cnt>0 then 1 else 0 end as is_service,\n" +
                        "nvl(ss.cnt,0) as service_cnt,\n" +
                        "null as manufacture_date,\n" +
                        "null as manufacture_date_type,\n" +
                        "null as spec,\n" +
                        "null as item_stock,\n" +
                        "null as sku_cnt,\n" +
                        "ls.logistic_type_names as logistics_type,\n" +
                        "null as logistics_price,\n" +
                        "i_one_p.sale_type,\n" +
                        "case when i_sc.item_id is not null then 1 else 0 end as is_shop_coupon,\n" +
                        "base.item_name,\n" +
                        "s1.milk_stage1 as milk_stage_property,\n" +
                        "s2.milk_stage2 as milk_stage_title,\n" +
                        "cast(s2.gram as int),\n" +
                        "case when s2.pack is not null then 1 else 0 end as is_new_pack,\n" +
                        "case when s2.damage is not null then 1 else 0 end as is_damage,\n" +
                        "lc.series_name as series_name,\n" +
                        "s2.brackets1 as small_brackets_info,\n" +
                        "s2.brackets2 as middle_brackets_info,\n" +
                        "ip.item_pool_id as item_pool_id,\n" +
                        "pp.pay_price as max_pay_price,\n" +
                        "mt.price as max_exp_price,\n" +
                        "mt.active_info_tag as active_info_tag,\n" +
                        "ts.trade_count,\n" +
                        "mt.return_rate,\n" +
                        "case when iss.store_id is not null then 1 else 0 end as is_store,\n" +
                        "mt.top_name,\n" +
                        "mt.item_tags_info,\n" +
                        "case when s1.milk_stage2=s2.milk_stage2 then s2.milk_stage2 else '异常' end as valid_stage,\n" +
                        "w1.weight as property_gram,\n" +
                        "case when s2.gram=w1.weight then s2.gram else null end as valid_gram,\n" +
                        "pp.pay_time as max_pay_time,\n" +
                        "pp.avg_pay_price_7ds as avg_pay_price_7ds,\n" +
                        "pp.avg_pay_price_15ds as avg_pay_price_15ds,\n" +
                        "pp.avg_pay_price_30ds as avg_pay_price_30ds,\n" +
                        "pp.avg_pay_price_90ds as avg_pay_price_90ds\n" +
                        "from\n" +
                        "item_base base\n" +
                        "left join\n" +
                        "is_shop_coupon i_sc on i_sc.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_activity i_act on i_act.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_one_piece i_one_p on i_one_p.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_brand ticb on ticb.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_cate ticc on ticc.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_pro ticp on ticp.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_material im on im.item_id=base.item_id\n" +
                        "left join\n" +
                        "sale_service ss on ss.item_id=base.item_id\n" +
                        "left join\n" +
                        "logistic_service ls on ls.item_id=base.item_id\n" +
                        "left join\n" +
                        "stage1 s1 on s1.item_id=base.item_id\n" +
                        "left join\n" +
                        "stage2 s2 on s2.item_id=base.item_id\n" +
                        "left join\n" +
                        "loca lc on lc.item_id=base.item_id\n" +
                        "left join\n" +
                        "item_pool ip on ip.item_id=base.item_id\n" +
                        "left join\n" +
                        "pay_price pp on pp.item_id=base.item_id\n" +
                        "left join\n" +
                        "mai_tbl mt on mt.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_store iss on iss.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_shop_coupon isc on isc.item_id=base.item_id\n" +
                        "left join\n" +
                        "trade_shop ts on ts.item_id=base.item_id\n" +
                        "left join\n" +
                        "weight1 w1 on w1.item_id=base.item_id";



        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = Common.getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("ytdw.dwd_item_property_d", "ytdw.dwd_brand_series_d", "ytdw.dw_hpc_prm_activity_item_d", "ytdw.dwd_material_relations_d", "ytdw.dwd_item_full_d", "ytdw.dwd_item_pool_d", "ytdw.dwd_item_extra_d", "ytdw.dwd_logistic_carry_d", "ytdw.dwd_coupon_d", "ytdw.dw_item_d", "ytdw.dw_order_d", "ytdw.dwd_item_after_sale_tag_d", "ytdw.dw_hpc_flw_search_area_exposure_di", "ytdw.dwd_brand_d", "ytdw.dim_hpc_itm_item_d", "ytdw.dwd_category_property_d", "ytdw.dwd_item_d", "ytdw.ods_t_item_d", "reco.st_item_popularity_d", "ytdw.dwd_smc_promotion_scope_d", "ytdw.dwd_supplier_store_d").toJavaSet(), "reco.dws_item_base_info_d"));

        System.out.println(lineage);
    }

    @Test
    public void test4() {
        String sql = "SELECT col1 FROM " +
                "       (SELECT col11, SUM(col2) AS col2sum FROM t1  --commit  \n" +
                "        where id in (select id from t2)  GROUP BY col1) t2 WHERE t2.col2sum > 10 ";

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);
        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);
        TSelectSqlStatementVisitor tSelectSqlStatementVisitor = new TSelectSqlStatementVisitor();
        tCustomSqlStatement.accept(tSelectSqlStatementVisitor);
        System.out.println(tSelectSqlStatementVisitor.getSourceTableNames());
        System.out.println(tSelectSqlStatementVisitor.getTargetTableName());
        System.out.println(tSelectSqlStatementVisitor.getLimitSize());

    }
}
