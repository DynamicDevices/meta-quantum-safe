#!/usr/bin/env python3
import re
import sys
from pathlib import Path


def _read_version() -> str:
    p = Path("meta-quantum-safe-liboqs-version.txt")
    if not p.exists():
        return "unknown"
    return p.read_text(errors="ignore").strip() or "unknown"


def _extract_ids(header_path: Path, macro_prefix: str) -> set[str]:
    txt = header_path.read_text(errors="ignore")
    pat = re.compile(rf'^#define\s+{macro_prefix}_alg_\w+\s+"([^"]+)"', re.M)
    return set(pat.findall(txt))


def _find_header(candidates: list[str]) -> Path:
    for c in candidates:
        p = Path(c)
        if p.exists():
            return p
    raise FileNotFoundError(f"Could not find any of: {candidates}")


def main() -> int:
    version = _read_version()

    kem_h = _find_header(["src/kem/kem.h", "build/include/oqs/kem.h"])
    sig_h = _find_header(["src/sig/sig.h", "build/include/oqs/sig.h"])

    kems = _extract_ids(kem_h, "OQS_KEM")
    sigs = _extract_ids(sig_h, "OQS_SIG")

    # Always-on sanity (should hold across versions)
    required_kems = {"ML-KEM-768"}
    required_sigs = {"ML-DSA-44"}

    missing = []
    for k in sorted(required_kems):
        if k not in kems:
            missing.append(f"missing KEM {k}")
    for s in sorted(required_sigs):
        if s not in sigs:
            missing.append(f"missing SIG {s}")

    # Version-specific assertions (lightweight, high-signal)
    if version.startswith("0.10.1"):
        # 0.10.1 has IPD identifiers and classic Dilithium names
        if "ML-KEM-768-ipd" not in kems:
            missing.append("expected KEM ML-KEM-768-ipd on 0.10.1")
        if "Dilithium2" not in sigs:
            missing.append("expected SIG Dilithium2 on 0.10.1")
        if any(x.startswith("NTRU-HPS-") for x in kems):
            missing.append("did not expect NTRU-HPS-* KEMs on 0.10.1")
    elif version.startswith("0.15.0"):
        # 0.15.0 drops IPD identifiers and classic Dilithium names; adds NTRU family.
        if "ML-KEM-768-ipd" in kems:
            missing.append("did not expect KEM ML-KEM-768-ipd on 0.15.0")
        if "Dilithium2" in sigs:
            missing.append("did not expect SIG Dilithium2 on 0.15.0")
        if "NTRU-HPS-2048-509" not in kems:
            missing.append("expected KEM NTRU-HPS-2048-509 on 0.15.0")
    elif version == "git":
        # git is pinned; assert a couple of stable expectations for our pinned SRCREV.
        if "Dilithium2" not in sigs:
            missing.append("expected SIG Dilithium2 on git (pinned SRCREV)")
        if "ML-KEM-768-ipd" in kems:
            missing.append("did not expect KEM ML-KEM-768-ipd on git (pinned SRCREV)")

    if missing:
        sys.stderr.write("qs-capability-check failed:\n")
        for m in missing:
            sys.stderr.write(f"  - {m}\n")
        sys.stderr.write(f"version={version}\n")
        sys.stderr.write(f"kem_h={kem_h}\n")
        sys.stderr.write(f"sig_h={sig_h}\n")
        return 1

    print(f"qs-capability-check: OK (version={version})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

