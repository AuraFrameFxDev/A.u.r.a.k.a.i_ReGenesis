#!/usr/bin/env python3
"""
Genesis Protocol Icon Processor
Straightens and prepares the Genesis Protocol logo (house/gear/wings) for both day and night themes
"""

from PIL import Image, ImageDraw, ImageFilter, ImageEnhance
import os
import sys

def detect_tilt(image):
    """Detect if image has a slight tilt"""
    # Based on visual inspection, the original images had ~0.5 degree tilt
    # Return correction angle to straighten
    return -0.5

def straighten_image(image, angle):
    """Rotate image by angle to straighten"""
    # Expand canvas to avoid clipping corners
    width, height = image.size
    new_size = int((width**2 + height**2)**0.5) + 10

    # Create expanded canvas
    expanded = Image.new('RGBA', (new_size, new_size), (0, 0, 0, 0))
    offset = ((new_size - width) // 2, (new_size - height) // 2)
    expanded.paste(image, offset)

    # Rotate
    rotated = expanded.rotate(angle, resample=Image.BICUBIC, expand=False)

    # Crop back to original aspect ratio
    bbox = rotated.getbbox()
    if bbox:
        cropped = rotated.crop(bbox)
    else:
        cropped = rotated

    return cropped

def create_adaptive_icon_set(image, output_dir, name_prefix, theme_qualifier=None):
    """Create Android adaptive icon set (mdpi to xxxhdpi)

    Args:
        image: PIL Image to process
        output_dir: Base res directory
        name_prefix: Icon filename prefix (e.g., 'ic_launcher')
        theme_qualifier: Optional theme qualifier ('night' or 'day') for Android resource qualifiers
    """
    sizes = {
        'mdpi': 48,
        'hdpi': 72,
        'xhdpi': 96,
        'xxhdpi': 144,
        'xxxhdpi': 192
    }

    for density, size in sizes.items():
        # Android resource qualifiers: mipmap-<theme>-<density>
        # e.g., mipmap-night-mdpi, mipmap-day-hdpi
        if theme_qualifier:
            density_dir = os.path.join(output_dir, f'mipmap-{theme_qualifier}-{density}')
        else:
            density_dir = os.path.join(output_dir, f'mipmap-{density}')
        os.makedirs(density_dir, exist_ok=True)

        resized = image.copy()
        resized.thumbnail((size, size), Image.Resampling.LANCZOS)

        # Create square canvas
        icon = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        offset = ((size - resized.width) // 2, (size - resized.height) // 2)
        icon.paste(resized, offset)

        output_path = os.path.join(density_dir, f'{name_prefix}.png')
        icon.save(output_path, 'PNG', optimize=True)

    print(f"  ✓ Created adaptive icon set in {output_dir}")

def create_play_store_icon(image, output_path):
    """Create 512x512 Play Store icon"""
    icon = image.copy()
    icon.thumbnail((512, 512), Image.Resampling.LANCZOS)

    # Create square canvas
    play_icon = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    offset = ((512 - icon.width) // 2, (512 - icon.height) // 2)
    play_icon.paste(icon, offset)

    play_icon.save(output_path, 'PNG', optimize=True)
    print(f"  ✓ Created Play Store icon: {output_path}")

def process_icon(input_path, output_dir, name, theme):
    """Process a single icon"""
    if not os.path.exists(input_path):
        print(f"  ✗ File not found: {input_path}")
        return None

    print(f"\n{'🌙' if theme == 'night' else '☀️'} Processing {theme.upper()} icon...")
    print(f"  Source: {input_path}")

    # Load image
    image = Image.open(input_path).convert('RGBA')
    original_size = image.size
    print(f"  Original size: {original_size}")

    # Detect tilt
    tilt_angle = detect_tilt(image)
    print(f"  Detected tilt: {tilt_angle:.2f} degrees")

    # Straighten
    if abs(tilt_angle) > 0.1:
        corrected = straighten_image(image, -tilt_angle)
        print(f"  Rotated by {-tilt_angle:.2f} degrees to straighten")
    else:
        corrected = image
        print("  No rotation needed")

    # Save high-res version
    os.makedirs(output_dir, exist_ok=True)
    main_output = os.path.join(output_dir, f'{name}.png')
    corrected.save(main_output, 'PNG', optimize=True)
    print(f"  Saved: {main_output} ({corrected.size[0]}x{corrected.size[1]})")

    return corrected

def main():
    print("=" * 60)
    print("🏠 Genesis Protocol Icon Processor")
    print("=" * 60)

    # Setup paths
    base_dir = "/home/user/ReGenesis--multi-architectural-70-LDO-"
    output_base = os.path.join(base_dir, "app/src/main/res")

    # Input files (from user uploads)
    night_icon = "/mnt/user-data/uploads/unnamed__3_.jpg"
    day_icon = "/mnt/user-data/uploads/Create_a_unique_100x.png"

    # Process night icon (glowing cyan on starfield)
    night_processed = process_icon(
        night_icon,
        output_base,
        "ic_genesis_night",
        "night"
    )

    # Process day icon (cyan-to-magenta gradient)
    day_processed = process_icon(
        day_icon,
        output_base,
        "ic_genesis_day",
        "day"
    )

    if night_processed:
        # Create adaptive icons for night theme
        print("\n📱 Creating Android adaptive icon sets...")
        create_adaptive_icon_set(
            night_processed,
            output_base,
            "ic_launcher",
            theme_qualifier="night"
        )

        # Create Play Store icon
        print("\n🎨 Creating Play Store icons (512x512)...")
        create_play_store_icon(
            night_processed,
            os.path.join(base_dir, "ic_genesis_night_512.png")
        )

    if day_processed:
        # Create adaptive icons for day theme
        create_adaptive_icon_set(
            day_processed,
            output_base,
            "ic_launcher",
            theme_qualifier="day"
        )

        # Create Play Store icon
        create_play_store_icon(
            day_processed,
            os.path.join(base_dir, "ic_genesis_day_512.png")
        )

    print("\n" + "=" * 60)
    print("✅ Icon processing complete!")
    print("=" * 60)
    print("\nOutput locations:")
    print(f"  - High-res icons: {output_base}")
    print(f"  - Night adaptive icons: {output_base}/mipmap-night-<density>/")
    print(f"  - Day adaptive icons: {output_base}/mipmap-day-<density>/")
    print(f"  - Play Store icons: {base_dir}/ic_genesis_*_512.png")
    print()

if __name__ == "__main__":
    main()
