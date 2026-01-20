# Build up test image here
# Pull in *all* available ptest packages for installed components so OEQA's
# runtime ptest suite gets broad coverage.
IMAGE_FEATURES += "ptest-pkgs"

IMAGE_INSTALL += "\
        python3-pyyaml \
        ptest-runner \
        liboqs \
        liboqs-ptest \
"

DEFAULT_TEST_SUITES:pn-${PN} = "ssh ping ptest"

