import secrets

from logger import *

def handle(file_name: str) -> None:
    token: str = secrets.token_hex(32)
    try:
        with open(file_name, "w") as file:
            file.write(token)
            success(f"Generated: {token}")
    except OSError as e:
        error(f"Could not write to '{file_name}': {e}")
