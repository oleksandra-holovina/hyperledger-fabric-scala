# HLF

### Modules

- **api** - akka http application that exposes 2 endpoints:
    - POST /business-date
    - GET /business-date
- **contracts** contains a smart contract written in Scala that saves and retrieves business-date from the chain
- **test-network**  
https://hyperledger-fabric.readthedocs.io/en/release-2.2/test_network.html#

### Running locally

#### 1. Bring up the network and deploy smart contracts.

```cd modules/test-network```

```./network.sh up -ca```

```./network.sh createChannel```

```./network.sh deployCC -ccn hlf -ccp ../contracts -ccl scala```

#### 2. In a separate terminal from the root folder run http server:

```sbt "project api" "runMain com.hlf.MainApp"```

#### 3. Once everything has started, in a separate terminal run:

```curl -X POST -H "Content-Type: application/json" -d "2022-22-12" http://localhost:8080/business-date```

```curl http://localhost:8080/business-date```



