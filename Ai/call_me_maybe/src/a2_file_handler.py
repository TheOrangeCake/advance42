from pydantic import BaseModel, TypeAdapter, ConfigDict
from typing import Literal
from .z1_utils import error


type AcceptDataTypes = Literal[
    "string",
    "number",
    "integer",
    "boolean"
]


class DataShapeConfig(BaseModel):
    """Field Validation for function definition, input and output Json."""
    model_config = ConfigDict(
        extra='forbid',
        frozen=True,
        str_min_length=1,
        str_strip_whitespace=True,
    )


class FunctionData(DataShapeConfig):
    """Data shape for function definition."""
    name: str
    description: str
    parameters: dict[str, Item]
    returns: Item


class Item(DataShapeConfig):
    """Data type for FunctionData object."""
    type: AcceptDataTypes


class FileHandler(BaseModel):
    """Open files then read and parse its content."""

    def read_functions(self, path: str) -> list[FunctionData]:
        """Read and parse the list of functions definition.

        Args:
            path (str): path to the functions definition file

        Returns:
            list[FunctionData]: list of FunctionData object
        """

        try:
            with open(path, 'r', encoding='utf-8') as file:
                raw = file.read()
                return TypeAdapter(list[FunctionData]).validate_json(raw)
        except FileNotFoundError:
            error("Functions definition file was not found")
        except PermissionError:
            error("Functions definition file permission denied")
        except Exception as e:
            error(f"{e}")
