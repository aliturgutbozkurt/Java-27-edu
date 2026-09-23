#!/usr/bin/env bash
#
# build-pdfs.sh — render every Markdown file in this repository to PDF.
#
# Produces, under pdf/:
#   one PDF per Markdown file, named so they are distinguishable (22 of the
#   source files are called README.md), plus a single combined book.
#
# Requires pandoc and Google Chrome. Chrome is used as the PDF engine because
# pandoc's default needs a LaTeX installation, and Chrome renders the tables and
# code blocks in these lessons correctly without one.
#
# Usage:
#   ./scripts/build-pdfs.sh            # everything
#   ./scripts/build-pdfs.sh --book     # only the combined book
#
set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="$REPO_ROOT/pdf"
CSS="$REPO_ROOT/scripts/pdf.css"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

# --- preflight ---------------------------------------------------------------
if ! command -v pandoc >/dev/null 2>&1; then
  echo "pandoc is not installed."
  echo "  macOS:  brew install pandoc"
  exit 1
fi

if [ ! -x "$CHROME" ]; then
  for candidate in \
      "/Applications/Chromium.app/Contents/MacOS/Chromium" \
      "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge" \
      "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"; do
    [ -x "$candidate" ] && CHROME="$candidate" && break
  done
fi

if [ ! -x "$CHROME" ]; then
  echo "No Chromium-based browser found to render PDFs."
  echo "  Install Google Chrome, or use: pandoc --pdf-engine=xelatex (needs LaTeX)"
  exit 1
fi

mkdir -p "$OUT_DIR"

# --- html -> pdf -------------------------------------------------------------
render() {
  local html="$1" pdf="$2"

  rm -f "$pdf"

  # Chrome headless writes the PDF and then does not exit. Waiting on the
  # process therefore hangs forever, which is what the first version of this
  # script did. So: launch it, poll for the finished file, and kill it.
  #
  # --no-pdf-header-footer drops Chrome's URL and date furniture, and the
  # profile directory is temporary so this never touches a real browser session.
  "$CHROME" --headless --disable-gpu --no-first-run \
      --user-data-dir="$WORK/chrome-profile" \
      --no-pdf-header-footer \
      --print-to-pdf="$pdf" "file://$html" >/dev/null 2>&1 &
  local pid=$!

  # Poll for up to 30 seconds. A file is only finished when its size has
  # stopped growing, so require two consecutive equal readings before
  # declaring it done.
  local waited=0 size=0 previous=-1
  while [ $waited -lt 300 ]; do
    if [ -f "$pdf" ]; then
      size=$(wc -c < "$pdf" 2>/dev/null || echo 0)
      if [ "$size" -gt 0 ] && [ "$size" = "$previous" ]; then
        break
      fi
      previous=$size
    fi
    sleep 0.1
    waited=$((waited + 1))
  done

  kill -9 "$pid" 2>/dev/null
  wait "$pid" 2>/dev/null

  [ -s "$pdf" ]
}

# Give each file a name that says what it is. Twenty-two of them are README.md,
# so mirroring the source names would produce an unusable directory.
output_name() {
  local rel="$1"
  case "$rel" in
    README.md)                     echo "00-overview" ;;
    SPEC.md)                       echo "00-spec" ;;
    tasks/plan.md)                 echo "00-plan" ;;
    tasks/todo.md)                 echo "00-tasks" ;;
    modules/*/homework/README.md)  echo "$(basename "$(dirname "$(dirname "$rel")")")-homework" ;;
    modules/*/README.md)           echo "$(basename "$(dirname "$rel")")" ;;
    *)                             echo "$(echo "${rel%.md}" | tr '/' '-')" ;;
  esac
}

