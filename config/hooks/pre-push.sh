#!/bin/sh
echo "Running detekt analysis and unit tests..."

OUTPUT="result"
./gradlew detekt --daemon > ${OUTPUT}
EXIT_CODE=$?
if [ ${EXIT_CODE} -ne 0 ]; then
    cat ${OUTPUT}
    rm ${OUTPUT}
    echo "*********************************************"
    echo "           Detekt Analysis Failed            "
    echo "  Please check and fix rules before pushing  "
    echo "*********************************************"
    exit ${EXIT_CODE}
else
    rm ${OUTPUT}
    echo "*********************************************"
    echo "          Detekt Analysis succeeded          "
    echo "*********************************************"
fi

#./gradlew app:testTstDebug --daemon > ${OUTPUT}
#EXIT_CODE=$?
#if [ ${EXIT_CODE} -ne 0 ]; then
#    cat ${OUTPUT}
#    rm ${OUTPUT}
#    echo "*********************************************"
#    echo "            Unit Tests Failed                "
#    echo "    Please fix the tests before pushing      "
#    echo "*********************************************"
#    exit ${EXIT_CODE}
#else
#    rm ${OUTPUT}
#    echo "*********************************************"
#    echo "            Unit tests succeeded             "
#    echo "*********************************************"
#fi