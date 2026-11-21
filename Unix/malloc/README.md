# Malloc
A simpler reimplementation of `malloc()`, `free()` and `reaclloc()`. The goal is to dive deeper into memory manipulation to learn the mechanism under the hood.

## Concept
### Page
We should start by the concept of page. A page represent an unit of memory in a computer **virtual memory**. We can get the size of 1 page by calling `sysconf(_SC_PAGESIZE)`. Why do we need page? Remember the **virtual memory**? It will be mapped to the **physical memory** using page. If we map **virtual memory** to **physical memory** byte to byte, then the **physical memory** will run out of space just by holding the mapping table. Consequencely, the memory is divided into "block" of memory which are aligned with page size.

## Implementation
To simulate real `malloc()` but in a simplified way, we are going to implement 3 different memory zones: `TINY`, `SMALL` and `LARGE`. A zone consist of its metadata and a linked list of block. A block is a node in a linked list, it is made of the block metadata and allocated memory.

- Block visualization:
```md
[ [block metadata] allocated space ]->[ [block metadata] allocated space ]->[ [block metadata] allocated space ]->...
```
- Zone visualization:
```md
[ [zone metadata] [block]->[block]->[block]->[block] ]->[ [zone metadata] [block]->[block]->[block]->[block] ]->...
```

### TINY zone
`Syscall` is expensive and we should avoid doing too many of it, especially when the needed ressource is small. The TINY zone is created to minimize `syscall` to optimize ressource. Its implementation start by pre-allocating a big memory chunk using `mmap()`, this allocation will be able to hold at least 100 chunks of `TINY` size in memory. In another word, if we only ever allocate memory of exactly `TINY` size, then 100 of these allocations will be handled by `TINY` zone.

When a `malloc()` of size smaller than or equal to `TINY` size is called, we will attempt to find the first big enough block in the `TINY` zone to return to the user. If no block is found then we will create a new `TINY` zone and link it to our zone linked list. If a block is found, we will return that block or perform a splitting based on the size of that block, it will be then marked as `USED` and a pointer to the free space will be returned. A `free()` call will simply marked that block as `FREE` then we will attempt to merge adjection free block.

A splitting operation is an operation to split a block into smaller blocks to adapt to `malloc()` call of smaller size. For example a block of 8 pages can be splited into 2 blocks of 3 pages and 5 pages if a `malloc()` asked for 3 pages.

A merge operation is an operation to merge adjection free block together. To take an example, if we have 2 blocks of 3 and 5 pages size next to each other, A `malloc()` asked for 6 pages wouldn't fit in any of them. But since both are free, we can merge them to create a 8 pages block, not our 6 pages call will fit !

### SMALL zone
The SMALL zone is mechanically the same as the TINY zone, but with a bigger size.

### LARGE zone
This is actually not implemented as a zone because each `malloc()` call of large size will result in a newly allocated block using `mmap()`. This mean no complicate block manipulation like splitting or merging. So the LARGE zone is simply a linked list of block.
```md
[block]->[block]->[block]->[block]->[block]->[block]->...
```
Calling `free()` simply unlink a block and called `munmap()` on that block.


## Ressources
- [Dynamic linking](https://www.cs-fundamentals.com/c-programming/static-and-dynamic-linking-in-c#create-and%20use-shared-libraries)
- [Functions mmap() and munmap()](https://man7.org/linux/man-pages/man2/mmap.2.html)
- [Function sysconf()](https://man7.org/linux/man-pages/man3/sysconf.3.html)
- [Computer page related to sysconf()](https://en.wikipedia.org/wiki/Page_(computer_memory))
- [Function getrlimit()](https://man7.org/linux/man-pages/man2/getrlimit.2.html)
- [Suite malloc()](https://man7.org/linux/man-pages/man3/malloc.3.html)
