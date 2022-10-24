package priv.hb.sample.example1;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;

public class GithubRepoPageProcessor implements PageProcessor {
    // private final Log log = LogFactory.getLog(getClass());


    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }


    public void echoLog() {
        // log.info("abc");
    }

    public static void main(String[] args) throws InterruptedException {
        // Thread.sleep(1000000L);
        Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft").thread(5).run();
        // GithubRepoPageProcessor.log.info("log:{}", "abc");

        // new GithubRepoPageProcessor().echoLog();




    }
}