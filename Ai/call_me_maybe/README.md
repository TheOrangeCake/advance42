# Call me maybe

## Steps
- Parse args
	- Validate and parse function definition file to a json object
		- Construct a list of allowed function
	- Validate and parse input file into an array
	- Validate output file
- Create base model with Pydantic to defined and validate input and output structures
- Create a function to block the invalid token by setting the score to negative infinity (FSM - Finite State Machine)
	- Force next token based on JSON format and output structure ("prompt", "name", "parameters"). Realistically, only value for "name" and "parameters" will need to be generated
	- Parse the generated type to force close when needed and validate next token
	- Once the "name" is know, "parameters" keys are also know so can be forced as well
- Initialize LLM model (class Small_LLM_Model.init())
- Craft prompt using prompt engineering (add function definition json and Pydantic base model)
- Open Small_LLM_Model.get_path_to_vocab_file() to get IDs for JSON schema tokens
- Loop through input
	- Call Small_LLM_Model.encode() with the (prompt + input) -> get back the token tensor
	- Add start state to the tensor
	- Loop
		- Pass the tensor to Small_LLM_Model.get_logits_from_input_ids() to get back the logits of next token
		- Mask out the invalid token (Note: compare logit list with vocab to know what token it is)
		- Append that token to the tensor (need to separate base tensor and generated tensor here)
		- Repeat till JSON complete
	- Format the output and append to the final list
- Write the final list to a file


</br>
</br>


## Concepts
### Function Calling
Function calling or Tool calling is a method to improve LLMs response quality. Programmers will provide LLMs with tools and APIs that LLMs can use. LLMs will parse the user input, decide what tools and APIs to use, then parse user input to tools / APIs arguments and call the tools. LLMs will then incorporate the result into its response.

### Constrained decoding
Constrained decoding is an act of restricting the next token choice in every step of generation. This ensure the final output match a specific structure or schema.

</br>
</br>

## Tools
| Tool | Purpose |
|:-----|:--------|
|uv|package and venv manager|
|flake8|python linter|
|mypy|static type checking|
|pydantic| data validation. JSON parsing and serialize|
|json|JSON manipulation|
|numpy|Better array object|

</br>
</br>

## Install UV
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

