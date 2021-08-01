package org.learn.serialization.thrift;

import org.apache.thrift.TException;
import org.learn.serialization.thrift.impl.UserResource;
import org.learn.serialization.thrift.impl.UserService;
import org.learn.serialization.thrift.impl.InvalidOperationException;

import java.util.Collections;
import java.util.List;

public class UserServiceImpl implements UserService.Iface {
    @Override
    public UserResource get(int id) throws InvalidOperationException, TException {
        // add some action
        return new UserResource();
    }

    @Override
    public void save(UserResource resource) throws InvalidOperationException, TException {
        // add some action
//        saveResource();
    }

    @Override
    public List<UserResource> getList() throws InvalidOperationException, TException {
        // add some action
        return Collections.emptyList();
    }

    @Override
    public boolean ping() throws InvalidOperationException, TException {
        return false;
    }
}
