# Hyperledger Fabric

Starts a Hyperledger Fabric network consisting of a single orderer and two organizations with one peer each.

In order to start the network execute:

```
docker-compose up
```

The startup process takes up to a minute. As soon as no new messages are written to the console for like 10 seconds, the network is ready to be used.

Stop the network via 


```
docker-compose down
```

## Note

When installing chaincode to peers of the network, a new docker image is created.
The names of those images start with "dev-<name-of-the-peer>-<name-of-the-chaincode>".
If there were errors during the deployment, make sure to remove those images and restart the network.