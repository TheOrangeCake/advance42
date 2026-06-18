### Quick Start
- Libsodium: for encryption. [Guide](https://doc.libsodium.org/doc/quickstart)
    ```sh
    cd dependencies/libsodium/
    ./configure
    make && make check
    sudo make install
    cd ../../
    ```

- Run:
    ```sh
    make
    ./stockholm -h
    ```