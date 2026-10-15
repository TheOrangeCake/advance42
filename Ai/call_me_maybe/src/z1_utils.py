import sys
from typing import Never


def error(message: str) -> Never:
    print(f"Error: {message}", file=sys.stderr)
    sys.exit(1)
