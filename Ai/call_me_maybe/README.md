*This project has been created as part of the 42 curriculum by hoannguy.*

# Call me maybe

</br>
</br>

TODO:
- Token visualization
- makefile debug rule

## Description
Bridge the gap between LLM non deterministic output and function deterministic parameters by guarantee the LLM output to be a valid JSON. The function called must be correct and the parameters are correctly formatted.
### Brief
LLM output is non deterministic by default and depend on various factors like training, weights, skills, etc. Programs, however, are often rely on preformatted output. This create a friction between LLM and programs.
### Goal
The goal of the project is to close the gap between LLM and programs by making the LLM output deterministic. The techniques used are **Function Calling** and **Constrained Decoding**.

</br>
</br>

## Instructions
### Install UV
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```
### Create environnement
```bash
make install
```
### Run program
- No argument
```bash
make run
```
- With arguments
```bash
make ARGS= \
"--input "path"" \
"--functions_definition "path"" \
"--output "path""
```

</br>
</br>

## Resources
### Documents
- [Function Calling](https://www.geeksforgeeks.org/artificial-intelligence/function-calling-in-llms/)
- [Constrained Decoding](https://docs.nvidia.com/deeplearning/triton-inference-server/user-guide/docs/tutorials/Feature_Guide/Constrained_Decoding/README.html)
- [Docstring google style](https://sphinxcontrib-napoleon.readthedocs.io/en/latest/example_google.html)
- [Qwen Prompt Engineering](https://qwen-ai.com/prompts/)
- [Pydantic Validation](https://pydantic.dev/docs/validation/latest/get-started/)
### AI use
AI was used to explore new concepts, validate approaches, find edge cases and some light code formatting.

</br>
</br>

## Algorithm explanation
### Constrained decoding
Constrained decoding is an act of restricting the next token choice in every step of generation. This ensure the final output match a specific structure or schema.
### Algorithm
1. It starts with a LLM call with the prompt.
2. Receive back a list of float which is the "score" for each token. The next token will be picked based on this list and additional context.
3. The token will be picked based on the STATE of the output:
    - Function name: The token is match with the list of the predefined functions. If there is only 1 match then we auto-complete the function name. This help reduce LLM call and also guarantee the right function name.
    - Parameters: depend on the parameter type (string, boolean, number, integer), only specific tokens will be checked and validated. For example, integer parameter only accept and check digit and minus tokens.
4. Any part that does not require LLM generation will be automatically detected and appended to the output. The detection work based on the STATE of the output.
5. There are 4 STATES:
    - NAME: Function name generation
    - PARAMS: Parameter arguments generation
    - NEXT_PARAMS: Transition state between parameters. Set when a parameter is finished to move to the next
    - DONE: Generation is finished
    - NONE: Used when encoutered error during generation, or no predefined function is selected
So the loop is: 
LLM call -> Logits generation -> Token selection -> Output auto-complete -> State change -> Update prompt -> ...

</br>
</br>

## Design decistions:
### Key choices:
- Python in OOP: Object oriented approach is a natural way to separate responsibility. Here are classes used:
    - ArgParser: handle program arguments
    - FileHandler: handle file io operations
    - StateManager: track current STATE
    - OutputPart: craft the output parts using selected tokens while respecting a json format
    - Prompt: Create base prompt
    - Picker: Picking the next token
    - Utils: utility functions
- Output auto-complete: To limit LLM call for better performance and lower cost
- Error handling with slots filled with "None": Guarantee Json output without skipping prompts

</br>
</br>

## Performance analysis
### Accuracy
Very high accuracy due to auto completion and specifically crafted prompts
### Speed
Program prioritises readability and separation of concern architecture, so the speed can be improved. The major bottle neck is still the LLM call; Better hardware and faster server response will improve the speed drastically.
### Reliability
JSON format output is 100% guaranteed thank to constrained decoding

</br>
</br>

## Challenges faced
- Error handling when no function fit the prompt -> Include an example in the prompt of what to return in no match case.
- Bugs due to unusual form of token, for example "-" could be returned as " -" instead -> Observe token generation step to identify problematic tokens.

</br>
</br>

## Testing strategy
The program is simple enough and the separation of concern was clear, so the testing is simply testing with different input and observe the result.

## Example usage
- Prompts:
```json
[
  {
    "prompt": "What is the sum of -21 and 0021?"
  },
  {
    "prompt": "Register user \"hoannguy\" to event \"Evaluation\", first time is false"
  },
  {
    "prompt": "Greet shrek"
  },
  {
    "prompt": "What is the sum of 2 and 3?"
  },
  {
    "prompt": "What is the sum of 265.2 and 345.7?"
  },
  {
    "prompt": "What is the sum of 1e3 and 1e2?"
  },
  {
    "prompt": "Replace all numbers in \"Hello 34 I'm 233 years old\" with NUMBERS"
  },
  {
    "prompt": "Do a backflip"
  },
  {
    "prompt": "What time is it"
  }
]
```
- Response:
```json
[
	{
		"prompt": "What is the sum of -21 and 0021?",
		"name": "fn_add_integers",
		"parameters": {"a": -21, "b": 21}
	},
	{
		"prompt": "Register user \"hoannguy\" to event \"Evaluation\", first time is false",
		"name": "fn_user_registration",
		"parameters": {"event": "Evaluation", "user": "hoannguy", "isFirstTime": false}
	},
	{
		"prompt": "Greet shrek",
		"name": "fn_greet",
		"parameters": {"name": "shrek"}
	},
	{
		"prompt": "What is the sum of 2 and 3?",
		"name": "fn_add_integers",
		"parameters": {"a": 2, "b": 3}
	},
	{
		"prompt": "What is the sum of 265.2 and 345.7?",
		"name": "fn_add_numbers_float",
		"parameters": {"a": 265.2, "b": 345.7}
	},
	{
		"prompt": "What is the sum of 1e3 and 1e2?",
		"name": "fn_add_numbers_float",
		"parameters": {"a": 1000.0, "b": 100.0}
	},
	{
		"prompt": "Replace all numbers in \"Hello 34 I'm 233 years old\" with NUMBERS",
		"name": "fn_substitute_string_with_regex",
		"parameters": {"source_string": "Hello 34 I'm 233 years old", "regex": "([0-9]+)", "replacement": "NUMBERS"}
	},
	{
		"prompt": "Do a backflip",
		"name": "None",
		"parameters": {}
	},
	{
		"prompt": "What time is it",
		"name": "fn_get_current_time",
		"parameters": {}
	}
]
```

</br>
</br>

## Tools
| Tool | Purpose |
|:-----|:--------|
|uv|package and venv manager|
|flake8|python linter|
|mypy|static type checking|
|pydantic| data validation. JSON parsing and serialize|

</br>
</br>



