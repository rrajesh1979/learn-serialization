namespace cpp org.learn.serialization.thrift.impl
namespace java org.learn.serialization.thrift.impl

exception InvalidOperationException {
    1: i32 code,
    2: string description
}

struct AccountResource {
    1: i64 serialVersionUID,
    2: string firstName,
    3: string lastName,
    4: string address,
    5: i32 accountNumber,
    6: double accountBalance,
    7: string accountType
}

service AccountService {

    AccountResource get(1:i32 accountNumber) throws (1:InvalidOperationException e),

    void save(1:AccountResource resource) throws (1:InvalidOperationException e),

    list <AccountResource> getList() throws (1:InvalidOperationException e),

    bool ping() throws (1:InvalidOperationException e)
}