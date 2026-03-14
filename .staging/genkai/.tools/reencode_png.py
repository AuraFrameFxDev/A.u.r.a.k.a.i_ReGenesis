"""
Re-encode a PNG file using Pillow to fix common AAPT compilation issues
(e.g. corrupted chunks, uncommon color profiles, or metadata that AAPT can't handle).

Usage:
  python .\.tools\reencode_png.py "path\to\image.png"

The script will create a backup alongside the original (.bak) and overwrite the original with
an optimized, stripped PNG.
"""
import sys
import os
from shutil import copy2

try:
    from PIL import Image
except Exception as e:
    print("Pillow is not installed: ", e)
    raise


def reencode(path: str) -> int:
    if not os.path.isfile(path):
        print(f"File not found: {path}")
        return 2
    bak = path + ".bak"
    try:
        copy2(path, bak)
        print(f"Backup created: {bak}")
    except Exception as e:
        print(f"Failed to create backup: {e}")
        return 3

    try:
        with Image.open(path) as im:
            # Convert to RGBA if image has alpha or to RGB otherwise
            mode = 'RGBA' if 'A' in im.getbands() else 'RGB'
            im_converted = im.convert(mode)
            # Save to a temporary file then replace
            tmp = path + ".tmp"
            im_converted.save(tmp, format='PNG', optimize=True)
            os.replace(tmp, path)
            print(f"Re-encoded and replaced: {path}")
            return 0
    except Exception as e:
        print(f"Failed to re-encode image: {e}")
        return 4


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: reencode_png.py <path-to-png>")
        sys.exit(1)
    path = sys.argv[1]
    rc = reencode(path)
    sys.exit(rc)

