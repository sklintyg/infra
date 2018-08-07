# Testing with Redis

If you're not running an redis instance locally, you may use one from your local minishift.

Forward port 6379 from openshift to localhost (127.0.0.1):

    $ oc port-forward redis-2-khfv4 6379:6379
    
Then just use host and port localhost:6379 or 127.0.0.1:6379

