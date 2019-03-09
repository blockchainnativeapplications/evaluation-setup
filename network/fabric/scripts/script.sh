#!/bin/bash
CHANNEL_NAME=$1
DELAY=2
FABRIC_START_TIMEOUT=5

sleep ${FABRIC_START_TIMEOUT}

# import utils
. scripts/util.sh

echo "======================================================"
echo "===========  Create channel $CHANNEL_NAME  ==========="
echo "======================================================"

# Create the channel
CORE_PEER_LOCALMSPID=Org1MSP
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
CORE_PEER_ADDRESS=peer0.org1.example.com:7051
set -x
peer channel create -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx
res=$?
set +x

verifyResult $res "Failed to create channel $CHANNEL_NAME"

# Join peer0.org1.example.com to channel
sleep $DELAY

echo "======================================================"
echo "==============  Join peers to channel  ==============="
echo "======================================================"

CORE_PEER_LOCALMSPID=Org1MSP
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
CORE_PEER_ADDRESS=peer0.org1.example.com:7051
set -x
executeWithRetry "peer channel join -b $CHANNEL_NAME.block"
res=$?
set +x
verifyResult $res "Failed to join $CORE_PEER_ADDRESS to channel $CHANNEL_NAME"

# Join peer0.org2.example.com to channel
sleep $DELAY

CORE_PEER_LOCALMSPID=Org2MSP
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
CORE_PEER_ADDRESS=peer0.org2.example.com:7051
set -x
executeWithRetry "peer channel join -b $CHANNEL_NAME.block"
res=$?
set +x
verifyResult $res "Failed to join $CORE_PEER_ADDRESS to channel $CHANNEL_NAME"

echo "======================================================"
echo "=========  Update anchor peers to channel  ==========="
echo "======================================================"

# Update anchor peer for org1.example.com
sleep $DELAY

CORE_PEER_LOCALMSPID=Org1MSP
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
CORE_PEER_ADDRESS=peer0.org1.example.com:7051
set -x
peer channel update -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/${CORE_PEER_LOCALMSPID}anchors.tx
res=$?
set +x
verifyResult $res "Failed to update anchor peer for org1"

# Update anchor peer for org2.example.com
sleep $DELAY

CORE_PEER_LOCALMSPID=Org2MSP
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
CORE_PEER_ADDRESS=peer0.org2.example.com:7051
set -x
peer channel update -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/${CORE_PEER_LOCALMSPID}anchors.tx
res=$?
set +x
verifyResult $res "Failed to update anchor peer for org2"


