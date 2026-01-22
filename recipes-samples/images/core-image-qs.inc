# Build up test image here.
# Keep ptest coverage focused on recipes provided by this layer (e.g. liboqs),
# rather than enabling Yocto-wide ptests via IMAGE_FEATURES += "ptest-pkgs"
# which significantly increases runtime and can introduce unrelated failures.

IMAGE_INSTALL += "\
        python3-pyyaml \
        ptest-runner \
        openssl \
        liboqs \
        liboqs-ptest \
        oqs-provider \
        oqs-provider-ptest \
"

DEFAULT_TEST_SUITES:pn-${PN} = "ssh ping ptest"

