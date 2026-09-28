import logging
from pathlib import Path
import matplotlib.pyplot as graph

PLOTS_ROOT = Path(__file__).resolve().parents[2] / "plots"


def save_fig(subdir: str, filename: str, tight: bool = True) -> Path:
    out_dir = PLOTS_ROOT / subdir
    out_dir.mkdir(parents=True, exist_ok=True)
    out_path = out_dir / filename
    if tight:
        graph.tight_layout()
    graph.savefig(out_path)
    logging.info(f"Saved plot to {out_path}")
    return out_path
