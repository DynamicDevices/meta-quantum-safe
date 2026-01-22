# meta-quantum-safe

[![CI main](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=main&label=CI%20main)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Amain)
[![CI scarthgap](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=scarthgap&label=CI%20scarthgap)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Ascarthgap)
[![CI kirkstone](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=kirkstone&label=CI%20kirkstone)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Akirkstone)
[![CI whinlatter](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=whinlatter&label=CI%20whinlatter)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Awhinlatter)

A Yocto/OpenEmbedded layer for experimenting with **post-quantum (quantum-safe) cryptography** on embedded Linux targets.

Primary focus today:
- `liboqs` packaging for Yocto
- Running `liboqs` ptests via `ptest-runner` (QEMU + hardware)
- Keeping CI builds reproducible across supported Yocto release branches

## Build status

| Branch | Support status* | CI status |
| --- | --- | --- |
| `main` | Integration branch (development) | [![main](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=main&label=build%20%26%20test)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Amain) |
| `kirkstone` | LTS | [![kirkstone](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=kirkstone&label=build%20%26%20test)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Akirkstone) |
| `scarthgap` | LTS (until Apr 2028) | [![scarthgap](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=scarthgap&label=build%20%26%20test)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Ascarthgap) |
| `whinlatter` | Supported (non-LTS) | [![whinlatter](https://img.shields.io/github/actions/workflow/status/DynamicDevices/meta-quantum-safe/CI_github.yml?branch=whinlatter&label=build%20%26%20test)](https://github.com/DynamicDevices/meta-quantum-safe/actions/workflows/CI_github.yml?query=branch%3Awhinlatter) |

\* Support status follows the Yocto Project release/support schedule: [Yocto Releases](https://wiki.yoctoproject.org/wiki/Releases)

### Notes on whinlatter pins

The `whinlatter` CI job currently builds against **poky `walnascar`** (pinned) because poky does not publish a `whinlatter` branch; `meta-openembedded` does publish `whinlatter`.

## NIST PQC standards (updated)

NIST has published the first set of finalized post-quantum cryptography standards:

| NIST reference | Standardized name(s) | Origin (NIST round-3 name) | What it is |
| --- | --- | --- | --- |
| **FIPS 203** | **ML-KEM** | CRYSTALS-Kyber | Key Encapsulation Mechanism (KEM) |
| **FIPS 204** | **ML-DSA** | CRYSTALS-Dilithium | Digital signature algorithm |
| **FIPS 205** | **SLH-DSA** | SPHINCS+ | Stateless hash-based digital signature algorithm |
| **FIPS 206** *(draft / future)* | **FN-DSA** | Falcon | Digital signature algorithm (draft / planned) |

Reference: [NIST releases first 3 finalized post-quantum encryption standards (Aug 2024)](https://www.nist.gov/news-events/news/2024/08/nist-releases-first-3-finalized-post-quantum-encryption-standards)

## Recipes provided by this layer

- **`liboqs`**: Open Quantum Safe C library (`https://openquantumsafe.org`)
- **`liboqs-ptest`**: ptest package that runs a lightweight test subset on target

This layer defaults to a pinned, stable `liboqs` version (currently **`0.15.0`**).

To select a specific version in your build (and keep `liboqs-ptest` aligned), add to `conf/local.conf`:

```conf
PREFERRED_VERSION:pn-liboqs = "0.15.0"        # also supported: "0.10.1", "git"
PREFERRED_VERSION:pn-liboqs-ptest = "0.15.0"
```

CI is intended to cover **`0.10.1`** and **`0.15.0`** across **`x86-64`** and **`arm64`** for supported branches.

### OpenSSL support

`liboqs` OpenSSL support is controlled via `PACKAGECONFIG`:

```conf
# Disable OpenSSL support (minimal builds)
PACKAGECONFIG:pn-liboqs = ""
```

## Using this layer in your Yocto build

Add this layer to `conf/bblayers.conf`:

```conf
BBLAYERS += " \
  ${OEROOT}/layers/meta-quantum-safe \
"
```

Then add `liboqs` to your image:

```conf
IMAGE_INSTALL:append = " liboqs"
```

## Testing (ptests)

### Enable runtime testimage + ptests

In `conf/local.conf`:

```conf
IMAGE_CLASSES += "testimage "
```

In your image recipe (or an image `.inc`):

```conf
IMAGE_INSTALL += "\
    ptest-runner \
    liboqs \
    liboqs-ptest \
"

DEFAULT_TEST_SUITES:pn-${PN} = "ssh ping ptest"
```

Then run:

```bash
bitbake <your-image> -c testimage
```

### Run ptests directly on target

```bash
ptest-runner -d /usr/lib
```

### What `liboqs-ptest` runs

The `run-ptest` script is designed to be **fast and memory-friendly**:
- Prefers **native test binaries** from `build/tests/` (e.g. `test_kem`, `test_sig`, `test_aes`, `test_sha3`, …)
- Picks compatible algorithm IDs based on the installed headers
- Optional extra algorithm coverage can be enabled with:

```bash
LIBOQS_PTEST_EXTRA_ALGS=1 ptest-runner -d /usr/lib
```

## CI overview

CI is defined in `.github/workflows/CI_github.yml` and typically:
- Pins `poky` and `meta-openembedded` commits via `.github/ci-pins.json`
- Builds `test-image-qs`
- Runs OEQA runtime tests (`ssh`, `ping`, `ptest`) under QEMU
- Uploads artifacts/logs (kept intentionally small)

## Maintainer

Alex J Lennon <ajlennon@dynamicdevices.co.uk>

## Licensing

This repository is licensed under the MIT license.

`liboqs` is also licensed under MIT, but contains sub-components under other licenses. See: https://openquantumsafe.org/liboqs/license.html

In this layer, the `liboqs` recipe reflects this by declaring a combined license set (MIT plus licenses from bundled implementations such as Apache-2.0 / CC0-1.0 / BSD-3-Clause) and by referencing representative license texts via `LIC_FILES_CHKSUM` per pinned upstream version.

