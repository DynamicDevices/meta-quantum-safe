# KAS local build configs

These KAS configs let you build/test this layer using your **local checkout** of `meta-quantum-safe`.

## Prerequisites

- `kas` installed (e.g. `python3 -m pip install --user kas`)

## Quickstart

- **Scarthgap (recommended / matches `main` integration CI mapping)**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
kas build kas/scarthgap.yml
```

- **Kirkstone**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
kas build kas/kirkstone.yml
```

- **Whinlatter**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
kas build kas/whinlatter.yml
```

## Notes

- Each config uses a separate `build_dir` so you can switch between releases without clobbering.
- Revisions for `poky` and `meta-openembedded` are pinned to match `.github/ci-pins.json`.
- Target image: `test-image-qs` (includes `liboqs` + `liboqs-ptest` and is suitable for `testimage`).

