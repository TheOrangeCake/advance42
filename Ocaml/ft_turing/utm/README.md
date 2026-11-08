# Universal Turing Machine
This directory is composed of 2 different applications:
- utm_builder
- machine_encoder


## utm_builder
This application is used to create the universal turing machine's JSON description. This application was coded because writing the JSON by hands seems really stupid and a really smart way to have a LOT of bugs in the UTM.

### Use
`./utm_builder > YOUR_FILE.json`


## machine_encoder
This application is used to format and encode a machine to have a string that is accepted by the UTM. 

### Restrictions
The machine CAN'T contains several characters that are reserved by the UTM.
The restricted characters are:
- ~
- |
- ;
- \#
- ^
- Space
- L
- R

### Use
`./machine_encoder YOUR_MACHINE.json INPUT`


## Compiling
`make` will compile the 2 applications
`make builder` to only compile the utm_builder
`make encoder` to only compile the machine_encoder
`make opt` will compile the 2 applications but only the native build
`make byt` will compile the 2 applications but only the bytecode build
