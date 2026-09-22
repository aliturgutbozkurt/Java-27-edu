#!/usr/bin/env bash
#
# verify-examples.sh — proves every example in this curriculum actually runs.
#
# The deliverable here is teaching material, so "correct" means the code a
# learner is told to run does what the lesson claims. This script is what
# makes that a fact rather than a hope.
#
# Usage:
#   ./scripts/verify-examples.sh                         # whole repository
#   ./scripts/verify-examples.sh modules/01-getting-started   # one module
#
# Each .java file may declare an expectation in its first 10 lines:
#
#   (no marker)               compile and run; must exit 0
#   // EXPECT: compile-error  must FAIL to compile (teaching a compile error)
#   // EXPECT: preview        run with --enable-preview --source 27; must exit 0
#   // EXPECT: compile-only   must compile; not run (helpers, multi-file setups)
#   // EXPECT: runtime-error  must compile, then FAIL at runtime (teaching a crash)
#
set -uo pipefail

REQUIRED_JDK=27
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_TIMEOUT=60

# --- colours, only when attached to a terminal -----------------------------
if [ -t 1 ]; then
  RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'; DIM=$'\033[2m'; RESET=$'\033[0m'
else
  RED=''; GREEN=''; YELLOW=''; DIM=''; RESET=''
fi

pass_count=0
fail_count=0
failures=()

# --- preflight: the JDK must be the one this curriculum targets ------------
if ! command -v java >/dev/null 2>&1; then
  echo "${RED}No 'java' on PATH.${RESET}"
  echo "This curriculum targets JDK ${REQUIRED_JDK}. Install it and try again."
  exit 1
fi

detected_jdk="$(java -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+).*/\1/')"

if [ "$detected_jdk" != "$REQUIRED_JDK" ]; then
  echo "${RED}Wrong JDK.${RESET}"
  echo "  Required: ${REQUIRED_JDK}"
  echo "  Found:    ${detected_jdk:-unknown}  ($(java -version 2>&1 | head -1))"
  echo
  echo "Every example here is verified against JDK ${REQUIRED_JDK}. Running them on"
  echo "another release may fail for reasons that have nothing to do with the lesson."
  echo
  echo "On macOS:  export JAVA_HOME=\$(/usr/libexec/java_home -v ${REQUIRED_JDK})"
  exit 1
fi

# --- portable timeout ------------------------------------------------------
# Concurrency examples can hang. Coreutils' timeout is not on macOS by default,
# so fall back to a plain background-and-kill when it is missing.
if command -v timeout >/dev/null 2>&1; then
  with_timeout() { timeout "$RUN_TIMEOUT" "$@"; }
elif command -v gtimeout >/dev/null 2>&1; then
  with_timeout() { gtimeout "$RUN_TIMEOUT" "$@"; }
else
  with_timeout() {
    "$@" &
    local pid=$!
    # The watchdog's output goes to /dev/null on purpose. Callers wrap this in
    # command substitution, and $( ) waits for every process holding the pipe's
    # write end. A watchdog that inherited that pipe would keep it open for the
    # full sleep, stalling each file for RUN_TIMEOUT even when it finished at once.
    ( sleep "$RUN_TIMEOUT"; kill -9 "$pid" 2>/dev/null ) >/dev/null 2>&1 &
    local watchdog=$!
    wait "$pid" 2>/dev/null
    local rc=$?
    kill -9 "$watchdog" 2>/dev/null
    wait "$watchdog" 2>/dev/null
    return $rc
  }
fi

# --- reporting helpers -----------------------------------------------------
report_pass() {
  pass_count=$((pass_count + 1))
  printf '  %sPASS%s  %s %s(%s)%s\n' "$GREEN" "$RESET" "$1" "$DIM" "$2" "$RESET"
}

report_fail() {
  local file="$1" reason="$2" output="$3"
  fail_count=$((fail_count + 1))
  failures+=("$file")
  printf '  %sFAIL%s  %s\n' "$RED" "$RESET" "$file"
  printf '        %s\n' "$reason"
  if [ -n "$output" ]; then
    printf '%s\n' "$output" | sed 's/^/        | /'
  fi
}

# --- the check for a single file -------------------------------------------
check_file() {
  local file="$1"
  local rel="${file#"$REPO_ROOT"/}"
  local marker output rc
  local workdir

  marker="$(head -10 "$file" | grep -o 'EXPECT: *[a-z-]*' | head -1 | sed 's/EXPECT: *//')"
  workdir="$(mktemp -d)"

  case "$marker" in
    compile-error)
      output="$(javac -d "$workdir" "$file" 2>&1)"
      rc=$?
      if [ $rc -ne 0 ]; then
        report_pass "$rel" "failed to compile, as intended"
      else
        report_fail "$rel" "Declared '// EXPECT: compile-error' but it compiled cleanly." ""
      fi
      ;;

    compile-only)
      output="$(javac -d "$workdir" "$file" 2>&1)"
      rc=$?
      if [ $rc -eq 0 ]; then
        report_pass "$rel" "compiled"
      else
        report_fail "$rel" "Declared '// EXPECT: compile-only' but compilation failed." "$output"
      fi
      ;;

    runtime-error)
      output="$(with_timeout java "$file" 2>&1)"
      rc=$?
      if [ $rc -eq 124 ] || [ $rc -eq 137 ]; then
        report_fail "$rel" "Timed out after ${RUN_TIMEOUT}s rather than failing." "$output"
      elif [ $rc -ne 0 ]; then
        # Distinguish a compile failure from the runtime crash we asked for.
        if printf '%s' "$output" | grep -q 'error: compilation failed'; then
          report_fail "$rel" "Declared '// EXPECT: runtime-error' but it failed to COMPILE. Use compile-error instead." "$output"
        else
          report_pass "$rel" "crashed at runtime, as intended"
        fi
      else
        report_fail "$rel" "Declared '// EXPECT: runtime-error' but it exited 0." ""
      fi
      ;;

    preview)
      output="$(with_timeout java --enable-preview --source "$REQUIRED_JDK" "$file" 2>&1)"
      rc=$?
      if [ $rc -eq 0 ]; then
        report_pass "$rel" "ran with --enable-preview"
      elif [ $rc -eq 124 ] || [ $rc -eq 137 ]; then
        report_fail "$rel" "Timed out after ${RUN_TIMEOUT}s." "$output"
      else
        report_fail "$rel" "Exited ${rc} while running with --enable-preview." "$output"
      fi
      ;;

    '')
      output="$(with_timeout java "$file" 2>&1)"
      rc=$?
      if [ $rc -eq 0 ]; then
        report_pass "$rel" "ran"
      elif [ $rc -eq 124 ] || [ $rc -eq 137 ]; then
        report_fail "$rel" "Timed out after ${RUN_TIMEOUT}s. If it blocks on purpose, add '// EXPECT: compile-only'." "$output"
      else
        report_fail "$rel" "Exited ${rc}." "$output"
      fi
      ;;

    *)
      report_fail "$rel" "Unknown marker '// EXPECT: ${marker}'. Valid: compile-error, compile-only, runtime-error, preview." ""
      ;;
  esac

  rm -rf "$workdir"
}

