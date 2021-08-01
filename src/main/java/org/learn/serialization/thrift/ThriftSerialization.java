package org.learn.serialization.thrift;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.learn.domain.Account;
import org.learn.domain.User;
import org.learn.serialization.thrift.impl.AccountResource;
import org.learn.serialization.thrift.impl.UserResource;

import java.io.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThriftSerialization {
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

        serializationHistogram = metrics.histogram("serializationTime " + AccountResource.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + AccountResource.class);
        fileName = "account.thrift.data";
        AccountResource account = new AccountResource();
        account.setSerialVersionUID(1L);
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setAddress("1899 Johnstown Road, East Dundee, Illinois, 60118");
        account.setAccountNumber(10002);
        account.setAccountBalance(100000.00);
        account.setAccountType("Savings");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new ThriftSerialization().fileSerialization(fileName, account, AccountResource.class);
        }

        serializationHistogram = metrics.histogram("serializationTime " + UserResource.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + UserResource.class);
        fileName = "user.thrift.data";
        UserResource user = new UserResource();
        user.setId(1);
        user.setName("Mark");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new ThriftSerialization().fileSerialization(fileName, user, UserResource.class);
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
            TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
            byte[] byteBuffer = null;
            switch (ObjClass.getName()) {
                case "org.learn.serialization.thrift.impl.UserResource":
                    byteBuffer = serializer.serialize((UserResource)obj);
                    break;
                case "org.learn.serialization.thrift.impl.AccountResource":
                    byteBuffer = serializer.serialize((AccountResource)obj);
            }
            fileOutputStream.write(byteBuffer);
            fileOutputStream.close();
            watch.stop();
            long serializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            serializationHistogram.update(serializationTimeTaken);
//            log.info("[java-serialization-file] Serialization of {}} object took :: {} micro seconds", ObjClass, serializationTimeTaken);
            //end serialization

            //start de-serialization
            TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
            FileInputStream fileInputStream = new FileInputStream(filePath);
            watch.reset();
            watch.start();
            switch (ObjClass.getName()) {
                case "org.learn.serialization.thrift.impl.UserResource":
                    UserResource userRead = new UserResource();
                    deserializer.deserialize(userRead, fileInputStream.readAllBytes());
                    break;
                case "org.learn.serialization.thrift.impl.AccountResource":
                    AccountResource accountRead = new AccountResource();
                    deserializer.deserialize(accountRead, fileInputStream.readAllBytes());
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
