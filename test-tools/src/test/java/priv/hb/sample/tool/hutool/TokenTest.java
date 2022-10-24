package priv.hb.sample.tool.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;

/**
 * 中文分词器测试
 * @author hubin
 * @date 2022年10月14日 19:07
 */
public class TokenTest {
    @Test
    public void test1() {
        //自动根据用户引入的分词库的jar来自动选择使用的引擎
        TokenizerEngine engine = TokenizerUtil.createEngine();

        //解析文本
        String text = "这两个方法的区别在于返回值";
        Result result = engine.parse(text);
        //输出：这 两个 方法 的 区别 在于 返回 值
        for (Word word : result) {
            System.out.println(word);
        }
    }
}
