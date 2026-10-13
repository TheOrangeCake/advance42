import json
from .z1_utils import error


JSON_KEYS = ['name', 'description', 'parameters', 'returns']


class fileHandler():
    def __init__(self) -> None:
        return

    def read_functions(self, path: str) -> dict:
        print(path)
        try:
            with open(path, 'r') as file:
                self.content = json.load(file)
                self.__validateJsonKeys(JSON_KEYS)
        except FileNotFoundError:
            error("Functions definition file was not found")
        except PermissionError:
            error("Functions definition file permission denied")
        except json.JSONDecodeError as e:
            error(f"Invalid JSON syntax: {e}")
        except Exception as e:
            error(f"{e}")

    def __validateJsonKeys(key: list[str]) -> None:
        return




