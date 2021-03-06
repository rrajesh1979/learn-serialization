package org.learn.serialization;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.learn.domain.Account;
import org.learn.domain.User;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JavaSerialization {
    static final MetricRegistry metrics = new MetricRegistry();
    public static final int NUM_ITERATIONS = 100000;
    static Histogram serializationHistogram = null;
    static Histogram deSerializationHistogram = null;

    public static void main(String[] args) {
        String fileName = null;
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(log)
                .build();
        reporter.start(1, TimeUnit.MINUTES);

        serializationHistogram = metrics.histogram("serializationTime " + Account.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + Account.class);
        fileName = "account.txt";
        Account account = new Account("John", "Doe", "1899 Johnstown Road, East Dundee, Illinois, 60118", 10002, 100000.00, "Savings");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new JavaSerialization().fileSerialization(fileName, account, Account.class);
        }

        serializationHistogram = metrics.histogram("serializationTime " + User.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + User.class);
        fileName = "user.txt";
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new JavaSerialization().fileSerialization(fileName, user, User.class);
        }
        reporter.report();

    }

    public void fileSerialization(String fileName, Object obj, Class ObjClass) {
        String filePath =
                "/Users/rrajesh1979/Dropbox/My Mac (Rajesh’s MacBook Pro)/Documents/Learn/gitrepo/serialization/learn-serialization/src/main/resources/protocols/"
                + fileName;

        StopWatch watch = new StopWatch();

        try {
            //start serialization
            watch.start();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
            watch.stop();
            long serializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            serializationHistogram.update(serializationTimeTaken);
//            log.info("[java-serialization-file] Serialization of {}} object took :: {} micro seconds", ObjClass, serializationTimeTaken);
            //end serialization

            //start de-serialization
            watch.reset();
            watch.start();
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            switch (ObjClass.getName()) {
                case "User":
                    User deserializedUser = (User) objectInputStream.readObject();
                    break;
                case "Account":
                    Account deserializedAccount = (Account) objectInputStream.readObject();
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
