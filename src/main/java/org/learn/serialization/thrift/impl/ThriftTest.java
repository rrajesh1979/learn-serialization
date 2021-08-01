package org.learn.serialization.thrift.impl;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.io.*;

public class ThriftTest {
    public static void main(String[] args) {
        String fileName = "user.thrift.data";
        String filePath =
                "/Users/rrajesh1979/Dropbox/My Mac (Rajesh’s MacBook Pro)/Documents/Learn/gitrepo/serialization/learn-serialization/src/main/resources/protocols/"
                        + fileName;
        UserResource user = new UserResource();
        user.setId(1);
        user.setName("Mark");
        try {
            TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
            byte[] buf = serializer.serialize(user);
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(buf);
            fos.close();

            TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
            FileInputStream fileInputStream = new FileInputStream(filePath);
            UserResource userRead = new UserResource();
            deserializer.deserialize(userRead, fileInputStream.readAllBytes());
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
