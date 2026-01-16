# KAS local build configs

These KAS configs let you build/test this layer using your **local checkout** of `meta-quantum-safe`.

## Prerequisites

- `kas` installed (e.g. `python3 -m pip install --user kas`)

## Quickstart

- **Scarthgap (recommended / matches `main` integration CI mapping)**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
mkdir -p build-kas/scarthgap
KAS_WORK_DIR="$PWD/build-kas/scarthgap" kas build kas/scarthgap.yml
```

- **Kirkstone**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
mkdir -p build-kas/kirkstone
KAS_WORK_DIR="$PWD/build-kas/kirkstone" kas build kas/kirkstone.yml
```

- **Whinlatter**:

```bash
cd /home/ajlennon/data_drive/dd/meta-quantum-safe
mkdir -p build-kas/whinlatter
KAS_WORK_DIR="$PWD/build-kas/whinlatter" kas build kas/whinlatter.yml
```

## Notes

- Use `KAS_WORK_DIR` to keep separate build directories per release so you can switch without clobbering.
- Revisions for `poky` and `meta-openembedded` are pinned to match `.github/ci-pins.json`.
- Target image: `test-image-qs` (includes `liboqs` + `liboqs-ptest` and is suitable for `testimage`).

