#!/usr/bin/env python3
"""
🎨 ReGenesis Sprite Batch Clipper
Removes backgrounds from hundreds of sprite images using AI
"""

import os
import sys
from pathlib import Path
from typing import List, Optional
import argparse

try:
    from rembg import remove
    from PIL import Image
    import numpy as np
    from tqdm import tqdm
    HAS_TQDM = True
except ImportError as e:
    if 'tqdm' in str(e):
        HAS_TQDM = False
    else:
        print("ERROR: Missing dependencies. Install with:")
        print("  pip install rembg pillow numpy")
        print("\nOptional GPU acceleration:")
        print("  pip install onnxruntime-gpu")
        print("\nOptional progress bars:")
        print("  pip install tqdm")
        sys.exit(1)


class SpriteBatchClipper:
    """Batch process sprite images to remove backgrounds"""

    def __init__(self, input_dir: str, output_dir: str, quality: str = 'high'):
        self.input_dir = Path(input_dir)
        self.output_dir = Path(output_dir)
        self.quality = quality
        self.processed = 0
        self.failed = 0
        self.skipped = 0

    def get_image_files(self) -> List[Path]:
        """Find all image files in input directory"""
        extensions = {'.png', '.jpg', '.jpeg', '.webp', '.bmp'}
        files = []

        for ext in extensions:
            files.extend(self.input_dir.rglob(f'*{ext}'))
            files.extend(self.input_dir.rglob(f'*{ext.upper()}'))

        return sorted(files)

    def preserve_directory_structure(self, input_path: Path) -> Path:
        """Maintain folder structure in output directory"""
        relative_path = input_path.relative_to(self.input_dir)
        output_path = self.output_dir / relative_path

        # Change extension to .png for transparency support
        output_path = output_path.with_suffix('.png')

        # Create parent directories
        output_path.parent.mkdir(parents=True, exist_ok=True)

        return output_path

    def clip_sprite(self, input_path: Path, output_path: Path, quiet: bool = False) -> bool:
        """Remove background from a single sprite"""
        try:
            if not quiet:
                print(f"  Processing: {input_path.name}...", end=' ')

            # Load image
            with Image.open(input_path) as img:
                # Remove background
                output = remove(
                    img,
                    alpha_matting=True if self.quality == 'high' else False,
                    alpha_matting_foreground_threshold=240,
                    alpha_matting_background_threshold=10,
                    alpha_matting_erode_size=10
                )

                # Save as PNG with transparency
                output.save(output_path, 'PNG', optimize=True)

            if not quiet:
                print(f"✅ DONE")
            return True

        except Exception as e:
            if not quiet:
                print(f"❌ FAILED: {e}")
            else:
                # Still log errors even in quiet mode
                print(f"\n❌ ERROR on {input_path.name}: {e}")
            return False

    def process_all(self, skip_existing: bool = True):
        """Process all images in batch"""
        files = self.get_image_files()
        total = len(files)

        if total == 0:
            print(f"❌ No image files found in: {self.input_dir}")
            return

        print(f"\n🎨 ReGenesis Sprite Batch Clipper")
        print(f"=" * 60)
        print(f"Input:  {self.input_dir}")
        print(f"Output: {self.output_dir}")
        print(f"Files:  {total} images found")
        print(f"Quality: {self.quality}")
        print(f"=" * 60)
        print()

        # Use tqdm progress bar if available
        iterator = tqdm(files, desc="Processing sprites", unit="img") if HAS_TQDM else files

        for idx, input_path in enumerate(iterator if HAS_TQDM else files, 1):
            output_path = self.preserve_directory_structure(input_path)

            # Skip if already processed
            if skip_existing and output_path.exists():
                if not HAS_TQDM:
                    print(f"[{idx}/{total}] SKIP: {input_path.name} (already exists)")
                self.skipped += 1
                continue

            if not HAS_TQDM:
                print(f"[{idx}/{total}]", end=' ')

            if self.clip_sprite(input_path, output_path, quiet=HAS_TQDM):
                self.processed += 1
                if HAS_TQDM:
                    iterator.set_postfix({"✅": self.processed, "⏭️": self.skipped, "❌": self.failed})
            else:
                self.failed += 1
                if HAS_TQDM:
                    iterator.set_postfix({"✅": self.processed, "⏭️": self.skipped, "❌": self.failed})

        # Summary
        print()
        print(f"=" * 60)
        print(f"✅ Processed: {self.processed}")
        print(f"⏭️  Skipped:   {self.skipped}")
        print(f"❌ Failed:    {self.failed}")
        print(f"📁 Output:    {self.output_dir}")
        print(f"=" * 60)


def main():
    parser = argparse.ArgumentParser(
        description="Batch remove backgrounds from sprite images using AI",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Process all sprites in folder
  python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites

  # High quality with alpha matting (slower but better edges)
  python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --quality high

  # Fast processing (good for testing)
  python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --quality fast

  # Reprocess existing files
  python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --reprocess
        """
    )

    parser.add_argument('input_dir', help='Directory containing raw sprite images')
    parser.add_argument('output_dir', help='Directory to save clipped sprites')
    parser.add_argument(
        '--quality',
        choices=['fast', 'high'],
        default='high',
        help='Processing quality (default: high)'
    )
    parser.add_argument(
        '--reprocess',
        action='store_true',
        help='Reprocess files even if output already exists'
    )

    args = parser.parse_args()

    # Validate input directory
    if not Path(args.input_dir).exists():
        print(f"❌ ERROR: Input directory does not exist: {args.input_dir}")
        sys.exit(1)

    # Create clipper and process
    clipper = SpriteBatchClipper(args.input_dir, args.output_dir, args.quality)
    clipper.process_all(skip_existing=not args.reprocess)


if __name__ == '__main__':
    main()
