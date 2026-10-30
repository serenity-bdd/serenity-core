#!/usr/bin/env bash
# Compile each guidance example set in a copy of its harness project.
# Usage: check-examples.sh [--run] [example-set ...]   (default: all sets, compile only)
# Set MVN_OFFLINE= (empty) to allow downloads, as in CI; the default is offline (-o).
# --run also runs the tests (mvn verify, headless Chrome / Playwright, needs network for the sample sites)
set -u
HERE="$(cd "$(dirname "$0")" && pwd)"
declare -A HARNESS=( [sp-selenium]=h-sp-selenium [sp-rest]=h-sp-rest [rest-steps]=h-sp-rest [sp-playwright]=h-sp-playwright [cuke-sp]=h-cuke-sp [cuke-plain]=h-cuke-sp [cuke-pages]=h-cuke-sp [config]=h-po-selenium [living-docs]=h-sp-selenium [living-docs-annotated]=h-sp-selenium [po-selenium]=h-po-selenium [po-playwright]=h-po-playwright )
RUN=0; if [ "${1:-}" = "--run" ]; then RUN=1; shift; fi
SETS=("$@"); [ ${#SETS[@]} -eq 0 ] && SETS=("${!HARNESS[@]}")
WORK="$(mktemp -d)"
for s in "${SETS[@]}"; do
  [ -d "$HERE/examples/$s" ] || continue
  cp -R "$HERE/../../eval/harness/${HARNESS[$s]}" "$WORK/$s"
  cp -R "$HERE/examples/$s/." "$WORK/$s/src/test/java/"
  # non-Java resources (feature files) belong on the test classpath
  if [ -d "$HERE/examples/$s/resources" ]; then rm -rf "$WORK/$s/src/test/java/resources"; cp -R "$HERE/examples/$s/resources/." "$WORK/$s/src/test/resources/"; fi
  if [ -d "$HERE/examples/$s/features" ]; then rm -rf "$WORK/$s/src/test/java/features"; cp -R "$HERE/examples/$s/features" "$WORK/$s/src/test/resources/"; fi
  if [ $RUN -eq 1 ]; then
    if (cd "$WORK/$s" && mvn -q -B verify -Dheadless.mode=true >"$WORK/$s.log" 2>&1); then echo "RUN PASS $s ($(cat "$WORK"/$s/target/surefire-reports/*.xml 2>/dev/null | grep -o '<testsuite [^>]*' | grep -o 'tests="[0-9]*"' | cut -d\" -f2 | paste -sd+ - | bc) tests)"; else echo "RUN FAIL $s ($WORK/$s.log)"; grep -E "Tests run:|FAIL|ERROR\]" "$WORK/$s.log" | head -8; fi
  elif (cd "$WORK/$s" && mvn -q -B ${MVN_OFFLINE--o} test-compile >"$WORK/$s.log" 2>&1); then echo "PASS $s"; else echo "FAIL $s"; grep -E "ERROR.*\.java|error:" "$WORK/$s.log" | head -15; fi
done
