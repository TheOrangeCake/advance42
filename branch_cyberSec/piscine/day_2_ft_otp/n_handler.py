import secrets

from logger import *

def handle(file_name: str) -> None:
    token = secrets.token_hex(64)
    try:
        with open(file_name, "w") as file:
            file.write(token)
            success(f"Generated: {token}")
    except OSError as e:
        error(f"Could not write to '{file_name}': {e}")
