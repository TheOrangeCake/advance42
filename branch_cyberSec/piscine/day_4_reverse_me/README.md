# Set up
```sh
./level1 -> cannot execute: required file not found
./level2 -> cannot execute: required file not found
```
I have thought that the binary required an arrgument to run. Turn out level 1 and level 2 are 32 bit binaries and the computer used is 64 bits.
So we need to add support for 32 bits architecture to able to run the program:
```sh
sudo dpkg --add-architecture i386
sudo apt update
sudo apt install libc6:i386
```

---
# Level 1
Step by step to reverse engineering the program:
- Step 1: run [ltrace](https://www.man7.org/linux/man-pages/man1/ltrace.1.html) (A call tracer that show library call and its parameters)
```sh
ltrace ./level1

Result:
__libc_start_main(0x64c111c0, 1, 0xffbc0324, 0 <unfinished ...>
printf("Please enter key: ")                            = 18
__isoc99_scanf(0x64c12029, 0xffbc01fc, 0x64c14000, 0x5f5f0001Please enter key: test
) = 1
strcmp("test", "__stack_check")                         = 1
printf("Nope.\n"Nope.
)                                       = 6
+++ exited (status 0) +++
```
- Step 2: analyse the result. Just by interpreting the tracer, we can see the program called:
    - `main()`
    - `printf("Please enter key: ")`
    - `scanf("Please enter key:")` (I have entered a random key)
    - `strcmp("test", "__stack_check")` -> This is the most important finding, it compares my input with "__stack_check" which is the password that we need to find
    - `printf("Nope.\n")` (Obviously wrong key)
- Step 3: test the password `__stack_check`
```sh
./level1
Please enter key: __stack_check
Good job.
```

---
# Level 2
Step by step to reverse engineering the program:
- Step 1: run [strings](https://man7.org/linux/man-pages/man1/strings.1.html) (print the sequences of printable characters in files)
```sh
strings level2

Result:
[...]
strlen
atoi
exit
puts
printf
stdin
strcmp
fflush
memset
[...]
```
- Step 2: analyse the result. Looking at the list of function used, we can roughly guess the actions
    - `main()`
    - `printf("Please enter key: ")`
    - `memset()` -> prepare space
    - `stdin`, `fflush()` -> read from stdin somehow then flush the stream
    - `atoi()`, `strlen()`, `strcmp()` -> 2 scenarios here: password is an int, then transformed somehow into string to compare with something else. Or password is a string, then transformed somehow into an int to check with something else
    - `puts()`, `exit()` -> print result and quit if fail

- Step 3: Run the debugger
```sh
gdb level2
```

- Step 2: Trace the program step and compare with the assembler code and the register
```sh
(gdb) run
(gdb) stepi
(gdb) info registers
(gdb) x/s $address
```