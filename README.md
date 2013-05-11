play-twitter-api
================

Twitter API sample application using twitter4j on Play! framework (v2.1.0) .

Env
------

+  Play! framework version 2.1.0
+  sbt version 0.12.2
+  JDK 1.6_x
+  MySQL
+  twitter4j-core version 3.0.3
 
how-to-use
------
### 1. create twitter account ###
　Please create Twiiter account if you don't have one .

### 2. create My application ###
　Visit  [https://dev.twitter.com/apps](https://dev.twitter.com/apps "twitter dev") , and create new My application .  

Set  

+   `Website` :
    _http://127.0.0.1:9000_  

+   `Callback URL` :
    _http://127.0.0.1:9000/twitter/callback_ 


### 3. MySQL configuration ###
　On your mysql console  

    CREATE DATABASE twitter_api_play;

　Then on ProjectRoot/conf/application.conf  line 48 ~ 50  

    # for local mysql
    db.default.url="jdbc:mysql://127.0.0.1:3306/twitter_api_play?characterEncoding=UTF8&connectionCollation=utf8mb4_general_ci"
    db.default.user="Here is your mysql user name"
    db.default.password="Here is your mysql user passwd"

Set  

+   `db.default.user` :
    _user name of database_  

+   `db.default.password` :
    _passwd of user_ 


Have fun .
