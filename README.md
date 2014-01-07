This project contains two aspects:
* Record web interactions from within a running jetty instance, and replay/diff later. Note that this logs everything, so make sure you're happy for all your client data to appear in the db (unencrypted etc)
* Record web interactions using a client proxy, and allow for replaying against multiple targets later and capturing output (diff will then be done externally)

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
* Run your tests in the browser, you should see the Proxy/History tab being populated as you go
* in the Proxy/History tab select the rows of requests that you'd like to replay later
* rightclick->Save items, say into requests.xml

### replay
* create a file (say) regexps.txt where you will store a list of regexps for massaging the urls (to switch server to dev etc)
* it is recommended to include the line "Accept-Encoding: gzip, ===Accept-Encoding: "
* add patterns to the file in the format "vg.no===google.com" which will transform any mention of vg.no to google.com (where === is the separator)
* launch BurpUrlFetcher by: 
	java BurpUrlFetcher requests.xml regexps.txt
* The output will be a list of files in the current directory 
    the name will be n-xxx where n is the request number and xxx is the http return code
    the content of the files will be the data that is retrieved
    
Using internal eclipse recorder
===============================

Purpose
-------
Allow capturing all requests (with headers and method) and responses (with headers and responseCode) into a db. Afterwards, you can launch to run the queries against another instance (with some optional regexp mangling) and compare the responses

Required Software
-----------------
* A db with a jdbc driver
* and you need to tweak you jetty configuration

Procedure
---------
### Prepare
* get yourself a database and create this table:
	CREATE  TABLE IF NOT EXISTS `mydb`.`qtable` (
      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
      `url` VARCHAR(1000) NOT NULL ,
      `method` VARCHAR(100) NOT NULL ,
      `headers` VARCHAR(2000) NOT NULL ,
      `responseCode` INT NOT NULL ,
      `responseHeaders` VARCHAR(2000) NOT NULL ,
      `responseBody` VARCHAR(60000) NOT NULL ,
      PRIMARY KEY (`id`) )
* Set up your jetty server to use the filter ResponseLogger (see HelloWorld for an example)
* Launch your jetty with these system properties set, default values in brackets: RequestLoggerDBDriver (com.mysql.jdbc.Driver), RequestLoggerDBUrl (jdbc:mysql://localhost:3306/mydb), RequestLoggerDBUser (root), RequestLoggerDBPass (no default)

### Record
* Make calls to the db server, and see the table in the db populate. Note that this will use resources in the webserver and synchronously write to the db, so make sure you test this in your QA environment

### Replay
* Please make sure you have regexps configured for translating any production hosts to qa..., and also firewalls to prevent mistakes
* launch DbUrlFetcher setting the same db properties as above, and
    java DbUrlFetcher regexps.txt
* The output will contain info about every url mentioned in the db (it compares returnCode, headers and the body)