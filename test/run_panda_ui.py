#!/usr/bin/env python3
"""Run Panda with an isolated temporary data file for repeatable UI tests.

Written by Codex: Prevent one persistence test from changing the starting data
used by later test cases, and optionally verify the final stored file exactly.
"""

from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


def normalized(text: str) -> str:
    """Normalize line endings and an optional final newline."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def main() -> int:
    """Run one Panda session against a disposable copy of the requested data."""
    parser = argparse.ArgumentParser()
    parser.add_argument("--fixture", help="existing data copied into the temporary file")
    parser.add_argument("--expected-data", help="file containing the expected saved data")
    args = parser.parse_args()

    # Written by Codex: Close the temporary file before Java opens it on Windows.
    with tempfile.NamedTemporaryFile(suffix=".txt", delete=False) as temporary_file:
        data_path = Path(temporary_file.name)

    try:
        if args.fixture:
            shutil.copyfile(args.fixture, data_path)

        result = subprocess.run(
            ["java", "-cp", "build/classes", "Panda", str(data_path)],
            input=sys.stdin.read(),
            text=True,
            capture_output=True,
        )
        sys.stdout.write(result.stdout)
        sys.stderr.write(result.stderr)
        if result.returncode != 0:
            return result.returncode

        if args.expected_data:
            actual_data = normalized(data_path.read_text(encoding="utf-8"))
            expected_data = normalized(Path(args.expected_data).read_text(encoding="utf-8"))
            if actual_data != expected_data:
                print("Saved data did not match the expected file.", file=sys.stderr)
                print(f"Actual data:\n{actual_data}", file=sys.stderr)
                print(f"Expected data:\n{expected_data}", file=sys.stderr)
                return 1
        return 0
    finally:
        data_path.unlink(missing_ok=True)


if __name__ == "__main__":
    raise SystemExit(main())
