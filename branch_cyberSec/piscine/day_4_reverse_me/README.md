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
- **Step 1**: run [ltrace](https://www.man7.org/linux/man-pages/man1/ltrace.1.html) (A call tracer that show library call and its parameters)
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
- **Step 2**: analyse the result. Just by interpreting the tracer, we can see the program called:
    - `main()`
    - `printf("Please enter key: ")`
    - `scanf("Please enter key:")` (I have entered a random key)
    - `strcmp("test", "__stack_check")` -> This is the most important finding, it compares my input with "__stack_check" which is the password that we need to find
    - `printf("Nope.\n")` (Obviously wrong key)
- **Step 3**: test the password `__stack_check`
```sh
./level1
Please enter key: __stack_check
Good job.
```

---
# Level 2
Step by step to reverse engineering the program:
- **Step 1**: run [strings](https://man7.org/linux/man-pages/man1/strings.1.html) (print the sequences of printable characters in files)
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

- **Step 2**: Run the debugger, then show assembler code
```sh
gdb level2
gdb disas main
```

- **Step 3**: analyse the result. Looking at the list of function used and the order, we can roughly guess the actions:
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

        0x0000132c <+92>:	movsbl -0x34(%ebp),%ecx     -> Copy first charater at stack base + 34 to ecx (so input[1])
        0x00001330 <+96>:	mov    $0x30,%eax           -> Copy 0 to eax
        0x00001335 <+101>:	cmp    %ecx,%eax            -> Compare 0 to input[1]
        0x00001337 <+103>:	je     0x1345 <main+117>    -> If equal then jump
        0x0000133d <+109>:	mov    -0x40(%ebp),%ebx
        0x00001340 <+112>:	call   0x1220 <no>
        0x00001345 <+117>:	movsbl -0x35(%ebp),%ecx     -> Copy second charater at stack base + 35 to ecx (so input[0])
        0x00001349 <+121>:	mov    $0x30,%eax           -> Copy 0 to eax
        0x0000134e <+126>:	cmp    %ecx,%eax            -> Compare 0 to input[0]
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

- **Step 4**: Brute force
    What information we have on password:
    - Start with `00`
    - Must be transformable to `elabere` using `atoi()`
    - The reserved memory use `memset()` which mean the algorithm use bytes.<br>
    This lead us to the result: `00101108097098101114101`.<br>
    The result is composed of: 00 + 101 108 097 098 101 114 101, which is byte representation of ascii `elabere`.
- **Step 5**: Confirm with `ltrace`
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
---
# Level 3
Step by step to reverse engineering the program:
- **Step 1**: Running `strings` and `ltrace` didn't yield much result. We will process directly with the debugger and assembler code
    ```sh
    gdb level3
    gdb disas main
    ```
    A quick scan of the result show the flow: <br>
    `printf()`->`scanf()`->`malloc()`->`fflush()`->`memset()`->`strlen()`->`atoi()`->`strcmp()`->`free()`

- **Step 2**: Analyse in depth
    - Under `scanf()` validation:
    ```sh
    [...]
    0x0000000000001365 <+69>:	movsbl -0x3f(%rbp),%ecx     -> Copy scanf() input[0] to ecx
    0x0000000000001369 <+73>:	mov    $0x32,%eax           -> Copy '2' to eax
    0x000000000000136e <+78>:	cmp    %ecx,%eax            -> Compare 2 to input[1]
    0x0000000000001370 <+80>:	je     0x137b <main+91>     -> If equal, jump to continue
    [...]
    0x000000000000137b <+91>:	movsbl -0x40(%rbp),%ecx     -> Copy input[1] to ecx
    0x000000000000137f <+95>:	mov    $0x34,%eax           -> Copy '4' to eax
    0x0000000000001384 <+100>:	cmp    %ecx,%eax            -> Compare 4 to input[0], -0x40 is before -0x3f 
    0x0000000000001386 <+102>:	je     0x1391 <main+113>
    [...]
    ```
    So we know the first 2 characters must be `42`.
    - Examinate further:
    ```sh
    [...]
    0x00000000000013a6 <+134>:	mov    $0x9,%edx            -> Buffer size 9 bytes
    0x00000000000013ab <+139>:	call   0x1060 <memset@plt>
    0x00000000000013b0 <+144>:	movb   $0x2a,-0x21(%rbp)    -> Set the first character of the buffer to '*'
    [...]
    ```
    So the final transformed string will start with `*` and has 8 characters total.
    - At this point let try `ltrace` again just like level 2, they have the same pattern after all.
    ```sh
    ltrace ./level3

    printf("Please enter key: ")                            = 18
    __isoc99_scanf(0x5b7ee431c056, 0x7ffc1dd610f0, 0, 0Please enter key: 42
    )    = 1
    fflush(0x7c48d26038e0)                                  = 0
    memset(0x7ffc1dd6110f, '\0', 9)                         = 0x7ffc1dd6110f
    strlen("*")                                             = 1
    strlen("42")                                            = 2
    strcmp("*", "********")                                 = -42
    puts("Nope."Nope.
    )                                           = 6
    exit(1 <no return ...>
    +++ exited (status 1) +++
    ```
    Exactly the same pattern as Level 2.
    
- **Step 4**: Brute force
    What information we have on password:
    - Start with `42`
    - Must be transformable to `********` using `atoi()`
    - The reserved memory use `memset()` which mean the algorithm use bytes.<br>
    This lead us to the result: `42042042042042042042042`.<br>
    The result is composed of: 42 + 042 042 042 042 042 042 042, which is byte representation of ascii `********`.

- **Step 5**: Confirm with `ltrace`
    ```sh
    ltrace ./level3
    
    printf("Please enter key: ")                            = 18
    __isoc99_scanf(0x60c6361e6056, 0x7ffe0d1d6800, 0, 0Please enter key: 42042042042042042042042
    )    = 1
    fflush(0x78146aa038e0)                                  = 0
    memset(0x7ffe0d1d681f, '\0', 9)                         = 0x7ffe0d1d681f
    strlen("*")                                             = 1
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 0, 0x7ffe0d1d6800, 23)             = 42
    strlen("**")                                            = 2
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("***")                                           = 3
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("****")                                          = 4
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("*****")                                         = 5
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("******")                                        = 6
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("*******")                                       = 7
    strlen("42042042042042042042042")                       = 23
    atoi(0x7ffe0d1d67fc, 42, 0x7ffe0d1d6800, 23)            = 42
    strlen("********")                                      = 8
    strcmp("********", "********")                          = 0
    puts("Good job."Good job.
    )                                       = 10
    +++ exited (status 0) +++
    ```