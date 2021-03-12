package net.gf;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	private static final String MAILTO = "mailto:";
	private static final String HREF = "href";
	private static final String HTTP = "http";

	private static final int HTTP_OK = 200;
	private static final int WIDTH = 100;

	private final URL url;
	private final int level;

	private final String host;
	private final String httpHost;
	private final String httpsHost;

	private volatile int process = 0;

	private final Set<String> links = new CopyOnWriteArraySet<>();

	private final Map<String, Set<String>> siteMap = new ConcurrentHashMap<>();

	public WebCrawler(URL url, int level) {
		this.url = url;
		this.level = level;

		this.host = this.url.toString();
		this.httpHost = "http://www." + this.url.getHost();
		this.httpsHost = "https://" + this.url.getHost();

		links.add(this.host);

		find(this.host, 0);

		System.out.println();
	}

	public Set<String> find() {
		return this.links;
	}

	public Map<String, Set<String>> getSiteMap() {
		return this.siteMap;
	}

	private void find(final String url, final int level) {
		if (this.process >= WIDTH) {
			System.out.println(".");
			this.process = -1;
		} else
			System.out.print(".");

		this.process++;

		if (this.level > 0 && level >= this.level)
			return;

		final int nextLevel = level + 1;

		final Connection connection = Jsoup.connect(url);

		Response response = null;
		try {
			response = connection.execute();
		} catch (IOException e) {
			return;
		}

		if (response.statusCode() != HTTP_OK) {
			return;
		}

		try {
			final Document document = response.parse();

			final Elements elements = document.getElementsByAttribute(HREF);

			final Set<String> hrefs = new HashSet<>();

			for (Element element : elements) {
				String href = element.attr(HREF);

				// Don't add mailto
				if (href.startsWith(MAILTO))
					continue;

				// Counting relative link
				if (!href.startsWith(HTTP)) {
					if (url.endsWith("/"))
						href = url + href;
					else {
						if (href.startsWith("/"))
							href = url.substring(url.lastIndexOf("/")) + href;
						else
							href = url.substring(url.lastIndexOf("/")) + "/" + href;
					}
				}

				// Crawl ready
				if (links.contains(href))
					continue;
				
				// Add same domain link for next level crawl
				if (href.startsWith(this.httpsHost) || href.startsWith(this.host) || href.startsWith(this.httpHost)) {
					hrefs.add(href);

					Set<String> subHref = siteMap.get(url);
					if (subHref == null) {
						subHref = new HashSet<>();
						siteMap.put(url, subHref);
					}

					subHref.add(href);
				}
			}

			links.addAll(hrefs);

			hrefs.stream().forEach(t -> {
				find(t, nextLevel);
			});
		} catch (Exception e) {
			throw new RuntimeException(url, e);
		}

		return;
	}
}
