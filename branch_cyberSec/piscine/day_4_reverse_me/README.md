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

- Step 2: Run the debugger, then show assembler code
```sh
gdb level2
gdb disas main
```

- Step 3: analyse the result. Looking at the list of function used and the order, we can roughly guess the actions:
    - `main()`
    - `printf("Please enter key: ")`
    - `scanf()` -> get user input
    - `fflush()` -> clear input stream
    - `memset()` -> prepare space
    - `atoi()`, `strlen()`, `strcmp()` -> Looking at the order of function all: password is an int, then transformed somehow into string to compare with something else.
    - `puts()`, `exit()` -> print result and quit if fail

    Let analyse the assembler code further:
    - Under `scanf()`, we noticed:
        ```
        [...]
        0x0000130e <+62>:	call   0x10c0 <__isoc99_scanf@plt>
        0x00001313 <+67>:	mov    %eax,-0xc(%ebp)
        0x00001316 <+70>:	mov    $0x1,%eax            -> Put 1 in eax
        0x0000131b <+75>:	cmp    -0xc(%ebp),%eax      -> Compare 1 (eax) with value returned with scanf
        0x0000131e <+78>:	je     0x132c <main+92>     -> If equal 1 then jump
        0x00001324 <+84>:	mov    -0x40(%ebp),%ebx
        0x00001327 <+87>:	call   0x1220 <no>

        0x0000132c <+92>:	movsbl -0x34(%ebp),%ecx     -> Copy first charater at stack base + 34 to ecx (so input[0])
        0x00001330 <+96>:	mov    $0x30,%eax           -> Copy 0 to eax
        0x00001335 <+101>:	cmp    %ecx,%eax            -> Compare 0 to input[0]
        0x00001337 <+103>:	je     0x1345 <main+117>    -> If equal then jump
        0x0000133d <+109>:	mov    -0x40(%ebp),%ebx
        0x00001340 <+112>:	call   0x1220 <no>
        0x00001345 <+117>:	movsbl -0x35(%ebp),%ecx     -> Copy second charater at stack base + 35 to ecx (so input[1])
        0x00001349 <+121>:	mov    $0x30,%eax           -> Copy 0 to eax
        0x0000134e <+126>:	cmp    %ecx,%eax            -> Compare 0 to input[1]
        0x00001350 <+128>:	je     0x135e <main+142>    -> If equal then jump
        0x00001356 <+134>:	mov    -0x40(%ebp),%ebx
        0x00001359 <+137>:	call   0x1220 <no>
        [...]
        ```
        Basically this is a check if input length is at least 1, if input[0] == 0 and if input[1] == 0. From this we know that the first 2 bytes of the password must be `00`.
    -  At this point we can try with `ltrace` with `00`
        ```sh
        ltrace ./level2

        __libc_start_main(0x63efb2d0, 1, 0xffc844f4, 0 <unfinished ...>
        printf("Please enter key: ")                            = 18
        __isoc99_scanf(0x63efcd2e, 0xffc84403, 0, 0xffc86fefPlease enter key: 00
        )   = 1
        fflush(0xebc975c0)                                      = 0
        memset(0xffc8441b, '\0', 9)                             = 0xffc8441b
        strlen("d")                                             = 1
        strlen("00")                                            = 2
        strcmp("d", "delabere")                                 = -1
        puts("Nope."Nope.
        )                                           = 6
        exit(1 <no return ...>
        +++ exited (status 1) +++
        ```
        We can see the addition of `fflush()`, `memset()`, `strlen()` and `strcmp()`, so the `00` as start is correct. There is still no `atoi()` call, so there must be a transformation before the final `strcmp()` with `"delabere"`.
    - Let continue with assembler code
    ```
    [...]
    0x0000137a <+170>:	lea    -0x1d(%ebp),%eax     -> Store the buffer address to eax
    0x0000137d <+173>:	xor    %ecx,%ecx            -> Set ecx to 0
    0x0000137f <+175>:	mov    %eax,(%esp)          -> Copy address of buffer to stack
    0x00001382 <+178>:	movl   $0x0,0x4(%esp)       -> Copy '0' to stack
    0x0000138a <+186>:	movl   $0x9,0x8(%esp)       -> Copy '9' to stack
    0x00001392 <+194>:	call   0x10b0 <memset@plt>
    0x00001397 <+199>:	movb   $0x64,-0x1d(%ebp)    -> Copy 'd' to buffer[0]
    0x0000139b <+203>:	movb   $0x0,-0x36(%ebp)
    [...]
    ```

    Then right before `memset()`, the first movl copy 0 to the stack, the second movl copy 9 to the stack. These values will be used as argument for `memset()`. So here we prepare a space of 9 bytes. This might hint that the password is 9 bytes.<br>
    After that we see that character `d` is copied to buffer[0], combined with the `ltrace` from step 2, we know the final transformed string must be `"elabere"`.

- Step 4: Brute force
    What information we have on password:
    - Start with `00`
    - Must be transformable to `elabere` using `atoi()`
    - The reserved memory use `memset()` which mean the algorithm use bytes.<br>
    This lead us to the result: `00101108097098101114101`.<br>
    The result is composed of: 00 + 101 108 097 098 101 114 101, which is byte representation of ascii `elabere`.
- Step 5: Confirm with `ltrace`
    ```sh
    ltrace ./level2
    __libc_start_main(0x5b4682d0, 1, 0xffd6c4a4, 0 <unfinished ...>
    printf("Please enter key: ")                            = 18
    __isoc99_scanf(0x5b469d2e, 0xffd6c3b3, 0, 0xffd6efefPlease enter key: 00101108097098101114101
    )   = 1
    fflush(0xee15a5c0)                                      = 0
    memset(0xffd6c3cb, '\0', 9)                             = 0xffd6c3cb
    strlen("d")                                             = 1
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 101
    strlen("de")                                            = 2
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 108
    strlen("del")                                           = 3
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 97
    strlen("dela")                                          = 4
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 98
    strlen("delab")                                         = 5
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 101
    strlen("delabe")                                        = 6
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 114
    strlen("delaber")                                       = 7
    strlen("00101108097098101114101")                       = 23
    atoi(0xffd6c3af, 0, 9, 0xffd6efef)                      = 101
    strlen("delabere")                                      = 8
    strcmp("delabere", "delabere")                          = 0
    puts("Good job."Good job.
    )                                       = 10
    +++ exited (status 0) +++
    ```
