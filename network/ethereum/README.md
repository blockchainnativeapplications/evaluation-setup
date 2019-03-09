# Ethereum

Starts a private Ethereum network consisting a single miner.

In order to start the network execute:

```
docker-compose up
```

The startup process takes a few minutes. Once the  message "Generating DAG in progress epoch=0" with "percentage=100%" appears, the network is ready to be used.

Stop the network via 


```
docker-compose down
```