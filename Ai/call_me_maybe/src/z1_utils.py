import sys
from typing import Never


def error(message: str) -> Never:
    print(f"Error: {message}")
    sys.exit(1)
