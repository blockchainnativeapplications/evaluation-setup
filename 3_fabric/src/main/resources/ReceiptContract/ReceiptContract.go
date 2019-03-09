package main // package main is important, otherwise no executable but instead a linkable library is built

import (
	"fmt"
	"strings"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SimpleChaincode struct {
}

var logger = shim.NewLogger("receipt")

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	logger.Info("Init")
	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()

	logger.Infof("- Invoke: %s", function)

	switch function {

	case "storeReceipt":

		if len(args) < 1 {
			return shim.Error("storeReceipt requires one argument")
		}

		newHash := args[0]

		hashesBytes, error := stub.GetState("receiptHashes")

		if error != nil {
			return shim.Error(fmt.Sprintf("failed to retrieve receipt hashes: %s", error))
		}

		hashes := strings.Split(string(hashesBytes), ",")

		hashes = append(hashes, newHash)

		stub.SetEvent("receiptAdded", []byte(newHash))

		return shim.Success([]byte(""))

	case "getReceiptHashes":

		hashesBytes, error := stub.GetState("receiptHashes")

		if error != nil {
			return shim.Error(fmt.Sprintf("failed to retrieve receipt hashes: %s", error))
		}

		return shim.Success(hashesBytes)

	default:
		return shim.Success([]byte("Unsupported operation"))
	}
}

func main() {
	logger.SetLevel(shim.LogDebug)
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		logger.Criticalf("Error starting chaincode: %s", err)
	}
}