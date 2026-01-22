FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DESCRIPTION = "Open Quantum Safe provider module for OpenSSL 3 (adds PQ algorithms via liboqs)"
SUMMARY = "OpenSSL 3 provider for post-quantum algorithms backed by liboqs"
SECTION = "crypto"
HOMEPAGE = "https://openquantumsafe.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ab9b4308908ace39992d3080dd26824a"

SRC_URI = "git://github.com/open-quantum-safe/oqs-provider.git;protocol=https;branch=main \
           file://run-ptest \
          "

# Tag 0.6.1: compatible with liboqs 0.10.1 (doesn't reference newer OQS_SIG IDs like mayo/cross/uov).
SRCREV = "c4130ea3ae14f8adfb08235d0d7c5c5a3470666b"

S = "${WORKDIR}/git"

DEPENDS = "openssl liboqs"

inherit cmake pkgconfig ptest

# The provider module installs as an unversioned `.so` under `ossl-modules/`,
# so make it an explicit runtime package. Also allow the main package to be
# empty (it can just depend on the module package), otherwise it may not be
# emitted and rootfs (dnf) won't find `oqs-provider`.
PACKAGES += "${PN}-module"
FILES:${PN}-module = "${libdir}/ossl-modules/*"
RDEPENDS:${PN} += "${PN}-module"
ALLOW_EMPTY:${PN} = "1"

# Ensure the provider installs to the *target* module dir (not a sysroot-derived path).
EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DOQS_PROVIDER_BUILD_STATIC=OFF \
    -DOPENSSL_MODULES_PATH=${libdir}/ossl-modules \
"

do_install:append() {
    # Belt-and-suspenders: ensure the module ends up in the expected runtime dir.
    install -d ${D}${libdir}/ossl-modules
    if [ -d "${B}/lib" ]; then
        # oqs-provider builds the module into ${B}/lib with PREFIX "" and OUTPUT_NAME oqsprovider
        if [ -e "${B}/lib/oqsprovider.so" ]; then
            cp -a --no-preserve=ownership "${B}/lib/oqsprovider.so"* "${D}${libdir}/ossl-modules/" || true
        elif [ -e "${B}/lib/oqsprovider.dylib" ]; then
            cp -a --no-preserve=ownership "${B}/lib/oqsprovider.dylib"* "${D}${libdir}/ossl-modules/" || true
        elif [ -e "${B}/bin/oqsprovider.dll" ]; then
            install -d ${D}${libdir}/ossl-modules
            cp -a --no-preserve=ownership "${B}/bin/oqsprovider.dll"* "${D}${libdir}/ossl-modules/" || true
        fi
    fi
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
    echo "${PV}" > ${D}${PTEST_PATH}/meta-quantum-safe-oqs-provider-version.txt
}

# The module lives under OpenSSL's module directory, which isn't in default FILES for ${PN}.
# (Packaged via ${PN}-module above.)

RDEPENDS:${PN}-ptest += "bash openssl"

