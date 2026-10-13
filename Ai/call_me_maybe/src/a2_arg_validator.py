from argparse import Namespace


class argValidator():
    def __init__(self) -> None:
        return

    def validate(self, args: Namespace) -> bool:
        return True if args else False
