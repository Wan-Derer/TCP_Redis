### **User manual**
- start your Redis server;
- start server (Redis address=localhost and port=6379 are hardcoded in this version);
- start one or more clients (TCP address=23 is hardcoded in this version).

In client terminal:
- enter server address (press 'Enter' for localhost);
- type commands (find supported commands below) and see server responses;
- use Redis terminal to correctness check;
- type 'exit' to client terminate (server will not terminated yet).

In server terminal:
- type 'exit' to server terminate.


### **Commands supported in this version**

**SET**

Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type. Any previous time to live associated with the key is discarded on successful SET operation.

Syntax:

    SET key value

    SET key value NX|XX

    SET key value NX|XX EX seconds

Options

The SET command supports a set of options that modify its behavior:

- EX seconds -- Set the specified expire time, in seconds.
- PX milliseconds -- Set the specified expire time, in milliseconds.
- NX -- Only set the key if it does not already exist.
- XX -- Only set the key if it already exist.
- KEEPTTL -- Retain the time to live associated with the key.

Return value: OK if SET was executed correctly.



**GET**

Get the value of key. If the key does not exist the special value nil is returned. An error is returned if the value stored at key is not a string, because GET only handles string values

Syntax: `GET key`

Return value: the value of key, or nil when key does not exist.



**DEL key [key ...]**

Time complexity: O(N) where N is the number of keys that will be removed. When a key to remove holds a value other than a string, the individual complexity for this key is O(M) where M is the number of elements in the list, set, sorted set or hash. Removing a single key that holds a string value is O(1).

Removes the specified keys. A key is ignored if it does not exist.

Return value: the number of keys that were removed.


**KEYS pattern**

Time complexity: O(N) with N being the number of keys in the database, under the assumption that the key names in the database and the given pattern have limited length.

Returns all keys matching pattern.

While the time complexity for this operation is O(N), the constant times are fairly low. For example, Redis running on an entry level laptop can scan a 1 million key database in 40 milliseconds.

Warning: consider KEYS as a command that should only be used in production environments with extreme care. It may ruin performance when it is executed against large databases. This command is intended for debugging and special operations, such as changing your keyspace layout. Don't use KEYS in your regular application code. If you're looking for a way to find keys in a subset of your keyspace, consider using SCAN or sets.

Supported glob-style patterns:

h?llo matches hello, hallo and hxllo
h*llo matches hllo and heeeello
h[ae]llo matches hello and hallo, but not hillo
h[^e]llo matches hallo, hbllo, ... but not hello
h[a-b]llo matches hallo and hbllo
Use \ to escape special characters if you want to match them verbatim.

Return value
Array reply: list of keys matching pattern.

