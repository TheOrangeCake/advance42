import sys
from typing import Never
from pydantic import BaseModel


class Utils(BaseModel):
    """Utility class."""

    @staticmethod
    def error(message: str) -> Never:
        """Print error message and exit program with code 1.

        Args:
            message (str): message to print to stderr

        Returns:
            Never
        """
        print(f"Error: {message}", file=sys.stderr)
        sys.exit(1)
