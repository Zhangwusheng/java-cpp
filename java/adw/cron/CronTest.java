package com.aipai.adw.cron;

import com.cronutils.htime.HDateTimeFormatBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by zhangwusheng on 15/11/5.
 */
public class CronTest{

    public static void main ( String[] args ) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor ( CronType.UNIX );

        CronParser parser = new CronParser (cronDefinition);
        Cron quartzCron = parser.parse("30 0 1 1,6,12 * ");

        LocalDateTime now = LocalDateTime.now();
        DateTime dt = now.toDateTime ();
        ExecutionTime executionTime = ExecutionTime.forCron ( quartzCron );

        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
        DateTime lastExecution = executionTime.lastExecution(dt);
        CronField field = quartzCron.retrieve ( CronFieldName.HOUR );
        String ss = field.getExpression ().asString ();
        System.out.println (ss );
        //Get date for next execution
        DateTime nextExecution = executionTime.nextExecution(dt);

        String strDateOnly = dtf.print(lastExecution);
        String strDateOnly2 = dtf.print(nextExecution);

        System.out.println ( strDateOnly );
        System.out.println ( strDateOnly2 );

        //Time from last execution
        Duration timeFromLastExecution = executionTime.timeFromLastExecution(now.toDateTime( DateTimeZone.getDefault ( )));

        //Time to next execution
        Duration timeToNextExecution = executionTime.timeToNextExecution(now.toDateTime( DateTimeZone.getDefault ( )));

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern ( "MM/dd/yyyy" );

        DateTimeFormatter formatter =
                HDateTimeFormatBuilder
                        .getInstance ( )
                        .forJodaTime()
                        .getFormatter( Locale.US)
                        .forPattern("June 9, 2011");
        String formattedDateTime = formatter.print(lastExecution);

        System.out.println (formattedDateTime );
    }
}
