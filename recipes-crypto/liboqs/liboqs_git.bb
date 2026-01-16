require liboqs.inc

ORG="open-quantum-safe"
BRANCH="main"
SRCREV = "5afca642057faa54878cf6937b46fe6f00b45646"

LICENSE:append = " & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=4b93ef2da47496727a4e8a59f443844e \
    file://src/sig_stfl/xmss/LICENSE;md5=8bed4ee3d2d03b58e2206a2cdf714e04 \
    file://src/sig_stfl/lms/external/license.txt;md5=175bba43b4ad4394534eeffe563ae226 \
    file://src/kem/bike/additional_r4/LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
    file://src/kem/kyber/pqcrystals-kyber_kyber512_ref/LICENSE;md5=011166b0c98b6730cf581065b9a9f069 \
"
