from argparse import ArgumentParser, Namespace
from pydantic import BaseModel, ConfigDict


FUNCTION_DEFAULT = 'data/input/functions_definition.json'
INPUT_DEFAULT = 'data/input/function_calling_tests.json'
OUTPUT_DEFAULT = 'data/output/function_calling_results.json'


class ArgParser(BaseModel):
    """Handling program arguments parsing"""

    model_config = ConfigDict(arbitrary_types_allowed=True)

    parser: ArgumentParser

    def __init__(self) -> None:
        """Initialize and configure parser"""

        parser = ArgumentParser(
            prog='Call Me Maybe',
            description='Next 42 project about \
                function calling and constrained decoding'
        )
        parser.add_argument(
            '-f',
            '--functions_definition',
            dest='functions',
            default=FUNCTION_DEFAULT
        )
        parser.add_argument(
            '-i',
            '--input',
            dest='inputs',
            default=INPUT_DEFAULT
        )
        parser.add_argument(
            '-o',
            '--output',
            dest='outputs',
            default=OUTPUT_DEFAULT
        )

        # Call the pydantic init
        super().__init__(parser=parser)

    def parse(self, args: list[str]) -> Namespace:
        """Parse the program arguments then print them to stdout.

        Args:
            args (list[str]): list of arguments

        Returns:
            argparse.Namespace: object containing arguments and its values
        """

        res = self.parser.parse_args(args)
        print(f"Functions defintion file:\t{res.functions}")
        print(f"Prompt input file:\t\t{res.inputs}")
        print(f"Result output file:\t\t{res.outputs}")
        return res
