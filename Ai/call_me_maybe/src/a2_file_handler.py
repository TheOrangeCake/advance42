from pydantic import BaseModel, TypeAdapter, ConfigDict
from typing import Literal
from pathlib import Path
from .z1_utils import Utils


class DataShapeConfig(BaseModel):
    """Field Validation for function definition, input and output Json."""
    model_config = ConfigDict(
        extra='forbid',
        frozen=True,
        str_min_length=1,
        str_strip_whitespace=True,
    )


type AcceptDataTypes = Literal[
    "string",
    "number",
    "integer",
    "boolean"
]


class Item(DataShapeConfig):
    """Data type for FunctionData object."""
    type: AcceptDataTypes


class FunctionData(DataShapeConfig):
    """Data shape for function definition."""
    name: str
    description: str
    parameters: dict[str, Item]
    returns: Item


class InputPrompt(DataShapeConfig):
    prompt: str


class FileHandler(BaseModel):
    """Open files then read and parse its content."""

    @staticmethod
    def read_functions(path: str) -> list[FunctionData]:
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
            Utils.error("Functions definition file was not found")
        except PermissionError:
            Utils.error("Functions definition file permission denied")
        except Exception as e:
            Utils.error(f"{e}")

    @staticmethod
    def read_inputs(path: str) -> list[InputPrompt]:
        """Read and parse the list of input prompts.

        Args:
            path (str): path to the input file

        Returns:
            list[InputPrompt]: list of InputPrompt object
        """

        try:
            with open(path, 'r', encoding='utf-8') as file:
                raw = file.read()
                return TypeAdapter(list[InputPrompt]).validate_json(raw)
        except FileNotFoundError:
            Utils.error("Input file was not found")
        except IsADirectoryError:
            Utils.error("Input file path is a directory")
        except PermissionError:
            Utils.error("Input file permission denied")
        except Exception as e:
            Utils.error(f"{e}")

    @staticmethod
    def validate_output_file(path: str) -> None:
        """Create missing parent directories and check output file
        validity for append.

        Args:
            path (str): path to the output file

        Returns:
            None
        """
        try:
            Path(path).parent.mkdir(parents=True, exist_ok=True)
            with open(path, 'a', encoding="utf-8"):
                pass
        except FileNotFoundError:
            Utils.error("Output file was not found")
        except (FileExistsError, NotADirectoryError):
            Utils.error("Output file path has a non-directory component")
        except IsADirectoryError:
            Utils.error("Output file path is a directory")
        except PermissionError:
            Utils.error("Output file permission denied")
        except Exception as e:
            Utils.error(f"{e}")

    @staticmethod
    def read_vocab(path: str) -> dict[str, int]:
        """Read and parse the list of llm vocab.

        Args:
            path (str): path to the vocab file

        Returns:
            dict[str, int]: dict of token str value and its id
        """

        try:
            with open(path, 'r', encoding='utf-8') as file:
                raw = file.read()
                vocab = TypeAdapter(dict[str, int]).validate_json(raw)
                if len(vocab) != len(set(vocab.values())):
                    Utils.error("Vocab file has duplicated id")
                return vocab
        except FileNotFoundError:
            Utils.error("Vocab file was not found")
        except PermissionError:
            Utils.error("Vocab file permission denied")
        except Exception as e:
            Utils.error(f"{e}")

    @staticmethod
    def write_outputs(path: str, outputs: str) -> None:
        """
        """
        try:
            with open(path, 'w', encoding="utf-8") as file:
                file.write(outputs)
        except FileNotFoundError:
            Utils.error("Output file was not found")
        except (FileExistsError, NotADirectoryError):
            Utils.error("Output file path has a non-directory component")
        except IsADirectoryError:
            Utils.error("Output file path is a directory")
        except PermissionError:
            Utils.error("Output file permission denied")
        except Exception as e:
            Utils.error(f"{e}")
