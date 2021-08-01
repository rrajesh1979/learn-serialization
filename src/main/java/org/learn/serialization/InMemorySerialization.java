package org.learn.serialization;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.learn.domain.Account;
import org.learn.domain.User;
import org.learn.playground.InstrumentationAgent;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemorySerialization {
    public static final int NUM_ITERATIONS = 1;
    static final MetricRegistry metrics = new MetricRegistry();
    static Histogram serializationHistogram = null;
    static Histogram deSerializationHistogram = null;

    public static void main(String[] args) {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(log)
                .build();
        reporter.start(1, TimeUnit.MINUTES);

        serializationHistogram = metrics.histogram("serializationTime " + Account.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + Account.class);
        Account account = new Account("John", "Doe", "1899 Johnstown Road, East Dundee, Illinois, 60118", 10002, 100000.00, "Savings");
        log.info("Object size of :: {} is :: {}", account.getClass().getName(), InstrumentationAgent.getObjectSize(account));
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new InMemorySerialization().fileSerialization(account, Account.class);
        }

        serializationHistogram = metrics.histogram("serializationTime " + User.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + User.class);
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        log.info("Object size of :: {} is :: {}", user.getClass().getName(), InstrumentationAgent.getObjectSize(user));
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new InMemorySerialization().fileSerialization(user, User.class);
        }
        reporter.report();


    }

    public void fileSerialization(Object obj, Class ObjClass) {
        StopWatch watch = new StopWatch();

        try {
            //start serialization
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            watch.start();
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
            watch.stop();
            long serializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            serializationHistogram.update(serializationTimeTaken);
//            log.info("[java-serialization-file] Serialization of {}} object took :: {} micro seconds", ObjClass, serializationTimeTaken);
            //end serialization

            //start de-serialization
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            watch.reset();
            watch.start();
            User deserializedUser = null;
            Account deserializedAccount = null;
            switch (ObjClass.getName()) {
                case "org.learn.domain.User":
                    deserializedUser = (User) objectInputStream.readObject();
                    break;
                case "org.learn.domain.Account":
                    deserializedAccount = (Account) objectInputStream.readObject();
            }
            objectInputStream.close();
            watch.stop();
            long deSerializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            deSerializationHistogram.update(deSerializationTimeTaken);
//            log.info("[java-serialization-file] De-Serialization of {}} object took :: {} micro seconds", ObjClass.getName(), deSerializationTimeTaken);
            //end de-serialization
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
