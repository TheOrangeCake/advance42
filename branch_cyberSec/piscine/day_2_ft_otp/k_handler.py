from cryptography.fernet import Fernet
from math import floor
import hmac
import hashlib
import time

from logger import *
from g_handler import is_validate

SECRET_KEY_FILE_NAME = "ft_otp.secret"

# https://mojoauth.com/hashing/hmac-sha1-in-python/#advantages-and-disadvantages-of-hmac-sha1
def handle(key: str) -> None:
    encrypted_key: bytes
    try:
        with (open(key, "rb") as file):
            encrypted_key = file.read()
    except OSError as e:
        error(f"Could not read from '{key}': {e}")
        return

    secret_key: str
    try:
        with (open(SECRET_KEY_FILE_NAME, "r") as file):
            secret_key = file.read()
    except OSError as e:
        error(f"Could not read from '{SECRET_KEY_FILE_NAME}': {e}")
        return

    fernet = Fernet(secret_key)

    hex_seed: str = fernet.decrypt(encrypted_key).decode()
    if (not is_validate(hex_seed)):
        error(f"Invalid key: '{hex_seed}'")
        return

    seed: bytes = bytes.fromhex(hex_seed)

    current_time = time.time()
    current_slot = floor(current_time / 30)
    counter = current_slot.to_bytes(8, byteorder="big")

    # Hash 20 bytes (note: F = 4 bits, 0F = 0000 1111, 7F = 0111 1111)
    hmac_sha1 = hmac.new(seed, counter, hashlib.sha1).digest()
    # Get last 4 bits -> value is offset
    offset = hmac_sha1[-1] & 0x0F
    # Transform 4 bytes (32 bits) from offset in hash into int, big endian
    # -> mask first bit so unsigned
    binary = int.from_bytes(hmac_sha1[offset:offset + 4], "big") & 0x7FFFFFFF
    # Truncated to 6 digits
    otp = binary % 1_000_000
    print(f"{otp:06d}")
