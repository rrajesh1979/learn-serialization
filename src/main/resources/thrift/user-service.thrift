namespace cpp org.learn.serialization.thrift.impl
namespace java org.learn.serialization.thrift.impl

exception InvalidOperationException {
    1: i32 code,
    2: string description
}

struct UserResource {
    1: i32 id,
    2: string name,
    3: i64 serialVersionUID
}

service UserService {

    UserResource get(1:i32 id) throws (1:InvalidOperationException e),

    void save(1:UserResource resource) throws (1:InvalidOperationException e),

    list <UserResource> getList() throws (1:InvalidOperationException e),

    bool ping() throws (1:InvalidOperationException e)
}