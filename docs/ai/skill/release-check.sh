#!/usr/bin/env bash
# Everything that must pass before the AI guidance (skill, AGENTS.md variants, docs pages, llms.txt) is released.
# Usage: release-check.sh [--no-run]    (--no-run skips running the example tests; compile only)
# Environment: MVN_OFFLINE= (empty) allows Maven downloads (CI); CHECK_VERSIONS_FLAGS=--offline skips the Maven Central lookup.
set -u
HERE="$(cd "$(dirname "$0")" && pwd)"
fail=0
step() { echo; echo "== $1"; shift; "$@" || { echo "FAILED: $1"; fail=1; }; }

step "regenerate the AGENTS.md variants (no diff expected)" bash -c "cd '$HERE/../agents-md' && python3 build.py >/dev/null && git diff --quiet -- variants"
step "claims in the guidance exist in the released jars" python3 "$HERE/verify-claims.py"
step "versions are current and consistent" python3 "$HERE/check-versions.py" ${CHECK_VERSIONS_FLAGS:-}
if [ "${1:-}" = "--no-run" ]; then
  step "examples compile" bash "$HERE/serenity-bdd/check-examples.sh"
else
  step "troubleshooting entries reproduce and their fixes work" python3 "$HERE/troubleshooting/repro.py"
  step "Cucumber how-to commands do what the reference says" python3 "$HERE/cucumber-checks/verify.py"
  step "REST step-library claims hold" python3 "$HERE/rest-checks/verify.py"
  step "Cucumber with page objects claims hold" python3 "$HERE/cucumber-checks/verify_pages.py"
  step "test-result files and results.py behave as the reference says" python3 "$HERE/results-checks/verify.py"
  step "upgrade claims hold (moved classes, versions, JUnit 4)" python3 "$HERE/upgrade-checks/verify.py"
  step "configuration behaviour is as the reference says" python3 "$HERE/config-checks/verify.py"
  step "examples compile and their tests pass" bash -c "out=\$(bash '$HERE/serenity-bdd/check-examples.sh' --run); echo \"\$out\"; ! echo \"\$out\" | grep -q FAIL"
fi
echo; [ $fail -eq 0 ] && echo "RELEASE CHECK PASSED" || echo "RELEASE CHECK FAILED"
exit $fail
