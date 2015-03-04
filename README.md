# Snow-Day-Alarm
Smart alarm for detecting snow days in CBSD

#Planned functionality
Android alarm application which will use the <a href="http://www.twitter.com/CBSDInfo">@CBSDInfo</a> twitter account to automatically disable or delay alarms in concordance with snow days or two hour delays.

#How it works
<h2>Finding snow days</h2>
To determine snow days/delays (I will refer to these as Special Days), the program first pulls the most recent tweets
from @CBSDInfo. Then, it uses a custom keyword based search (although a little more complex than that) which, insofar
as CBSD has formatted special day indicative tweets, works consistently.  Once a tweet is flagged as refering to a
special day, it uses the natty java library to pull a specific date from the tweet.  This date and its associated status are then loaded into a SQL Lite database on the users device, where they will be pulled on the correct day.
<h2>Alarm Templates</h2>
The actual alarm scheduling is handled by Alarm Templates which have a name, time, days of the week on which to trigger, and actions to take on a delay or cancellation - all stored in a database local to the device.  When they are made, they will schedule the next alarm they are responsible for (So if a template active on tues-fri at 3:00 is created on sunday, the app will schedule an alarm at 3:00 on tues).  Then, each time an alarm is triggered, its associated template is called upon to generate the next alarm and so on.  However, these alarms are not only registered with Android's Alarm Manager, but are stored in a separate database.
<h2>Daily Alarm Database</h2>
Since the Alarm Manger is limited in its ability to reliable maintain alarms (especially on reboots) and store information ot be used on alarm firing, a separate database is used to contain this info, and only the ID is passed in the alarm manager.  As such, on alarm events, the app can find the assoiciated alarm, the alarm's state etc.  Another important function of this database is to store day specific delay information, and for displaying the currrent day's alarms in the user interface.