# A readable title for the PDF's first page and its document metadata.
title_for() {
  local rel="$1"
  case "$rel" in
    README.md)                     echo "Java 27 for Programmers New to Java" ;;
    SPEC.md)                       echo "Specification" ;;
    tasks/plan.md)                 echo "Implementation Plan" ;;
    tasks/todo.md)                 echo "Task Breakdown" ;;
    modules/*/homework/README.md)
      local id
      id="$(basename "$(dirname "$(dirname "$rel")")")"
      echo "Homework: ${id}" ;;
    modules/*/README.md)           echo "$(basename "$(dirname "$rel")")" ;;
    *)                             echo "${rel%.md}" ;;
  esac
}

# --- collect sources ---------------------------------------------------------
# Ordered so the combined book reads front to back: overview, spec, then the
# modules in sequence with each homework following its lesson.
sources=()
[ -f "$REPO_ROOT/README.md" ] && sources+=("README.md")
[ -f "$REPO_ROOT/SPEC.md" ] && sources+=("SPEC.md")
for dir in "$REPO_ROOT"/modules/*/; do
  id="$(basename "$dir")"
  [ -f "$dir/README.md" ] && sources+=("modules/$id/README.md")
  [ -f "$dir/homework/README.md" ] && sources+=("modules/$id/homework/README.md")
done
[ -f "$REPO_ROOT/tasks/plan.md" ] && sources+=("tasks/plan.md")
[ -f "$REPO_ROOT/tasks/todo.md" ] && sources+=("tasks/todo.md")

echo "Rendering ${#sources[@]} Markdown files to PDF"
echo

book_only=false
[ "${1:-}" = "--book" ] && book_only=true

# --- per-file PDFs -----------------------------------------------------------
made=0
failed=0

if [ "$book_only" = false ]; then
  for rel in "${sources[@]}"; do
    name="$(output_name "$rel")"
    title="$(title_for "$rel")"
    html="$WORK/$name.html"
    pdf="$OUT_DIR/$name.pdf"

    # pagetitle, not title. Setting `title` makes pandoc render a heading
    # block above the content, and every one of these files already opens with
    # its own H1, so that produced the title twice. pagetitle sets only the
    # document's <title>, which becomes the PDF's metadata title.
    pandoc "$REPO_ROOT/$rel" \
        --standalone \
        --css="$CSS" \
        --embed-resources \
        --metadata pagetitle="$title" \
        --from=gfm \
        --to=html5 \
        -o "$html" 2>/dev/null

    if render "$html" "$pdf"; then
      printf '  ok    %-42s %6s KB\n' "$name.pdf" "$(( $(wc -c < "$pdf") / 1024 ))"
      made=$((made + 1))
    else
      printf '  FAIL  %s\n' "$name.pdf"
      failed=$((failed + 1))
    fi
  done
  echo
fi

# --- the combined book -------------------------------------------------------
echo "Building the combined book"

book_md="$WORK/book.md"
: > "$book_md"

for rel in "${sources[@]}"; do
  # A rule before each document makes the page break land cleanly.
  printf '\n\n<div class="page-break"></div>\n\n' >> "$book_md"
  cat "$REPO_ROOT/$rel" >> "$book_md"
  printf '\n' >> "$book_md"
done

pandoc "$book_md" \
    --standalone \
    --css="$CSS" \
    --embed-resources \
    --metadata title="Java 27 for Programmers New to Java" \
    --toc --toc-depth=2 \
    --from=gfm \
    --to=html5 \
    -o "$WORK/book.html" 2>/dev/null

if render "$WORK/book.html" "$OUT_DIR/Java-27-Curriculum.pdf"; then
  printf '  ok    %-42s %6s KB\n' "Java-27-Curriculum.pdf" \
      "$(( $(wc -c < "$OUT_DIR/Java-27-Curriculum.pdf") / 1024 ))"
  made=$((made + 1))
else
  printf '  FAIL  Java-27-Curriculum.pdf\n'
  failed=$((failed + 1))
fi

# --- summary -----------------------------------------------------------------
echo
if [ $failed -eq 0 ]; then
  echo "Done. $made PDF(s) in ${OUT_DIR#"$REPO_ROOT"/}/"
  exit 0
fi

echo "$made succeeded, $failed failed."
exit 1
