package junit5;

import org.junit.Assert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author hubin
 * @date 2022年08月03日 16:48
 */
public class Test1 {
    @Test
    public void tet1() {
        Assertions.assertTrue(1 == 1);
    }

    /**
     * 参数化测试，使用不同的参数多次运行。
     *
     * @param value
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100})
    public void test2(int value) {
        System.out.println(value);
        Assertions.assertTrue(value > 0);
    }

    /**
     * 参数化测试,使用csv格式来输入
     * @param name
     * @param address
     */
    @ParameterizedTest(name = "case-{index}")
    // @formatter:off
    @CsvSource(value = {
            "hb  | hangzhou",
            "fsl | tonglu" }, delimiter = '|')
    // @formatter:on
    public void ParameterTest(String name, String address) {
        System.out.println("name:" + name + " ,address: " + address);

    }


    /**
     * 重复测试，括号中的值为重复的次数
     */
    @Test
    @RepeatedTest(5)
    public void test3() {
        Assertions.assertTrue(1 > 0);
    }


    @Test
    @DisplayName("第一个测试方法")
    public void test() {
        System.out.println("Ghế gấp Bắc Kinh, ghế mặt trăng ngoài trời, ghế cắm trại cao cấp, ghế recliner di động, ghế giải trí, đi biển, câu cá, dã ngoại.".length());
        Assertions.assertTrue(true);

    }


    @Test
    public void test1() {
        String i = "insert overwrite table ods_search_dataworks_test1 partition (ds='20220802')\\nselect\\n\\tt7911.brand as brand,\\n\\tt7912.cn_name as brand_cn_name,\\n\\tt7912.en_name as brand_en_name,\\n\\tt7912.name as brand_name,\\n\\tt7911.category as category,\\n\\tt7914.name as category_first_name,\\n\\tt7911.category_id_first as category_id_first,\\n\\tt7911.category_id_second as category_id_second,\\n\\tt7911.category_id_third as category_id_third,\\n\\tt7913.name as category_name,\\n\\tt7915.name as category_second_name,\\n\\tt7916.name as category_third_name,\\n\\tt7917.threshold as threshold,\\n\\tt7917.power as power,\\n\\tt7911.id as id,\\n\\tt7911.item_describe as item_describe,\\n\\tt7911.id as item_id,\\n\\tt7911.item_name as item_name,\\n\\tt7911.picture as picture,\\n\\tt7911.search_fuzzy_word as search_fuzzy_word,\\n\\tt7912.id as t_brand_id,\\n\\tt7914.id as t_category_first_id,\\n\\tt7913.id as t_category_id,\\n\\tt7915.id as t_category_second_id,\\n\\tt7916.id as t_category_third_id,\\n\\tt7917.id as sync_search_flow_item_d_id\\nfrom (select * from odps_t_item where ds=20220802 ) as t7911\\nleft join (select * from odps_t_brand where ds=20220802 ) as t7912 on t7911.brand= t7912.id\\nleft join (select * from odps_t_category where ds=20220802 ) as t7913 on t7911.category= t7913.id\\nleft join (select * from odps_t_category where ds=20220802 ) as t7914 on t7911.category_id_first= t7914.id\\nleft join (select * from odps_t_category where ds=20220802 ) as t7915 on t7911.category_id_second= t7915.id\\nleft join (select * from odps_t_category where ds=20220802 ) as t7916 on t7911.category_id_third= t7916.id\\nleft join (select * from odps_sync_search_flow_item_d where ds=20220802 ) as t7917 on t7911.id= t7917.item_id";
        System.out.println(i.replace("\\n", "").replace("\\t", ""));
    }


}
