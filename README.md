# Snow-Day-Alarm
Smart alarm for detecting snow days in CBSD

#Planned functionality
Android alarm application which will use the <a href="http://www.twitter.com/CBSDInfo">@CBSDInfo<\a> to automatically
disable or delay alarms in concordance with snow days or two hour delays.

#How it works
<h2>Finding snow days</h2>
To determine snow days/delays (I will refer to these as Special Days), the program first pulls the most recent tweets
from @CBSDInfo. Then, it uses a custom keyword based search (although a little more complex than that) which, insofar
as CBSD has formatted special day indicative tweets, works consistently.  Once a tweet is flagged as refering to a
special day, it uses the natty java library to pull a specific date from the tweet.  This date and its associated status
are then loaded into a SQL Lite database on the users device, where they will be pulled on the correct day.
<h2>Triggering alarms</h2>
While I have not yet reached this point in the project, I believe the alarms active on a specific day will be loaded
into a separate database containing date specific information
