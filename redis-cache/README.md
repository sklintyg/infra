# Testing with Redis

If you're not running an redis instance locally, you may use one from your local minishift.

Forward port 6379 from openshift to localhost (127.0.0.1):

    $ oc port-forward redis-2-khfv4 6379:6379
    
Then just use host and port localhost:6379 or 127.0.0.1:6379

# Running redis-sentinel locally

In this little tutorial we'll set up Redis Sentinel with 2 redis-server instances running on port 7000 and 7001, and 3 redis sentinel instances running on ports 5000, 5001 and 5002.

Make sure you have installed redis locally, for example using homebrew.

Create a folder somewhere and then create three files called sentinel1.conf, sentinel2.conf and sentinel3.conf

_sentinel1.conf_:

    port 5000
    sentinel monitor mymaster 127.0.0.1 7000 2
    sentinel down-after-milliseconds mymaster 5000
    sentinel failover-timeout mymaster 60000
    protected-mode no
    
For 2 and 3, change port 5000 to 5001 and 5002 respectively

Open 5 terminal windows in the current folder. Run each command in a separate window:

    > redis-server --port 7000
    > redis-server --port 7001
    > redis-sentinel sentinel1.conf
    > redis-sentinel sentinel2.conf
    > redis-sentinel sentinel3.conf
    
Your redis-sentinel solution should now be up and running.

To configure an Intygstjänster application to connect to Redis Sentinel, enable the Spring profile "redis-sentinel" and configure the application like this:

    redis.host=127.0.0.1;127.0.0.1;127.0.0.1
    redis.port=5000;5001;5002
    redis.sentinel.master.name=mymaster
    
DONE!!
