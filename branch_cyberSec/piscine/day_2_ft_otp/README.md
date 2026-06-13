# ft_otp

Cybersecurity Piscine — a TOTP (Time-based One-Time Password) generator, based on [RFC 6238](https://datatracker.ietf.org/doc/html/rfc6238) / [RFC 4226](https://datatracker.ietf.org/doc/html/rfc4226).

The program stores a master key safely in an encrypted file, then generates ephemeral 6-digit passwords from it — the same kind of codes used by authenticator apps. Passwords change every 30 seconds.

## Requirements

- Python 3.10+ (uses `match` statements)
- Dependencies:

```bash
pip install cryptography colorama
```

## Usage

```bash
python3 ft_otp.py -flag argument
```

| Flag | Argument | Description |
|------|----------|-------------|
| `-g` | hex key / file | Validates a 64-character hex key and stores it **encrypted** in `ft_otp.key` |
| `-k` | encrypted key file | Generates a temporary 6-digit password from the stored key and prints it |
| `-n` | file name | (extra) Generates a random 256-bit hex token and writes it to the given file |

The program accepts exactly one flag and one argument.

## Examples

Generate a random key, store it encrypted, then produce one-time passwords:

```bash
# create a 64-char hex key
$ python3 ft_otp.py -n key.hex
[SUCCESS] Generated: a1b2c3...

# encrypt and save it to ft_otp.key
$ python3 ft_otp.py -g key.hex
[SUCCESS] Key was successfully saved in ft_otp.key.

# generate a one-time password
$ python3 ft_otp.py -k ft_otp.key
836492

# 30 seconds later, a new one
$ python3 ft_otp.py -k ft_otp.key
123518
```

The `-g` flag also accepts the key inline instead of a file path:

```bash
$ python3 ft_otp.py -g 5712...   # 64 hex characters
```

A key must be **exactly 64 hexadecimal characters** (256 bits), or it is rejected.

## How it works

### `-g` — storing the key

1. The key is read from a file (or taken directly as the argument) and validated (64 hex chars).
2. A fresh [Fernet](https://cryptography.io/en/latest/fernet/) secret is generated and written to `ft_otp.secret`.
3. The hex key is encrypted with that secret and written to `ft_otp.key`.

> The encrypted key (`ft_otp.key`) is useless without the matching `ft_otp.secret`.

### `-k` — generating a password

1. `ft_otp.key` is decrypted using `ft_otp.secret`, recovering the hex seed.
2. The current Unix time is divided into 30-second slots (`floor(time / 30)`) and packed as an 8-byte big-endian counter.
3. `HMAC-SHA1(seed, counter)` is computed, then truncated per RFC 4226:
   - the low 4 bits of the last byte give an **offset**;
   - 4 bytes are read from that offset and masked to 31 bits;
   - the result modulo `1_000_000` yields the 6-digit code.

You can cross-check the output against a reference implementation:

```bash
brew install oath-toolkit
oathtool --totp $(cat key.hex)
```

## Files

| File | Role |
|------|------|
| `ft_otp.py` | Entry point — argument parsing and dispatch |
| `g_handler.py` | `-g`: key validation + Fernet encryption |
| `k_handler.py` | `-k`: decryption + TOTP generation |
| `n_handler.py` | `-n`: random hex token generation |
| `logger.py` | Colored `info` / `success` / `error` output |

Generated artifacts (`ft_otp.key`, `ft_otp.secret`, `*.hex`, …) are git-ignored.

## Notes

- Per the subject, **no TOTP/HOTP library is used** — the algorithm is implemented by hand on top of `hmac` / `hashlib`. Only `cryptography` (for at-rest key encryption) and standard time access are used as helpers.
- The time step is fixed at 30 seconds and the output is always 6 digits.
</content>
</invoke>
