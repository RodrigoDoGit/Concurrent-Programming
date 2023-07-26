import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Concurrent crawler.
 *
 */
public class ConcurrentCrawler extends AbstractCrawler {

  public static void main(String[] args) throws Exception {
    int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
    String rootPath = args.length > 1 ? args[1] : "http://localhost:8123/";
    ConcurrentCrawler cc = new ConcurrentCrawler(threads);
    cc.setVerboseOutput(true);
    cc.crawl(new URL(rootPath));
    cc.stop();
  }

  private final ExecutorService pool;
  private final Set<URL> seen_urls = Collections.synchronizedSet(new HashSet<>());
  private final AtomicInteger request_id = new AtomicInteger(0);
  private final AtomicInteger pending_url_count = new AtomicInteger(0);

  public ConcurrentCrawler(int threads) throws IOException {
    pool = Executors.newFixedThreadPool(threads);
  }

  public void stop() throws Exception { 
    pool.shutdown();
  }
  
  @Override
  public void crawl(URL root) {
    long t = System.currentTimeMillis();
    log("Starting at %s", root);

    // Spawn first task
    seen_urls.add(root);
    pending_url_count.getAndIncrement();
    pool.submit(new CrawlTask(root));

    // Wait for all tasks to terminate
    while (pending_url_count.get() > 0) {
      try {
        Thread.sleep(1);
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    } 
    t =  System.currentTimeMillis() - t;
    int n = request_id.get();
    log("Done: %d transfers in %d ms (%.2f transfers/s)", n,
        t, (1e+03 * n) / t);
  }

  private class CrawlTask implements Runnable {

    final int rid;
    final URL url;

    CrawlTask(URL url) {
      this.rid = request_id.incrementAndGet(); 
      this.url = url;
    }

    @Override
    public void run() { 
      File htmlContents = download(rid, url);
      if (htmlContents != null) {
        for (URL newURL : parseLinks(url, htmlContents)) {
          if (seen_urls.add(newURL)) {
            pending_url_count.getAndIncrement();
            pool.submit(new CrawlTask(newURL));
          } 
        }
      }
      pending_url_count.getAndDecrement();
    }
  }
}
