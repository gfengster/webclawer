package net.gf;

import java.net.URL;
import java.util.TreeMap;

public class WebCrawlerApp {

	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		}
		
		try {
			String urlString = null;
			int level = 2;
			if (args.length == 1 && "-help".equals(args[0])) {
				urlString = args[0];
			} 

			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
				case "-url":
					urlString = args[++i];
					break;
				case "-level":
					level = Integer.parseInt(args[++i]);
					break;
				case "-help":
					printHelp();
				}
			}
			
			final URL url = new URL(urlString);

			System.out.println("Search link begin with " + url + ". level is " + level);
			
			final WebCrawler crawler = new WebCrawler(url, level);
						
			new TreeMap<>(crawler.getSiteMap()).entrySet().stream().forEach(e -> {
				System.out.println(e.getKey());
				e.getValue().stream().forEach(t -> {
					System.out.println("\t" + t);
				});
			});
			
			System.out.println("Total links is " + crawler.find().size());
		} catch (Exception e) {
			e.printStackTrace();
			printHelp();
		}

	}

	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("-url {url} -level {level}");
		System.out.println("-url the url start to crawl with");
		System.out.println("-level the maximum level to crawl, default is 2");
		System.out.println("If level is negative, crawler will search whole site.");
		System.out.println("For example:");
		System.out.println("java -jar crawler-0.0.1-jar-with-dependencies.jar -url http://wiprodigital.com -level 2");
		
		System.exit(1);
	}
}
