package priv.hb.sample.tool;

import org.junit.Test;

import java.util.BitSet;

import io.vavr.collection.List;

/**
 * bitmap(bitSet测试)
 * <p>
 * 当随着存储的元素越来越多，
 * BitSet内部会动态扩充，
 * 最终内部是由N个long来存储。
 *
 * @author hubin
 * @date 2022年09月06日 11:30
 */
public class BitSetTest {
    @Test
    public void testGetSetClear() {
        // 1.默认构造方法,位的大小默认为64
        BitSet bs = new BitSet();
        int num = 9;
        // 设置num后，查询num，应该存在
        bs.set(num);
        boolean exist = bs.get(num);
        System.out.println(num + " set,    exist= " + exist);

        System.out.println(bs);

        // 清除num后，查询num，应该不存在
        bs.clear(num);
        exist = bs.get(num);
        System.out.println(num + " remove, exist= " + exist);
    }

    @Test
    public void testLogicOperation() {
        BitSet bs1 = new BitSet();
        BitSet bs2 = new BitSet();


        // 添加一些整数到BitSet中
        for (int i = 0; i < 11; i++) {
            if ((i % 2) == 0) {
                bs1.set(i);
            }
            if ((i % 5) != 0) {
                bs2.set(i);
            }
        }


        System.out.println("bs1: " + bs1);
        System.out.println("bs2: " + bs2);


        // 备份原始的bs2，下面的逻辑操作会修改bs2
        BitSet bs2Origin = BitSet.valueOf(bs2.toLongArray());

        // 1.AND 交集，取出相同的数字
        bs2.and(bs1);
        System.out.println("bs2 AND bs1: " + bs2);

        // 2.OR 并集，取出所有的数字
        bs2 = BitSet.valueOf(bs2Origin.toLongArray());
        bs2.or(bs1);
        System.out.println("bs2 OR bs1: " + bs2);

        // 3.XOR 差集， bs2和bs1中不相同的数字集合
        bs2 = BitSet.valueOf(bs2Origin.toLongArray());
        bs2.xor(bs1);
        System.out.println("bs2 XOR bs1: " + bs2);

        // io.vavr.collection.BitSet
    }

    @Test
    public void testSortAndUniqueArray() {
        int[] array = new int[]{1, 2, 3, 29, 22, 0, 3, 29};
        BitSet bitSet = new BitSet(array.length);


        for (int i = 0; i < array.length; i++) {
            bitSet.set(array[i], true);
        }


        System.out.println("bitset可以存储的数量：" + bitSet.size());
        System.out.println("bitset实际存储的数量：" + bitSet.cardinality());
        System.out.println("排序去重后：" + bitSet);


        // 使用迭代器访问数据
        String mkString = List.ofAll(bitSet.stream().toArray()).mkString(",");
        System.out.println(mkString);

    }

    @Test
    public void test2() {
        io.vavr.collection.BitSet<Integer> bitSet = io.vavr.collection.BitSet.of(1, 2, 3, 29, 22, 0, 3, 29);
        System.out.println(bitSet.size());
        System.out.println(bitSet);

        io.vavr.collection.BitSet<Integer> bitSet1 = io.vavr.collection.BitSet.of(0, 2, 4, 6, 8, 10);
        io.vavr.collection.BitSet<Integer> bitSet2 = io.vavr.collection.BitSet.of(1, 2, 3, 4, 6, 7, 8, 9);

        System.out.println(bitSet1.diff(bitSet2));

    }
}
