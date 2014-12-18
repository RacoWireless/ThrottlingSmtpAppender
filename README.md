ThrottlingSmtpAppender
======================

Provides a way to throttle the log4net SMTP appender to not send out multiple similar emails

```xml
<appender name="SmtpAppender" type="Log4Net.ThrottlingSmtpAppender.ThrottlingSmtpAppender, Log4Net.ThrottlingSmtpAppender">
  <matchCoefficient>.5</matchCoefficient>
  <cacheTimeoutMinutes>5</cacheTimeoutMinutes>
  <bufferSize value="10" />
  <evaluator type="log4net.Core.LevelEvaluator">
	<threshold value="ERROR" />
  </evaluator>
  <from value="from@email.com" />
  <layout type="log4net.Layout.PatternLayout">
	<param name="ConversionPattern" value="%d{yyyy-MM-dd hh:mm:ss.ff tt} [%-5level]  %m%n" />
  </layout>
  <lossy value="true" />
  <smtpHost value="127.0.0.1" />
  <subject value="Email Subject" />
  <to value="to@email.com" />
</appender>
```

There are two new configuration options added to the standard SmtpAppender
* matchCoefficient
 * Determines how closely two strings must match for them to be considered equal (as determined by DuoVia.FuzzyStrings.  The value 0.5 is a good place to start
* cacheTimeoutMinutes
 * How many minutes before a string is expired from the cache, and a new email would be sent.
