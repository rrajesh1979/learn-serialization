package org.learn.serialization;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.learn.domain.Account;
import org.learn.domain.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KryoSerialization {
    public static final int NUM_ITERATIONS = 100000;
    static final MetricRegistry metrics = new MetricRegistry();
    static Histogram serializationHistogram = null;
    static Histogram deSerializationHistogram = null;

    static Kryo kryo = new Kryo();


    public static void main(String[] args) {
        kryo.register( Account.class );
        kryo.register( User.class );

        String fileName = null;
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);

        serializationHistogram = metrics.histogram("serializationTime " + Account.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + Account.class);
        fileName = "account.dat";
        Account account = new Account("John", "Doe", "1899 Johnstown Road, East Dundee, Illinois, 60118", 10002, 100000.00, "Savings");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new KryoSerialization().kryoSerialization(fileName, account, Account.class);
        }

        serializationHistogram = metrics.histogram("serializationTime " + User.class);
        deSerializationHistogram = metrics.histogram("deSerializationTime " + User.class);
        fileName = "user.dat";
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            new KryoSerialization().kryoSerialization(fileName, user, User.class);
        }
        reporter.report();

    }

    public void kryoSerialization(String fileName, Object obj, Class ObjClass) {
        String filePath =
                "/Users/rrajesh1979/Dropbox/My Mac (Rajesh’s MacBook Pro)/Documents/Learn/gitrepo/serialization/learn-serialization/src/main/resources/protocols/"
                        + fileName;
        StopWatch watch = new StopWatch();

        try {
            //start serialization
            watch.start();
            Output output = new Output(new FileOutputStream(filePath));
            kryo.writeClassAndObject(output, obj);
            output.close();
            watch.stop();
            long serializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            serializationHistogram.update(serializationTimeTaken);
            //end serialization

            //start de-serialization
            watch.reset();
            watch.start();
            Input input = new Input(new FileInputStream(filePath));
            switch (ObjClass.getName()) {
                case "User":
                    User readUser = (User)kryo.readClassAndObject(input);
                    break;
                case "Account":
                    Account readAccount = (Account)kryo.readClassAndObject(input);
            }
            input.close();
            watch.stop();
            long deSerializationTimeTaken = watch.getTime(TimeUnit.MICROSECONDS);
            deSerializationHistogram.update(deSerializationTimeTaken);
            //end de-serialization
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
