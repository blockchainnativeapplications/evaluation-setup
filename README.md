Blockchain-native Applications
================================

This repository contains the sources used in the evaluation of the Master's thesis blockchain-native applications.
The `master` branch contains the tasks' sources as handed out to participants, the branch `solution` contains a sample solution.

Folder Structure
------------------

-  `1_warmup`, `2_ethereum` and `3_fabric`  
	Prepared projects for the tasks 1, 2 and 3
	
- `network`   
	Docker setup for the Ethereum and Hyperledger Fabric networks

- `documentation`   
	Javadoc of the framework blockchain-native applications
	
- `repository`   
	Maven repository containing the built framework


Build
-----

Use the command below in one of the project subfolders to build the corresponding project.

```
    ./gradlew build
```

In order to execute the projects use

```
    ./gradlew run
```

Troubleshooting
------------------

- Check out the common issues section in the assignment description