pragma solidity ^0.4.0;

contract ReceiptContract {

    bytes32[] public receiptHashes;

    event ReceiptAdded(
        bytes32 indexed sha256Hash
    );

    function storeReceipt(bytes32 sha256Hash) public {
        receiptHashes.push(sha256Hash);
    }

    function getReceiptHashes() public view returns (bytes32[]){
        return receiptHashes;
    }
}
