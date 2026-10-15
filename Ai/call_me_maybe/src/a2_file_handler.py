from pydantic import TypeAdapter, ConfigDict
from pydantic.dataclasses import dataclass
from .z1_utils import error


@dataclass(
    config=ConfigDict(
        extra='forbid',
        frozen=True,
        str_min_length=1,
        str_strip_whitespace=True,

    )
)
class FunctionsData:
    name: str
    description: str
    parameters: dict[str, Items]
    returns: Items


@dataclass(
    config=ConfigDict(
        extra='forbid',
        frozen=True,
        str_min_length=1,
        str_strip_whitespace=True,

    )
)
class Items:
    type: str


class FileHandler:
    def read_functions(self, path: str) -> list[FunctionsData]:
        try:
            with open(path, 'r') as file:
                raw = file.read()
                return TypeAdapter(list[FunctionsData]).validate_json(raw)
        except FileNotFoundError:
            error("Functions definition file was not found")
        except PermissionError:
            error("Functions definition file permission denied")
        except Exception as e:
            error(f"{e}")
