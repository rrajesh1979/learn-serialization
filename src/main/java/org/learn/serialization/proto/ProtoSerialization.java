package org.learn.serialization.proto;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.learn.domain.Account;
import org.learn.domain.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProtoSerialization {
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
        fileName = "account.proto.data";
        AccountProtos.Account accountProto = AccountProtos.Account.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setAddress("1899 Johnstown Road, East Dundee, Illinois, 60118")
                .setAccountNumber(10002)
                .setAccountBalance(100000.00)
                .setAccountType("Savings")
                .build();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new ProtoSerialization().fileSerialization(fileName, accountProto, AccountProtos.Account.class);
        }

        serializationHistogram = metrics.histogram("serializationTime " + User.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + User.class);
        fileName = "user.proto.data";
        UserProtos.User userProto = UserProtos.User.newBuilder().setId(1).setName("Mark").build();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new ProtoSerialization().fileSerialization(fileName, userProto, UserProtos.User.class);
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
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            watch.start();
            switch (ObjClass.getName()) {
                case "org.learn.serialization.proto.UserProtos$User":
                    ((UserProtos.User)obj).writeTo(fileOutputStream);
                    break;
                case "org.learn.serialization.proto.AccountProtos$Account":
                    ((AccountProtos.Account)obj).writeTo(fileOutputStream);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            watch.stop();
            long serializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            serializationHistogram.update(serializationTimeTaken);
//            log.info("[java-serialization-file] Serialization of {}} object took :: {} micro seconds", ObjClass, serializationTimeTaken);
            //end serialization

            //start de-serialization
            FileInputStream fileInputStream = new FileInputStream(filePath);
            watch.reset();
            watch.start();
            UserProtos.User deserializedUser = null;
            AccountProtos.Account deserializedAccount = null;
            switch (ObjClass.getName()) {
                case "org.learn.serialization.proto.UserProtos$User":
                    deserializedUser = UserProtos.User.newBuilder().mergeFrom(fileInputStream).build();
                    break;
                case "org.learn.serialization.proto.AccountProtos$Account":
                    deserializedAccount = AccountProtos.Account.newBuilder().mergeFrom(fileInputStream).build();
            }
            fileInputStream.close();
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
