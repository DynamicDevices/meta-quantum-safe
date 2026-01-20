FILESEXTRAPATHS:prepend = "${THISDIR}/${PN}:"

DESCRIPTION = "C library for prototyping and experimenting with quantum-resistant cryptography"
SUMMARY = "C library for prototyping and experimenting with quantum-resistant cryptography"
SECTION = "crypto"
HOMEPAGE = "https://openquantumsafe.org/"
CVE_PRODUCT = "liboqs"

# liboqs vendors multiple upstream implementations; licensing is a combination.
# Per-version recipes define LIC_FILES_CHKSUM to point at representative license texts present in that release.
LICENSE = "MIT & Apache-2.0 & CC0-1.0"

ORG="open-quantum-safe"
BRANCH="0.10.1-release"
SRC_URI = "git://github.com/${ORG}/liboqs.git;protocol=https;branch=${BRANCH} \
	   file://run-ptest \
	   file://qs-capability-check.py \
           file://01-check-installed-library.patch \
"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "-DOQS_USE_OPENSSL=ON -DOQS_DLOPEN_OPENSSL=OFF,-DOQS_USE_OPENSSL=OFF,,openssl"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON -DOQS_DIST_BUILD=OFF"
EXTRA_OECMAKE:append = " ${@bb.utils.contains('PACKAGECONFIG', 'openssl', '-DOQS_USE_OPENSSL=ON -DOQS_DLOPEN_OPENSSL=OFF', '-DOQS_USE_OPENSSL=OFF', d)}"

S = "${WORKDIR}/git"

DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'openssl', '', d)}"

inherit pkgconfig cmake ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH} ${D}${PTEST_PATH}/build
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
    install -m 0755 ${WORKDIR}/qs-capability-check.py ${D}${PTEST_PATH}/qs-capability-check.py
    echo "${PV}" > ${D}${PTEST_PATH}/meta-quantum-safe-liboqs-version.txt

    cp -r --no-preserve=ownership ${B}/include ${D}${PTEST_PATH}/build/
    cp -r --no-preserve=ownership ${S}/src ${D}${PTEST_PATH}/
    cp -r --no-preserve=ownership ${S}/docs ${D}${PTEST_PATH}/
    cp -r --no-preserve=ownership ${S}/scripts ${D}${PTEST_PATH}/
    cp -r --no-preserve=ownership ${S}/tests ${D}${PTEST_PATH}/
    cp -r --no-preserve=ownership ${B}/tests ${D}${PTEST_PATH}/build/
}

# Keep ptest runtime deps minimal so older release branches don't need meta-python.
# Our run-ptest uses python3 for lightweight header parsing and capability checks.
RDEPENDS:${PN}-ptest += "bash python3 binutils"
