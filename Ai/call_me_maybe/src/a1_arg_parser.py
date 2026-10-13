from argparse import ArgumentParser, Namespace


FUNCTION_DEFAULT = 'src/data/input/functions_definition.json'
INPUT_DEFAULT = 'src/data/input/function_calling_tests.json'
OUTPUT_DEFAULT = 'src/data/output/function_calling_results.json'


class argParser:
    def __init__(self) -> None:
        self.parser = ArgumentParser(
            prog='Call Me Maybe',
            description='Next 42 project about \
                function calling and constrained decoding'
        )
        self.parser.add_argument(
            '-f',
            '--functions_definition',
            dest='functions',
            default=FUNCTION_DEFAULT
        )
        self.parser.add_argument(
            '-i',
            '--input',
            default=INPUT_DEFAULT
        )
        self.parser.add_argument(
            '-o',
            '--output',
            default=OUTPUT_DEFAULT
        )

    def parse(self, args: list) -> Namespace:
        res = self.parser.parse_args(args)
        print(f"Functions defintion file:\t{res.functions}")
        print(f"Prompt input file:\t\t{res.input}")
        print(f"Result output file:\t\t{res.output}")
        return res
