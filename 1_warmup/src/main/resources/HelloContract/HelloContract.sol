pragma solidity ^0.4.0;

contract HelloContract {

    string greeting;

    constructor (string g) public{
        greeting = g;
    }

    event greeted(string name);

    function hello(string greeter) public returns (string) {
        emit greeted(greeter);
        return string(abi.encodePacked(greeting, ' ', greeter, '!'));
    }
}
