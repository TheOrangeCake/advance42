from pydantic import BaseModel, ConfigDict
from llm_sdk import Small_LLM_Model
from enum import Enum


class STATE(Enum):
    """Steps of the output generation."""
    NAME = 1
    PARAMS = 2
    NEXT_PARAMS = 3
    DONE = 4
    NONE = 5


class StateManager(BaseModel):
    """Hold the current state, shared between Main and LogitPicker."""
    model_config = ConfigDict(arbitrary_types_allowed=True)

    state: STATE = STATE.NAME

