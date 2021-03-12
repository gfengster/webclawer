The project build/run needs java 8, maven 3.6.3.

1. Build the project

   `mvn clean package`

2. Run the application

   Usage:<br>
      -url {url} -level {level}<br>
      where:<br>
      -url the url start to crawl with<br>
      -level the maximum level to crawl, default is 2<br>
      If level is negative, crawler will search whole site.<br>

   `java -jar ./target/crawler-0.0.1-jar-with-dependencies.jar -help`<br>

   will print out this help.

   Some example crawling a web site.

   `java -jar crawler-0.0.1-jar-with-dependencies.jar -url http://wiprodigital.com -level 1`

   `java -jar crawler-0.0.1-jar-with-dependencies.jar -url http://wiprodigital.com`

   The output should looks like:

   `http://wiprodigital.com`<br>
   `____https://wiprodigital.com/xmlrpc.php?rsd`<br>
   `____https://wiprodigital.com/xmlrpc.php`

      