# --- collect the files to check --------------------------------------------
target="${1:-}"
if [ -n "$target" ]; then
  search_root="$REPO_ROOT/${target#"$REPO_ROOT"/}"
  if [ ! -d "$search_root" ]; then
    echo "${RED}No such directory: ${target}${RESET}"
    exit 1
  fi
else
  search_root="$REPO_ROOT"
fi

java_files=()
while IFS= read -r f; do
  java_files+=("$f")
done < <(
  find "$search_root" \
    \( -path '*/examples/*.java' -o -path "$REPO_ROOT/solutions/*.java" \) \
    -type f 2>/dev/null | sort
)

echo "Verifying with JDK ${detected_jdk} in ${search_root#"$REPO_ROOT"/}"
echo

if [ ${#java_files[@]} -eq 0 ]; then
  echo "${YELLOW}No example or solution files found yet.${RESET}"
  echo "Nothing to verify, which is fine while the curriculum is still being written."
  exit 0
fi

for f in "${java_files[@]}"; do
  check_file "$f"
done

# --- summary ---------------------------------------------------------------
echo
total=$((pass_count + fail_count))
if [ $fail_count -eq 0 ]; then
  echo "${GREEN}All ${total} file(s) behaved as declared.${RESET}"
  exit 0
fi

echo "${RED}${fail_count} of ${total} file(s) failed:${RESET}"
for f in "${failures[@]}"; do
  echo "  - $f"
done
exit 1
