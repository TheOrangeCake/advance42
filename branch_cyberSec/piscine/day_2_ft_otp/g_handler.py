import re
import os
from cryptography.fernet import Fernet

from logger import *

SECRET_KEY_FILE_NAME = "ft_otp.secret"
ENCRYPTED_KEY_FILE_NAME = "ft_otp.key"

def is_validate(key: str) -> bool:
    if (len(key) != 64):
        error("key must be 64 hexadecimal characters.")
        return False
    if (not re.fullmatch("[a-fA-F0-9]+", key)):
        error(f"Invalid key: '{key}'")
        return False
    return True

def generate_secret_key() -> bytes | None:
    key: bytes = Fernet.generate_key()

    try:
        with (open(SECRET_KEY_FILE_NAME, "wb") as file):
            file.write(key)
    except OSError as e:
        error(f"Could not write secret key to '{SECRET_KEY_FILE_NAME}': {e}" )
        return None
    
    return key

# https://www.geeksforgeeks.org/python/how-to-encrypt-and-decrypt-strings-in-python/
def handle(key: str) -> None:
    hex_key: str

    if os.path.isfile(key):
        try:
            with (open(key, "r") as file):
                hex_key = file.read().strip()
        except OSError as e:
            error(f"Could not read from '{key}': {e}")
            return
    else:
        hex_key = key.strip()
    
    if (not is_validate(hex_key)):
        return
    
    secret_key = generate_secret_key()
    if (secret_key is None):
        return
    
    fernet = Fernet(secret_key)

    encrypted_key = fernet.encrypt(hex_key.encode())

    try:
        with (open(ENCRYPTED_KEY_FILE_NAME, "wb") as file):
            file.write(encrypted_key)
    except OSError as e:
        error(f"Could not write encrypted key to '{ENCRYPTED_KEY_FILE_NAME}': {e}" )
        return
    
    success(f"Key was successfully saved in {ENCRYPTED_KEY_FILE_NAME}.")
