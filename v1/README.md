This project contains three aspects:
* Record web interactions from within a running jetty instance, and replay/diff later. Note that this logs everything, so make sure you're happy for all your client data to appear in the db (unencrypted etc)
* Record using a standalone (probably) serverside proxy (othwise the same as above). Works for HTTP but not HTTPS
* Record web interactions using a client proxy, and allow for replaying against multiple targets later and capturing output (diff will then be done externally)

Using internal jetty recorder
=============================

Purpose
-------
Allow capturing all requests (with headers and method) and responses (with headers and responseCode) into a db. Afterwards, you can launch to run the queries against another instance (with some optional regexp mangling) and compare the responses

Required Software
-----------------
* A db with a jdbc driver (or implement IResponseLogger and set the ResponseLoggerClass property to the full classname)
* and you need to tweak your jetty configuration

Procedure
---------
### Prepare
* get yourself a database and create this table:
  <pre>
  CREATE  TABLE IF NOT EXISTS `mydb`.`qtable` (
    `id` BIGINT NOT NULL ,
    `url` VARCHAR(1000) NOT NULL ,
    `method` VARCHAR(100) NOT NULL ,
    `headers` VARCHAR(2000) NOT NULL ,
    `body` VARCHAR(4000) NOT NULL ,
    `responseCode` INT NOT NULL ,
    `responseHeaders` VARCHAR(2000) NOT NULL ,
    `responseBody` VARCHAR(56000) NOT NULL ,
    PRIMARY KEY (`id`) )
  </pre>
* Set up your jetty server to use the filter ResponseFilter (see HelloWorld for an example)
* Ensure your jdbc db driver is on the classpath (or alternative writer)
* Launch your jetty with these system properties set, default values in brackets: RequestLoggerDBDriver (com.mysql.jdbc.Driver), RequestLoggerDBUrl (jdbc:mysql://localhost:3306/mydb), RequestLoggerDBUser (root), RequestLoggerDBPass (no default)

### Record
* Make calls to the web server, and see the table in the db populate. Note that this will use resources in the webserver and synchronously write to the db, so make sure you test this in your QA environment

### Replay
* Please make sure you have regexps configured for translating any production hosts to qa..., and also ensure you have firewalls to prevent mistakes
* To handle sessionids and cookies to group together requests and propagate new ids during replay set the two properties 
    - -DcookiesToUpdate=<commaseparated list of cookieNames> - any Set-Cookie header in one reply will be propagated to future calls
    - -DcookiesToDrop=<commaseparated list of cookieNames> - prevents this cookie from being sent on any request 
* launch DbUrlFetcher setting the same db properties as above, and
    java DbUrlFetcher regexps.txt
* The output will contain info about every url mentioned in the db (it compares returnCode, headers and the body)

Using standalone proxy
======================

Purpose
-------
Allow capturing all requests (with headers and method) and responses (with headers and responseCode) into a db. Afterwards, you can launch to run the queries against another instance (with some optional regexp mangling) and compare the responses

Required Software
-----------------
* A db with a jdbc driver (or implement IResponseLogger and set the ResponseLoggerClass property to the full classname)

Procedure
---------
### Prepare
* create the same db table as described for the internal recorder
* Ensure your jdbc db driver is on the classpath (or alternative writer)
* launch <pre>java Proxy <host:port></pre> where the argument is the host and port to redirect the query to. If you need a different URL rewrite you have to modify the class Proxy)

### Record
* Make calls to the proxy, and see the table in the db populate. Note that this will gather the entire request/response in memory and then synchronously write it to the db, so make sure you test this in your QA environment

### Replay
See description of internal recorder

Using client proxy
==================

Purpose
-------
using a recording of a set of web requests, apply some regexps on the requests and store the responses. This will (separately) allow you to make two runs of the program against different backends and compare the result.

Required software
-----------------
Burp Suite Free Edition http://portswigger.net/index.html - provides a clientside proxy and allows for recording of requests to an xml file

Procedure
---------
### prepare
* launch BurpSuite
* go to the Proxy/Intercept tab and ensure that intercepts are disabled (the tool will later block otherwise)
* go to the Proxy/Options tab and add a proxy "127.0.0.1:8080" in the top "Proxy Listeners"
* Launch your browser and configure a the proxy to 127.0.0.1:8080

### record
* Run your tests in the browser, you should see the Proxy/History tab in Burp being populated as you go
* in the Proxy/History tab select the rows of requests that you'd like to replay later
* rightclick->Save items, say into requests.xml

### replay
* create a file (say) regexps.txt where you will store a list of regexps for massaging the urls (to switch server to dev etc)
* add patterns to the file in the format "vg.no===google.com" which will transform any mention of vg.no to google.com (where === is the separator)
* it is recommended to include the line "Accept-Encoding: gzip, ===Accept-Encoding: "
* launch BurpUrlFetcher by: 
	java BurpUrlFetcher requests.xml regexps.txt
* The output will be a list of files in the current directory 
  - the name will be n-xxx where n is the request number and xxx is the http return code
  - the content of the files will be the data that is retrieved
    