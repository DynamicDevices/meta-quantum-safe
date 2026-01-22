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

# Ensure the provider installs to the *target* module dir (not a sysroot-derived path).
EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DOQS_PROVIDER_BUILD_STATIC=OFF \
    -DOPENSSL_MODULES_PATH=${libdir}/ossl-modules \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
    echo "${PV}" > ${D}${PTEST_PATH}/meta-quantum-safe-oqs-provider-version.txt
}

# The module lives under OpenSSL's module directory, which isn't in default FILES for ${PN}.
FILES:${PN} += "${libdir}/ossl-modules/*"

RDEPENDS:${PN}-ptest += "bash openssl"

